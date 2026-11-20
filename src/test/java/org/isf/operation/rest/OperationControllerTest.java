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
package org.isf.operation.rest;

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

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.operation.data.OperationHelper;
import org.isf.operation.dto.OperationDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.mapper.OperationMapper;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.operation.model.Operation;
import org.isf.patient.manager.PatientBrowserManager;
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

import com.fasterxml.jackson.databind.ObjectMapper;

class OperationControllerTest {

	@Mock
	protected OperationBrowserManager operationBrowserManagerMock;

	@Mock
	protected AdmissionBrowserManager admissionBrowserManager;

	@Mock
	protected OperationRowBrowserManager operationRowBrowserManager;

	@Mock
	protected PatientBrowserManager patientBrowserManager;

	@Mock
	protected OpdMapper opdMapper;

	@Mock
	protected OperationRowMapper operationRowMapper;

	protected OperationMapper operationMapper = new OperationMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new OperationController(
				operationBrowserManagerMock,
				admissionBrowserManager,
				operationRowBrowserManager,
				patientBrowserManager,
				operationMapper,
				opdMapper,
				operationRowMapper
			))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(operationMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void newOperation_shouldReturnCreated() throws Exception {
		String request = "/operations";

		Operation operation = OperationHelper.setup();
		OperationDTO body = operationMapper.map2DTO(operation);
		String code = body.getCode();

		when(operationBrowserManagerMock.descriptionControl(body.getDescription(), body.getType().getCode()))
			.thenReturn(false);

		when(operationBrowserManagerMock.newOperation(operationMapper.map2Model(body)))
			.thenReturn(operation);

		when(operationBrowserManagerMock.getOperationByCode(code))
			.thenReturn(operation);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(OperationHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated());
	}

	@Test
	void updateOperation_shouldReturnOk() throws Exception {
		String request = "/operations/{code}";
		String code = "25";

		Operation operation = OperationHelper.setup();
		OperationDTO body = operationMapper.map2DTO(operation);

		when(operationBrowserManagerMock.isCodePresent(code))
			.thenReturn(true);

		when(operationBrowserManagerMock.updateOperation(operation))
			.thenReturn(operation);

		this.mockMvc
			.perform(put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(OperationHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk());
	}

	@Test
	void getOperations_shouldReturnAllOperations() throws Exception {
		String request = "/operations";

		List<Operation> results = OperationHelper.setupOperationList(3);

		List<OperationDTO> operationDTOs = operationMapper.map2DTOList(results);

		when(operationBrowserManagerMock.getOperation())
			.thenReturn(results);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(new ObjectMapper().writeValueAsString(operationDTOs))));
	}

	@Test
	void deleteOperation_shouldReturnTrue() throws Exception {
		String request = "/operations/{code}";
		Operation deleteOperation = OperationHelper.setup();
		OperationDTO body = operationMapper.map2DTO(deleteOperation);
		String code = body.getCode();

		when(operationBrowserManagerMock.getOperationByCode(code))
			.thenReturn(OperationHelper.setup());

		String isDeleted = "true";
		this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(isDeleted)));
	}
}
