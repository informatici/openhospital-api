package org.isf.opd.data;

import org.isf.disease.model.Disease;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.model.Opd;
import org.isf.opd.test.TestOpd;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class OpdHelper {

	private static ObjectMapper objectMapper;

	public static Opd setup() throws OHException {
		Patient patient = new Patient() ;
		Disease disease = new Disease();
		Ward ward = new Ward();
		Visit visit = new Visit();
		boolean usingSet = true;
		return new TestOpd().setup(patient, disease, ward, visit, usingSet);
	}


	public static String asJsonString(OpdDTO body) {
		// TODO Auto-generated method stub
		try {
			return getObjectMapper().writeValueAsString(body);
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

