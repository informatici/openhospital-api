package org.isf.disctype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.test.TestDischargeType;
import org.isf.operation.data.OperationHelper;
import org.isf.operation.model.Operation;
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
	
	public static ArrayList<DischargeType> setupDischargeTypeList(int size) {
		return (ArrayList<DischargeType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return DischargeTypeHelper.setup(""+i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

}
