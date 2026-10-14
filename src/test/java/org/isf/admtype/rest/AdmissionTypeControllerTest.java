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
package org.isf.admtype.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
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

class AdmissionTypeControllerTest {

	@Mock
	protected AdmissionTypeBrowserManager admissionTypeManagerMock;

	protected AdmissionTypeMapper admissionTypemapper = new AdmissionTypeMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new AdmissionTypeController(admissionTypeManagerMock, admissionTypemapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(admissionTypemapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void newAdmissionType_shouldReturnCreated() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");

		when(admissionTypeManagerMock.newAdmissionType(admissionType))
			.thenReturn(admissionType);

		when(admissionTypeManagerMock.isCodePresent(body.getCode()))
			.thenReturn(true);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(AdmissionTypeDTOHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated());
	}

	@Test
	void updateAdmissionTypes_shouldReturnOk_whenCodeExists() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");

		when(admissionTypeManagerMock.isCodePresent(admissionType.getCode()))
			.thenReturn(true);

		when(admissionTypeManagerMock.updateAdmissionType(admissionType))
			.thenReturn(admissionType);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(AdmissionTypeDTOHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk());
	}

	@Test
	void getAdmissionTypes_shouldReturnOk() throws Exception {
		String request = "/admissiontypes";

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");
		List<AdmissionType> admissionTypesFound = new ArrayList<>();
		admissionTypesFound.add(admissionType);
		when(admissionTypeManagerMock.getAdmissionType())
			.thenReturn(admissionTypesFound);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk());
	}

	@Test
	void deleteAdmissionType_shouldReturnOk_whenCodeExists() throws Exception {
		String request = "/admissiontypes/{code}";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		String code = body.getCode();

		when(admissionTypeManagerMock.isCodePresent(code))
			.thenReturn(true);

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");
		ArrayList<AdmissionType> admissionTypesFound = new ArrayList<>();
		admissionTypesFound.add(admissionType);
		when(admissionTypeManagerMock.getAdmissionType())
			.thenReturn(admissionTypesFound);

		this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk());
	}

}
