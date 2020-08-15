package org.isf.dlvrtype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.test.TestDeliveryType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryTypeHelper {
	public static DeliveryType setup(int i) throws OHException {
		TestDeliveryType testDeliveryType = new  TestDeliveryType();
		DeliveryType deliveryType = testDeliveryType.setup(false);
		deliveryType.setCode(deliveryType.getCode()+i);
		return deliveryType;
	}

	public static ArrayList<DeliveryType> setupDeliveryTypeList(int size) {
		return (ArrayList<DeliveryType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return DeliveryTypeHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

	public static String asJsonString(DeliveryTypeDTO body) {
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
