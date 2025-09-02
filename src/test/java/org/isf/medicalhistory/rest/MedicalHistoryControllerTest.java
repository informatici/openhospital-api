/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalhistory.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.isf.medicalhistory.data.MedicalHistoryHelper;
import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
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

public class MedicalHistoryControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalHistoryControllerTest.class);

	@Mock
	private MedicalHistoryBrowsingManager mhManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	private final PatientMapper patientMapper = new PatientMapper();

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
		ReflectionTestUtils.setField(patientMapper, "modelMapper", modelMapper);
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
			.andReturn();

		LOGGER.debug("result : {}", result);
	}

	@Test
	void testGetOne_404() throws Exception {
		String request = "/medicalhistories/{id}";
		int id = 999;

		when(mhManagerMock.getMedicalHistoryById(id)).thenReturn(null);

		this.mockMvc
			.perform(get(request, id).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	@Test
	void testGetOne_200() throws Exception {
		String request = "/medicalhistories/{id}";
		MedicalHistory mh = MedicalHistoryHelper.setup();

		int patientCode = 1;

		mhManagerMock.add(mh);

		when(mhManagerMock.getMedicalHistoryById(1)).thenReturn(mh);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testCreateMedicalHistory_404() throws Exception {
		MedicalHistory mh = MedicalHistoryHelper.setup();

		int patientCode = 1;
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(patientCode);
		Patient newPatient = PatientHelper.setup();
		newPatient.setCode(patientCode);

		mh.setPatient(newPatient);
		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);

		when(mhManagerMock.add(mhMapper.map2Model(dto))).thenReturn(mh);

		MvcResult result = mockMvc
			.perform(post("/medicalhistories")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(dto))))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testCreateMedicalHistory_201() throws Exception {
		MedicalHistory mh = MedicalHistoryHelper.setup();

		int patientCode = 1;
		Patient newPatient = PatientHelper.setup();
		newPatient.setCode(patientCode);

		mh.setPatient(newPatient);
		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);

		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(newPatient);
		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(newPatient);

		when(mhManagerMock.add(any(MedicalHistory.class))).thenReturn(mh);

		MvcResult result = mockMvc
			.perform(post("/medicalhistories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(dto))))
			.andExpect(status().isCreated())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.patient.code").value(patientCode))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testCreateMedicalHistory_404_PatientNotFound() throws Exception {
		MedicalHistory mh = MedicalHistoryHelper.setup();
		MedicalHistoryDTO dto = mhMapper.map2DTO(mh);

		when(patientBrowserManagerMock.getPatientById(dto.getPatient().getCode())).thenReturn(null);

		MvcResult result = mockMvc
			.perform(post("/medicalhistories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(dto))))
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateMed() throws Exception {
		String request = "/medicalhistories/{id}";

		MedicalHistoryDTO body = MedicalHistoryHelper.setup(mhMapper);
		Integer code = 10;
		body.getPatient().setCode(code);
		body.setId(1);

		MedicalHistory old = mhMapper.map2Model(body);

		MedicalHistory update = mhMapper.map2Model(body);

		when(mhManagerMock.getMedicalHistoryById(body.getId()))
			.thenReturn(old);

		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode()))
			.thenReturn(patient);

		when(mhManagerMock.update(update))
			.thenReturn(update);

		MvcResult result = this.mockMvc
			.perform(put(request, body.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateMedNotFound_404() throws Exception {
		String request = "/medicalhistories/{id}";

		MedicalHistoryDTO body = MedicalHistoryHelper.setup(mhMapper);
		body.setId(999);
		Integer code = 10;
		body.getPatient().setCode(code);

		when(mhManagerMock.getMedicalHistoryById(body.getId()))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(put(request, body.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalHistoryHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}
}
