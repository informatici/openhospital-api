/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

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
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.mapper.DischargeTypeMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdmissionControllerTest {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdmissionControllerTest.class);

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
	
	@Mock
	private DischargeTypeBrowserManager dischargeTypeManagerMock;

	@Autowired
	private AdmissionMapper admissionMapper = new AdmissionMapper();

	@Autowired
	private AdmittedPatientMapper admittedMapper = new AdmittedPatientMapper();
	
	@Autowired
	private DischargeTypeMapper dischargeMapper = new DischargeTypeMapper();
	
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AdmissionController(admissionManagerMock, patientManagerMock, wardManagerMock,
						diseaseManagerMock, operationManagerMock, pregTraitTypeManagerMock,
						dlvrTypeManagerMock, dlvrrestTypeManagerMock, admissionMapper,
						admittedMapper, dischargeTypeManagerMock, dischargeMapper))
				.setControllerAdvice(new OHResponseEntityExceptionHandler())
				.build();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		ReflectionTestUtils.setField(admissionMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(admittedMapper, "modelMapper", modelMapper);
	}

//	@Test
//	public void testGetAdmissions_200() throws Exception {
//		String request = "/admissions/{id}";
//		int id = 1;
//
//		Admission admission = AdmissionHelper.setup();
//		when(admissionManagerMock.getAdmission(id))
//				.thenReturn(admission);
//
//		MvcResult result = this.mockMvc
//				.perform(
//						get(request, id)
//						.contentType(MediaType.APPLICATION_JSON)
//				)
//				.andDo(log())
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(status().isOk())
//				.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
//				.andReturn();
//
//		LOGGER.debug("result: {}", result);
//	}

	@Test
	public void testGetCurrentAdmission_200() throws Exception {
		String request = "/admissions/current";
		Integer patientCode = 1;

		Patient patient = PatientHelper.setup();
		when(patientManagerMock.getPatientById(patientCode))
				.thenReturn(patient);

		Admission admission = AdmissionHelper.setup();
		when(admissionManagerMock.getCurrentAdmission(patient))
				.thenReturn(admission);

		MvcResult result = this.mockMvc
				.perform(get(request)
						.param("patientCode", patientCode.toString())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAllAdmittedPatients_200() throws Exception {
		String request = "/admissions/allAdmittedPatients";
		List<AdmittedPatient> admittedPatients = PatientHelper.setupAdmittedPatientList(2);

		when(admissionManagerMock.getAdmittedPatients())
				.thenReturn(admittedPatients);

		MvcResult result = this.mockMvc
				.perform(get(request)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(admittedMapper.map2DTOList(admittedPatients)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAdmittedPatientsSearch_200() throws Exception {
		String request = "/admissions/admittedPatients?searchterms={searchTerms}";
		List<AdmittedPatient> admittedPatients = PatientHelper.setupAdmittedPatientList(2);

		String searchTerms = "";
		when(admissionManagerMock.getAdmittedPatients(any(), any(), any(String.class)))
				.thenReturn(admittedPatients);
		MvcResult result = this.mockMvc
				.perform(get(request, searchTerms)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(admittedMapper.map2DTOList(admittedPatients)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAdmittedPatients_200() throws Exception {
		String request = "/admissions/admittedPatients?searchterms={searchTerms}&admissionRange={admissionRange}&dischargeRange={dischargeRange}";
		List<AdmittedPatient> admittedPatients = PatientHelper.setupAdmittedPatientList(2);

		String searchTerms = "";
		when(admissionManagerMock.getAdmittedPatients(any(), any(), any(String.class)))
				.thenReturn(admittedPatients);
		
		MvcResult result = this.mockMvc
				.perform(get(request, searchTerms, null, null)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(PatientHelper.asJsonString(admittedMapper.map2DTOList(admittedPatients)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetPatientAdmissions_200() throws Exception {
		int patientCode = 1;
		String request = "/admissions/" + patientCode;

		Patient patient = PatientHelper.setup();
		when(patientManagerMock.getPatientById(patientCode))
				.thenReturn(patient);

		List<Admission> listAdmissions = AdmissionHelper.setupAdmissionList(2);
		when(admissionManagerMock.getAdmissions(patient))
				.thenReturn(listAdmissions);

		MvcResult result = this.mockMvc
				.perform(get(request, patientCode)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTOList(listAdmissions)))))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetNextYProg_200() throws Exception {
		String request = "/admissions/getNextProgressiveIdInYear";
		String wardCode = "1";

		when(wardManagerMock.isCodePresent(wardCode))
				.thenReturn(true);

		Integer nextYProg = 1;
		when(admissionManagerMock.getNextYProg(wardCode))
				.thenReturn(nextYProg);

		MvcResult result = this.mockMvc
				.perform(
						get(request)
							.param("wardcode", wardCode)
							.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(nextYProg.toString())))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetUsedWardBed_200() throws Exception {
		String request = "/admissions/getBedsOccupationInWard?wardid={wardCode}";
		String wardCode = "1";

		when(wardManagerMock.isCodePresent(wardCode))
				.thenReturn(true);

		Integer bed = 1012;
		when(admissionManagerMock.getUsedWardBed(wardCode))
				.thenReturn(bed);

		MvcResult result = this.mockMvc
				.perform(get(request, wardCode)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(bed.toString())))
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteAdmission_200() throws Exception {
		Integer id = 123;
		String request = "/admissions/{id}";

		Admission admission = AdmissionHelper.setup();
		when(admissionManagerMock.getAdmission(id))
				.thenReturn(admission);

		when(admissionManagerMock.setDeleted(id)).thenReturn(true);

		this.mockMvc
				.perform(
						delete(request, id)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(log())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("true")))
				.andReturn();
	}

	@Test
	public void testDischargeAdmission_200() throws Exception {
		    
		    Integer patientCode = 1;
			String request = "/admissions/discharge";
			Patient patient = PatientHelper.setup();
			patient.setCode(patientCode);
			when(patientManagerMock.getPatientById(patientCode)).thenReturn(patient);
			
			Admission admission = AdmissionHelper.setup();
			when(admissionManagerMock.getCurrentAdmission(patient)).thenReturn(admission);
			Disease disease1 = DiseaseHelper.setup();
			Disease disease2 = DiseaseHelper.setup();
			Disease disease3 = DiseaseHelper.setup();
			String dichargeTypeCode = "B";
			DischargeType dischargeType = DischargeTypeHelper.setup(dichargeTypeCode);
			admission.setAdmitted(0);
			admission.setDisDate(LocalDateTime.now());
			admission.setDiseaseOut1(disease1);
			admission.setDiseaseOut1(disease2);
			admission.setDiseaseOut1(disease3);
			admission.setDisType(dischargeType);
			
			when(admissionManagerMock.updateAdmission(admission)).thenReturn(admission);
			
			when(dischargeTypeManagerMock.isCodePresent(dichargeTypeCode)).thenReturn(true);
			
			AdmissionDTO admissionDTO = admissionMapper.map2DTO(admission);	
			this.mockMvc
					.perform(
							post(request)
								.param("patientCode", patientCode.toString())
								.contentType(MediaType.APPLICATION_JSON)
								.content(AdmissionHelper.asJsonString(admissionDTO))
					)
					.andDo(log())
					.andExpect(status().isOk())
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

		List<Ward> wardList = WardHelper.setupWardList(2);
		when(wardManagerMock.getWards())
				.thenReturn(wardList);

		List<AdmissionType> admissionTypeList = AdmissionTypeDTOHelper.setupAdmissionTypeList(3);
		when(admissionManagerMock.getAdmissionType())
				.thenReturn(admissionTypeList);

		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		when(patientManagerMock.getPatientById(body.getPatient().getCode()))
				.thenReturn(patient);

		List<Disease> diseaseList = DiseaseHelper.setupDiseaseList(3);
		when(diseaseManagerMock.getDiseaseAll())
				.thenReturn(diseaseList);

		List<Operation> operationsList = OperationHelper.setupOperationList(3);
		when(operationManagerMock.getOperation())
				.thenReturn(operationsList);

		List<DischargeType> disTypes = DischargeTypeHelper.setupDischargeTypeList(3);
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

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testUpdateAdmissions() throws Exception {
		String request = "/admissions";

		AdmissionDTO body = AdmissionHelper.setup(admissionMapper);
		Integer code = 10;
		body.getPatient().setCode(code);

		Admission old = admissionMapper.map2Model(body);
		Admission update = admissionMapper.map2Model(body);

		when(admissionManagerMock.getAdmission(body.getId()))
				.thenReturn(old);

		List<Ward> wardList = WardHelper.setupWardList(2);
		when(wardManagerMock.getWards())
				.thenReturn(wardList);

		List<AdmissionType> admissionTypeList = AdmissionTypeDTOHelper.setupAdmissionTypeList(3);
		when(admissionManagerMock.getAdmissionType())
				.thenReturn(admissionTypeList);

		Patient patient = PatientHelper.setup();
		patient.setCode(code);
		when(patientManagerMock.getPatientById(body.getPatient().getCode()))
				.thenReturn(patient);

		when(patientManagerMock.getPatientById(body.getPatient().getCode()))
				.thenReturn(patient);

		List<Disease> diseaseList = DiseaseHelper.setupDiseaseList(3);
		when(diseaseManagerMock.getDiseaseAll())
				.thenReturn(diseaseList);

		List<Operation> operationsList = OperationHelper.setupOperationList(3);
		when(operationManagerMock.getOperation())
				.thenReturn(operationsList);

		List<DischargeType> disTypes = DischargeTypeHelper.setupDischargeTypeList(3);
		when(admissionManagerMock.getDischargeType())
				.thenReturn(disTypes);

		List<PregnantTreatmentType> pregTTypes = PregnantTreatmentTypeHelper.setupPregnantTreatmentTypeList(3);
		when(pregTraitTypeManagerMock.getPregnantTreatmentType())
				.thenReturn(pregTTypes);

		when(admissionManagerMock.updateAdmission(update))
				.thenReturn(update);

        MvcResult result = this.mockMvc
				.perform(put(request)
						.contentType(MediaType.APPLICATION_JSON)
						.content(AdmissionHelper.asJsonString(body))
				)
				.andDo(log())
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isOk())
				.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
