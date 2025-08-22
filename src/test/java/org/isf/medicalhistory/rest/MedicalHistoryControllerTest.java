package org.isf.medicalhistory.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.isf.medicalhistory.data.MedicalHistoryHelper;
import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.utils.exception.OHException;
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

public class MedicalHistoryControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalHistoryControllerTest.class);

	@Mock
	private MedicalHistoryBrowsingManager mhManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	private final MedicalHistoryMapper mhMapper = new MedicalHistoryMapper();
	private MockMvc mockMvc;
	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new MedicalHistoryController(mhManagerMock, mhMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(mhMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetOne_200() throws Exception {
		String request = "/medicalhistories/{id}";
		Integer id = 1;

		MedicalHistory mh = MedicalHistoryHelper.setup();
		mh.setId(id);

		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);

		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(mh);

		MvcResult result = this.mockMvc
			.perform(get(request, id))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().json(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(dto))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetAllMedicalHistories_200() throws Exception {
		String request = "/medicalhistories";

		List<MedicalHistory> results = MedicalHistoryHelper.genList(5);
		List<MedicalHistoryDTO> parsedResults = mhMapper.map2DTOList(results);

		when(mhManagerMock.getAll()).thenReturn(results);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(MedicalHistoryHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetByPatientCode_200() throws Exception {
		String request = "/medicalhistories/patient/{patientCode}";
		Integer patientCode = 1;

		List<MedicalHistory> histories = MedicalHistoryHelper.genList(3);
		histories.forEach(h -> {
			try {
				h.setPatient(PatientHelper.setup());
			} catch (OHException e) {
				throw new RuntimeException(e);
			}
		});
		List<MedicalHistoryDTO> dtos = mhMapper.map2DTOList(histories);

		when(mhManagerMock.getMedicalHistoriesByPatientCode(patientCode)).thenReturn(histories);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(MedicalHistoryHelper.getObjectMapper().writeValueAsString(dtos))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetByPatientCode_404() throws Exception {
		String request = "/medicalhistories/patient/{patientCode}";
		Integer patientCode = 999;

		when(mhManagerMock.getMedicalHistoriesByPatientCode(patientCode)).thenReturn(List.of());

		this.mockMvc
			.perform(get(request, patientCode))
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	@Test
	void testCreate_201() throws Exception {
		String request = "/medicalhistories";
		Patient patient = PatientHelper.setup();
		Integer patientCode = 1;
		patient.setCode(patientCode);

		MedicalHistory medicalHistory = MedicalHistoryHelper.setup();
		medicalHistory.setPatient(patient);

		MedicalHistoryDTO body = mhMapper.map2DTO(medicalHistory);

		when(patientBrowserManagerMock.getPatientById(patientCode)).thenReturn(patient);

		when(mhManagerMock.add(any(MedicalHistory.class))).thenReturn(medicalHistory);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateMedicalHistory_200() throws Exception {
		String request = "/medicalhistories/{id}";
		Integer id = 1;

		Patient patient = PatientHelper.setup();
		patient.setCode(1);

		MedicalHistory existingHistory = MedicalHistoryHelper.setup();
		existingHistory.setId(id);
		existingHistory.setPatient(patient);

		MedicalHistory updatedHistory = MedicalHistoryHelper.setup();
		updatedHistory.setId(id);
		updatedHistory.setPatient(patient);

		MedicalHistoryDTO body = mhMapper.map2DTO(updatedHistory);

		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(existingHistory);
		when(mhManagerMock.update(any(MedicalHistory.class))).thenReturn(updatedHistory);

		MvcResult result = this.mockMvc
			.perform(put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateMedicalHistory_404() throws Exception {
		String request = "/medicalhistories/{id}";
		Integer id = 999;

		MedicalHistoryDTO body = mhMapper.map2DTO(MedicalHistoryHelper.setup());

		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(null);

		this.mockMvc
			.perform(put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isNotFound());
	}
}
