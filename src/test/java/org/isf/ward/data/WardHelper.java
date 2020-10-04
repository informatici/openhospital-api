package org.isf.ward.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.utils.exception.OHException;
import org.isf.ward.dto.WardDTO;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WardHelper {
	
	public static Ward setup() throws OHException {
		TestWard testWard = new TestWard();
		Ward ward = testWard.setup(false);
		return ward;
	}
	
	public static Ward setup(int id) throws OHException {
		TestWard testWard = new TestWard();
		Ward ward = testWard.setup(false);
		//ward.setCode(ward.getCode()+id);
		return ward;
	}

	public static ArrayList<Ward> setupWardList(int size) {
		return (ArrayList<Ward>) IntStream.range(0, size)
					.mapToObj(i -> {	try {
											return WardHelper.setup(i);
										} catch (OHException e) {
											e.printStackTrace();
										}
										return null;
									}).collect(Collectors.toList());
			
	}
	
	public static String asJsonString(WardDTO wardDTO) {
		try {
			return new ObjectMapper().writeValueAsString(wardDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
