package org.isf.lab.data;

import org.isf.lab.dto.LaboratoryRowDTO;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.test.TestLaboratoryRow;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LaboratoryRowHelper {
	public static LaboratoryRow setup() throws OHException {
		Laboratory lab = new Laboratory();
		boolean usingSet = true;
		return new TestLaboratoryRow().setup(lab, usingSet);
	}


	public static String asJsonString(LaboratoryRowDTO body) {
		// TODO Auto-generated method stub
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
