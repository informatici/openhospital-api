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
package org.isf.patient.mapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.isf.shared.GenericMapper;
import org.isf.shared.mapper.mappings.PatientMapping;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper extends GenericMapper<Patient, PatientDTO> {

	public PatientMapper() {
		super(Patient.class, PatientDTO.class);
	}
	@PostConstruct
	private void postConstruct() {
		PatientMapping.addMapping(modelMapper);
	}

	@Override
	public List<PatientDTO> map2DTOList(List<Patient> list) {
		return list.stream().map(it -> map2DTO(it)).collect(Collectors.toList());
	}

	@Override
	public List<Patient> map2ModelList(List<PatientDTO> list) {
		return list.stream().map(it -> map2Model(it)).collect(Collectors.toList());
	}
}