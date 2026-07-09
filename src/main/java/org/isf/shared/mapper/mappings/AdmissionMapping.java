/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.shared.mapper.mappings;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.model.Admission;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class AdmissionMapping {

	public static void addMapping(ModelMapper modelMapper) {
		// Admission extends SoftDeletableAuditable, which adds the String fields deletedBy/deletedDate.
		// With ambiguity ignored, ModelMapper otherwise matches one of those to the DTO's String 'deleted'
		// instead of the char 'deleted', leaving it null. Map the 'deleted' flag explicitly in both
		// directions with a converter (the char <-> String conversion cannot be expressed as a plain getter).
		modelMapper.getConfiguration().setAmbiguityIgnored(true);

		Converter<Character, String> charToString =
			context -> context.getSource() == null ? null : String.valueOf(context.getSource().charValue());
		Converter<String, Character> stringToChar =
			context -> context.getSource() == null || context.getSource().isEmpty() ? 'N' : context.getSource().charAt(0);

		modelMapper.typeMap(Admission.class, AdmissionDTO.class).addMappings(mapper ->
			mapper.using(charToString).map(Admission::getDeleted, AdmissionDTO::setDeleted));

		modelMapper.typeMap(AdmissionDTO.class, Admission.class).addMappings(mapper ->
			mapper.using(stringToChar).map(AdmissionDTO::getDeleted, Admission::setDeleted));
	}
}
