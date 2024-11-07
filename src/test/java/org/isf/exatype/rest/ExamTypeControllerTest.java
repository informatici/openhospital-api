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
package org.isf.exatype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.IntStream;

import org.isf.OpenHospitalApiApplication;
import org.isf.exatype.dto.ExamTypeDTO;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.mapper.ExamTypeMapper;
import org.isf.exatype.model.ExamType;
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
 * Exam Type Controller Test
 * @author Silevester D.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
public class ExamTypeControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ExamTypeMapper mapper;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExamTypeBrowserManager manager;

	@Test
	@WithMockUser(username = "admin", authorities = {"examtypes.read"})
	@DisplayName("Get all exam types")
	void testGetAll() throws Exception {
		List<ExamType> examTypes = generateExamTypes(5);
		List<ExamTypeDTO> examTypeDTOs = mapper.map2DTOList(examTypes);

		when(manager.getExamType()).thenReturn(examTypes);

		mvc.perform(get("/examtypes")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examTypeDTOs))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examtypes.create"})
	@DisplayName("Add new exam type")
	void testNewExamType() throws Exception {
		ExamType examType = generateExamTypes(1).get(0);
		ExamTypeDTO examTypeDTO = mapper.map2DTO(examType);

		when(manager.newExamType(any())).thenReturn(examType);

		mvc.perform(post("/examtypes")
				.content(objectMapper.writeValueAsString(examTypeDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examTypeDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examtypes.update"})
	@DisplayName("Update exam type")
	void testUpdateExamType() throws Exception {
		ExamType examType = generateExamTypes(1).get(0);
		ExamTypeDTO examTypeDTO = mapper.map2DTO(examType);

		when(manager.isCodePresent(anyString())).thenReturn(true);
		when(manager.updateExamType(any())).thenReturn(examType);

		mvc.perform(put("/examtypes/{id}", examType.getCode())
				.content(objectMapper.writeValueAsString(examTypeDTO))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(examTypeDTO))))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"examtypes.delete"})
	@DisplayName("Delete exam type")
	void testDelete() throws Exception {
		List<ExamType> examTypes = generateExamTypes(1);

		when(manager.isCodePresent(anyString())).thenReturn(true);
		when(manager.getExamType()).thenReturn(examTypes);
		doNothing().when(manager).deleteExamType(any());

		mvc.perform(delete("/examtypes/{id}", examTypes.get(0).getCode())
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(true))))
			.andReturn();
	}

	private List<ExamType> generateExamTypes(int size) {
		return IntStream.range(0, size)
			.mapToObj(i -> new ExamType("Exam " + i, "Description " + i))
			.toList();
	}
}
