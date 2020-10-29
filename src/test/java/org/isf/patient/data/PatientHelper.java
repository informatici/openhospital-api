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
package org.isf.patient.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientHelper {

	public static PatientDTO setup(PatientMapper patientMapper) throws OHException {
		Patient patient = setup();
		return patientMapper.map2DTO(patient);
	}

	public static String asJsonString(PatientDTO patientDTO) {
		try {
			return new ObjectMapper().writeValueAsString(patientDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Patient setup() throws OHException {
		return new TestPatient().setup(true);
	}

	public static ArrayList<Patient> setupPatientList(int size) {
		return (ArrayList<Patient>) IntStream.range(1, size + 1)
				.mapToObj(i -> {
							Patient ep = null;
							try {
								ep = PatientHelper.setup();
								ep.setCode(i);
							} catch (OHException e) {
								e.printStackTrace();
							}
							return ep;
						}
				).collect(Collectors.toList());
	}

	public static ArrayList<AdmittedPatient> setupAdmittedPatientList(int size) {
		ArrayList<Patient> patientList = setupPatientList(size);
		ArrayList<Admission> admissionList = AdmissionHelper.setupAdmissionList(size);
		return (ArrayList<AdmittedPatient>) IntStream.range(0, size)

				.mapToObj(i -> new AdmittedPatient(patientList.get(i), admissionList.get(i))

				).collect(Collectors.toList());
	}

	public static String asJsonString(List<?> list) {
		try {
			return new ObjectMapper().writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}