/**
 * 
 */
package org.isf.patient.rest;

import static org.hamcrest.Matchers.containsString;
import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
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
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ecastaneda1
 *
 */

public class PatientControllerTest {
	private final Logger logger = LoggerFactory.getLogger(PatientControllerTest.class);

	@Mock
    private PatientBrowserManager patientBrowserManagerMock = Mockito.mock(PatientBrowserManager.class);

    private MockMvc mockMvc;

    @Before
    public void setup() {
    	Mockito.reset(patientBrowserManagerMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new PatientController(patientBrowserManagerMock)).build();
    }
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_new_patient_is_call_with_short_wrong_body_then_UnsupportedMediatype() throws Exception {
		assertNotNull(patientBrowserManagerMock);
		
		String request = "/patients";
		
		this.mockMvc
			.perform(post(request).content(new byte[]{'a', 'b', 'c'}))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(content().string(containsString("")));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_new_patient_is_call_with_long_wrong_body_then_BadRequest() throws Exception {
		assertNotNull(patientBrowserManagerMock);
		
		String request = "/patients";
		PatientDTO newPatient =  new PatientDTO();
		
		when(patientBrowserManagerMock.newPatient(any(Patient.class))).thenReturn(true);
		
		this.mockMvc
			.perform(post(request).content(newPatient.getBlobPhoto()))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("")));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_new_patient_PatientBrowserManager_getPatient_returns_null_then_BadRequest() throws Exception {
		assertNotNull(patientBrowserManagerMock);
		
		String request = "/patients";
		PatientDTO newPatient =  new PatientDTO();
		
		when(patientBrowserManagerMock.getPatient(any(String.class))).thenReturn(null);
		
		this.mockMvc
			.perform(post(request).content(newPatient.getBlobPhoto()))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("")));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_new_patient_PatientBrowserManager_newPatient_returns_false_then_BadRequest() throws Exception {
		assertNotNull(patientBrowserManagerMock);
		
		String request = "/patients";
		PatientDTO newPatient =  new PatientDTO();
		
		when(patientBrowserManagerMock.newPatient(any(Patient.class))).thenReturn(false);
		
		this.mockMvc
			.perform(post(request).content(newPatient.getBlobPhoto()))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("")));
	}
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#newPatient(org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_new_patient_and_both_calls_to_PatientBrowserManager_success_then_Created() throws Exception {
		assertNotNull(patientBrowserManagerMock);
		
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
					.content(PatientDTOHelper.asJsonString(newPatientDTO).getBytes()))
			.andDo(print())
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
				.content(PatientDTOHelper.asJsonString(newPatientDTO).getBytes()))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(code.toString())));
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_put_update_patient_with_invalid_body_and_existent_code_then_BadRequest() throws Exception {
		
		Integer code= 12345;
		String request = "/patients/{code}";
					
		MvcResult result = this.mockMvc
		.perform(
				put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new byte[3]))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andReturn();
		
		String content = result.getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#updatePatient(int, org.isf.patient.dto.PatientDTO)}.
	 * @throws Exception 
	 */
	@Test
	public void when_put_update_patient_with_valid_body_and_unexistent_code_then_Exception() throws Exception {
		//TODO
		logger.debug(" ---- > Having issues capturoing the exception returned");
		
		Integer code= 12345;
		String request = "/patients/{code}";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		Patient	newPatient = PatientHelper.setupPatient();
		newPatient.setCode(code);
		
		when(patientBrowserManagerMock.updatePatient(any(Patient.class))).thenReturn(false);
					
		this.mockMvc = MockMvcBuilders.standaloneSetup(patientBrowserManagerMock)
                .setControllerAdvice(withExceptionControllerAdvice())
               .build();
		
		MvcResult result = this.mockMvc
				.perform(put(request, code).contentType(MediaType.APPLICATION_JSON)
						.content(PatientDTOHelper.asJsonString(newPatientDTO).getBytes()))
				//.andDo(print()).andExpect(status().is4xxClientError()).andExpect(status().isBadRequest())
				.andDo(print()).andExpect(status().is4xxClientError()).andExpect(status().isNotFound())
				// .andExpect(status().isNoContent()); //TODO is not better Not Found than an
				// exception as well for the GET than Non_Content?
				.andExpect(content().string(containsString(""))).andReturn();
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		
		logger.debug("oHAPIException: {}", oHAPIException);
		
		//oHAPIException.ifPresent( (se) -> assertThat(se, is(notNullValue())));
		//oHAPIException.ifPresent( (se) -> assertThat(se, is(instanceOf(OHAPIException.class))));
	}
	
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatients(java.lang.Integer, java.lang.Integer)}.
	 * @throws Exception 
	 */
	@Test
	public void whet_get_patients_non_parameters_then_return_list_of_PatientDTO_page_0_default_size_and_OK() throws Exception {
		String request = "/patients";
		Integer code = 0;
		
		int expectedPageSize = Integer.parseInt(PatientController.DEFAULT_PAGE_SIZE);
		
		Patient	expectedPatient = PatientHelper.setupPatient();
		expectedPatient.setCode(code);
		
		ArrayList<Patient> patientList = PatientHelper.setupPatientList(expectedPageSize);
		
        List<PatientDTO> expectedPatienDTOList = patientList.stream().map(it -> getObjectMapper().map(it, PatientDTO.class)).collect(Collectors.toList());

	
		when(patientBrowserManagerMock.getPatient(any(Integer.class),any(Integer.class)))
			.thenReturn(patientList);
				
		this.mockMvc
		.perform(
				get(request)
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andDo(print())
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
		.andDo(print())
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
		.andDo(print())
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
		.andDo(print())
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
		.andDo(print())
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
		.andDo(print())
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
		.andDo(print())
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
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(eq(patient))).thenReturn(true);
		
		this.mockMvc
		.perform(
				delete(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				)		
		.andDo(print())
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
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(null);
		
		this.mockMvc
		.perform(
				delete(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				)		
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#deletePatient(int)}.
	 * @throws Exception 
	 */
	@Test
	public void when_delete_patients_with_existent_code_but_fail_deletion_then_Exception() throws Exception {
		Integer code = 123;
		String request = "/patients/{code}";
		PatientDTO expectedPatientDTO =  PatientDTOHelper.setup();
		expectedPatientDTO.setCode(code);
		Patient	patient = PatientHelper.setupPatient();
		patient.setCode(code);
				
		when(patientBrowserManagerMock.getPatient(eq(code))).thenReturn(patient);

		when(patientBrowserManagerMock.deletePatient(eq(patient))).thenReturn(false);
		
		this.mockMvc
		.perform(
				delete(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				)		
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Patient is not deleted!")));
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
	
	
	private ExceptionHandlerExceptionResolver withExceptionControllerAdvice() {
	    final ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
	        @Override
	        protected ServletInvocableHandlerMethod getExceptionHandlerMethod(final HandlerMethod handlerMethod,
	            final Exception exception) {
	            Method method = new ExceptionHandlerMethodResolver(OHResponseEntityExceptionHandler.class).resolveMethod(exception);
	            if (method != null) {
	                return new ServletInvocableHandlerMethod(new OHResponseEntityExceptionHandler(), method);
	            }
	            return super.getExceptionHandlerMethod(handlerMethod, exception);
	        }
	    };
	    exceptionResolver.afterPropertiesSet();
	    return exceptionResolver;
	}
	
}
