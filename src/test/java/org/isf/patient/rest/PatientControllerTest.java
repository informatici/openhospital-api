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
package org.isf.patient.rest;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.dto.PatientSTATUS;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpMediaTypeNotSupportedException;

/**
 * @author ecastaneda1
 */
public class PatientControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatientControllerTest.class);

	@Mock
	private PatientBrowserManager patientBrowserManagerMock;
	
	@Mock
	private AdmissionBrowserManager admissionBrowserManagerMock;

	private PatientMapper patientMapper = new PatientMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new PatientController(patientBrowserManagerMock, admissionBrowserManagerMock, patientMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(patientMapper, "modelMapper", modelMapper);
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_post_patients_is_call_without_contentType_header_then_HttpMediaTypeNotSupportedException() throws Exception {
		String request = "/patients";

		MvcResult result = this.mockMvc
				.perform(post(request).content(new byte[] { 'a', 'b', 'c' }))
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isUnsupportedMediaType())
				.andExpect(content().string(anyOf(nullValue(), equalTo(""))))
				.andReturn();

		Optional<HttpMediaTypeNotSupportedException> exception = Optional.ofNullable((HttpMediaTypeNotSupportedException) result.getResolvedException());
		LOGGER.debug("exception: {}", exception);
		exception.ifPresent(se -> assertThat(se, notNullValue()));
		exception.ifPresent(se -> assertThat(se, instanceOf(HttpMediaTypeNotSupportedException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_post_patients_is_call_with_empty_body_then_BadRequest_HttpMessageNotReadableException() throws Exception {
		String request = "/patients";
		String empty_body = "";

		MvcResult result = this.mockMvc
				.perform(
						post(request)
								.content(empty_body.getBytes())
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(anyOf(nullValue(), equalTo(""))))
				.andReturn();

		Optional<HttpMessageNotReadableException> exception = Optional.ofNullable((HttpMessageNotReadableException) result.getResolvedException());
		LOGGER.debug("exception: {}", exception);
		exception.ifPresent(se -> assertThat(se, notNullValue()));
		exception.ifPresent(se -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_post_patients_PatientBrowserManager_getPatient_returns_null_then_OHAPIException_BadRequest() throws Exception {
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);

		when(patientBrowserManagerMock.getPatientByName(any(String.class))).thenReturn(null);  //FIXME: why we were searching by name?

		MvcResult result = this.mockMvc
				.perform(
						post(request)
								.contentType(MediaType.APPLICATION_JSON)
								.content(PatientHelper.asJsonString(newPatientDTO))
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) //TODO Create OHCreateAPIException
				.andExpect(content().string(containsString("Patient is not created!")))
				.andReturn();

		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_post_patients_PatientBrowserManager_newPatient_returns_false_then_Created() throws Exception {
		Integer code = 12345;
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);

		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(patientMapper.map2Model(newPatientDTO)); //TODO: verify if it's correct

		MvcResult result = this.mockMvc
				.perform(post(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(PatientHelper.asJsonString(newPatientDTO)))
				.andDo(log())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString(code.toString())))
				.andReturn();

		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_post_patients_and_both_calls_to_PatientBrowserManager_success_then_Created() throws Exception {
		Integer code = 12345;
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);
		Patient newPatient = PatientHelper.setup();
		newPatient.setCode(code);

		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(newPatient);
		when(patientBrowserManagerMock.getPatientByName(any(String.class))).thenReturn(newPatient);

		this.mockMvc
				.perform(
						post(request)
								.contentType(MediaType.APPLICATION_JSON)
								.content(PatientHelper.asJsonString(newPatientDTO)))
				.andDo(log())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString(code.toString())));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_put_update_patient_with_valid_body_and_existent_code_then_BadRequest() throws Exception {
		String request = "/patients/{code}";
		
		Integer code = 12345;
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);
		
		Patient updatedAfterReadPatient = PatientHelper.setup();
		updatedAfterReadPatient.setCode(code);
		updatedAfterReadPatient.setLock(3);
		
		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(updatedAfterReadPatient);

		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(null);

		this.mockMvc
				.perform(
						put(request, code)
								.contentType(MediaType.APPLICATION_JSON)
								.content(PatientHelper.asJsonString(newPatientDTO)))
				.andDo(log())
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Patient is not updated!")));

	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_put_update_patient_with_invalid_body_and_existent_code_then_HttpMessageNotReadableException_BadRequest() throws Exception {
		Integer code = 12345;
		String request = "/patients/{code}";

		MvcResult result = this.mockMvc
				.perform(
						put(request, code)
								.contentType(MediaType.APPLICATION_JSON)
								.content(new byte[3]))
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest())
				.andReturn();

		Optional<HttpMessageNotReadableException> exception = Optional.ofNullable((HttpMessageNotReadableException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", exception);
		exception.ifPresent(se -> assertThat(se, notNullValue()));
		exception.ifPresent(se -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_put_update_patient_with_valid_body_and_unexistent_code_then_OHAPIException_BadRequest() throws Exception {
		String request = "/patients/{code}";

		Integer code = 123;
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);
		
		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		MvcResult result = this.mockMvc
				.perform(put(request, code).contentType(MediaType.APPLICATION_JSON)
						.content(PatientHelper.asJsonString(newPatientDTO)))
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) //TODO Create OHUpdateAPIException
				.andExpect(content().string(containsString("Patient not found!"))).andReturn();

		//TODO Create OHUpdateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatients(java.lang.Integer, java.lang.Integer)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void whet_get_patients_non_parameters_then_return_list_of_PatientDTO_page_0_default_size_and_OK() throws Exception {
		String request = "/patients";

		int expectedPageSize = Integer.parseInt(PatientController.DEFAULT_PAGE_SIZE);

		List<Patient> patientList = PatientHelper.setupPatientList(expectedPageSize);

		List<PatientDTO> expectedPatientDTOList = patientMapper.map2DTOList(patientList);

		when(patientBrowserManagerMock.getPatient(any(Integer.class), any(Integer.class)))
				.thenReturn(patientList);

		this.mockMvc
				.perform(
						get(request)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTOList))))
				.andReturn();
		
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatient(java.lang.Integer)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_get_patients_with_existent_code_and_not_admitted_then_response_PatientDTO_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		
		PatientDTO expectedPatientDTO = patientMapper.map2DTO(patient);
		expectedPatientDTO.setStatus(PatientSTATUS.O);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);
		
		when(admissionBrowserManagerMock.getCurrentAdmission(patient)).thenReturn(null);
		
		this.mockMvc
				.perform(
						get(request, code)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTO))))
				.andReturn();
				
		
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatient(java.lang.Integer)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_get_patients_with_existent_code_and_admitted_then_response_PatientDTO_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		Admission admission = AdmissionHelper.setup();
		patient.setCode(code);
		admission.setPatient(patient);
		
		PatientDTO expectedPatientDTO = patientMapper.map2DTO(patient);
		expectedPatientDTO.setStatus(PatientSTATUS.I);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);
		
		when(admissionBrowserManagerMock.getCurrentAdmission(patient)).thenReturn(admission);
		
		this.mockMvc
				.perform(
						get(request, code)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTO))))
				.andReturn();
				
		
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception
	 */
	@Test
	public void when_get_patients_search_without_name_and_unexistent_code_then_response_null_and_NO_Content() throws Exception {
		Integer code = 1000;
		String request = "/patients/search";

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		this.mockMvc
				.perform(
						get(request)
								.param("code", code.toString())
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isNoContent());
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception
	 */
	@Test
	public void when_get_patients_search_without_name_and_without_code_then_response_null_and_NO_Content() throws Exception {
		String request = "/patients/search";

		this.mockMvc
				.perform(
						get(request)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isNoContent());
	}


	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception
	 */
	@Test
	public void when_get_patients_search_with_unexistent_name_and_without_code_then_response_null_and_NO_Content() throws Exception {
		String name = "unexistent_name";
		String request = "/patients/search";

		when(patientBrowserManagerMock.getPatientByName(name)).thenReturn(null);

		this.mockMvc
				.perform(
						get(request)
								.param("name", name)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isNoContent());
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_delete_patients_with_existent_code_then_response_true_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(patient)).thenReturn(true);

		this.mockMvc
				.perform(
						delete(request, code)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_delete_patients_with_unexistent_code_then_response_Not_Found() throws Exception {
		Integer code = 111;
		String request = "/patients/{code}";

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		this.mockMvc
				.perform(
						delete(request, code)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isNotFound());
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void when_delete_patients_with_existent_code_but_fail_deletion_then_OHAPIException_BadRequest() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(patient)).thenReturn(false);

		MvcResult result = this.mockMvc
				.perform(
						delete(request, code)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) //TODO Create OHDeleteAPIException
				.andExpect(content().string(containsString("Patient is not deleted!")))
				.andReturn();

		//TODO Create OHDeleteAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

}
