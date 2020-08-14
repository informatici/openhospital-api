package org.isf.distype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiseaseTypeHelper {

	public static DiseaseType setup(int i) throws OHException {
		TestDiseaseType testDiseaseType = new  TestDiseaseType();
		DiseaseType diseaseType = testDiseaseType.setup(false);
		diseaseType.setCode(diseaseType.getCode()+i);
		return diseaseType;
	}
	
	public static Disease setup(DiseaseType diseaseType) throws OHException {
		TestDisease td = new TestDisease();
		return td.setup(diseaseType, false);
	}
	
	public static ArrayList<DiseaseType> setupDiseaseTypeList(int size) {
		return (ArrayList<DiseaseType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return DiseaseTypeHelper.setup(i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}

	
	public static String asJsonString(DiseaseTypeDTO body) {
		try {
			return new ObjectMapper().writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
