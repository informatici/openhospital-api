/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.distype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.isf.distype.data.DiseaseTypeHelper;
import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.mapper.DiseaseTypeMapper;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

class DiseaseTypeControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseTypeControllerTest.class);

	@Mock
	protected DiseaseTypeBrowserManager diseaseTypeBrowserManager;

	protected DiseaseTypeMapper diseaseTypeMapper = new DiseaseTypeMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new DiseaseTypeController(diseaseTypeBrowserManager, diseaseTypeMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseTypeMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetAllDiseaseTypes_200() throws Exception {
		String request = "/diseasetypes";

		List<DiseaseType> results = DiseaseTypeHelper.setupDiseaseTypeList(3);

		List<DiseaseTypeDTO> parsedResults = diseaseTypeMapper.map2DTOList(results);

		when(diseaseTypeBrowserManager.getDiseaseType())
			.thenReturn(results);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(DiseaseTypeHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testNewDiseaseType_201() throws Exception {
		String request = "/diseasetypes";
		int code = 123;
		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(false);

		when(diseaseTypeBrowserManager.newDiseaseType(diseaseTypeMapper.map2Model(body)))
			.thenReturn(diseaseType);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void newDiseaseType_400() throws Exception {
		String request = "/diseasetypes";
		int code = 123;
		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(true);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Specified Disease Type code is already used.")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void newDiseaseType_500() throws Exception {
		String request = "/diseasetypes";
		int code = 123;
		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(false);

		when(diseaseTypeBrowserManager.newDiseaseType(any(DiseaseType.class)))
			.thenThrow(new OHServiceException(new OHExceptionMessage("Error")));

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is5xxServerError())
			.andExpect(status().isInternalServerError())
			.andExpect(content().string(containsString("Failed to create disease type.")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateDiseaseType_200() throws Exception {
		String request = "/diseasetypes";
		int code = 456;

		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(true);

		when(diseaseTypeBrowserManager.updateDiseaseType(diseaseTypeMapper.map2Model(body)))
			.thenReturn(diseaseType);

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void updateDiseaseType_404() throws Exception {
		String request = "/diseasetypes";
		int code = 456;

		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(false);

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("Disease Type not found.")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void updateDiseaseType_500() throws Exception {
		String request = "/diseasetypes";
		int code = 456;

		DiseaseType diseaseType = DiseaseTypeHelper.setup(code);
		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(diseaseType);

		when(diseaseTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(true);

		when(diseaseTypeBrowserManager.updateDiseaseType(any(DiseaseType.class)))
			.thenThrow(new OHServiceException(new OHExceptionMessage("Error")));

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is5xxServerError())
			.andExpect(status().isInternalServerError())
			.andExpect(content().string(containsString("Disease Type not updated.")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testDeleteDiseaseType_200() throws Exception {
		String request = "/diseasetypes/{code}";

		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(DiseaseTypeHelper.setup(0));
		String code = body.getCode();

		when(diseaseTypeBrowserManager.getDiseaseType(anyString()))
			.thenReturn(DiseaseTypeHelper.setupDiseaseTypeList(1).get(0));

		String isDeleted = "true";
		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(isDeleted)))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void deleteDiseaseType_404() throws Exception {
		String request = "/diseasetypes/{code}";

		when(diseaseTypeBrowserManager.getDiseaseType(anyString()))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(delete(request, "notThere"))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("No Disease Type found with the given code.")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void deleteDiseaseType_200_NotDeleted() throws Exception {
		String request = "/diseasetypes/{code}";

		DiseaseTypeDTO body = diseaseTypeMapper.map2DTO(DiseaseTypeHelper.setup(0));
		String code = body.getCode();

		when(diseaseTypeBrowserManager.getDiseaseType(anyString()))
			.thenReturn(DiseaseTypeHelper.setupDiseaseTypeList(1).get(0));

		doThrow(new OHServiceException(new OHExceptionMessage("Error")))
			.when(diseaseTypeBrowserManager).deleteDiseaseType(any(DiseaseType.class));

		String isDeleted = "false";
		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(isDeleted)))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
