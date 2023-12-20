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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.lab.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.lab.data.LaboratoryHelper;
import org.isf.lab.dto.LabWithRowsDTO;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryForPrintMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.mapper.LaboratoryRowMapper;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.junit.Before;
import org.junit.Test;
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

public class LaboratoryControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryControllerTest.class);

	@Mock
	protected LabManager laboratoryManager;

	@Mock
	protected ExamBrowsingManager examManager;

	@Mock
	private PatientBrowserManager patientBrowserManager;

	protected LaboratoryMapper laboratoryMapper = new LaboratoryMapper();

	protected LaboratoryRowMapper laboratoryRowMapper = new LaboratoryRowMapper();

	protected LaboratoryForPrintMapper laboratoryForPrintMapper = new LaboratoryForPrintMapper();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
						.standaloneSetup(new LaboratoryController(laboratoryManager, patientBrowserManager, examManager, laboratoryMapper, laboratoryRowMapper,
										laboratoryForPrintMapper))
						.setControllerAdvice(new OHResponseEntityExceptionHandler())
						.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(laboratoryMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testNewLaboratory_201() throws Exception {
		String request = "/laboratories";

		LabWithRowsDTO labWithRowsDTO = new LabWithRowsDTO();
		Laboratory lab = LaboratoryHelper.setup();
		lab.getExam().setCode(Double.valueOf(Math.random()).toString());
		Patient patient = PatientHelper.setup();
		patient.setCode(Double.valueOf(Math.random()).intValue());

		lab.setPatient(patient);
		List<String> labRows = new ArrayList<>();
		labRows.add("good");
		labRows.add("material");

		LaboratoryDTO body = laboratoryMapper.map2DTO(lab);
		labWithRowsDTO.setLaboratoryDTO(body);
		labWithRowsDTO.setLaboratoryRowList(labRows);

		when(laboratoryManager.newLaboratory(any(Laboratory.class), anyList())).thenReturn(lab);
		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(examManager.getExams()).thenReturn(Collections.singletonList(lab.getExam()));

		MvcResult result = this.mockMvc
						.perform(post(request)
										.content(LaboratoryHelper.asJsonString(labWithRowsDTO))
										.contentType(MediaType.APPLICATION_JSON))
						.andDo(log())
						.andExpect(status().is2xxSuccessful())
						.andExpect(status().isCreated())
						.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateLaboratory_200() throws Exception {
		String request = "/laboratories/{code}";

		LabWithRowsDTO labWithRowsDTO = new LabWithRowsDTO();
		Laboratory lab = LaboratoryHelper.setup();
		lab.setCode(Double.valueOf(Math.random()).intValue());
		lab.getExam().setCode(Double.valueOf(Math.random()).toString());
		lab.setStatus(LaboratoryStatus.open.toString());
		Patient patient = PatientHelper.setup();
		patient.setCode(Double.valueOf(Math.random()).intValue());

		lab.setPatient(patient);
		List<String> labRows = new ArrayList<>();
		labRows.add("good");
		labRows.add("material");

		LaboratoryDTO body = laboratoryMapper.map2DTO(lab);
		labWithRowsDTO.setLaboratoryDTO(body);
		labWithRowsDTO.setLaboratoryRowList(labRows);

		when(laboratoryManager.newLaboratory(any(Laboratory.class), anyList())).thenReturn(lab);
		when(laboratoryManager.getLaboratory(anyInt())).thenReturn(Optional.of(lab));
		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(examManager.getExams()).thenReturn(Collections.singletonList(lab.getExam()));

		MvcResult result = this.mockMvc
						.perform(put(request, lab.getCode())
										.content(LaboratoryHelper.asJsonString(labWithRowsDTO))
										.contentType(MediaType.APPLICATION_JSON))
						.andDo(log())
						.andExpect(status().is2xxSuccessful())
						.andExpect(status().isOk())
						.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetLaboratory_200() throws Exception {
		String request = "/laboratories/byPatientId/{patId}";

		LabWithRowsDTO labWithRowsDTO = new LabWithRowsDTO();
		Laboratory lab = LaboratoryHelper.setup();
		lab.setCode(Double.valueOf(Math.random()).intValue());
		lab.getExam().setCode(Double.valueOf(Math.random()).toString());
		lab.getExam().setProcedure(1);
		lab.setStatus(LaboratoryStatus.done.toString());
		Patient patient = PatientHelper.setup();
		patient.setCode(Double.valueOf(Math.random()).intValue());

		lab.setPatient(patient);
		List<String> labRows = new ArrayList<>();
		labRows.add("good");
		labRows.add("material");

		LaboratoryDTO body = laboratoryMapper.map2DTO(lab);
		labWithRowsDTO.setLaboratoryDTO(body);
		labWithRowsDTO.setLaboratoryRowList(labRows);

		when(laboratoryManager.newLaboratory(any(Laboratory.class), anyList())).thenReturn(lab);
		when(laboratoryManager.getLaboratory(anyInt())).thenReturn(Optional.of(lab));
		when(laboratoryManager.getLaboratory(any(Patient.class))).thenReturn(Collections.singletonList(lab));
		when(patientBrowserManager.getPatientById(anyInt())).thenReturn(patient);
		when(examManager.getExams()).thenReturn(Collections.singletonList(lab.getExam()));
		MvcResult result = this.mockMvc
						.perform(get(request, patient.getCode())
										.contentType(MediaType.APPLICATION_JSON))
						.andDo(log())
						.andExpect(status().is2xxSuccessful())
						.andExpect(status().isOk())
						.andExpect(jsonPath("$[0].laboratoryDTO.exam.code").value(lab.getExam().getCode()))
						.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
