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
package org.isf.vactype.rest;

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
import org.isf.vactype.data.VaccineTypeHelper;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.mapper.VaccineTypeMapper;
import org.isf.vactype.model.VaccineType;
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

public class VaccineTypeControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(VaccineTypeControllerTest.class);

	@Mock
	protected VaccineTypeBrowserManager vaccineTypeBrowserManagerMock;

	protected VaccineTypeMapper vaccineTypeMapper = new VaccineTypeMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new VaccineTypeController(vaccineTypeBrowserManagerMock, vaccineTypeMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(vaccineTypeMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetVaccineType_200() throws Exception {
		String request = "/vaccinetype";

		List<VaccineType> vaccinesTypeList = VaccineTypeHelper.setupVaccineList(4);

		when(vaccineTypeBrowserManagerMock.getVaccineType())
				.thenReturn(vaccinesTypeList);

		List<VaccineTypeDTO> expectedVaccineTypeDTOs = vaccineTypeMapper.map2DTOList(vaccinesTypeList);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(VaccineTypeHelper.getObjectMapper().writeValueAsString(expectedVaccineTypeDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewVaccineType_200() throws Exception {
		String request = "/vaccinetype";
		String code = "ZZ";
		VaccineTypeDTO body = vaccineTypeMapper.map2DTO(VaccineTypeHelper.setup(code));

		VaccineType vaccineTypeModel = vaccineTypeMapper.map2Model(body);
		when(vaccineTypeBrowserManagerMock.newVaccineType(vaccineTypeMapper.map2Model(body)))
				.thenReturn(vaccineTypeModel);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VaccineTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateVaccineType_200() throws Exception {
		String request = "/vaccinetype";
		String code = "ZZ";
		VaccineTypeDTO body = vaccineTypeMapper.map2DTO(VaccineTypeHelper.setup(code));

		VaccineType vaccineTypeModel = vaccineTypeMapper.map2Model(body);
		when(vaccineTypeBrowserManagerMock.updateVaccineType(vaccineTypeMapper.map2Model(body)))
				.thenReturn(vaccineTypeModel);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VaccineTypeHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteVaccineType_200() throws Exception {
		String request = "/vaccinetype/{code}";
		String basecode = "0";

		VaccineType vaccineType = VaccineTypeHelper.setup(basecode);
		VaccineTypeDTO body = vaccineTypeMapper.map2DTO(vaccineType);
		String code = body.getCode();

		when(vaccineTypeBrowserManagerMock.findVaccineType(code))
				.thenReturn(vaccineType);

		when(vaccineTypeBrowserManagerMock.deleteVaccineType(vaccineTypeMapper.map2Model(body)))
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
	public void testCheckVaccineTypeCode_200() throws Exception {
		String request = "/vaccinetype/check/{code}";

		String code = "AA";
		VaccineType vaccineType = VaccineTypeHelper.setup(code);

		when(vaccineTypeBrowserManagerMock.isCodePresent(vaccineType.getCode()))
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

}
