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
package org.isf.disease.rest;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.isf.disease.data.DiseaseHelper;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

class DiseaseControllerTest {

	@Mock
	private DiseaseBrowserManager diseaseBrowserManagerMock;

	private final DiseaseMapper diseaseMapper = new DiseaseMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new DiseaseController(diseaseBrowserManagerMock, diseaseMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(diseaseMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void getDiseasesOpd_shouldReturnOpdDiseases() throws Exception {
		String request = "/diseases/opd";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd())
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseasesOpdByCode_shouldReturnOpdDiseasesOfType() throws Exception {
		String request = "/diseases/opd/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseOpd(typeCode))
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseasesIpdOut_shouldReturnIpdOutDiseases() throws Exception {
		String request = "/diseases/ipd/out";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut())
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseasesIpdOutByCode_shouldReturnIpdOutDiseasesOfType() throws Exception {
		String request = "/diseases/ipd/out/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseasesIpdIn_shouldReturnIpdInDiseases() throws Exception {

		String request = "/diseases/ipd/in";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdIn())
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseasesIpdInByCode_shouldReturnIpdInDiseasesOfType() throws Exception {
		String request = "/diseases/ipd/in/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseIpdIn(typeCode))
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseases_shouldReturnOpdAndIpdDiseases() throws Exception {
		String request = "/diseases/both";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease())
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseases_shouldReturnOpdAndIpdDiseasesOfType() throws Exception {
		String request = "/diseases/both/{typecode}";

		String typeCode = "1";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDisease(typeCode))
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request, typeCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));

	}

	@Test
	void getAllDiseases_shouldReturnAllDiseases() throws Exception {
		String request = "/diseases/all";

		List<Disease> diseases = DiseaseHelper.setupDiseaseList(3);
		when(diseaseBrowserManagerMock.getDiseaseAll())
			.thenReturn(diseases);

		this.mockMvc
			.perform(get(request))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTOList(diseases))));
	}

	@Test
	void getDiseaseByCode_shouldReturnDisease() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		Disease disease = DiseaseHelper.setup();
		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(disease);

		this.mockMvc
			.perform(get(request, code))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(diseaseMapper.map2DTO(disease))));
	}

	@Test
	void newDisease_shouldReturnCreatedDisease() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(false);

		when(diseaseBrowserManagerMock.descriptionControl(disease.getDescription(), disease.getType().getCode()))
			.thenReturn(false);

		when(diseaseBrowserManagerMock.newDisease(disease))
			.thenReturn(disease);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(body)));
	}

	@Test
	void updateDisease_shouldReturnUpdatedDisease() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(true);

		when(diseaseBrowserManagerMock.updateDisease(disease))
			.thenReturn(disease);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(DiseaseHelper.getObjectMapper().writeValueAsString(body)));
	}

	@Test
	void deleteDisease_shouldReturnTrue_whenCodeExists() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(disease);

		this.mockMvc
			.perform(delete(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
;
	}

	@Test
	void getDiseasesOpd_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/opd";

		when(diseaseBrowserManagerMock.getDiseaseOpd())
			.thenReturn(null);

		this.mockMvc
			.perform(get(request).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting OPD diseases."));
	}

	@Test
	void getDiseasesOpdByCode_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/opd/{typecode}";

		String typeCode = "1";

		when(diseaseBrowserManagerMock.getDiseaseOpd(typeCode))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, typeCode).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting OPD diseases."));
	}

	@Test
	void getDiseasesIpdOut_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/ipd/out";

		when(diseaseBrowserManagerMock.getDiseaseIpdOut())
			.thenReturn(null);

		this.mockMvc
			.perform(get(request).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting IPD out diseases."));
	}

	@Test
	void getDiseasesIpdOutByCode_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/ipd/out/{typecode}";

		String typeCode = "1";

		when(diseaseBrowserManagerMock.getDiseaseIpdOut(typeCode))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, typeCode).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting IPD out diseases."));
	}

	@Test
	void getDiseasesIpdIn_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/ipd/in";

		when(diseaseBrowserManagerMock.getDiseaseIpdIn())
			.thenReturn(null);

		this.mockMvc
			.perform(get(request).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting IPD-in diseases."));
	}

	@Test
	void getDiseasesIpdInByCode_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/ipd/in/{typecode}";

		String typeCode = "1";

		when(diseaseBrowserManagerMock.getDiseaseIpdIn(typeCode))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, typeCode).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting IPD-in diseases."));
	}

	@Test
	void getDiseases_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/both";

		when(diseaseBrowserManagerMock.getDisease())
			.thenReturn(null);

		this.mockMvc
			.perform(get(request).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting diseases."));
	}

	@Test
	void getDiseases_shouldReturnInternalServerError_whenManagerReturnsNullForType() throws Exception {
		String request = "/diseases/both/{typecode}";

		String typeCode = "1";

		when(diseaseBrowserManagerMock.getDisease(typeCode))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, typeCode).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting diseases by type code."));
	}

	@Test
	void getAllDiseases_shouldReturnInternalServerError_whenManagerReturnsNull() throws Exception {
		String request = "/diseases/all";

		when(diseaseBrowserManagerMock.getDiseaseAll())
			.thenReturn(null);

		this.mockMvc
			.perform(get(request).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Error getting all diseases."));
	}

	@Test
	void getDiseaseByCode_shouldReturnNotFound_whenCodeDoesNotExist() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(null);

		this.mockMvc
			.perform(get(request, code).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("No disease found with the specified code."));
	}

	@Test
	void newDisease_shouldReturnBadRequest_whenCodeAlreadyExists() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(true);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Duplicated disease code."));
	}

	@Test
	void newDisease_shouldReturnBadRequest_whenDescriptionAlreadyExistsForType() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(false);

		when(diseaseBrowserManagerMock.descriptionControl(disease.getDescription(), disease.getType().getCode()))
			.thenReturn(true);

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Duplicated disease description for the same disease type."));
	}

	@Test
	void newDisease_shouldReturnInternalServerError_whenCreationFails() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(false);

		when(diseaseBrowserManagerMock.descriptionControl(disease.getDescription(), disease.getType().getCode()))
			.thenReturn(false);

		when(diseaseBrowserManagerMock.newDisease(any(Disease.class)))
			.thenThrow(new OHServiceException(new OHExceptionMessage("Failure")));

		this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Disease not created."));
	}

	@Test
	void updateDisease_shouldReturnNotFound_whenCodeDoesNotExist() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(false);

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Disease not found."));
	}

	@Test
	void updateDisease_shouldReturnInternalServerError_whenUpdateFails() throws Exception {
		String request = "/diseases";

		Disease disease = DiseaseHelper.setup();
		DiseaseDTO body = diseaseMapper.map2DTO(disease);

		when(diseaseBrowserManagerMock.isCodePresent(disease.getCode()))
			.thenReturn(true);

		when(diseaseBrowserManagerMock.updateDisease(any(Disease.class)))
			.thenThrow(new OHServiceException(new OHExceptionMessage("Failure")));

		this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(DiseaseHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("Disease not updated."));
	}

	@Test
	void deleteDisease_shouldReturnNotFound_whenCodeDoesNotExist() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(null);

		this.mockMvc
			.perform(delete(request, code).accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("No disease found with the specified code."));
	}

	@Test
	void deleteDisease_shouldReturnFalse_whenDeletionFails() throws Exception {
		String request = "/diseases/{code}";

		String code = "999";

		Disease disease = DiseaseHelper.setup();
		when(diseaseBrowserManagerMock.getDiseaseByCode(code))
			.thenReturn(disease);

		doThrow(new OHServiceException(new OHExceptionMessage("Failure")))
			.when(diseaseBrowserManagerMock).deleteDisease(any(Disease.class));

		this.mockMvc
			.perform(delete(request, code))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}

}
