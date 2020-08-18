package org.isf.dlvrrestype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.test.TestDeliveryResultType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryResultTypeHelper {
	
	public static DeliveryResultType setup(int i) throws OHException {
		TestDeliveryResultType testDeliveryResultType = new  TestDeliveryResultType();
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(false);
		deliveryResultType.setCode(deliveryResultType.getCode()+i);
		return deliveryResultType;
	}

	public static ArrayList<DeliveryResultType> setupDeliveryResultTypeList(int size) {
		return (ArrayList<DeliveryResultType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return DeliveryResultTypeHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}


	public static String asJsonString(DeliveryResultTypeDTO body) {
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
