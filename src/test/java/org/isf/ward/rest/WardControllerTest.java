/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.ward.rest;

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

import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.ward.data.WardHelper;
import org.isf.ward.dto.WardDTO;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.mapper.WardMapper;
import org.isf.ward.model.Ward;
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

public class WardControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WardControllerTest.class);

	@Mock
	protected WardBrowserManager wardBrowserManagerMock;

	protected WardMapper wardMapper = new WardMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new WardController(wardBrowserManagerMock, wardMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(wardMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetWards_200() throws Exception {
		String request = "/wards";

		List<Ward> wardList = WardHelper.setupWardList(4);

		when(wardBrowserManagerMock.getWards())
				.thenReturn(wardList);

		List<WardDTO> expectedWardDTOs = wardMapper.map2DTOList(wardList);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(WardHelper.getObjectMapper().writeValueAsString(expectedWardDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetWardsNoMaternity_200() throws Exception {
		String request = "/wardsNoMaternity";

		List<Ward> wardList = WardHelper.setupWardList(4);

		when(wardBrowserManagerMock.getWardsNoMaternity()) //TODO OP-6 BUG (CORE) on WardIoOperationRepository.java line 15 about method name
				.thenReturn(wardList);

		List<WardDTO> expectedWardDTOs = wardMapper.map2DTOList(wardList);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(WardHelper.getObjectMapper().writeValueAsString(expectedWardDTOs))))
				// TODO assert that all wards on list are WRD_ID_A <> M
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetCurrentOccupation() throws Exception {
		String request = "/wards/occupation/{code}";

		int code = 4;

		Ward ward = WardHelper.setup(code);

		Integer numberOfPatients = 6;

		when(wardBrowserManagerMock.findWard(ward.getCode()))
				.thenReturn(ward);

		when(wardBrowserManagerMock.getCurrentOccupation(ward))
				.thenReturn(numberOfPatients);

		MvcResult result = this.mockMvc
				.perform(get(request, ward.getCode()))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(numberOfPatients.toString())))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewWard_200() throws Exception {
		String request = "/wards";
		int code = 1;
		Ward ward = WardHelper.setup(code);
		WardDTO body = wardMapper.map2DTO(ward);

		when(wardBrowserManagerMock.newWard(wardMapper.map2Model(body)))
				.thenReturn(ward);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(WardHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateWard_200() throws Exception {
		String request = "/wards";
		int code = 1;
		Ward ward = WardHelper.setup(code);
		WardDTO body = wardMapper.map2DTO(ward);

		boolean isUpdated = true;
		when(wardBrowserManagerMock.updateWard(wardMapper.map2Model(body)))
				.thenReturn(ward);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(WardHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteWard() throws Exception {
		String request = "/wards/{code}";
		int basecode = 1;

		Ward ward = WardHelper.setup(basecode);
		WardDTO body = wardMapper.map2DTO(ward);
		String code = body.getCode();

		when(wardBrowserManagerMock.findWard(code))
				.thenReturn(ward);

		when(wardBrowserManagerMock.deleteWard(wardMapper.map2Model(body)))
				.thenReturn(true);

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
	public void testCheckWardCode() throws Exception {
		String request = "/wards/check/{code}";

		int basecode = 1;

		Ward ward = WardHelper.setup(basecode);

		String code = ward.getCode();

		when(wardBrowserManagerMock.isCodePresent(ward.getCode()))
				.thenReturn(true);

		MvcResult result = this.mockMvc
				.perform(get(request, code))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string("true"))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testCheckWardMaternityCode_true_200() throws Exception {
		String request = "/wards/check/maternity/{createIfNotExist}";

		Boolean createIfNotExist = true;

		when(wardBrowserManagerMock.maternityControl(createIfNotExist))
				.thenReturn(createIfNotExist);

		MvcResult result = this.mockMvc
				.perform(get(request, createIfNotExist))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(createIfNotExist.toString()))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testCheckWardMaternityCode_false_200() throws Exception {
		String request = "/wards/check/maternity/{createIfNotExist}";

		Boolean createIfNotExist = false;

		when(wardBrowserManagerMock.maternityControl(createIfNotExist))
				.thenReturn(createIfNotExist);

		MvcResult result = this.mockMvc
				.perform(get(request, createIfNotExist))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(createIfNotExist.toString()))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
