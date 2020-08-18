package org.isf.dlvrrestype.rest;

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

import org.isf.dlvrrestype.data.DeliveryResultTypeHelper;
import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.mapper.DeliveryResultTypeMapper;
import org.isf.dlvrrestype.model.DeliveryResultType;
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

public class DeliveryResultTypeControllerTest {
	
private final Logger logger = LoggerFactory.getLogger(DeliveryResultTypeControllerTest.class);
	
	@Mock
	protected DeliveryResultTypeBrowserManager deliveryResultTypeBrowserManagerMock;
	
	protected DeliveryResultTypeMapper deliveryResultTypeMapper = new DeliveryResultTypeMapper();

	private MockMvc mockMvc;
	
	
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DeliveryResultTypeController(deliveryResultTypeBrowserManagerMock, deliveryResultTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(deliveryResultTypeMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testNewDeliveryResultType_201() throws Exception {
		String request = "/deliveryresulttypes";
		int code = 123;
		DeliveryResultType deliveryResultType = DeliveryResultTypeHelper.setup(code);
		DeliveryResultTypeDTO  body = deliveryResultTypeMapper.map2DTO(deliveryResultType);
		
		
		ArrayList<DeliveryResultType> results = new ArrayList<DeliveryResultType>();
		results.add(deliveryResultType);
		
		when(deliveryResultTypeBrowserManagerMock.getDeliveryResultType())
				.thenReturn(results);
				
		boolean isCreated = true;
		when(deliveryResultTypeBrowserManagerMock.newDeliveryResultType(deliveryResultTypeMapper.map2Model(body)))
			.thenReturn(isCreated);
		
	
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DeliveryResultTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateDeliveryResultTypet_200() throws Exception {
		String request = "/deliveryresulttypes";
		int code = 456;
		
		DeliveryResultType deliveryResultType = DeliveryResultTypeHelper.setup(code);
		DeliveryResultTypeDTO  body = deliveryResultTypeMapper.map2DTO(deliveryResultType);

		
		when(deliveryResultTypeBrowserManagerMock.codeControl(body.getCode()))
			.thenReturn(true);
		
		boolean isUpdated = true;
		when(deliveryResultTypeBrowserManagerMock.updateDeliveryResultType(deliveryResultTypeMapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DeliveryResultTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDeliveryResultTypes_200() throws JsonProcessingException, Exception {
		String request = "/deliveryresulttypes";
		
		ArrayList<DeliveryResultType> results = DeliveryResultTypeHelper.setupDeliveryResultTypeList(3);
		
		List<DeliveryResultTypeDTO> dlvrrestTypeDTOs = deliveryResultTypeMapper.map2DTOList(results);
		
		when(deliveryResultTypeBrowserManagerMock.getDeliveryResultType())
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
	public void testDeleteDeliveryResultType() throws Exception {
		String request = "/deliveryresulttypes/{code}";
		
		DeliveryResultTypeDTO  body = deliveryResultTypeMapper.map2DTO(DeliveryResultTypeHelper.setup(0));
		String code = body.getCode();
		
		when(deliveryResultTypeBrowserManagerMock.codeControl(code))
		.thenReturn(true);
		
		when(deliveryResultTypeBrowserManagerMock.getDeliveryResultType())
		.thenReturn(DeliveryResultTypeHelper.setupDeliveryResultTypeList(1));
			
		when(deliveryResultTypeBrowserManagerMock.deleteDeliveryResultType(deliveryResultTypeMapper.map2Model(body)))
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
