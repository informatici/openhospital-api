package org.isf.conditioning.rest;

import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ConditioningControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditioningControllerTest.class);

	@Mock
	protected ConditioningBrowserManager conBrowserManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	@Mock
	protected UserBrowsingManager userBrowsingManagerMock;

	protected ConditioningMapper conditioningMapper = new ConditioningMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
						.standaloneSetup(new ConditioningController(conBrowserManagerMock, conditioningMapper, userBrowsingManagerMock,
										patientBrowserManagerMock))
						.setControllerAdvice(new OHResponseEntityExceptionHandler())
						.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(conditioningMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewConditioning_success() throws Exception {
	}

	@Test
	void testNewConditioning_patientNotFound() throws Exception {
	}

	@Test
	void testGetConditioningByPatientCode_success() throws Exception {
	}

	@Test
	void testGetConditioningByPatientCode_notFound() throws Exception {
	}

	@Test
	void testUpdateConditioning_success() throws Exception {
	}

	@Test
	void testUpdateConditioning_notFound() throws Exception {
	}
}
