package org.isf.admtype.data;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.utils.exception.OHException;

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

	public static String asJsonString(AdmissionTypeDTO admissionTypeDTO) {
		try {
			return new ObjectMapper().writeValueAsString(admissionTypeDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
