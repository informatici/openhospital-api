package org.isf.distype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.distype.data.DiseaseTypeHelper;
import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.mapper.DiseaseTypeMapper;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiseaseTypeControllerTest {
	private final Logger logger = LoggerFactory.getLogger(DiseaseTypeControllerTest.class);
	
	@Mock
	protected DiseaseTypeBrowserManager diseaseTypeBrowserManager;
	
	protected DiseaseTypeMapper diseaseTypeMapper = new DiseaseTypeMapper();

	private MockMvc mockMvc;
	
	
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DiseaseTypeController(diseaseTypeBrowserManager, diseaseTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseTypeMapper, "modelMapper", modelMapper);
    }
	

	@Test
	public void testGetAllDiseaseTypes_200() throws JsonProcessingException, Exception {
		String request = "/diseasetypes";
		
		ArrayList<DiseaseType> results = DiseaseTypeHelper.setupDiseaseTypeList(3);
		
		List<DiseaseTypeDTO> parsedResults=diseaseTypeMapper.map2DTOList(results);
		
		when(diseaseTypeBrowserManager.getDiseaseType())
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
	public void testNewDiseaseType_201() throws Exception {
		String request = "/diseasetypes";
		int code = 123;
		DiseaseTypeDTO  body = diseaseTypeMapper.map2DTO(DiseaseTypeHelper.setup(code));
		
		
		when(diseaseTypeBrowserManager.codeControl(body.getCode()))
				.thenReturn(false);
				
		boolean isCreated = true;
		when(diseaseTypeBrowserManager.newDiseaseType(diseaseTypeMapper.map2Model(body)))
			.thenReturn(isCreated);
		
	
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DiseaseTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateDiseaseType_200() throws Exception {
		String request = "/diseasetypes";
		int code = 456;
		
		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO  body = diseaseTypeMapper.map2DTO(diseaseType);

		
		when(diseaseTypeBrowserManager.codeControl(body.getCode()))
			.thenReturn(true);
		
		boolean isUpdated = true;
		when(diseaseTypeBrowserManager.updateDiseaseType(diseaseTypeMapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DiseaseTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteDiseaseType_200() throws Exception {
		String request = "/diseasetypes/{code}";
		
		DiseaseTypeDTO  body = diseaseTypeMapper.map2DTO(DiseaseTypeHelper.setup(0));
		String code = body.getCode();
		
		when(diseaseTypeBrowserManager.getDiseaseType())
		.thenReturn(DiseaseTypeHelper.setupDiseaseTypeList(1));
		
	
		when(diseaseTypeBrowserManager.deleteDiseaseType(diseaseTypeMapper.map2Model(body)))
		.thenReturn(true);
		
		String isDeleted = "true";
		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(isDeleted)))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

}
