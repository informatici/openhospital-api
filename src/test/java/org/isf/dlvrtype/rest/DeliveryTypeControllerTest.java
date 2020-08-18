package org.isf.dlvrtype.rest;

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

import org.isf.dlvrtype.data.DeliveryTypeHelper;
import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.mapper.DeliveryTypeMapper;
import org.isf.dlvrtype.model.DeliveryType;
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

public class DeliveryTypeControllerTest {
	
	private final Logger logger = LoggerFactory.getLogger(DeliveryTypeControllerTest.class);
	
	@Mock
	protected DeliveryTypeBrowserManager deliveryTypeBrowserManagerMock;
	
	protected DeliveryTypeMapper deliveryTypeMapper = new DeliveryTypeMapper();

	private MockMvc mockMvc;
	
	
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DeliveryTypeController(deliveryTypeBrowserManagerMock, deliveryTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(deliveryTypeMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testNewDeliveryType_201() throws Exception {
		String request = "/deliverytypes";
		int code = 123;
		DeliveryType deliveryType = DeliveryTypeHelper.setup(code);
		DeliveryTypeDTO  body = deliveryTypeMapper.map2DTO(deliveryType);
		
		
		ArrayList<DeliveryType> results = new ArrayList<DeliveryType>();
		results.add(deliveryType);
		
		when(deliveryTypeBrowserManagerMock.getDeliveryType())
				.thenReturn(results);
				
		boolean isCreated = true;
		when(deliveryTypeBrowserManagerMock.newDeliveryType(deliveryTypeMapper.map2Model(body)))
			.thenReturn(isCreated);
		
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DeliveryTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateDeliveryTypet_200() throws Exception {
		String request = "/deliverytypes/{code}";
		int code = 456;
		
		DeliveryType deliveryType = DeliveryTypeHelper.setup(code);
		DeliveryTypeDTO  body = deliveryTypeMapper.map2DTO(deliveryType);

		
		when(deliveryTypeBrowserManagerMock.codeControl(body.getCode()))
			.thenReturn(true);
		
		boolean isUpdated = true;
		when(deliveryTypeBrowserManagerMock.updateDeliveryType(deliveryTypeMapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request,"ZZ"+code)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DeliveryTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDeliveryTypes_200() throws JsonProcessingException, Exception {
		String request = "/deliverytypes";
		
		ArrayList<DeliveryType> results = DeliveryTypeHelper.setupDeliveryTypeList(3);
		
		List<DeliveryTypeDTO> dlvrrestTypeDTOs = deliveryTypeMapper.map2DTOList(results);
		
		when(deliveryTypeBrowserManagerMock.getDeliveryType())
			.thenReturn(results);
				
		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())	
				.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(dlvrrestTypeDTOs))))
				.andReturn();
			
			logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteDeliveryType_200() throws Exception {
	String request = "/deliverytypes/{code}";
		
		DeliveryTypeDTO  body = deliveryTypeMapper.map2DTO(DeliveryTypeHelper.setup(0));
		String code = body.getCode();
		
		when(deliveryTypeBrowserManagerMock.codeControl(code))
		.thenReturn(true);
		
		when(deliveryTypeBrowserManagerMock.getDeliveryType())
		.thenReturn(DeliveryTypeHelper.setupDeliveryTypeList(1));
			
		when(deliveryTypeBrowserManagerMock.deleteDeliveryType(deliveryTypeMapper.map2Model(body)))
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
