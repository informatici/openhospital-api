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
package org.isf.exam.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.OpenHospitalApiApplication;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exam.data.ExamHelper;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.dto.ExamWithRowsDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.mapper.ExamTypeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class ExamControllerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(ExamControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ExamMapper examMapper;

	@Autowired
	private ExamTypeMapper examTypeMapper;

	@MockBean
	private ExamBrowsingManager examManager;

	@MockBean
	private ExamTypeBrowserManager examTypeBrowserManager;

	@MockBean
	private ExamRowIoOperationRepository examRowIoOperationRepository;

	@Nested
	@DisplayName("Create exam")
	class CreateExamTests {

		@Test
		@DisplayName("Should create a new exam with associated examrows")
		@WithMockUser(username = "admin", authorities = { "exams.create", "examrows.create" })
		void shouldCreateExamWithRows() throws Exception {
			ExamWithRowsDTO payload = ExamHelper.generateExamWithRowsDTO();

			when(examManager.create(any(), any())).thenReturn(examMapper.map2Model(payload.exam()));
			when(examTypeBrowserManager.findByCode(any())).thenReturn(examTypeMapper.map2Model(payload.exam().getExamtype()));

			var result = mvc.perform(
					post("/exams").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payload)))
				.andDo(log())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("description", equalTo(payload.exam().getDescription())))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to create exam procedure 1 with rows when default result doesn't match")
		@WithMockUser(username = "admin", authorities = { "exams.create", "examrows.create" })
		void shouldFailToCreateExamWithInvalidDefaultResult() throws Exception {
			ExamDTO examDTO = ExamHelper.generateExam();
			examDTO.setDefaultResult("IRES");

			when(examTypeBrowserManager.findByCode(any())).thenReturn(examTypeMapper.map2Model(examDTO.getExamtype()));

			var result = mvc.perform(
					post("/exams").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ExamHelper.generateExamWithRowsDTO(examDTO))))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to create exam when user doesn't have required permissions")
		@WithMockUser(username = "admin", authorities = { "examrows.create" })
		void shouldFailToCreateExamWhenInsufficientPermissions() throws Exception {
			var result = mvc.perform(
					post("/exams").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ExamHelper.generateExamWithRowsDTO())))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}

	@Nested
	@DisplayName("Update exam")
	class UpdateExamTests {

		@Test
		@DisplayName("Should update an exam with associated examrows")
		@WithMockUser(username = "admin", authorities = { "exams.update", "examrows.create", "examrows.delete" })
		void shouldUpdateExamWithRows() throws Exception {
			ExamWithRowsDTO payload = ExamHelper.generateExamWithRowsDTO();
			Exam exam = examMapper.map2Model(payload.exam());

			when(examManager.create(any(), any())).thenReturn(exam);
			when(examManager.findByCode(any())).thenReturn(exam);
			when(examManager.update(any(), any())).thenReturn(exam);
			when(examTypeBrowserManager.findByCode(any())).thenReturn(examTypeMapper.map2Model(payload.exam().getExamtype()));

			var result = mvc.perform(
					put("/exams/{code}", payload.exam().getCode()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payload)))
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(jsonPath("description", equalTo(payload.exam().getDescription())))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update exam procedure 1 with rows when default result doesn't match")
		@WithMockUser(username = "admin", authorities = { "exams.update", "examrows.create", "examrows.delete" })
		void shouldFailToUpdateExamWithInvalidDefaultResult() throws Exception {
			ExamDTO examDTO = ExamHelper.generateExam();
			examDTO.setDefaultResult("IRES");
			Exam exam = examMapper.map2Model(examDTO);

			when(examManager.create(any(), any())).thenReturn(exam);
			when(examManager.findByCode(any())).thenReturn(exam);
			when(examTypeBrowserManager.findByCode(any())).thenReturn(examTypeMapper.map2Model(examDTO.getExamtype()));

			var result = mvc.perform(
					put("/exams/{code}", examDTO.getCode()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(ExamHelper.generateExamWithRowsDTO(examDTO))))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message", containsString("Exam default result doesn't match")))
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update exam code in body doesn't match")
		@WithMockUser(username = "admin", authorities = { "exams.update", "examrows.create", "examrows.delete" })
		void shouldFailToUpdateExamWhenCodeInBodyDoesntMatch() throws Exception {
			var result = mvc.perform(
					put("/exams/{code}", "DD").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(ExamHelper.generateExamWithRowsDTO())))
				.andDo(log())
				.andExpect(status().isBadRequest())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}

		@Test
		@DisplayName("Should fail to update exam when user doesn't have required permissions")
		@WithMockUser(username = "admin", authorities = { "examrows.create", "examrows.delete" })
		void shouldFailToUpdateExamWhenInsufficientPermissions() throws Exception {
			var result = mvc.perform(
					put("/exams/hd").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ExamHelper.generateExamWithRowsDTO())))
				.andDo(log())
				.andExpect(status().isForbidden())
				.andReturn();

			LOGGER.debug("result: {}", result);
		}
	}
}
