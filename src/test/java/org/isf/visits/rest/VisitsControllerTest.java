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
package org.isf.visits.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.visits.data.VisitHelper;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.mapper.VisitMapper;
import org.isf.visits.model.Visit;
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

public class VisitsControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(VisitsControllerTest.class);

	@Mock
	protected VisitManager visitManagerMock;

	protected VisitMapper visitMapper = new VisitMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new VisitsController(visitManagerMock, visitMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(visitMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetVisit_200() throws Exception {
		String request = "/visit/{patID}";

		int patID = 0;
		List<Visit> visitsList = VisitHelper.setupVisitList(4);

		when(visitManagerMock.getVisits(patID))
				.thenReturn(visitsList);

		List<VisitDTO> expectedVisitsDTOs = visitMapper.map2DTOList(visitsList);

		MvcResult result = this.mockMvc
				.perform(get(request, patID))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(VisitHelper.getObjectMapper().writeValueAsString(expectedVisitsDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewVisit_201() throws Exception {
		String request = "/visit";
		int id = 1;
		VisitDTO body = visitMapper.map2DTO(VisitHelper.setup(id));

		when(visitManagerMock.newVisit(visitMapper.map2Model(body)))
				.thenReturn(visitMapper.map2Model(body));

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VisitHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewVisits_201() throws Exception {
		String request = "/visits";

		List<Visit> visitsList = VisitHelper.setupVisitList(4);

		List<VisitDTO> body = visitMapper.map2DTOList(visitsList);

		Boolean isCreated = true;
		when(visitManagerMock.newVisits(visitsList))
				.thenReturn(isCreated);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VisitHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString(isCreated.toString())))
				.andReturn();
		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteVisitsRelatedToPatient_200() throws Exception {
		String request = "/visit/{patId}";

		int id = 1;

		Boolean isDeleted = true;
		when(visitManagerMock.deleteAllVisits(id))
				.thenReturn(isDeleted);

		MvcResult result = this.mockMvc
				.perform(delete(request, id))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(isDeleted.toString())))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
