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
package org.isf.lab.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.lab.data.LaboratoryHelper;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryForPrintMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.mapper.LaboratoryRowMapper;
import org.isf.lab.model.Laboratory;
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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class LaboratoryControllerTest {
	
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LaboratoryControllerTest.class);

	@Mock
	protected LabManager labBrowserManagerMock;
	@Mock
    protected LabManager laboratoryManager;

	@Mock
    protected ExamBrowsingManager examManager;

	@Mock
    private PatientBrowserManager patientBrowserManager;

	@Mock
    private LaboratoryMapper laboratoryMapper;

	@Mock
    private LaboratoryRowMapper laboratoryRowMapper;

	@Mock
    private LaboratoryForPrintMapper laboratoryForPrintMapper;
	
	protected LaboratoryMapper labMapper = new LaboratoryMapper();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new LaboratoryController(laboratoryManager, patientBrowserManager, examManager, laboratoryMapper, laboratoryRowMapper, laboratoryForPrintMapper))
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
		
		Laboratory lab = LaboratoryHelper.setup();
		ArrayList<String> labRows =new ArrayList<String>();
		labRows.add("good");
		labRows.add("material");
		LaboratoryDTO body = laboratoryMapper.map2DTO(lab);
		when(laboratoryManager.newLaboratory(laboratoryMapper.map2Model(body), labRows));
		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}
	
	@Test
	public void testUpdateLaboratory_201() throws Exception {
		String request = "/laboratories/{code}";
		Integer code = 5;
		
		Laboratory lab = LaboratoryHelper.setup();
		ArrayList<String> labRows =new ArrayList<String>();
		labRows.add("lab");
		labRows.add("material");
		lab.setCode(code);
		LaboratoryDTO body = laboratoryMapper.map2DTO(lab);
		when(laboratoryManager.updateLaboratory(laboratoryMapper.map2Model(body), labRows));
		MvcResult result = this.mockMvc
				.perform(put(request, code)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}
	
	@Test
	public void testGetLaboratory_201() throws Exception {
		String request = "/laboratories/byPatientId/{patId}";
		Integer patId = 5;
		
		Patient patient =PatientHelper.setup();
		when(patientBrowserManager.getPatientById(patId)).thenReturn(patient);
		List<Laboratory> labList = LaboratoryHelper.genArrayList(4);
		when(laboratoryManager.getLaboratory(patient)).thenReturn(labList);
		MvcResult result = this.mockMvc
				.perform(get(request,patId)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(laboratoryMapper.map2DTOList(labList).toString())))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
