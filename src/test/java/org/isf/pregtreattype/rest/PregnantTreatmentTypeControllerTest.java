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
package org.isf.pregtreattype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PregnantTreatmentTypeControllerTest {

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
	void getAllPregnantTreatmentTypes_200() throws Exception {
		String request = "/pregnanttreatmenttypes";

		List<PregnantTreatmentType> results = PregnantTreatmentTypeHelper.setupPregnantTreatmentTypeList(3);

		List<PregnantTreatmentTypeDTO> parsedResults = pregnantTreatmentTypeMapper.map2DTOList(results);

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(results);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PregnantTreatmentTypeHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].code").value("ZZ"))
			.andExpect(jsonPath("$[0].description").value("TestDescription"));
	}

	@Test
	void newPregnantTreatmentType_201() throws Exception {
		String request = "/pregnanttreatmenttypes";
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);

		when(pregnantTreatmentTypeManager.newPregnantTreatmentType(pregnantTreatmentTypeMapper.map2Model(body)))
			.thenReturn(pregnantTreatmentType);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("ZZ"))
			.andExpect(jsonPath("$.description").value("TestDescription"))
			.andExpect(jsonPath("$.hashCode").value(0));
	}

	@Test
	void newPregnantTreatmentType_400() throws Exception {
		String request = "/pregnanttreatmenttypes";
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);

		when(pregnantTreatmentTypeManager.newPregnantTreatmentType(pregnantTreatmentTypeMapper.map2Model(body)))
			.thenReturn(null);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Pregnant Treatment Type not created."));
	}

	@Test
	void updatePregnantTreatmentType_200() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);
		String request = "/pregnanttreatmenttypes/{code}";
		String code = body.getCode();

		when(pregnantTreatmentTypeManager.isCodePresent(code))
			.thenReturn(true);

		when(pregnantTreatmentTypeManager.updatePregnantTreatmentType(pregnantTreatmentTypeMapper.map2Model(body)))
			.thenReturn(pregnantTreatmentType);

		this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ZZ"))
			.andExpect(jsonPath("$.description").value("TestDescription"))
			.andExpect(jsonPath("$.hashCode").value(0));
	}

	@Test
	void updatePregnantTreatmentType_404() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		PregnantTreatmentTypeDTO body = pregnantTreatmentTypeMapper.map2DTO(pregnantTreatmentType);
		String request = "/pregnanttreatmenttypes/{code}";
		String code = body.getCode();

		when(pregnantTreatmentTypeManager.isCodePresent(code))
			.thenReturn(false);

		this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PregnantTreatmentTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Pregnant Treatment Type not found."));
	}

	@Test
	void deletePregnantTreatmentType_200() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = PregnantTreatmentTypeHelper.setup();
		String request = "/pregnanttreatmenttypes/{code}";
		String code = pregnantTreatmentType.getCode();

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(List.of(pregnantTreatmentType));

		this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	void deletePregnantTreatmentType_404() throws Exception {
		String request = "/pregnanttreatmenttypes/{code}";
		String code = "nonExistingCode";

		when(pregnantTreatmentTypeManager.getPregnantTreatmentType())
			.thenReturn(List.of());

		this.mockMvc
			.perform(delete(request, code).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Pregnant Treatment Type not found."));
	}

}
