package org.isf.vactype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.utils.exception.OHException;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.test.TestVaccineType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VaccineTypeHelper {
	
	public static VaccineType setup(String code) throws OHException {
		TestVaccineType testVaccineType = new TestVaccineType();
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineType.setCode(code);
		return vaccineType;
	}

	public static String asJsonString(VaccineTypeDTO vaccineTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(vaccineTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<VaccineType> setupVaccineList(int size) {
		return (ArrayList<VaccineType>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return VaccineTypeHelper.setup(""+i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}
	
}
