/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patvac.mapper;

import jakarta.annotation.PostConstruct;

import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.patvac.model.PatientVaccine;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PatVacMapper extends GenericMapper<PatientVaccine, PatientVaccineDTO> {
	public PatVacMapper() {
		super(PatientVaccine.class, PatientVaccineDTO.class);
	}

	@PostConstruct
	private void postConstruct() {
		// pin the mapping of the "vaccine" property: implicit matching resolves it to the
		// LocalDateTime "vaccineDate" property and fails at map time
		modelMapper.typeMap(PatientVaccine.class, PatientVaccineDTO.class)
			.addMappings(mapper -> mapper.map(PatientVaccine::getVaccine, PatientVaccineDTO::setVaccine));
	}
}
