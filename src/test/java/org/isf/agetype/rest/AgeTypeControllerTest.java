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
package org.isf.agetype.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.isf.agetype.data.AgeTypeHelper;
import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.mapper.AgeTypeMapper;
import org.isf.agetype.model.AgeType;
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

import com.fasterxml.jackson.databind.ObjectMapper;

class AgeTypeControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AgeTypeControllerTest.class);

	@Mock
	private AgeTypeBrowserManager ageTypeManagerMock;

	private final AgeTypeMapper ageTypeMapper = new AgeTypeMapper();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new AgeTypeController(ageTypeManagerMock, ageTypeMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(ageTypeMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetAllAgeTypes_200() throws Exception {
		String request = "/agetypes";

		List<AgeType> results = AgeTypeHelper.genArrayList(5);
		List<AgeTypeDTO> parsedResults = ageTypeMapper.map2DTOList(results);

		when(ageTypeManagerMock.getAgeType())
			.thenReturn(results);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(AgeTypeHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateAgeType_200() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(5);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);

		when(ageTypeManagerMock.getTypeByCode(anyString())).thenReturn(ageTypes.get(0));
		when(ageTypeManagerMock.getTypeByCode(anyInt())).thenReturn(ageTypes.get(0));
		when(ageTypeManagerMock.updateAgeType(ageTypes)).thenReturn(ageTypes);

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(objectMapper.writeValueAsString(body))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void updateAgeType_nullCode_400() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(1);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);
		body.get(0).setCode(null);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("The age type with code null is not valid."));
	}

	@Test
	void updateAgeType_emptyCode_400() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(1);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);
		String code = "   ";
		body.get(0).setCode(code);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("The age type with code " + code + " is not valid."));
	}

	@Test
	void updateAgeType_unknownCode_400() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(1);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);

		when(ageTypeManagerMock.getTypeByCode(anyString())).thenReturn(null);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("The age type with code " + body.get(0).getCode() + " is not valid."));
	}

	@Test
	void updateAgeType_getTypeByCodeFails_500() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(1);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);

		when(ageTypeManagerMock.getTypeByCode(anyString())).thenThrow(new OHServiceException(new OHExceptionMessage("Unable to get age type")));

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Unable to get age type"));
	}

	@Test
	void updateAgeType_500() throws Exception {
		String request = "/agetypes";
		List<AgeType> ageTypes = AgeTypeHelper.genList(3);
		List<AgeTypeDTO> body = ageTypeMapper.map2DTOList(ageTypes);

		when(ageTypeManagerMock.getTypeByCode(anyString())).thenReturn(ageTypes.get(0));
		when(ageTypeManagerMock.updateAgeType(anyList())).thenThrow(new OHServiceException(new OHExceptionMessage("Update failed")));

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectMapper.writeValueAsString(body)))
			)
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Unable to update age types. Please check that you've correctly set values"));
	}

	@Test
	void testGetAgeTypeCodeByAge_200() throws Exception {

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
	void getAgeTypeCodeByAge_200_noMatchingCode() throws Exception {

		String request = "/agetypes/code?age={age}";
		int age = 200;

		when(ageTypeManagerMock.getTypeByAge(age))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, age))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("{}"));
	}

	@Test
	void testGetAgeTypeByIndex_200() throws Exception {
		String request = "/agetypes/{index}";
		int index = 10;
		AgeType ageType = AgeTypeHelper.setup(index);
		AgeTypeDTO ageTypeDTO = ageTypeMapper.map2DTO(ageType);

		when(ageTypeManagerMock.getTypeByCode(index))
			.thenReturn(ageType);

		MvcResult result = this.mockMvc
			.perform(get(request, index))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(AgeTypeHelper.getObjectMapper().writeValueAsString(ageTypeDTO))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void getAgeTypeByIndex_404() throws Exception {
		String request = "/agetypes/{index}";
		int index = 25;

		when(ageTypeManagerMock.getTypeByCode(index))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, index).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Age type not found with index :" + index));
	}

}
