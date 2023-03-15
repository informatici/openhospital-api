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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.patconsensus.mapper;

import org.isf.patconsensus.dto.PatientConsensusDTO;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patient.model.Patient;
import org.isf.shared.GenericMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class PatientConsensusMapper extends GenericMapper<PatientConsensus, PatientConsensusDTO> {

	public PatientConsensusMapper() {
		super(PatientConsensus.class, PatientConsensusDTO.class);
	}

	@Override
	public PatientConsensus map2Model(PatientConsensusDTO fromObj) {
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		PatientConsensus patientConsensus = super.map2Model(fromObj);
		if (fromObj.getPatientId() != null) {
			Patient patient = new Patient();
			patient.setCode(fromObj.getPatientId());
			patientConsensus.setPatient(patient);
		}
		return patientConsensus;
	}


	@Override
	public PatientConsensusDTO map2DTO(PatientConsensus fromObj) {
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		PatientConsensusDTO patientConsensus = super.map2DTO(fromObj);
		if (fromObj.getPatient() != null) {
			patientConsensus.setPatientId(fromObj.getPatient().getCode());
		}
		return patientConsensus;
	}

}
