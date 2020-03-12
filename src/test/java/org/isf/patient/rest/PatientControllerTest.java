/**
 * 
 */
package org.isf.patient.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
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
	 * Test method for {@link org.isf.patient.rest.PatientController#PatientController(org.isf.patient.manager.PatientBrowserManager)}.
	 * @throws Exception 
	 */
	@Test
	public void testPatientControllerTest() throws Exception {
		fail("Not yet implemented");
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
		
		//when(patientBrowserManagerMock.getPatient(any(String.class))).thenReturn(null);
		
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
		Patient	newPatient = PatientDTOHelper.setupPatient();
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
		Patient	newPatient = PatientDTOHelper.setupPatient();
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
		
		Integer code= 12345;
		String request = "/patients/{code}";
		PatientDTO newPatientDTO =  PatientDTOHelper.setup();
		newPatientDTO.setCode(code);
		Patient	newPatient = PatientDTOHelper.setupPatient();
		newPatient.setCode(code);
		
		when(patientBrowserManagerMock.updatePatient(any(Patient.class))).thenReturn(false);
					
		this.mockMvc
		.perform(
				put(request, code)
				.contentType(MediaType.APPLICATION_JSON)
				.content(PatientDTOHelper.asJsonString(newPatientDTO).getBytes()))
		.andDo(print())
		.andExpect(status().is4xxClientError())
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("")));
		//.andExpect(status().isNoContent());  //TODO is not better Not Found than an exception as well for the GET than Non_Content?

		
	}
	
	
	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatients(java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	public void testGetPatients() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#getPatient(java.lang.Integer)}.
	 */
	@Test
	public void testGetPatient() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#searchPatient(java.lang.String, java.lang.Integer)}.
	 */
	@Test
	public void testSearchPatient() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.isf.patient.rest.PatientController#deletePatient(int)}.
	 */
	@Test
	public void testDeletePatient() {
		fail("Not yet implemented");
	}

	
	static class PatientDTOHelper{
		public static PatientDTO setup() throws OHException{
			Patient patient = setupPatient();
			return OHModelMapper.getObjectMapper().map(patient, PatientDTO.class);
		}
		
		public static Patient setupPatient() throws OHException{
			return  new TestPatient().setup(true);
		}
		
		public static String asJsonString(PatientDTO patientDTO){
			try {
				return new ObjectMapper().writeValueAsString(patientDTO);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
}
