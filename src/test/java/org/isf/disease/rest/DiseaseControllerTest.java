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
package org.isf.disease.rest;

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

import org.isf.disease.data.DiseaseHelper;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
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

public class DiseaseControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DiseaseControllerTest.class);

	@Mock
	private DiseaseBrowserManager diseaseBrowserManagerMock;

	private DiseaseMapper diseaseMapper = new DiseaseMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new DiseaseController(diseaseBrowserManagerMock, diseaseMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseMapper, "modelMapper", modelMapper);
	}

	@Test
	public void testGetDiseasesOpd_200() throws Exception {
		String request = "/diseases/opd";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd())
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesOpdByCode_200() throws Exception {
		String request = "/diseases/opd/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd(typeCode))
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request, typeCode))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdOut_200() throws Exception {
		String request = "/diseases/ipd/out";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut())
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdOutByCode_200() throws Exception {
		String request = "/diseases/ipd/out/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request, typeCode))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdIn_200() throws Exception {

		String request = "/diseases/ipd/in";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdIn())
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesIpdInByCode_200() throws Exception {
		String request = "/diseases/ipd/out/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request, typeCode))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseases_200() throws Exception {
		String request = "/diseases/both";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease())
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseasesString_200() throws Exception {
		String request = "/diseases/both/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease(typeCode))
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request, typeCode))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);

	}

	@Test
	public void testGetAllDiseases_200() throws Exception {
		String request = "/diseases/all";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseAll())
				.thenReturn(diseases);

		MvcResult result = this.mockMvc
				.perform(get(request))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetDiseaseByCode() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		Disease disease = DiseaseHelper.setup();
		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
				.thenReturn(disease);

		MvcResult result = this.mockMvc
				.perform(get(request, code))
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTO(disease)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testNewDisease_200() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
				.thenReturn(false);

		when(diseaseBrowserManagerMock.descriptionControl(disease.getDescription(), disease.getType().getCode()))
				.thenReturn(false);

		when(diseaseBrowserManagerMock.newDisease(disease))
				.thenReturn(disease);

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isCreated())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateDisease_201() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
				.thenReturn(true);

		when(diseaseBrowserManagerMock.updateDisease(disease))
				.thenReturn(disease);

		MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteDisease() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
				.thenReturn(disease);

		when(diseaseBrowserManagerMock.deleteDisease(disease))
				.thenReturn(true);

		MvcResult result = this.mockMvc
				.perform(delete(request, code)
						.contentType(MediaType.APPLICATION_JSON)
						.content(DiseaseHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))

				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
