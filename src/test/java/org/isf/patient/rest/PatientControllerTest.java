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
package org.isf.patient.rest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.patadminissue.dto.PatientAdminIssueDTO;
import org.isf.patadminissue.manager.PatientAdminIssueBrowserManager;
import org.isf.patadminissue.mapper.PatientAdminIssueMapper;
import org.isf.patadminissue.model.PatientAdminIssue;
import org.isf.patconsensus.manager.PatientConsensusBrowserManager;
import org.isf.patconsensus.model.PatientConsensus;
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
import org.isf.shared.mapper.mappings.PatientMapping;
import org.isf.shared.pagination.Page;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
class PatientControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientControllerTest.class);

	@Mock
	private PatientBrowserManager patientBrowserManagerMock;

	@Mock
	private AdmissionBrowserManager admissionBrowserManagerMock;

	@Mock
	private PatientConsensusBrowserManager patientConsensusManagerMock;

	@Mock
	private PatientAdminIssueBrowserManager patientAdminIssueManagerMock;

	private final PatientMapper patientMapper = new PatientMapper();

	private final PatientAdminIssueMapper patientAdminIssueMapper = new PatientAdminIssueMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new PatientController(patientBrowserManagerMock, admissionBrowserManagerMock, patientMapper,
				patientConsensusManagerMock, patientAdminIssueManagerMock, patientAdminIssueMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
		ModelMapper modelMapper = new ModelMapper();
		PatientMapping.addMapping(modelMapper);
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(patientMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(patientAdminIssueMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	/**
	 * Test method for {@link PatientController#newPatient(PatientDTO)}.
	 *
	 * @throws Exception When error occurs
	 */
	@Test
	void when_post_patients_is_call_without_contentType_header_then_HttpMediaTypeNotSupportedException() throws Exception {
		String request = "/patients";

		MvcResult result = this.mockMvc
			.perform(post(request).content(new byte[] { 'a', 'b', 'c' }))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isUnsupportedMediaType())
			// TODO .andExpect(content().string(anyOf(nullValue(), equalTo(""))))
			.andExpect(content().string(containsString("Unsupported Media Type")))
			.andReturn();

		Optional<HttpMediaTypeNotSupportedException> exception = Optional.ofNullable((HttpMediaTypeNotSupportedException) result.getResolvedException());
		LOGGER.debug("exception: {}", exception);
		exception.ifPresent(se -> assertThat(se, notNullValue()));
		exception.ifPresent(se -> assertThat(se, instanceOf(HttpMediaTypeNotSupportedException.class)));
	}

	/**
	 * Test method for {@link PatientController#newPatient(PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_post_patients_is_call_with_empty_body_then_BadRequest_HttpMessageNotReadableException() throws Exception {
		String request = "/patients";
		String empty_body = "";

		MvcResult result = this.mockMvc
			.perform(
				post(request)
					.content(empty_body.getBytes())
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			// TODO .andExpect(content().string(anyOf(nullValue(), equalTo(""))))
			.andExpect(content().string(containsString("Failed to read request")))
			.andReturn();

		Optional<HttpMessageNotReadableException> exception = Optional.ofNullable((HttpMessageNotReadableException) result.getResolvedException());
		LOGGER.debug("exception: {}", exception);
		exception.ifPresent(se -> assertThat(se, notNullValue()));
		exception.ifPresent(se -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}

	/**
	 * Test method for {@link PatientController#newPatient(PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_post_patients_PatientBrowserManager_getPatient_returns_null_then_OHAPIException_BadRequest() throws Exception {
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);

		when(patientBrowserManagerMock.getPatientById(anyInt())).thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(
				post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(PatientHelper.asJsonString(newPatientDTO)))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // TODO Create OHCreateAPIException
			.andExpect(content().string(containsString("Patient not created.")))
			.andReturn();

		// TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link PatientController#newPatient(PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_post_patients_PatientBrowserManager_newPatient_returns_false_then_Created() throws Exception {
		Integer code = 12345;
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);

		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(patientMapper.map2Model(newPatientDTO)); // TODO: verify if it's correct

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(PatientHelper.asJsonString(newPatientDTO)))
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(code.toString())))
			.andReturn();

		// TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link PatientController#newPatient(PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_post_patients_and_both_calls_to_PatientBrowserManager_success_then_Created() throws Exception {
		Integer code = 12345;
		String request = "/patients";
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);
		Patient newPatient = PatientHelper.setup();
		newPatient.setCode(code);

		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(newPatient);
		when(patientBrowserManagerMock.getPatientById(anyInt())).thenReturn(newPatient);

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
	 * Test method for {@link PatientController#updatePatient(int, PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_put_update_patient_with_valid_body_and_existent_code_then_BadRequest() throws Exception {
		String request = "/patients/{code}";

		Integer code = 12345;
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);

		Patient updatedAfterReadPatient = PatientHelper.setup();
		updatedAfterReadPatient.setCode(code);
		updatedAfterReadPatient.setLock(3);

		PatientConsensus newPatientConsensus = new PatientConsensus();
		newPatientConsensus.setConsensusFlag(true);
		newPatientConsensus.setPatient(updatedAfterReadPatient);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(updatedAfterReadPatient);

		when(patientConsensusManagerMock.getPatientConsensusByUserId(code)).thenReturn(Optional.ofNullable(newPatientConsensus));

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
			.andExpect(content().string(containsString("Patient not updated.")));

	}

	/**
	 * Test method for {@link PatientController#updatePatient(int, PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_put_update_patient_with_invalid_body_and_existent_code_then_HttpMessageNotReadableException_BadRequest() throws Exception {
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
	 * Test method for {@link PatientController#updatePatient(int, PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_put_update_patient_with_valid_body_and_unexistent_code_then_OHAPIException_BadRequest() throws Exception {
		String request = "/patients/{code}";

		Integer code = 123;
		PatientDTO newPatientDTO = PatientHelper.setup(patientMapper);
		newPatientDTO.setCode(code);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(put(request, code).contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(PatientHelper.asJsonString(newPatientDTO))))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isNotFound()) // TODO Create OHUpdateAPIException
			.andExpect(content().string(containsString("Patient not found."))).andReturn();

		// TODO Create OHUpdateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}

	/**
	 * Test method for {@link PatientController#getPatients(int, int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_non_parameters_then_return_list_of_PatientDTO_page_0_default_size_and_OK() throws Exception {
		String request = "/patients";

		int expectedPageSize = Integer.parseInt(PatientController.DEFAULT_PAGE_SIZE);

		List<Patient> patientList = PatientHelper.setupPatientList(expectedPageSize);

		PagedResponse<Patient> patientPageable = new PagedResponse<>();
		patientPageable.setData(patientList);
		patientPageable.setPageInfo(PatientHelper.setParameterPage());
		Page<PatientDTO> expectedPatientDTOList = new Page<>();
		expectedPatientDTOList.setData(patientMapper.map2DTOList(patientList));
		expectedPatientDTOList.getData().forEach(patientDTO -> patientDTO.setAdministrativeIssues(List.of()));
		expectedPatientDTOList.setPageInfo(patientMapper.setParameterPageInfo(patientPageable.getPageInfo()));
		when(patientBrowserManagerMock.getPatientsPageable(anyInt(), anyInt()))
			.thenReturn(patientPageable);

		this.mockMvc
			.perform(get(request)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTOList.getData()))))
			.andReturn();

	}

	/**
	 * Test method for {@link PatientController#getPatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_with_existent_code_and_not_admitted_then_response_PatientDTO_and_OK() throws Exception {
		int code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);

		PatientDTO expectedPatientDTO = patientMapper.map2DTO(patient);
		expectedPatientDTO.setStatus(PatientSTATUS.O);
		expectedPatientDTO.setAdministrativeIssues(List.of());

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		when(admissionBrowserManagerMock.getCurrentAdmission(patient)).thenReturn(null);

		this.mockMvc
			.perform(
				get(request, code)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTO))))
			.andReturn();

	}

	/**
	 * Test method for {@link PatientController#getPatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_with_existent_code_and_admitted_then_response_PatientDTO_and_OK() throws Exception {
		int code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		Admission admission = AdmissionHelper.setup();
		patient.setCode(code);
		admission.setPatient(patient);

		PatientDTO expectedPatientDTO = patientMapper.map2DTO(patient);
		expectedPatientDTO.setStatus(PatientSTATUS.I);
		expectedPatientDTO.setAdministrativeIssues(List.of());

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		when(admissionBrowserManagerMock.getCurrentAdmission(patient)).thenReturn(admission);

		this.mockMvc
			.perform(
				get(request, code)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTO))))
			.andReturn();

	}

	/**
	 * Test method for
	 * {@link PatientController#searchPatient(String, String, java.time.LocalDateTime, String)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_search_without_name_and_unexistent_code_then_response_null_and_NO_Content() throws Exception {
		Integer code = 1000;
		String request = "/patients/search";

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		this.mockMvc
			.perform(
				get(request)
					.param("code", code.toString())
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk());
	}

	/**
	 * Test method for
	 * {@link PatientController#searchPatient(String, String, java.time.LocalDateTime, String)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_search_without_name_and_without_code_then_response_null_and_NO_Content() throws Exception {
		String request = "/patients/search";

		this.mockMvc
			.perform(
				get(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk());
	}

	/**
	 * Test method for
	 * {@link PatientController#searchPatient(String, String, java.time.LocalDateTime, String)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_search_with_unexistent_name_and_without_code_then_response_null_and_NO_Content() throws Exception {
		String name = null;
		String request = "/patients/search";

		when(patientBrowserManagerMock.getPatientById(null)).thenReturn(null);

		this.mockMvc
			.perform(
				get(request)
					.param("name", name)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk());
	}

	/**
	 * Test method for {@link PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_delete_patients_with_existent_code_then_response_true_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		this.mockMvc
			.perform(
				delete(request, code)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("true")));
	}

	/**
	 * Test method for {@link PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_delete_patients_with_unexistent_code_then_response_Not_Found() throws Exception {
		Integer code = 111;
		String request = "/patients/{code}";

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(null);

		this.mockMvc
			.perform(
				delete(request, code)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	/**
	 * Test method for {@link PatientController#deletePatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_delete_patients_with_existent_code_but_fail_deletion_then_OHAPIException_BadRequest() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);

		doThrow(new OHServiceException(new OHExceptionMessage("Failure"))).when(patientBrowserManagerMock).deletePatient(patient);

		MvcResult result = this.mockMvc
			.perform(
				delete(request, code)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // TODO Create OHDeleteAPIException
			.andExpect(content().string(containsString("Patient not deleted.")))
			.andReturn();

		// TODO Create OHDeleteAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		LOGGER.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent(se -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent(se -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	/**
	 * Test method for
	 * {@link PatientController#getPatientByCodes(List<Integer>)}.
	 *
	 * @throws Exception
	 */
	@Test
    void when_get_patients_with_the_list_of_existing_code_then_return_list_of_PatientDTO() throws Exception {
        String request = "/patients/by-codes";
        List<Patient> patientList = PatientHelper.setupPatientList(5);
        
        List<Integer> codes = new ArrayList<Integer>();
        
        for (Patient patient: patientList) {
        	codes.add(patient.getCode());
        }

        when(patientBrowserManagerMock.getPatientByCodes(anyList())).thenReturn(patientList);
        List<PatientDTO> expectedPatientDTOList = patientMapper.map2DTOList(patientList);
        expectedPatientDTOList.forEach(patientDTO -> patientDTO.setAdministrativeIssues(List.of()));

        this.mockMvc
            .perform(post(request)
                .content(codes.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(log())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(PatientHelper.asJsonString(expectedPatientDTOList))));
    }

	/**
	 * Test method for {@link PatientController#getPatient(int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patient_with_open_administrative_issues_then_the_PatientDTO_carries_them() throws Exception {
		int code = 123;
		String request = "/patients/{code}";
		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		PatientAdminIssue openIssue = new PatientAdminIssue(patient, "Identity document still to be verified");
		openIssue.setId(7);
		openIssue.setFromDate(LocalDateTime.of(2026, 1, 15, 18, 49, 57));

		List<PatientAdminIssueDTO> expectedIssues = patientAdminIssueMapper.map2DTOList(List.of(openIssue));

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patient);
		when(admissionBrowserManagerMock.getCurrentAdmission(patient)).thenReturn(null);
		when(patientAdminIssueManagerMock.getOpenIssues(code)).thenReturn(List.of(openIssue));

		this.mockMvc
			.perform(get(request, code).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.administrativeIssues", hasSize(1)))
			.andExpect(jsonPath("$.administrativeIssues[0].id").value(7))
			.andExpect(jsonPath("$.administrativeIssues[0].reason").value("Identity document still to be verified"))
			.andExpect(content().string(containsString("\"administrativeIssues\":" + PatientHelper.asJsonString(expectedIssues))));
	}

	/**
	 * Test method for {@link PatientController#getPatientByCodes(List)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_post_patients_by_codes_then_the_open_issues_are_read_once_and_grouped_by_patient() throws Exception {
		String request = "/patients/by-codes";
		Patient twiceFlagged = PatientHelper.setup();
		twiceFlagged.setCode(1);
		Patient onceFlagged = PatientHelper.setup();
		onceFlagged.setCode(2);
		Patient clear = PatientHelper.setup();
		clear.setCode(3);
		PatientAdminIssue firstIssue = new PatientAdminIssue(twiceFlagged, "Registration form still to be signed");
		firstIssue.setFromDate(LocalDateTime.of(2026, 1, 15, 18, 53, 17));
		PatientAdminIssue secondIssue = new PatientAdminIssue(twiceFlagged, "Identity document still to be verified");
		secondIssue.setFromDate(LocalDateTime.of(2026, 2, 1, 9, 0, 0));
		PatientAdminIssue otherIssue = new PatientAdminIssue(onceFlagged, "Referral letter missing from the file");
		otherIssue.setFromDate(LocalDateTime.of(2026, 2, 2, 9, 0, 0));

		when(patientBrowserManagerMock.getPatientByCodes(List.of(1, 2, 3))).thenReturn(List.of(twiceFlagged, onceFlagged, clear));
		when(patientAdminIssueManagerMock.getOpenIssues(List.of(1, 2, 3))).thenReturn(List.of(firstIssue, secondIssue, otherIssue));

		this.mockMvc
			.perform(post(request).contentType(MediaType.APPLICATION_JSON).content("[1, 2, 3]"))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].code").value(1))
			.andExpect(jsonPath("$[0].administrativeIssues", hasSize(2)))
			.andExpect(jsonPath("$[0].administrativeIssues[0].reason").value("Registration form still to be signed"))
			.andExpect(jsonPath("$[0].administrativeIssues[1].reason").value("Identity document still to be verified"))
			.andExpect(jsonPath("$[1].code").value(2))
			.andExpect(jsonPath("$[1].administrativeIssues", hasSize(1)))
			.andExpect(jsonPath("$[1].administrativeIssues[0].reason").value("Referral letter missing from the file"))
			.andExpect(jsonPath("$[2].code").value(3))
			.andExpect(jsonPath("$[2].administrativeIssues", hasSize(0)));

		verify(patientAdminIssueManagerMock, times(1)).getOpenIssues(anyCollection());
		verify(patientAdminIssueManagerMock, never()).getOpenIssues(anyInt());
	}

	/**
	 * Test method for {@link PatientController#getPatients(int, int)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_get_patients_returns_an_empty_page_then_no_open_issue_is_read() throws Exception {
		String request = "/patients";
		PagedResponse<Patient> emptyPage = new PagedResponse<>();
		emptyPage.setData(List.of());
		emptyPage.setPageInfo(PatientHelper.setParameterPage());

		when(patientBrowserManagerMock.getPatientsPageable(anyInt(), anyInt())).thenReturn(emptyPage);

		this.mockMvc
			.perform(get(request).contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data", hasSize(0)));

		verify(patientAdminIssueManagerMock, never()).getOpenIssues(anyCollection());
	}

	/**
	 * Test method for {@link PatientController#updatePatient(int, PatientDTO)}.
	 *
	 * @throws Exception
	 */
	@Test
	void when_put_patient_with_client_supplied_administrative_issues_then_they_are_ignored() throws Exception {
		String request = "/patients/{code}";
		Integer code = 12345;
		PatientDTO updatePatientDTO = PatientHelper.setup(patientMapper);
		updatePatientDTO.setCode(code);
		PatientAdminIssueDTO forgedIssue = new PatientAdminIssueDTO();
		forgedIssue.setReason("Forged by the client");
		updatePatientDTO.setAdministrativeIssues(List.of(forgedIssue));

		Patient patientRead = PatientHelper.setup();
		patientRead.setCode(code);
		PatientConsensus patientConsensus = new PatientConsensus();
		patientConsensus.setConsensusFlag(true);
		patientConsensus.setPatient(patientRead);

		when(patientBrowserManagerMock.getPatientById(code)).thenReturn(patientRead);
		when(patientConsensusManagerMock.getPatientConsensusByUserId(code)).thenReturn(Optional.of(patientConsensus));
		when(patientBrowserManagerMock.savePatient(any(Patient.class))).thenReturn(patientRead);

		this.mockMvc
			.perform(put(request, code).contentType(MediaType.APPLICATION_JSON).content(PatientHelper.asJsonString(updatePatientDTO)))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.administrativeIssues", hasSize(0)));

		verify(patientAdminIssueManagerMock, never()).openIssue(any());
		verify(patientAdminIssueManagerMock, never()).saveIssues(anyList(), anyList());
	}
}
