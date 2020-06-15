package org.isf.admtype.data;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.utils.exception.OHException;
import org.isf.ward.data.WardHelper;
import org.isf.ward.model.Ward;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdmissionTypeDTOHelper {

	public static AdmissionTypeDTO setup(AdmissionTypeMapper admissionTypeMapper) throws OHException {
		TestAdmissionType testAdmissionTypeHelper =  new TestAdmissionType();
		return admissionTypeMapper.map2DTO(testAdmissionTypeHelper.setup(false));
	}
	
	public static AdmissionType setup() throws OHException {
		TestAdmissionType testAdmissionType =  new TestAdmissionType();
		return testAdmissionType.setup(false);
	}
	
	public static ArrayList<AdmissionType> setupAdmissionTypeList(int size) {
		return (ArrayList<AdmissionType>) IntStream.range(0, size)
					.mapToObj(i -> {	try {
											return AdmissionTypeDTOHelper.setup();
										} catch (OHException e) {
											e.printStackTrace();
										}
										return null;
									}).collect(Collectors.toList());
			
	}

	public static String asJsonString(AdmissionTypeDTO admissionTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(admissionTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
