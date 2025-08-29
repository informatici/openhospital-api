package org.isf.medicalhistory.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.data.PatientHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class MedicalHistoryHelper {
	private static ObjectMapper objectMapper;

	public static MedicalHistory setup() {
		MedicalHistory mh = new MedicalHistory();
		try {
			mh.setPatient(PatientHelper.setup());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mh.setSiblingRank(1);
		mh.setTermPregnancy("Full-term");
		mh.setDeliveryMode("Normal");
		mh.setApgarScore("8/10");
		mh.setBirthWeight(3.2);
		mh.setVaccinationState("Up to date");
		mh.setAntiMalarialProphylaxis("None");
		mh.setDiet("Normal");
		mh.setDeParasitization("Yes");
		mh.setPsychomotorDev("Normal");
		mh.setSomaticGrowth("Normal");
		mh.setIronSupplement(true);
		mh.setFolicAcidSupplement(true);
		mh.setVitASupplement(false);
		mh.setOtherSupplements("None");
		mh.setTransfusion(false);
		mh.setLastTransfusionDate(LocalDateTime.now());
		mh.setSickleCell(false);
		mh.setDrugAllergy(false);
		mh.setAllergyPrecision("");
		mh.setHemylosis("None");
		mh.setOtherPersonalPathologies("");
		mh.setOtherFamilyPathologies("");
		return mh;
	}

	public static MedicalHistory setup(Integer id) {
		MedicalHistory mh = setup();
		mh.setId(id);
		return mh;
	}

	public static List<MedicalHistory> genList(int n) {
		return IntStream.range(0, n)
			.mapToObj(MedicalHistoryHelper::setup)
			.collect(Collectors.toList());
	}

	public static String asJsonString(MedicalHistoryDTO dto) {
		try {
			return getObjectMapper().writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
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
