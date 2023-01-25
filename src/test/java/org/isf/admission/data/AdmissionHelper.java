/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.admission.model.Admission;
import org.isf.admission.test.TestAdmission;
import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.data.DischargeTypeHelper;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.operation.model.Operation;
import org.isf.operation.test.TestOperation;
import org.isf.opetype.model.OperationType;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class AdmissionHelper {

	private static ObjectMapper objectMapper;

	public static Admission setup() throws OHException {
		TestAdmission testAdmission = new TestAdmission();
		TestWard testWard = new TestWard();
		Ward ward = testWard.setup(false);
		Patient patient = new TestPatient().setup(true);
		AdmissionType admissionType = AdmissionTypeDTOHelper.setup();

		TestDisease testDisease = new TestDisease();
		DiseaseType diseaseType = (new TestDiseaseType()).setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);

		TestOperation testOperation = new TestOperation();
		OperationType operationType = null;
		Operation operation = testOperation.setup(operationType, false);

		DischargeType dischargeType = DischargeTypeHelper.setup("0");

		PregnantTreatmentType pregTreatmentType = null;
		DeliveryType deliveryType = null;
		DeliveryResultType deliveryResult = null;

		return testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, false);
	}

	public static List<Admission> setupAdmissionList(int size) {
		return IntStream.range(1, size + 1)
				.mapToObj(i -> {
							Admission ep = null;
							try {
								ep = AdmissionHelper.setup();
							} catch (OHException e) {
								e.printStackTrace();
							}
							return ep;
						}
				).collect(Collectors.toList());
	}

	public static String asJsonString(AdmissionDTO admissionDTO) {
		try {
			return getObjectMapper().writeValueAsString(admissionDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AdmissionDTO setup(AdmissionMapper admissionMapper) throws OHException {
		return admissionMapper.map2DTO(AdmissionHelper.setup());
	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper()
					.registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module())
					.registerModule(new JavaTimeModule());
		}
		return objectMapper;
	}

}
