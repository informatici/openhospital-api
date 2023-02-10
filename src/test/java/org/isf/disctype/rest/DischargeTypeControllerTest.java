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
package org.isf.disctype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.disctype.data.DischargeTypeHelper;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.mapper.DischargeTypeMapper;
import org.isf.disctype.model.DischargeType;
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

public class DischargeTypeControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DischargeTypeControllerTest.class);

	@Mock
	protected DischargeTypeBrowserManager discTypeManagerMock;

	protected DischargeTypeMapper dischargeTypeMapper = new DischargeTypeMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DischargeTypeController(discTypeManagerMock, dischargeTypeMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(dischargeTypeMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewDischargeType_201() throws Exception {
		String request = "/dischargetypes";
		String code = "ZZ";
		DischargeTypeDTO body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));

		boolean isCreated = true;
		when(discTypeManagerMock.newDischargeType(dischargeTypeMapper.map2Model(body)))
				.thenReturn(isCreated);

		DischargeType dischargeType = new DischargeType("ZZ", "aDescription");
		List<DischargeType> dischTypeFounds = new ArrayList<>();
		dischTypeFounds.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
				.thenReturn(dischTypeFounds);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DischargeTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateDischargeTypet_200() throws Exception {
		String request = "/dischargetypes";
		String code = "ZZ";
		DischargeTypeDTO body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));

		when(discTypeManagerMock.isCodePresent(body.getCode()))
				.thenReturn(true);

		boolean isUpdated = true;
		when(discTypeManagerMock.updateDischargeType(dischargeTypeMapper.map2Model(body)))
				.thenReturn(isUpdated);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DischargeTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDischargeTypes_200() throws Exception {
		String request = "/dischargetypes";

		DischargeType dischargeType = new DischargeType("ZZ", "aDescription");
		List<DischargeType> dischTypes = new ArrayList<>();
		dischTypes.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
				.thenReturn(dischTypes);

		List<DischargeTypeDTO> expectedDischTypeDTOs = dischargeTypeMapper.map2DTOList(dischTypes);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DischargeTypeHelper.getObjectMapper().writeValueAsString(expectedDischTypeDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteDischargeType_200() throws Exception {
		String request = "/dischargetypes/{code}";
		String code = "ZZ";
		DischargeTypeDTO body = dischargeTypeMapper.map2DTO(DischargeTypeHelper.setup(code));

		when(discTypeManagerMock.isCodePresent(body.getCode()))
				.thenReturn(true);

		DischargeType dischargeType = new DischargeType("ZZ", "aDescription");
		List<DischargeType> dischTypeFounds = new ArrayList<>();
		dischTypeFounds.add(dischargeType);
		when(discTypeManagerMock.getDischargeType())
				.thenReturn(dischTypeFounds);

		when(discTypeManagerMock.deleteDischargeType(dischTypeFounds.get(0)))
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

}
