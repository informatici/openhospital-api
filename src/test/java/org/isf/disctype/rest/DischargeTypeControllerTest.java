package org.isf.disctype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
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

import org.isf.disctype.data.DischargeTypeHelper;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.mapper.DischargeTypeMapper;
import org.isf.disctype.model.DischargeType;
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

import com.fasterxml.jackson.databind.ObjectMapper;

public class DischargeTypeControllerTest {
	private final Logger logger = LoggerFactory.getLogger(DischargeTypeControllerTest.class);
	
	@Mock
	protected DischargeTypeBrowserManager discTypeManagerMock;
	
	protected DischargeTypeMapper dischargeTypeMapper = new DischargeTypeMapper();

	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DischargeTypeController(discTypeManagerMock, dischargeTypeMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(dischargeTypeMapper, "modelMapper", modelMapper);
    }


	@Test
	public void testNewDischargeType_201() throws Exception {
		String request = "/dischargetypes";
		String code = "ZZ";
		DischargeTypeDTO  body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));
		
		boolean isCreated = true;
		when(discTypeManagerMock.newDischargeType(dischargeTypeMapper.map2Model(body)))
			.thenReturn(isCreated);
		
		DischargeType dischargeType =  new DischargeType("ZZ","aDescription");
		ArrayList<DischargeType> dischTypeFounds = new ArrayList<DischargeType>();
		dischTypeFounds.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
			.thenReturn(dischTypeFounds);
		
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DischargeTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateDischargeTypet_200() throws Exception {
		String request = "/dischargetypes";
		String code = "ZZ";
		DischargeTypeDTO  body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));

		
		when(discTypeManagerMock.codeControl(body.getCode()))
			.thenReturn(true);
		
		DischargeType dischargeType =  new DischargeType("ZZ","aDescription");
		ArrayList<DischargeType> admtFounds = new ArrayList<DischargeType>();
		admtFounds.add(dischargeType);
		boolean isUpdated = true;
		when(discTypeManagerMock.updateDischargeType(dischargeTypeMapper.map2Model(body)))
			.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(DischargeTypeHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetDischargeTypes_200() throws Exception {
		String request = "/dischargetypes";
		
		DischargeType dischargeType =  new DischargeType("ZZ","aDescription");
		ArrayList<DischargeType> dischTypes = new ArrayList<DischargeType>();
		dischTypes.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
			.thenReturn(dischTypes);
		
		List<DischargeTypeDTO> expectedDischTypeDTOs = dischargeTypeMapper.map2DTOList(dischTypes);
		
		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(expectedDischTypeDTOs))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testDeleteDischargeType_200() throws Exception {
		String request = "/dischargetypes/{code}";
		String code = "ZZ";
		DischargeTypeDTO  body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));
		
		when(discTypeManagerMock.codeControl(body.getCode()))
		.thenReturn(true);
		
		DischargeType dischargeType =  new DischargeType("ZZ","aDescription");
		ArrayList<DischargeType> dischTypeFounds = new ArrayList<DischargeType>();
		dischTypeFounds.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
			.thenReturn(dischTypeFounds);
		
		when(discTypeManagerMock.deleteDischargeType(dischTypeFounds.get(0)))
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
