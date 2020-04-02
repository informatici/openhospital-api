package org.isf.patient.rest;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.OHModelMapper;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author ecastaneda1
 *
 */

public class PatientControllerTest {
	private final Logger logger = LoggerFactory.getLogger(PatientControllerTest.class);

	@Mock
    private PatientBrowserManager patientBrowserManagerMock;
	
    private MockMvc mockMvc;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new PatientController(patientBrowserManagerMock))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    }
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_post_patients_is_call_without_contentType_header_then_HttpMediaTypeNotSupportedException() throws Exception {
		String request = "/patients";
		
		MvcResult result = this.mockMvc
			.perform(post(request).content(new byte[]{'a', 'b', 'c'}))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(content().string(anyOf(nullValue(), equalTo(""))))
			.andReturn();
		
		Optional<HttpMediaTypeNotSupportedException> exception = Optional.ofNullable((HttpMediaTypeNotSupportedException) result.getResolvedException());
		logger.debug("exception: {}", exception);
		exception.ifPresent( (se) -> assertThat(se, notNullValue()));
		exception.ifPresent( (se) -> assertThat(se, instanceOf(HttpMediaTypeNotSupportedException.class)));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
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
		logger.debug("exception: {}", exception);
		exception.ifPresent( (se) -> assertThat(se, notNullValue()));
		exception.ifPresent( (se) -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_post_patients_PatientBrowserManager_getPatient_returns_null_then_OHAPIException_BadRequest() throws Exception {
		String request = "/patients";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		
		when(patientBrowserManagerMock.getPatient(any(String.class))).thenReturn(null);
		
		MvcResult result = this.mockMvc
			.perform(
				post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(PatientDTOHelper.asJsonString(newPatientDTO))
			)
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) //TODO Create OHCreateAPIException
			.andExpect(content().string(containsString("Patient is not created!")))
			.andReturn();
		
		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_post_patients_PatientBrowserManager_newPatient_returns_false_then_OHAPIException_BadRequest() throws Exception {
		Integer code= 12345;
		String request = "/patients";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		
		when(patientBrowserManagerMock.newPatient(any(Patient.class))).thenReturn(false);
		
		MvcResult result = this.mockMvc
			.perform(post(request)
			.contentType(MediaType.APPLICATION_JSON)
			.content(PatientDTOHelper.asJsonString(newPatientDTO)))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) //TODO Create OHCreateAPIException
			.andExpect(content().string(containsString("Patient is not created!")))
			.andReturn();
		
		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_post_patients_and_both_calls_to_PatientBrowserManager_success_then_Created() throws Exception {
		Integer code= 12345;
		String request = "/patients";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		Patient	newPatient = PatientHelper.setupPatient();
		newPatient.setCode(code);
		
		when(patientBrowserManagerMock.newPatient(any(Patient.class))).thenReturn(true);
		when(patientBrowserManagerMock.getPatient(any(String.class))).thenReturn(newPatient);
		
		this.mockMvc
			.perform(
					post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(PatientDTOHelper.asJsonString(newPatientDTO)))
			.andDo(log())
			.andExpect(status().isCreated())
			.andExpect(content().string(containsString(code.toString())));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_put_update_patient_with_valid_body_and_existent_code_then_OK() throws Exception {
		Integer code= 12345;
		String request = "/patients/{code}";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		Patient	newPatient = PatientHelper.setupPatient();
		newPatient.setCode(code);
	
		when(patientBrowserManagerMock.updatePatient(any(Patient.class))).thenReturn(true);
				
		this.mockMvc
			.perform(
					put(request, code)
					.contentType(MediaType.APPLICATION_JSON)
					.content(PatientDTOHelper.asJsonString(newPatientDTO)))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(code.toString())));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_put_update_patient_with_invalid_body_and_existent_code_then_HttpMessageNotReadableException_BadRequest() throws Exception {
		Integer code= 12345;
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
		logger.debug("oHAPIException: {}", exception);
		exception.ifPresent( (se) -> assertThat(se, notNullValue()));
		exception.ifPresent( (se) -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}
		
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_put_update_patient_with_valid_body_and_unexistent_code_then_OHAPIException_BadRequest() throws Exception {
		Integer code= 12345;
		String request = "/patients/{code}";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		Patient	newPatient = PatientHelper.setupPatient();
		newPatient.setCode(code);
		
		when(patientBrowserManagerMock.updatePatient(any(Patient.class))).thenReturn(false);
		
		MvcResult result = this.mockMvc
				.perform(put(request, code).contentType(MediaType.APPLICATION_JSON)
						.content(PatientDTOHelper.asJsonString(newPatientDTO)))
				.andDo(log())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) //TODO Create OHUpdateAPIException
				.andExpect(content().string(containsString("Patient is not updated!"))).andReturn();

		//TODO Create OHUpdateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatients(java.lang.Integer, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void whet_get_patients_non_parameters_then_return_list_of_PatientDTO_page_0_default_size_and_OK() throws Exception {
		String request = "/patients";
		
		int expectedPageSize = Integer.parseInt(PatientController.DEFAULT_PAGE_SIZE);
		
		ArrayList<Patient> patientList = PatientHelper.setupPatientList(expectedPageSize);
		
        List<PatientDTO> expectedPatienDTOList = patientList.stream().map(it -> getObjectMapper().map(it, PatientDTO.class)).collect(Collectors.toList());

	
		when(patientBrowserManagerMock.getPatient(any(Integer.class),any(Integer.class)))
			.thenReturn(patientList);
				
		this.mockMvc
			.perform(
					get(request)
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientDTOHelper.asJsonString(expectedPatienDTOList))))
			.andReturn();
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatient(java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_with_existent_code_then_response_PatientDTO_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);
		
		this.mockMvc
			.perform(
					get(request, code)
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientDTOHelper.asJsonString(expectedPatientDTO))))
			.andReturn();
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_search_with_existent_name_non_code_then_response_PatientDTO_and_OK() throws Exception {
		Integer code = 456;
		String name = "TestFirstName";
		String request = "/patients/search";
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(name))).thenReturn(patient);
		
		this.mockMvc
			.perform(
					get(request)
					.param("name", name)
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientDTOHelper.asJsonString(expectedPatientDTO))))
			.andReturn();
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_search_without_name_and_existent_code_then_response_PatientDTO_and_OK() throws Exception {
		Integer code = 678;
		String request = "/patients/search";
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);
		
		this.mockMvc
			.perform(
					get(request)
					.param("code", code.toString())
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(PatientDTOHelper.asJsonString(expectedPatientDTO))))
			.andReturn();
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_search_without_name_and_unexistent_code_then_response_null_and_NO_Content() throws Exception {
		Integer code = 1000;
		String request = "/patients/search";
		
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(null);
		
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
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_search_without_name_and_witout_code_then_response_null_and_NO_Content() throws Exception {
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
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void when_get_patients_search_with_unexistent_name_and_witout_code_then_response_null_and_NO_Content() throws Exception {
		String name = "unexistent_name";
		String request = "/patients/search";
				
		when(patientBrowserManagerMock.getPatient(eq(name))).thenReturn(null);
		
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
	 * @throws Exception 
	 */
	@Test
	public void when_delete_patients_with_existent_code_then_response_true_and_OK() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(eq(patient))).thenReturn(true);
		
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
	 * @throws Exception 
	 */
	@Test
	public void when_delete_patients_with_unexistent_code_then_response_Not_Found() throws Exception {
		Integer code = 111;
		String request = "/patients/{code}";
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(null);
		
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
	 * @throws Exception 
	 */
	@Test
	public void when_delete_patients_with_existent_code_but_fail_deletion_then_OHAPIException_BadRequest() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(eq(patient))).thenReturn(false);
		
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
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	static class PatientHelper{
		
		public static Patient setupPatient() throws OHException{
			return  new TestPatient().setup(true);
		}
		
		public static ArrayList<Patient> setupPatientList(int size) {
			return (ArrayList<Patient>) IntStream.range(1, size+1)
					.mapToObj(i -> {	Patient ep = null;
										try {
											ep = PatientHelper.setupPatient();
											ep.setCode(i);
										} catch (OHException e) {
											e.printStackTrace();
										}
										return ep;
									}
					).collect(Collectors.toList());
		}
	}
	
	static class PatientDTOHelper{
		public static PatientDTO setup() throws OHException{
			Patient patient = PatientHelper.setupPatient();
			return OHModelMapper.getObjectMapper().map(patient, PatientDTO.class);
		}
		
		public static String asJsonString(PatientDTO patientDTO){
			try {
				return new ObjectMapper().writeValueAsString(patientDTO);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static String asJsonString(List<PatientDTO> patientDTOList){
			try {
				return new ObjectMapper().writeValueAsString(patientDTOList);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
