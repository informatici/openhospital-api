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
package org.isf.medtype.rest;

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

import org.isf.medtype.data.MedicalTypeHelper;
import org.isf.medtype.dto.MedicalTypeDTO;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.mapper.MedicalTypeMapper;
import org.isf.medtype.model.MedicalType;
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

class MedicalTypeControllerTest {

	@Mock
	protected MedicalTypeBrowserManager medicalTypeBrowserManager;

	protected MedicalTypeMapper medicalTypeMapper = new MedicalTypeMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new MedicalTypeController(medicalTypeBrowserManager, medicalTypeMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(medicalTypeMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void getAllMedicalTypes_200() throws Exception {
		String request = "/medicaltypes";

		List<MedicalType> results = MedicalTypeHelper.setupMedicalTypeList(3);

		when(medicalTypeBrowserManager.getMedicalType())
			.thenReturn(results);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].code").value("Z0"))
			.andExpect(jsonPath("$[0].description").value("TestDescription"));
	}

	@Test
	void newMedicalType_201() throws Exception {
		String request = "/medicaltypes";
		MedicalType medicalType = MedicalTypeHelper.setup(1);
		MedicalTypeDTO body = medicalTypeMapper.map2DTO(medicalType);

		when(medicalTypeBrowserManager.newMedicalType(medicalTypeMapper.map2Model(body)))
			.thenReturn(medicalType);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("Z1"))
			.andExpect(jsonPath("$.description").value("TestDescription"));
	}

	@Test
	void updateMedicalType_200() throws Exception {
		String request = "/medicaltypes";
		MedicalType medicalType = MedicalTypeHelper.setup(2);
		MedicalTypeDTO body = medicalTypeMapper.map2DTO(medicalType);

		when(medicalTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(true);

		when(medicalTypeBrowserManager.updateMedicalType(medicalTypeMapper.map2Model(body)))
			.thenReturn(medicalType);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("Z2"))
			.andExpect(jsonPath("$.description").value("TestDescription"));
	}

	@Test
	void updateMedicalType_404() throws Exception {
		String request = "/medicaltypes";
		MedicalType medicalType = MedicalTypeHelper.setup(3);
		MedicalTypeDTO body = medicalTypeMapper.map2DTO(medicalType);

		when(medicalTypeBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(false);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(MedicalTypeHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Medical type not found."));
	}

	@Test
	void isCodeUsed_200() throws Exception {
		String request = "/medicaltypes/check/{code}";
		MedicalType medicalType = MedicalTypeHelper.setup(4);
		String code = medicalType.getCode();

		when(medicalTypeBrowserManager.isCodePresent(code))
			.thenReturn(true);

		this.mockMvc
			.perform(get(request, code))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	void deleteMedicalType_200() throws Exception {
		String request = "/medicaltypes/{code}";
		MedicalType medicalType = MedicalTypeHelper.setup(5);
		String code = medicalType.getCode();

		when(medicalTypeBrowserManager.getMedicalType())
			.thenReturn(List.of(medicalType));

		this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	void deleteMedicalType_404() throws Exception {
		String request = "/medicaltypes/{code}";
		MedicalType medicalType = MedicalTypeHelper.setup(6);
		String code = medicalType.getCode();

		when(medicalTypeBrowserManager.getMedicalType())
			.thenReturn(List.of(MedicalTypeHelper.setup(7)));

		this.mockMvc
			.perform(delete(request, code).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Medical type not found."));
	}

}
