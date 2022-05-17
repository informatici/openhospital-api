package org.isf.opd.data;

import org.isf.disease.model.Disease;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.model.Opd;
import org.isf.opd.test.TestOpd;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpdHelper {


	public static Opd setup() throws OHException {
		Patient patient = new Patient() ;
		Disease disease = new Disease();
		boolean usingSet = true;
		return new TestOpd().setup( patient, disease, usingSet);
	}


	public static String asJsonString(OpdDTO body) {
		// TODO Auto-generated method stub
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}

