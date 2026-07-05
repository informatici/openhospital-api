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
package org.isf.opetype.rest;

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

import org.isf.opetype.data.OperationTypeDTOHelper;
import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.mapper.OperationTypeMapper;
import org.isf.opetype.model.OperationType;
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

class OperationTypeControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationTypeControllerTest.class);

	@Mock
	protected OperationTypeBrowserManager operationTypeManagerMock;

	protected OperationTypeMapper operationTypemapper = new OperationTypeMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new OperationTypeController(operationTypeManagerMock, operationTypemapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(operationTypemapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewOperationType_201() throws Exception {
		String request = "/operationtypes";
		OperationTypeDTO body = OperationTypeDTOHelper.setup(operationTypemapper);

		OperationType operationType = new OperationType("ZZ", "TestDescription");

		when(operationTypeManagerMock.newOperationType(operationType))
			.thenReturn(operationType);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(OperationTypeDTOHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateOperationType_200() throws Exception {
		String request = "/operationtypes/{code}";
		OperationTypeDTO body = OperationTypeDTOHelper.setup(operationTypemapper);
		String code = body.getCode();
		OperationType operationType = new OperationType("ZZ", "TestDescription");

		when(operationTypeManagerMock.isCodePresent(code))
			.thenReturn(true);

		when(operationTypeManagerMock.updateOperationType(operationType))
			.thenReturn(operationType);

		MvcResult result = this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(OperationTypeDTOHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetOperationTypes_200() throws Exception {
		String request = "/operationtypes";

		OperationType operationType = new OperationType("ZZ", "TestDescription");
		List<OperationType> operationTypesFound = new ArrayList<>();
		operationTypesFound.add(operationType);
		when(operationTypeManagerMock.getOperationType())
			.thenReturn(operationTypesFound);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testDeleteOperationType_200() throws Exception {
		String request = "/operationtypes/{code}";
		OperationTypeDTO body = OperationTypeDTOHelper.setup(operationTypemapper);
		String code = body.getCode();

		OperationType operationType = new OperationType("ZZ", "TestDescription");
		ArrayList<OperationType> operationTypesFound = new ArrayList<>();
		operationTypesFound.add(operationType);
		when(operationTypeManagerMock.getOperationType())
			.thenReturn(operationTypesFound);

		MvcResult result = this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
