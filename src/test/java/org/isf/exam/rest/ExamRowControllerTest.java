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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.IntStream;

import org.isf.OpenHospitalApiApplication;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.manager.ExamRowBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exam.data.ExamHelper;
import org.isf.exam.dto.ExamRowDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.exam.mapper.ExamRowMapper;
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
 * Exam Row Controller Test
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class ExamRowControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ExamRowMapper mapper;

	@Autowired
	private ExamMapper examMapper;

	@MockBean
	private ExamBrowsingManager examManager;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExamRowBrowsingManager manager;

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.read"})
	@DisplayName("Should get all exam rows")
	void testGetAll() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		List<ExamRow> examRows = generateExamRows(exam, 5);
		List<ExamRowDTO> examRowDTOS = mapper.map2DTOList(examRows);

		when(manager.getExamRow()).thenReturn(examRows);

		mvc.perform(get("/examrows")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examRowDTOS))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.read"})
	@DisplayName("Should return exam rows when is performed")
	void testSearchExamRows() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		List<ExamRow> examRows = generateExamRows(exam, 5);
		List<ExamRowDTO> examRowDTOS = mapper.map2DTOList(examRows);

		when(manager.getExamRow(anyInt(), anyString())).thenReturn(examRows);

		mvc.perform(get("/examrows/search")
				.queryParam("code", "2")
				.queryParam("description", "EXA")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examRowDTOS))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.read"})
	@DisplayName("Should get exam rows using the code")
	void testGetExamRowsByCode() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		List<ExamRow> examRows = generateExamRows(exam, 4);
		List<ExamRowDTO> examRowDTOS = mapper.map2DTOList(examRows);

		when(manager.getExamRow(anyInt())).thenReturn(examRows);

		mvc.perform(get("/examrows/{code}", "2")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examRowDTOS))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.read"})
	@DisplayName("Should get all exam rows related to the given exam code")
	void testGetExamRowsByExamCode() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		List<ExamRow> examRows = generateExamRows(exam, 4);
		List<ExamRowDTO> examRowDTOS = mapper.map2DTOList(examRows);

		when(manager.getExamRowByExamCode(anyString())).thenReturn(examRows);

		mvc.perform(get("/examrows/byExamCode/{code}", "2")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examRowDTOS))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.create"})
	@DisplayName("Should add a new exam row")
	void testAddExamRow() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		ExamRow examRow = generateExamRows(exam, 1).get(0);
		ExamRowDTO examRowDTO = mapper.map2DTO(examRow);

		when(examManager.getExams()).thenReturn(List.of(exam));
		when(manager.newExamRow(any())).thenReturn(examRow);

		mvc.perform(post("/examrows")
				.content(objectMapper.writeValueAsString(examRowDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examRowDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.create"})
	@DisplayName("Should failed to add new exam row with wrong exam code")
	void testAddExamRowWithWrongExam() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		ExamRow examRow = generateExamRows(exam, 1).get(0);
		ExamRowDTO examRowDTO = mapper.map2DTO(examRow);

		exam.setCode("SOME CODE");
		when(examManager.getExams()).thenReturn(List.of(exam));
		when(manager.newExamRow(any())).thenReturn(examRow);

		mvc.perform(post("/examrows")
				.content(objectMapper.writeValueAsString(examRowDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.delete"})
	@DisplayName("Should delete exam row")
	void testDeleteExamRow() throws Exception {
		Exam exam = examMapper.map2Model(ExamHelper.generateExam());
		ExamRow examRow = generateExamRows(exam, 1).get(0);

		when(manager.getExamRow(anyInt())).thenReturn(List.of(examRow));
		doNothing().when(manager).deleteExamRow(any());

		mvc.perform(delete("/examrows/{code}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(true))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examrows.delete"})
	@DisplayName("Should failed to delete exam row with wrong code")
	void testDeleteExamRowWithWrongCode() throws Exception {
		when(manager.getExamRow(anyInt())).thenReturn(List.of());
		doNothing().when(manager).deleteExamRow(any());

		mvc.perform(delete("/examrows/{code}", "1")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isBadRequest());
	}

	private List<ExamRow> generateExamRows(Exam exam, int size) {
		return IntStream.range(0, size)
			.mapToObj(i -> {
				ExamRow examRow = new ExamRow(exam, "Exam row " + i);
				examRow.setCode(i);
				return examRow;
			})
			.toList();
	}
}
