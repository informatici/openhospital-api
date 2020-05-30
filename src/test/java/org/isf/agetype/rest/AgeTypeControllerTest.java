package org.isf.agetype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.rest.AdmissionTypeController;
import org.isf.admtype.rest.AdmissionTypeControllerTest;
import org.isf.agetype.data.AgeTypeHelper;
import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.mapper.AgeTypeMapper;
import org.isf.agetype.model.AgeType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgeTypeControllerTest {
	
private final Logger logger = LoggerFactory.getLogger(AdmissionTypeControllerTest.class);
	
	@Mock
	private AgeTypeBrowserManager ageTypeManagerMock;
	
	private AgeTypeMapper ageTypeMapper = new  AgeTypeMapper();

	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AgeTypeController(ageTypeManagerMock, ageTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(ageTypeMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetAllAgeTypes_200() throws JsonProcessingException, Exception {
		String request = "/agetypes";
		
		ArrayList<AgeType> results = AgeTypeHelper.genArrayList(5);
		List<AgeTypeDTO> parsedResults = ageTypeMapper.map2DTOList(results);
		
		when(ageTypeManagerMock.getAgeType())
			.thenReturn(results);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateAgeType_200() throws Exception {
		String request = "/agetypes";
		AgeTypeDTO body = ageTypeMapper.map2DTO(AgeTypeHelper.setup());
		
		ArrayList<AgeType> ageTypes = new ArrayList<AgeType>();
		ageTypes.add(AgeTypeHelper.setup());
				
		
		when(ageTypeManagerMock.updateAgeType(ageTypes))
			.thenReturn(true);
		
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(AgeTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetAgeTypeCodeByAge_200() throws Exception {
				
		String request = "/agetypes/code?age={age}";
		int age = 10;
		String responseString = "resultString";
	
		when(ageTypeManagerMock.getTypeByAge(age))
			.thenReturn(responseString);
		
		MvcResult result = this.mockMvc
			.perform(get(request,age))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(responseString)))
			.andExpect(content().string(containsString("code")))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetAgeTypeByIndex_200() throws JsonProcessingException, Exception {
		String request = "/agetypes/{index}";
		int index = 10;
		AgeType ageType = AgeTypeHelper.setup(index); 
	
		when(ageTypeManagerMock.getTypeByCode(index))
			.thenReturn(ageType);
		
		MvcResult result = this.mockMvc
			.perform(get(request,index))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(ageType))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

}
