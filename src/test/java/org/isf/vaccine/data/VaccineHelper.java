package org.isf.vaccine.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.utils.exception.OHException;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.test.TestVaccine;
import org.isf.vactype.test.TestVaccineType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VaccineHelper {
	
	public static Vaccine setup(String code) throws OHException {
		TestVaccine testVaccine = new TestVaccine();
		TestVaccineType testVaccineType = new TestVaccineType();
		Vaccine vaccine = testVaccine.setup(testVaccineType.setup(false),false);
		vaccine.setCode(code);
		return vaccine;
	}

	public static String asJsonString(VaccineDTO vaccineDTO) {
		try {
			return new ObjectMapper().writeValueAsString(vaccineDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Vaccine> setupVaccineList(int size) {
		return (ArrayList<Vaccine>) IntStream.range(0, size)
				.mapToObj(i -> {	try {
										return VaccineHelper.setup(""+i);
									} catch (OHException e) {
										e.printStackTrace();
									}
									return null;
								}).collect(Collectors.toList());
	}
	
}
