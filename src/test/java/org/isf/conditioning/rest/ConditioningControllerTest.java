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
package org.isf.conditioning.rest;

import org.isf.conditioning.data.ConditioningHelper;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.shared.mapper.mappings.PatientMapping;
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

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConditioningControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditioningControllerTest.class);

	@Mock
	protected ConditioningBrowserManager conBrowserManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	@Mock
	protected UserBrowsingManager userBrowsingManagerMock;

	private final ConditioningMapper conditioningMapper = new ConditioningMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new ConditioningController(conBrowserManagerMock, conditioningMapper, userBrowsingManagerMock,
				patientBrowserManagerMock))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		PatientMapping.addMapping(modelMapper);
		ReflectionTestUtils.setField(conditioningMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewConditioning_success() throws Exception {
		String request = "/conditionings";

		ConditioningDTO body = ConditioningHelper.setup(conditioningMapper);
		Conditioning conditioning = conditioningMapper.map2Model(body);

		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode()))
			.thenReturn(conditioning.getPatient());
		when(userBrowsingManagerMock.isUserNamePresent(body.getPerformBy().getUserName()))
			.thenReturn(true);
		when(conBrowserManagerMock.newConditioning(any(Conditioning.class)))
			.thenReturn(conditioning);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ConditioningHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testNewConditioning_patientNotFound() throws Exception {
		String request = "/conditionings";

		Conditioning conditioning = ConditioningHelper.setup();
		ConditioningDTO body = conditioningMapper.map2DTO(conditioning);

		when(patientBrowserManagerMock.getPatientById(conditioning.getPatient().getCode()))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ConditioningHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetConditioningByPatientCode_success() throws Exception {
		int patientCode = 1;
		String request = "/conditionings/{patientCode}";

		Patient patient = PatientHelper.setup();
		patient.setCode(patientCode);
		when(patientBrowserManagerMock.getPatientById(patientCode))
			.thenReturn(patient);

		List<Conditioning> conditionings = ConditioningHelper.setupConditioningList(2);
		when(conBrowserManagerMock.getConditioningByPatientCode(patientCode))
			.thenReturn(conditionings);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(ConditioningHelper.asJsonString(conditioningMapper.map2DTOList(conditionings)))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetConditioningByPatientCode_notFound() throws Exception {
		int patientCode = 1;
		String request = "/conditionings/{patientCode}";

		when(patientBrowserManagerMock.getPatientById(patientCode))
			.thenReturn(null);
		when(conBrowserManagerMock.getConditioningByPatientCode(patientCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateConditioning_success() throws Exception {
		int id = 1;
		String request = "/conditionings/{id}";

		Conditioning conditioning = ConditioningHelper.setup();
		conditioning.setId(id);
		ConditioningDTO body = conditioningMapper.map2DTO(conditioning);

		when(conBrowserManagerMock.getConditioningById(id)).thenReturn(conditioning);

		when(userBrowsingManagerMock.isUserNamePresent(body.getPerformBy().getUserName())).thenReturn(true);

		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode())).thenReturn(conditioning.getPatient());

		when(conBrowserManagerMock.updateConditioning(any(Conditioning.class))).thenReturn(conditioning);

		MvcResult result = this.mockMvc
			.perform(put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ConditioningHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateConditioning_notFound() throws Exception {
		int id = 1;
		String request = "/conditionings/{id}";

		Conditioning conditioning = ConditioningHelper.setup();
		conditioning.setId(id);
		ConditioningDTO body = conditioningMapper.map2DTO(conditioning);

		when(conBrowserManagerMock.getConditioningById(id)).thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ConditioningHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
