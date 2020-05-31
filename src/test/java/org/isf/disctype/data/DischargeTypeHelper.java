package org.isf.disctype.data;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.test.TestDischargeType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DischargeTypeHelper {

	public static DischargeType setup(String code) throws OHException {
		TestDischargeType testDischargeType = new TestDischargeType();
		DischargeType dischargeType = testDischargeType.setup(false);
		dischargeType.setCode(code);
		return dischargeType;
	}

	public static String asJsonString(DischargeTypeDTO dischargeTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(dischargeTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
