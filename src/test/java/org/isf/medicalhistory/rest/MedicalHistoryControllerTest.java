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
import org.isf.shared.mapper.mappings.PatientMapping;
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

import com.fasterxml.jackson.core.StreamWriteConstraints;

public class MedicalHistoryControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalHistoryControllerTest.class);

	@Mock
	private MedicalHistoryBrowsingManager mhManagerMock;


	protected PatientBrowserManager patientBrowserManagerMock;

	private final MedicalHistoryMapper mhMapper = new MedicalHistoryMapper();
	private MockMvc mockMvc;
	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new MedicalHistoryController(mhManagerMock, mhMapper, patientBrowserManagerMock))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		PatientMapping.addMapping(modelMapper);
		ReflectionTestUtils.setField(mhMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
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
	void testGetByPatientCode_200() throws Exception {
		String request = "/medicalhistories/patient/{patientCode}";
		List<MedicalHistory> medicalHistories = MedicalHistoryHelper.setupMedicalHistories(3);

		int patientCode = 1;

		when(mhManagerMock.getMedicalHistoriesByPatientCode(1)).thenReturn(medicalHistories);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(MedicalHistoryHelper.asJsonString(medicalHistories))))
			.andReturn();

		LOGGER.debug("result : {} " + result );
	}

//	@Test
//	void testGetOne_404() throws Exception {
//		String request = "/medicalhistories/{id}";
//		Integer id = 999; // an ID that doesn't exist
//
//		// Mock the manager to return null
//		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(null);
//
//		// Perform the GET request and expect 404
//		this.mockMvc
//			.perform(get(request, id))
//			.andDo(log())
//			.andExpect(status().isNotFound());
//	}
//
//	@Test
//	void testGetOne_200() throws Exception {
//		String request = "/medicalhistories/{id}";
//		Integer id = 1;
//
//		MedicalHistory mh = MedicalHistoryHelper.setup();
//		mh.setId(id);
//
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);
//
//		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(mh);
//
//		MvcResult result = this.mockMvc
//			.perform(get(request, id))
//			.andDo(log())
//			.andExpect(status().isOk())
//			.andExpect(content().json(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(dto))))
//			.andReturn();
//
//		LOGGER.debug("result: {}", result);
//	}
//
//	@Test
//	void testCreateMedicalHistory_201() throws Exception {
//		MedicalHistory mh = MedicalHistoryHelper.setup();
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);
//		Patient patient = PatientHelper.setup();
//
//		// Mock patient exists
//		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(patient);
//		// Mock saving history
//		when(mhManagerMock.add(mhMapper.map2Model(dto))).thenReturn(mh);
//
//		mockMvc.perform(post("/medicalhistories")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MedicalHistoryHelper.asJsonString(dto)))
//			.andExpect(status().isCreated())
//			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//	}
//
//	@Test
//	void testCreateMedicalHistory_404_PatientNotFound() throws Exception {
//		MedicalHistory mh = MedicalHistoryHelper.setup();
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);
//
//		// Mock patient not found
//		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(null);
//
//		mockMvc.perform(post("/medicalhistories")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MedicalHistoryHelper.asJsonString(dto)))
//			.andExpect(status().isNotFound());
//	}
//
//	// ===================== UPDATE MEDICAL HISTORY =====================
//	@Test
//	void testUpdateMedicalHistory_200() throws Exception {
//		Integer id = 1;
//		MedicalHistory mh = MedicalHistoryHelper.setup(id);
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);
//		Patient patient = PatientHelper.setup();
//
//		// Mock existing history
//		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(mh);
//		// Mock patient exists
//		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(patient);
//		// Mock update
//		when(mhManagerMock.update(mhMapper.map2Model(dto))).thenReturn(mh);
//
//		mockMvc.perform(put("/medicalhistories/{id}", id)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MedicalHistoryHelper.asJsonString(dto)))
//			.andExpect(status().isOk())
//			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//	}
//
//	@Test
//	void testUpdateMedicalHistory_404_PatientNotFound() throws Exception {
//		Integer id = 1;
//		MedicalHistory mh = MedicalHistoryHelper.setup(id);
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);
//
//		// Mock existing history
//		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(mh);
//		// Mock patient not found
//		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(null);
//
//		mockMvc.perform(put("/medicalhistories/{id}", id)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MedicalHistoryHelper.asJsonString(dto)))
//			.andExpect(status().isNotFound());
//	}
//
//	@Test
//	void testUpdateMedicalHistory_404_CodeMismatch() throws Exception {
//		Integer id = 1;
//		MedicalHistory mh = MedicalHistoryHelper.setup(2); // different ID
//		MedicalHistoryDTO dto = mhMapper.map2DTO(mh); // now dto has ID = 2
//
//		mockMvc.perform(put("/medicalhistories/{id}", id)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MedicalHistoryHelper.asJsonString(dto)))
//			.andExpect(status().isNotFound());
//	}
}
