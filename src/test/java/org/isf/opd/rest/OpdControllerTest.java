package org.isf.opd.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.dlvrtype.rest.DeliveryTypeControllerTest;
import org.isf.opd.data.OpdHelper;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class OpdControllerTest {
	
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DeliveryTypeControllerTest.class);

	@Mock
	protected OpdBrowserManager opdBrowserManagerMock;

	protected OpdMapper opdMapper = new OpdMapper();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new OpdController(opdBrowserManagerMock, opdMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(opdMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewOperation_201() throws Exception {
		String request = "/opds";
		
		Opd opd = OpdHelper.setup();
		OpdDTO body = opdMapper.map2DTO(opd);

		boolean isCreated = true;
		when(opdBrowserManagerMock.newOpd(opdMapper.map2Model(body)))
				.thenReturn(isCreated);
		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}

