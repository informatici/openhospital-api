/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.examination.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isf.OpenHospitalApiApplication;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Examination Controller Test
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class ExaminationControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private PatientExaminationMapper mapper;

	@MockBean
	private ExaminationBrowserManager manager;

	@MockBean
	private PatientBrowserManager patientBrowserManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should get patient examination using ID")
	void testGetPatientExaminationById() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);

		when(manager.getByID(anyInt())).thenReturn(patientExamination);

		mvc.perform(get("/examinations/{code}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should fail to get patient examination with wrong ID")
	void testGetPatientExaminationWithWrongId() throws Exception {
		when(manager.getByID(anyInt())).thenReturn(null);

		mvc.perform(get("/examinations/{code}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should get patient default examination")
	void testGetDefaultPatientExamination() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);

		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(manager.getDefaultPatientExamination(any())).thenReturn(patientExamination);

		mvc.perform(get("/examinations/defaultPatientExamination")
				.queryParam("patId", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should fail to get patient default examination with wrong patient ID")
	void testGetDefaultPatientExaminationWithWrongPatientID() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);

		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(null);
		when(manager.getDefaultPatientExamination(any())).thenReturn(patientExamination);

		mvc.perform(get("/examinations/defaultPatientExamination")
				.queryParam("patId", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should get patient default examination")
	void testGetFromLastPatientExamination() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);

		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(manager.getFromLastPatientExamination(any())).thenReturn(patientExamination);
		when(manager.getByID(anyInt())).thenReturn(patientExamination);

		mvc.perform(get("/examinations/fromLastPatientExamination/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should get last examination by patient ID")
	void testGetLastByPatientId() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);

		when(manager.getLastByPatID(anyInt())).thenReturn(patientExamination);

		mvc.perform(get("/examinations/lastByPatientId/{patId}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.read"})
	@DisplayName("Should get examination by patient ID")
	void testGetByPatientId() throws Exception {
		Patient patient = PatientHelper.setup();
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);

		when(manager.getByPatID(anyInt())).thenReturn(List.of(patientExamination));

		mvc.perform(get("/examinations/byPatientId/{patId}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(List.of(patientExaminationDTO)))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.create"})
	@DisplayName("Should add patient examination")
	void testNewExamination() throws Exception {
		Patient patient = PatientHelper.setup();
		patient.setCode(2);
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		patientExamination.setPex_sat(100.0);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);
		patientExaminationDTO.setPatientCode(patient.getCode());

		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(manager.saveOrUpdate(any())).thenReturn(patientExamination);

		mvc.perform(post("/examinations")
				.content(objectMapper.writeValueAsString(patientExaminationDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examinations.update"})
	@DisplayName("Should update patient examination")
	void testUpdateExamination() throws Exception {
		Patient patient = PatientHelper.setup();
		patient.setCode(2);
		PatientExamination patientExamination = new TestPatientExamination().setup(patient, false);
		patientExamination.setPex_ID(1);
		patientExamination.setPex_sat(100.0);
		PatientExaminationDTO patientExaminationDTO = mapper.map2DTO(patientExamination);
		patientExaminationDTO.setPatientCode(patient.getCode());

		when(manager.getByID(anyInt())).thenReturn(patientExamination);
		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(manager.saveOrUpdate(any())).thenReturn(patientExamination);

		mvc.perform(put("/examinations/{id}", String.valueOf(patientExamination.getPex_ID()))
				.content(objectMapper.writeValueAsString(patientExaminationDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(patientExaminationDTO))));
	}
}
