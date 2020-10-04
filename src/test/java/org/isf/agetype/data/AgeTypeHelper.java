package org.isf.agetype.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.model.AgeType;
import org.isf.agetype.test.TestAgeType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgeTypeHelper {

	public static AgeType setup() throws OHException {
		TestAgeType testAgeType =  new TestAgeType();
		AgeType ageType = testAgeType.setup(false); 
		return ageType;
	}
	
	public static AgeType setup(Integer id) throws OHException {
		AgeType ageType = AgeTypeHelper.setup();
		ageType.setCode(ageType.getCode()+id);
		return ageType;
	}



	public static List<AgeType> genList(int n) {
		return IntStream.range(0, n)
				.mapToObj(i -> {	try {
										return AgeTypeHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

	public static ArrayList<AgeType>  genArrayList(int n){
		return new ArrayList<AgeType>(AgeTypeHelper.genList(n));
	}

	public static String asJsonString(AgeTypeDTO ageTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(ageTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
