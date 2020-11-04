/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.agetype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.agetype.data.AgeTypeHelper;
import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.mapper.AgeTypeMapper;
import org.isf.agetype.model.AgeType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgeTypeControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AgeTypeControllerTest.class);

	@Mock
	private AgeTypeBrowserManager ageTypeManagerMock;

	private AgeTypeMapper ageTypeMapper = new AgeTypeMapper();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AgeTypeController(ageTypeManagerMock, ageTypeMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(ageTypeMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetAllAgeTypes_200() throws JsonProcessingException, Exception {
		String request = "/agetypes";

		ArrayList<AgeType> results = AgeTypeHelper.genArrayList(5);
		List<AgeTypeDTO> parsedResults = ageTypeMapper.map2DTOList(results);

		when(ageTypeManagerMock.getAgeType())
				.thenReturn(results);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(parsedResults))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateAgeType_200() throws Exception {
		String request = "/agetypes";
		AgeTypeDTO body = ageTypeMapper.map2DTO(AgeTypeHelper.setup());

		ArrayList<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(AgeTypeHelper.setup());

		when(ageTypeManagerMock.updateAgeType(ageTypes))
				.thenReturn(true);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(AgeTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAgeTypeCodeByAge_200() throws Exception {

		String request = "/agetypes/code?age={age}";
		int age = 10;
		String responseString = "resultString";

		when(ageTypeManagerMock.getTypeByAge(age))
				.thenReturn(responseString);

		MvcResult result = this.mockMvc
				.perform(get(request, age))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(responseString)))
				.andExpect(content().string(containsString("code")))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAgeTypeByIndex_200() throws JsonProcessingException, Exception {
		String request = "/agetypes/{index}";
		int index = 10;
		AgeType ageType = AgeTypeHelper.setup(index);

		when(ageTypeManagerMock.getTypeByCode(index))
				.thenReturn(ageType);

		MvcResult result = this.mockMvc
				.perform(get(request, index))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(ageType))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
