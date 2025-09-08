package org.isf.encounter.rest;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.conditioning.data.ConditioningHelper;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.conditioning.rest.ConditioningController;
import org.isf.conditioning.rest.ConditioningControllerTest;
import org.isf.encounter.data.EncounterHelper;
import org.isf.encounter.dto.EncounterDTO;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.mapper.EncounterMapper;
import org.isf.encounter.model.Encounter;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.shared.mapper.mappings.PatientMapping;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EncounterControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterControllerTest.class);

	@Mock
	protected EncounterBrowserManager encounterBrowserManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	@Mock
	protected ExaminationBrowserManager examinationBrowserManagerMock;

	@Mock
	protected OpdBrowserManager opdManagerMock;

	@Mock
	protected AdmissionBrowserManager admissionBrowserManagerMock;

	protected OpdMapper opdMapper = new OpdMapper();
	protected AdmissionMapper admissionMapper = new AdmissionMapper();
	protected PatientExaminationMapper examinationMapper = new PatientExaminationMapper();
	private final EncounterMapper encounterMapper = new EncounterMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new EncounterController(encounterBrowserManagerMock, encounterMapper, patientBrowserManagerMock, opdManagerMock, opdMapper, examinationBrowserManagerMock, examinationMapper, admissionBrowserManagerMock, admissionMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		PatientMapping.addMapping(modelMapper);
		ReflectionTestUtils.setField(encounterMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewEncounter_success() throws Exception {
		String request = "/encounters";

		EncounterDTO body = EncounterHelper.setup(encounterMapper);
		Encounter encounter = encounterMapper.map2Model(body);

		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode()))
			.thenReturn(encounter.getPatient());

		when(encounterBrowserManagerMock.saveEncounter(any(Encounter.class)))
			.thenReturn(encounter);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}


	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
}
