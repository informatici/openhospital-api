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
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.mapper.WardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class OpdControllerTest {
	
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpdControllerTest.class);

	@Mock
	protected OpdBrowserManager opdBrowserManagerMock;
	
	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	protected OpdMapper opdMapper = new OpdMapper();

	private MockMvc mockMvc;
	
	protected WardMapper mapperWard;
	
	
	protected WardBrowserManager wardManager;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new OpdController(opdBrowserManagerMock, opdMapper, patientBrowserManagerMock, mapperWard, wardManager))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(opdMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewOpd_201() throws Exception {
		String request = "/opds";
		Patient patient = PatientHelper.setup();
		Integer patientCode = 1;
		patient.setCode(patientCode);
		
		Opd opd = OpdHelper.setup();
		opd.setPatient(patient);
		
		OpdDTO body = opdMapper.map2DTO(opd);
		
		when(patientBrowserManagerMock.getPatientById(patientCode)).thenReturn(patient);

		when(opdBrowserManagerMock.newOpd(opdMapper.map2Model(body))).thenReturn(opd);
		
		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(OpdHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}

