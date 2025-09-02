package org.isf.medicalhistory.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.medicalhistory.TestMedicalHistory;
import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class MedicalHistoryHelper {
	private static ObjectMapper objectMapper;
	private static PatientIoOperationRepository patientRepository;

	public static MedicalHistory setup() throws OHException {
		TestMedicalHistory testMedicalHistory = new TestMedicalHistory();
		Patient patient = new TestPatient().setup(false);
		return testMedicalHistory.createMedicalHistory(patient) ;
	}

	public static List<MedicalHistory> setupMedicalHistories(int size) {
		return IntStream.range(1, size + 1)
			.mapToObj(i -> {
					MedicalHistory mh = null;
					try {
						mh = MedicalHistoryHelper.setup();
					} catch (OHException e) {
						e.printStackTrace();
					}
					return mh;
				}
			).collect(Collectors.toList());
	}

	public static String asJsonString(MedicalHistoryDTO medicalHistoryDTO) {
		try {
			return getObjectMapper().writeValueAsString(medicalHistoryDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<?> list) {
		try {
			return getObjectMapper().writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MedicalHistoryDTO setup(MedicalHistoryMapper medicalHistoryMapper) throws OHException {
		return medicalHistoryMapper.map2DTO(MedicalHistoryHelper.setup());
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
