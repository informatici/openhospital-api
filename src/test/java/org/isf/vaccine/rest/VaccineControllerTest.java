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
package org.isf.vaccine.rest;

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
import org.isf.vaccine.data.VaccineHelper;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.mapper.VaccineMapper;
import org.isf.vaccine.model.Vaccine;
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

public class VaccineControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(VaccineControllerTest.class);

	@Mock
	protected VaccineBrowserManager vaccineBrowserManagerMock;

	protected VaccineMapper vaccineMapper = new VaccineMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new VaccineController(vaccineBrowserManagerMock, vaccineMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(vaccineMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetVaccines_200() throws Exception {
		String request = "/vaccines";

		String code = "AA";
		List<Vaccine> vaccinesList = VaccineHelper.setupVaccineList(4);

		when(vaccineBrowserManagerMock.getVaccine())
				.thenReturn(vaccinesList);

		List<VaccineDTO> expectedVaccineDTOs = vaccineMapper.map2DTOList(vaccinesList);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(VaccineHelper.getObjectMapper().writeValueAsString(expectedVaccineDTOs))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetVaccinesByVaccineTypeCode_200() throws Exception {
		String request = "/vaccines/type-code/{vaccineTypeCode}";

		List<Vaccine> vaccinesList = VaccineHelper.setupVaccineList(4);
		String vaccineTypeCode = vaccinesList.get(0).getVaccineType().getCode();

		when(vaccineBrowserManagerMock.getVaccine(vaccineTypeCode))
				.thenReturn(vaccinesList);

		MvcResult result = this.mockMvc
				.perform(get(request, vaccineTypeCode))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(VaccineHelper.getObjectMapper().writeValueAsString(vaccineMapper.map2DTOList(vaccinesList))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewVaccine_201() throws Exception {
		String request = "/vaccines";
		String code = "ZZ";
		Vaccine vaccine = VaccineHelper.setup(code);
		VaccineDTO body = vaccineMapper.map2DTO(vaccine);

		when(vaccineBrowserManagerMock.newVaccine(vaccineMapper.map2Model(body)))
				.thenReturn(vaccine);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VaccineHelper.asJsonString(body))
				)
				.andDo(log())
				//.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();
		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateVaccine_200() throws Exception {
		String request = "/vaccines";
		String code = "ZZ";
		Vaccine vaccine = VaccineHelper.setup(code);
		VaccineDTO body = vaccineMapper.map2DTO(vaccine);

		when(vaccineBrowserManagerMock.updateVaccine(vaccineMapper.map2Model(body)))
				.thenReturn(vaccine);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(VaccineHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteVaccine_200() throws Exception {
		String request = "/vaccines/{code}";
		String basecode = "0";

		Vaccine vaccine = VaccineHelper.setup(basecode);
		VaccineDTO body = vaccineMapper.map2DTO(vaccine);
		String code = body.getCode();

		when(vaccineBrowserManagerMock.findVaccine(code))
				.thenReturn(vaccine);

		when(vaccineBrowserManagerMock.deleteVaccine(vaccineMapper.map2Model(body)))
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
	public void testCheckVaccineCode_200() throws Exception {
		String request = "/vaccines/check/{code}";

		String code = "AA";
		Vaccine vaccine = VaccineHelper.setup(code);

		when(vaccineBrowserManagerMock.isCodePresent(vaccine.getCode()))
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
