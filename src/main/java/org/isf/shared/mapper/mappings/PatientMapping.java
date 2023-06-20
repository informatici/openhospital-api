/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.modelmapper.ModelMapper;

public class PatientMapping {

	public static void addMapping(ModelMapper modelMapper) {

		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.typeMap(Patient.class, PatientDTO.class).addMappings(mapper -> {
			mapper.<Boolean> map(src -> src.getPatientConsensus().isConsensusFlag(), PatientDTO::setConsensusFlag);
			mapper.<Boolean> map(src -> src.getPatientConsensus().isServiceFlag(), PatientDTO::setConsensusServiceFlag);
			mapper.map(src -> src.getPatientProfilePhoto().getPhoto(), PatientDTO::setBlobPhoto);
		});

		modelMapper.typeMap(PatientDTO.class, Patient.class).addMappings(mapper -> {
			mapper.<Boolean> map(src -> src.isConsensusFlag(), (db, value) -> db.getPatientConsensus().setConsensusFlag(value));
			mapper.<Boolean> map(src -> src.isConsensusServiceFlag(), (db, value) -> db.getPatientConsensus().setServiceFlag(value));
			mapper.<Patient> map(src -> src, (db, value) -> {
				db.getPatientConsensus().setPatient(value);
			});
			mapper.map(src -> src.getBlobPhoto(), (destination, value) -> {
				destination.getPatientProfilePhoto().setPhoto((byte[]) value);
			});
		});

	}
}
