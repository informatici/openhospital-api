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

import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.patient.rest.PatientController;
import org.isf.shared.GenericMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper extends GenericMapper<Patient, PatientDTO> {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatientMapper.class);

	public PatientMapper() {
		super(Patient.class, PatientDTO.class);
	}

	@Override
	public PatientDTO map2DTO(Patient fromObj) {
		PatientDTO patientDTO = super.map2DTO(fromObj);
		if (fromObj.getPatientProfilePhoto() != null) {
			patientDTO.setBlobPhoto(fromObj.getPatientProfilePhoto().getPhoto());
		}
		return patientDTO;

	}
	@Override
	public PatientDTO map2DTOWS(Patient fromObj, Boolean status) {
		PatientDTO patientDTO = super.map2DTOWS(fromObj, status);
		if (fromObj.getPatientProfilePhoto() != null) {
			try {
				patientDTO.setBlobPhoto(fromObj.getPatientProfilePhoto().getPhoto());
			} catch (Exception e) {
				LOGGER.error("Error while trying to retrieve the profile photo.");
			}
		}
		PatientConsensus pc = fromObj.getPatientConsensus();
		patientDTO.setConsensusAdministrativeFlag(pc.isAdministrativeFlag());
		patientDTO.setConsensusFlag(pc.isConsensusFlag());
		patientDTO.setConsensusServiceFlag(pc.isServiceFlag());
		return patientDTO;

	}

	@Override
	public Patient map2Model(PatientDTO toObj) {

		Patient patient = super.map2Model(toObj);
		if (toObj.getBlobPhoto() != null) {
			PatientProfilePhoto photo = new PatientProfilePhoto();
			photo.setPatient(patient);
			photo.setPhoto(toObj.getBlobPhoto());
			patient.setPatientProfilePhoto(photo);
		}
		patient.setPatientConsensus(
						new PatientConsensus(toObj.isConsensusFlag(), toObj.isConsensusAdministrativeFlag(), toObj.isConsensusServiceFlag(), patient));
		return patient;
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