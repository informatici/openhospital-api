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
package org.isf.profession.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
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

import org.isf.profession.data.ProfessionHelper;
import org.isf.profession.dto.ProfessionDTO;
import org.isf.profession.manager.ProfessionBrowserManager;
import org.isf.profession.mapper.ProfessionMapper;
import org.isf.profession.model.Profession;
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

class ProfessionControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionControllerTest.class);

	@Mock
	protected ProfessionBrowserManager professionBrowserManager;

	protected ProfessionMapper professionMapper = new ProfessionMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new ProfessionController(professionBrowserManager, professionMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(professionMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetProfessions_200() throws Exception {
		String request = "/professions";

		List<Profession> results = ProfessionHelper.setupProfessionList(3);

		List<ProfessionDTO> parsedResults = professionMapper.map2DTOList(results);

		when(professionBrowserManager.getProfessions())
			.thenReturn(results);

		MvcResult result = this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(ProfessionHelper.getObjectMapper().writeValueAsString(parsedResults))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testNewProfession_201() throws Exception {
		String request = "/professions";
		int code = 123;
		Profession profession = ProfessionHelper.setup(code);
		ProfessionDTO body = professionMapper.map2DTO(profession);

		when(professionBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(false);

		when(professionBrowserManager.newProfession(professionMapper.map2Model(body)))
			.thenReturn(profession);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ProfessionHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateProfession_200() throws Exception {
		int code = 456;

		Profession profession = ProfessionHelper.setup(code);
		ProfessionDTO body = professionMapper.map2DTO(profession);
		String request = "/professions";

		when(professionBrowserManager.isCodePresent(body.getCode()))
			.thenReturn(true);

		when(professionBrowserManager.updateProfession(professionMapper.map2Model(body)))
			.thenReturn(profession);

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(ProfessionHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testDeleteProfession_200() throws Exception {
		String request = "/professions/{code}";

		ProfessionDTO body = professionMapper.map2DTO(ProfessionHelper.setup(0));
		String code = body.getCode();

		when(professionBrowserManager.getProfession(anyString()))
			.thenReturn(ProfessionHelper.setupProfessionList(1).get(0));

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

}
