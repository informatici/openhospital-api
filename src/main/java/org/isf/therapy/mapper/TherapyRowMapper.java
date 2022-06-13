/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.therapy.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.medicals.model.Medical;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.shared.GenericMapper;
import org.isf.therapy.dto.TherapyRowDTO;
import org.isf.therapy.model.TherapyRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TherapyRowMapper extends GenericMapper<TherapyRow, TherapyRowDTO> {
	@Autowired
	private PatientMapper patientMapper = new PatientMapper();
	public TherapyRowMapper() {
		super(TherapyRow.class, TherapyRowDTO.class);
	}
	

	@Override
	public TherapyRow map2Model(TherapyRowDTO toObj) {
		TherapyRow therapyRow = super.map2Model(toObj);

		// map medical
		Medical medical = new Medical();
		medical.setCode(toObj.getMedicalId());
		therapyRow.setMedical(medical);
		Patient patient = patientMapper.map2Model(toObj.getPatID());
		therapyRow.setPatient(patient);
		therapyRow.setStartDate(toObj.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		therapyRow.setEndDate(toObj.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		return therapyRow;
	}

	@Override
	public TherapyRowDTO map2DTO(TherapyRow fromObj) {
		TherapyRowDTO therapyRowDTO = super.map2DTO(fromObj);

		// map medical
		therapyRowDTO.setMedicalId(fromObj.getMedical());
		PatientDTO patID = patientMapper.map2DTO(fromObj.getPatient());
		therapyRowDTO.setPatID(patID);
		Instant instant1 = fromObj.getStartDate().atZone(ZoneId.systemDefault()).toInstant();
		Date date1 = Date.from(instant1);
		therapyRowDTO.setStartDate(date1);
		
		Instant instant2 = fromObj.getEndDate().atZone(ZoneId.systemDefault()).toInstant();
		Date date2 = Date.from(instant2);
		therapyRowDTO.setEndDate(date2);
		return therapyRowDTO;
	}

	@Override
	public List<TherapyRowDTO> map2DTOList(List<TherapyRow> list) {
		return (List<TherapyRowDTO>) list.stream().map(it -> map2DTO(it)).collect(Collectors.toList());
	}

	@Override
	public List<TherapyRow> map2ModelList(List<TherapyRowDTO> list) {
		return (List<TherapyRow>) list.stream().map(it -> map2Model(it)).collect(Collectors.toList());
	}

}
