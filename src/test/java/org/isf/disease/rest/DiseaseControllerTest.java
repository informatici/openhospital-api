package org.isf.disease.rest;

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

import org.isf.disease.data.DiseaseHelper;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
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

public class DiseaseControllerTest {
	private final Logger logger = LoggerFactory.getLogger(DiseaseControllerTest.class);
	
	@Mock
	private DiseaseBrowserManager diseaseBrowserManagerMock;
	
	private DiseaseMapper diseaseMapper= new  DiseaseMapper();

	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DiseaseController(diseaseBrowserManagerMock, diseaseMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetDiseasesOpd_200() throws Exception {
		String request = "/diseases/opd";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd())
			.thenReturn(diseases);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesOpdByCode_200() throws JsonProcessingException, Exception {
		String request = "/diseases/opd/{typecode}";
		
		String typeCode = "1";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd(typeCode))
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdOut_200() throws JsonProcessingException, Exception {
		String request = "/diseases/ipd/out";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut())
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testGetDiseasesIpdOutByCode_200() throws JsonProcessingException, Exception {
		String request = "/diseases/ipd/out/{typecode}";
		
		String typeCode = "1";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testGetDiseasesIpdIn_200() throws JsonProcessingException, Exception {
		
		String request = "/diseases/ipd/in";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdIn())
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdInByCode_200() throws JsonProcessingException, Exception {
		String request = "/diseases/ipd/out/{typecode}";
		
		String typeCode = "1";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDiseases_200() throws JsonProcessingException, Exception {
		String request = "/diseases/both";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease())
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testGetDiseasesString_200() throws JsonProcessingException, Exception {
		String request = "/diseases/both/{typecode}";
		
		String typeCode = "1";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease(typeCode))
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
		
		
	}

	@Test
	public void testGetAllDiseases_200() throws JsonProcessingException, Exception {
		String request = "/diseases/all";
		
		ArrayList<Disease> diseases =  DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseAll())
			.thenReturn(diseases);
			
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testGetDiseaseByCode() throws JsonProcessingException, Exception {	
		String request = "/diseases/{code}";
		
		int code = 1;
		
		Disease disease =  DiseaseHelper.setup();
		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(disease);
			
		MvcResult result = this.mockMvc
			.perform(get(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(diseaseMapper.map2DTO(disease)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testNewDisease_200() throws JsonProcessingException, Exception {
		String request = "/diseases";
		
		Disease disease =  DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);
		
		when(diseaseBrowserManagerMock.codeControl(disease.getCode()))
		.thenReturn(false);
	
		when(diseaseBrowserManagerMock.descriptionControl(disease.getDescription(), disease.getType().getCode()))
		.thenReturn(false);
		
		when(diseaseBrowserManagerMock.newDisease(disease))
			.thenReturn(true);
			
		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
						)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())	
				.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testUpdateDisease_201() throws Exception {
		String request = "/diseases";
		
		Disease disease =  DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);
		
		when(diseaseBrowserManagerMock.codeControl(disease.getCode()))
		.thenReturn(true);

		when(diseaseBrowserManagerMock.updateDisease(disease))
			.thenReturn(true);
			
		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
						)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())	
				.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteDisease() throws Exception {
		String request = "/diseases/{code}";
		
		int code  = 1;
		
		Disease disease =  DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);
		
		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
		.thenReturn(disease);

		when(diseaseBrowserManagerMock.deleteDisease(disease))
			.thenReturn(true);
			
		MvcResult result = this.mockMvc
				.perform(delete(request, code)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
						)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())	
				.andExpect(content().string(containsString("true")))

				.andReturn();
		
		logger.debug("result: {}", result);
	}
	
}
