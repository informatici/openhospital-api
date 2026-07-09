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
package org.isf.pregtreattype.rest;

import static org.hamcrest.Matchers.containsString;
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

import org.isf.pregtreattype.data.PregnantTreatmentTypeHelper;
import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.mapper.PregnantTreatmentTypeMapper;
import org.isf.pregtreattype.model.PregnantTreatmentType;
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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PregnantTreatmentTypeControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PregnantTreatmentTypeControllerTest.class);

	@Mock
	protected PregnantTreatmentTypeBrowserManager pregnantTreatmentTypeManager;

	protected PregnantTreatmentTypeMapper pregnantTreatmentTypeMapper = new PregnantTreatmentTypeMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new PregnantTreatmentTypeController(pregnantTreatmentTypeManager, pregnantTreatmentTypeMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(pregnantTreatmentTypeMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetAllPregnantTreatmentTypes_200() throws Exception {
		String request = "/pregnanttreatmenttypes";

		List<PregnantTreatmentType> results = PregnantTreatmentTypeHelper.setupPregnantTreatmentTypeList(3);

		List<PregnantTreatmentTypeDTO> parsedResults = pregnantTreatmentTypeMapper.map2DTOList(results);

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(results);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PregnantTreatmentTypeHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testNewPregnantTreatmentType_201() throws Exception {
		String request = "/pregnanttreatmenttypes";
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);

		when(pregnantTreatmentTypeManager.newPregnantTreatmentType(pregnantTreatmentTypeMapper.map2Model(body)))
			.thenReturn(pregnantTreatmentType);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdatePregnantTreatmentType_200() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);
		String request = "/pregnanttreatmenttypes/{code}";
		String code = body.getCode();

		when(pregnantTreatmentTypeManager.isCodePresent(code))
			.thenReturn(true);

		when(pregnantTreatmentTypeManager.updatePregnantTreatmentType(pregnantTreatmentTypeMapper.map2Model(body)))
			.thenReturn(pregnantTreatmentType);

		MvcResult result = this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdatePregnantTreatmentType_404() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);
		String request = "/pregnanttreatmenttypes/{code}";
		String code = body.getCode();

		when(pregnantTreatmentTypeManager.isCodePresent(code))
			.thenReturn(false);

		MvcResult result = this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testDeletePregnantTreatmentType_200() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		String request = "/pregnanttreatmenttypes/{code}";
		String code = pregnantTreatmentType.getCode();

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(List.of(pregnantTreatmentType));

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
	void testDeletePregnantTreatmentType_404() throws Exception {
		String request = "/pregnanttreatmenttypes/{code}";
		String code = "nonExistingCode";

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(List.of());

		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
