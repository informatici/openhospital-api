package org.isf.admtype.rest;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
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

public class AdmissionTypeControllerTest {
	private final Logger logger = LoggerFactory.getLogger(AdmissionTypeControllerTest.class);
	
	@Mock
	protected AdmissionTypeBrowserManager admtManagerMock;
	
	protected AdmissionTypeMapper admissionTypemapper = new  AdmissionTypeMapper();

	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AdmissionTypeController(admtManagerMock, admissionTypemapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(admissionTypemapper, "modelMapper", modelMapper);
    }

	@Test
	public void testNewAdmissionType_201() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		
		boolean isCreated = true;
		when(admtManagerMock.newAdmissionType(admissionTypemapper.map2Model(body)))
			.thenReturn(isCreated);
		
		AdmissionType admissionType =  new AdmissionType("ZZ","aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<AdmissionType>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
			.thenReturn(admtFounds);
		
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(AdmissionTypeDTOHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateAdmissionTypet_200() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		
		when(admtManagerMock.codeControl(body.getCode()))
			.thenReturn(true);
		
		AdmissionType admissionType =  new AdmissionType("ZZ","aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<AdmissionType>();
		admtFounds.add(admissionType);
		boolean isUpdated = true;
		when(admtManagerMock.updateAdmissionType(admissionTypemapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(AdmissionTypeDTOHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetAdmissionTypes_200() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		
		AdmissionType admissionType =  new AdmissionType("ZZ","aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<AdmissionType>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
			.thenReturn(admtFounds);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteAdmissionType_200() throws Exception {
		String request = "/admissiontypes/{code}";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		String code = body.getCode();


		when(admtManagerMock.codeControl(code))
		.thenReturn(true);
		
		AdmissionType admissionType =  new AdmissionType("ZZ","aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<AdmissionType>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
			.thenReturn(admtFounds);
		
		when(admtManagerMock.deleteAdmissionType(admtFounds.get(0)))
		.thenReturn(true);
		
		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

}
