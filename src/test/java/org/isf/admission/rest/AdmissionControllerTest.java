package org.isf.admission.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.admission.mapper.AdmittedPatientMapper;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.ward.manager.WardBrowserManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdmissionControllerTest {
	private final Logger logger = LoggerFactory.getLogger(AdmissionControllerTest.class);

	@Mock
	private AdmissionBrowserManager admissionManagerMock;

	@Mock
	private PatientBrowserManager patientManagerMock;

	@Mock
	private WardBrowserManager wardManagerMock;

	@Mock
	private DiseaseBrowserManager diseaseManagerMock;

	@Mock
	private OperationBrowserManager operationManagerMock;

	@Mock
	private PregnantTreatmentTypeBrowserManager pregTraitTypeManagerMock;

	@Mock
	private DeliveryTypeBrowserManager dlvrTypeManagerMock;

	@Mock
	private DeliveryResultTypeBrowserManager dlvrrestTypeManagerMock;
	
	@Autowired
	private AdmissionMapper admissionMapper = new AdmissionMapper();
	
	@Autowired 
	private AdmittedPatientMapper admittedMapper= new AdmittedPatientMapper();
	
	
	private MockMvc mockMvc;
		
	@Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new  AdmissionController(admissionManagerMock ,patientManagerMock ,wardManagerMock,
						 diseaseManagerMock, operationManagerMock, pregTraitTypeManagerMock, 
						 dlvrTypeManagerMock, dlvrrestTypeManagerMock,	admissionMapper, 
						 admittedMapper))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    	ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(admissionMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(admittedMapper, "modelMapper", modelMapper);
    }

	@Test
	public void testGetAdmissions_200() throws Exception {
		String request = "/admissions/{id}";
		Integer id = 1;
	
		Admission admission = AdmissionHelper.setup(id);
		when(admissionManagerMock.getAdmission(id))
			.thenReturn(admission);
		
		MvcResult result = this.mockMvc
			.perform(get(request,id)
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetCurrentAdmission_200() throws Exception {
		String request = "/admissions/current?patientcode={patientCode}";
		Integer patientCode = 1;
	
		Patient patient = new TestPatient().setup(true); // TODO refactor one PatientHelper class is a independent class
		when(patientManagerMock.getPatient(patientCode))
			.thenReturn(patient);
		
		Integer id = 0;
		Admission admission = AdmissionHelper.setup(id);
		when(admissionManagerMock.getCurrentAdmission(patient))
		.thenReturn(admission);
		
		MvcResult result = this.mockMvc
			.perform(get(request,patientCode)
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testGetAdmittedPatients_200() throws Exception {
		String request = "/admissions/admittedPatients?searchterms={}";
		Integer patientCode = 1;
	
		Patient patient = new TestPatient().setup(true); // TODO refactor one PatientHelper class is a independent class
		when(patientManagerMock.getPatient(patientCode))
			.thenReturn(patient);
		
		Integer id = 0;
		Admission admission = AdmissionHelper.setup(id);
		when(admissionManagerMock.getCurrentAdmission(patient))
		.thenReturn(admission);
		
		ArrayList<AdmittedPatient> amittedPatients = PatientHelper.setupAdmittedPatientList(2);
	
		GregorianCalendar[] admissionRange = null;
		GregorianCalendar[] dischargeRange = null;
		String searchTerms = "";
		//when(admissionManagerMock.getAdmittedPatients(admissionRange, dischargeRange, searchTerms))
		when(admissionManagerMock.getAdmittedPatients(any(GregorianCalendar[].class), any(GregorianCalendar[].class), any(String.class)))
		.thenReturn(amittedPatients);
		
		
		MvcResult result = this.mockMvc
			.perform(get(request, searchTerms )
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetPatientAdmissions() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextYProg() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUsedWardBed() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteAdmissionType() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewAdmissions() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateAdmissions() {
		fail("Not yet implemented");
	}

}
