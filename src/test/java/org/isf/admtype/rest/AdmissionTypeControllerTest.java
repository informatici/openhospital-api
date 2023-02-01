/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdmissionTypeControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdmissionTypeControllerTest.class);

	@Mock
	protected AdmissionTypeBrowserManager admtManagerMock;

	protected AdmissionTypeMapper admissionTypemapper = new AdmissionTypeMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AdmissionTypeController(admtManagerMock, admissionTypemapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(admissionTypemapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewAdmissionType_201() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);

		boolean isCreated = true;
		when(admtManagerMock.newAdmissionType(admissionTypemapper.map2Model(body)))
				.thenReturn(isCreated);

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");
		List<AdmissionType> admtFounds = new ArrayList<>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
				.thenReturn(admtFounds);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(AdmissionTypeDTOHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateAdmissionTypet_200() throws Exception {
		String request = "/admissiontypes";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);

		when(admtManagerMock.isCodePresent(body.getCode()))
				.thenReturn(true);

		boolean isUpdated = true;
		when(admtManagerMock.updateAdmissionType(admissionTypemapper.map2Model(body)))
				.thenReturn(isUpdated);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(AdmissionTypeDTOHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAdmissionTypes_200() throws Exception {
		String request = "/admissiontypes";

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");
		List<AdmissionType> admtFounds = new ArrayList<>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
				.thenReturn(admtFounds);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteAdmissionType_200() throws Exception {
		String request = "/admissiontypes/{code}";
		AdmissionTypeDTO body = AdmissionTypeDTOHelper.setup(admissionTypemapper);
		String code = body.getCode();

		when(admtManagerMock.isCodePresent(code))
				.thenReturn(true);

		AdmissionType admissionType = new AdmissionType("ZZ", "aDescription");
		ArrayList<AdmissionType> admtFounds = new ArrayList<>();
		admtFounds.add(admissionType);
		when(admtManagerMock.getAdmissionType())
				.thenReturn(admtFounds);

		when(admtManagerMock.deleteAdmissionType(admtFounds.get(0)))
				.thenReturn(true);

		MvcResult result = this.mockMvc
				.perform(delete(request, code))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
