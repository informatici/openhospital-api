package org.isf.admission.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.admission.mapper.AdmittedPatientMapper;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.data.AdmissionTypeDTOHelper;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.data.DischargeTypeHelper;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.data.DiseaseHelper;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.operation.data.OperationHelper;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.data.PregnantTreatmentTypeHelper;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.ward.data.WardHelper;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
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
	
		Admission admission = AdmissionHelper.setup();
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
	
		Patient patient = PatientHelper.setup(); 
		when(patientManagerMock.getPatient(patientCode))
			.thenReturn(patient);
		
		Integer id = 0;
		Admission admission = AdmissionHelper.setup();
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
		String request = "/admissions/admittedPatients?searchterms=searchTerms";
		ArrayList<AdmittedPatient> amittedPatients = PatientHelper.setupAdmittedPatientList(2);
	
		//GregorianCalendar[] admissionRange = null;
		//GregorianCalendar[] dischargeRange = null;
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
			.andExpect(content().string(containsString(PatientHelper.asJsonString(admittedMapper.map2DTOList(amittedPatients)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetPatientAdmissions_200() throws Exception {
		String request = "/admissions?patientcode={patientCode}";
		Integer patientCode = 1;

		Patient patient = PatientHelper.setup(); 
		when(patientManagerMock.getPatient(patientCode))
			.thenReturn(patient);
		
		ArrayList<Admission> admissions = AdmissionHelper.setupAdmissionList(2);
		when(admissionManagerMock.getAdmissions(patient))
		.thenReturn(admissions);
		
		MvcResult result = this.mockMvc
			.perform(get(request, patientCode )
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(PatientHelper.asJsonString(admissionMapper.map2DTOList(admissions)))))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	

	@Test
	public void testGetNextYProg_200() throws Exception {
		String request = "/admissions/getNextProgressiveIdInYear?wardcode={wardCode}";
		String wardCode = "1";

		when(wardManagerMock.codeControl(wardCode))
			.thenReturn(true);
		
		Integer nextYProg = 1;
		when(admissionManagerMock.getNextYProg(wardCode))
		.thenReturn(nextYProg);
		
		MvcResult result = this.mockMvc
			.perform(get(request, wardCode )
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(nextYProg.toString())))
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testGetUsedWardBed_200() throws Exception {
		String request = "/admissions/getBedsOccupationInWard?wardid={wardCode}";
		String wardCode = "1";

		when(wardManagerMock.codeControl(wardCode))
			.thenReturn(true);
		
		Integer bed = 1012;
		when(admissionManagerMock.getUsedWardBed(wardCode))
		.thenReturn(bed);
		
		MvcResult result = this.mockMvc
			.perform(get(request, wardCode )
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andExpect(content().string(containsString(bed.toString())))
			.andReturn();
		
		logger.debug("result: {}", result);
	}
	
	@Test
	public void testDeleteAdmissionType_200() throws Exception {
		Integer id = 123;
		String request = "/admissions/{id}";
		
		Admission admission = AdmissionHelper.setup();
		when(admissionManagerMock.getAdmission(id))
			.thenReturn(admission);

		when(admissionManagerMock.setDeleted(eq(id))).thenReturn(true);
				
		this.mockMvc
			.perform(
					get(request, id)
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("true")))
			.andReturn();
	}
	

	@Test
	public void testNewAdmissions_201() throws Exception {
		String request = "/admissions";
		
		
		Integer id = 1;
		AdmissionDTO body = AdmissionHelper.setup(admissionMapper);
		Integer code = 10;
		body.getPatient().setCode(code);
		
		Admission newAdmission = admissionMapper.map2Model(body);

		
		
		
		when(admissionManagerMock.newAdmissionReturnKey(newAdmission))
			.thenReturn(id);

		ArrayList<Ward> wardList = WardHelper.setupWardList(2);
		when(wardManagerMock.getWards())
			.thenReturn(wardList);

		ArrayList<AdmissionType> admissionTypeList =  AdmissionTypeDTOHelper.setupAdmissionTypeList(3);
		when(admissionManagerMock.getAdmissionType())
		.thenReturn(admissionTypeList);

		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		when(patientManagerMock.getPatient(body.getPatient().getCode()))
		.thenReturn(patient);
		
		when(patientManagerMock.getPatient(body.getPatient().getCode()))
		.thenReturn(patient);
		
		ArrayList<Disease> diseaseList = DiseaseHelper.setupDiseaseList(3);
		when(diseaseManagerMock.getDisease())
		.thenReturn(diseaseList);
		
		
		ArrayList<Operation> operationsList = OperationHelper.setupOperationList(3);
		when(operationManagerMock.getOperation())
		.thenReturn(operationsList);
		
		ArrayList<DischargeType> disTypes = DischargeTypeHelper.setupDischargeTypeList(3);
		when(admissionManagerMock.getDischargeType())
		.thenReturn(disTypes);
		
	
		
		MvcResult result = this.mockMvc
			.perform(post(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(AdmissionHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isCreated())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

	@Test
	public void testUpdateAdmissions() throws Exception {
		String request = "/admissions";
		
		AdmissionDTO body = AdmissionHelper.setup(admissionMapper);
		Integer code = 10;
		body.getPatient().setCode(code);
		
		Admission old = admissionMapper.map2Model(body);
		Admission update = admissionMapper.map2Model(body);
		
		
		when(admissionManagerMock.getAdmission(eq(body.getId())))
		.thenReturn(old);
		
		ArrayList<Ward> wardList = WardHelper.setupWardList(2);
		when(wardManagerMock.getWards())
			.thenReturn(wardList);
		
		ArrayList<AdmissionType> admissionTypeList =  AdmissionTypeDTOHelper.setupAdmissionTypeList(3);
		when(admissionManagerMock.getAdmissionType())
		.thenReturn(admissionTypeList);

		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		when(patientManagerMock.getPatient(body.getPatient().getCode()))
		.thenReturn(patient);
		
		when(patientManagerMock.getPatient(body.getPatient().getCode()))
		.thenReturn(patient);
		
		ArrayList<Disease> diseaseList = DiseaseHelper.setupDiseaseList(3);
		when(diseaseManagerMock.getDisease())
		.thenReturn(diseaseList);
		
		
		ArrayList<Operation> operationsList = OperationHelper.setupOperationList(3);
		when(operationManagerMock.getOperation())
		.thenReturn(operationsList);
		
		ArrayList<DischargeType> disTypes = DischargeTypeHelper.setupDischargeTypeList(3);
		when(admissionManagerMock.getDischargeType())
		.thenReturn(disTypes);
		
		ArrayList<PregnantTreatmentType> pregTTypes = PregnantTreatmentTypeHelper.setupPregnantTreatmentTypeList(3);
		when(pregTraitTypeManagerMock.getPregnantTreatmentType())
		.thenReturn(pregTTypes);

		
		boolean isUpdated = true;
		
		when(admissionManagerMock.updateAdmission(update))
		.thenReturn(isUpdated);
		
		MvcResult result = this.mockMvc
			.perform(put(request)
					.contentType(MediaType.APPLICATION_JSON)
					.content(AdmissionHelper.asJsonString(body))
					)
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())	
			.andReturn();
		
		logger.debug("result: {}", result);
	}

}
