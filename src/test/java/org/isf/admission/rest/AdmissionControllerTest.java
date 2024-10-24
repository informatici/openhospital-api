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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
import java.util.Objects;

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
import org.isf.shared.mapper.mappings.PatientMapping;
import org.isf.ward.data.WardHelper;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdmissionControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdmissionControllerTest.class);

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
	private PregnantTreatmentTypeBrowserManager pregnancyTreatmentTypeManagerMock;

	@Mock
	private DeliveryTypeBrowserManager deliveryTypeManager;

	@Mock
	private DeliveryResultTypeBrowserManager deliveryResultTypeManagerMock;

	@Mock
	private DischargeTypeBrowserManager dischargeTypeManagerMock;

	private final AdmissionMapper admissionMapper;

	private final AdmittedPatientMapper admittedMapper;

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	public AdmissionControllerTest() {
		admittedMapper = new AdmittedPatientMapper();
		admissionMapper = new AdmissionMapper();
	}

	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new AdmissionController(admissionManagerMock, patientManagerMock, wardManagerMock,
				diseaseManagerMock, pregnancyTreatmentTypeManagerMock,
				deliveryTypeManager, deliveryResultTypeManagerMock, admissionMapper,
				admittedMapper, dischargeTypeManagerMock)
			)
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		PatientMapping.addMapping(modelMapper);
		ReflectionTestUtils.setField(admissionMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(admittedMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

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
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTO(admission)))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetAllAdmittedPatients_200() throws Exception {
		String request = "/admissions/admittedPatients";
		List<AdmittedPatient> admittedPatients = PatientHelper.setupAdmittedPatientList(2);

		when(admissionManagerMock.getAdmittedPatients(any(), any(), any(String.class)))
			.thenReturn(admittedPatients);

		MvcResult result = this.mockMvc
			.perform(get(request)
				.contentType(MediaType.APPLICATION_JSON))
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
				.contentType(MediaType.APPLICATION_JSON))
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
				.contentType(MediaType.APPLICATION_JSON))
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
		String request = "/admissions/patient/{patientCode}" ;

		Patient patient = PatientHelper.setup();
		when(patientManagerMock.getPatientById(patientCode))
			.thenReturn(patient);

		List<Admission> listAdmissions = AdmissionHelper.setupAdmissionList(2);
		when(admissionManagerMock.getAdmissions(patient))
			.thenReturn(listAdmissions);

		MvcResult result = this.mockMvc
			.perform(get(request, patientCode)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(AdmissionHelper.asJsonString(admissionMapper.map2DTOList(listAdmissions)))))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testGetNextYProgressiveId_200() throws Exception {
		String request = "/admissions/getNextProgressiveIdInYear";
		String wardCode = "1";

		when(wardManagerMock.isCodePresent(wardCode))
			.thenReturn(true);

		Integer nextYProgressiveId = 1;
		when(admissionManagerMock.getNextYProg(wardCode))
			.thenReturn(nextYProgressiveId);

		MvcResult result = this.mockMvc
			.perform(
				get(request)
					.param("wardcode", wardCode)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(nextYProgressiveId.toString())))
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
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(bed.toString())))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	public void testDeleteAdmission_200() throws Exception {
		int id = 123;
		String request = "/admissions/{id}";

		Admission admission = AdmissionHelper.setup();
		when(admissionManagerMock.getAdmission(id))
			.thenReturn(admission);

		this.mockMvc
			.perform(
				delete(request, id)
					.contentType(MediaType.APPLICATION_JSON))
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
		String dischargeTypeCode = "B";
		DischargeType dischargeType = DischargeTypeHelper.setup(dischargeTypeCode);
		admission.setAdmitted(0);
		admission.setDisDate(LocalDateTime.now());
		admission.setDiseaseOut1(disease1);
		admission.setDiseaseOut1(disease2);
		admission.setDiseaseOut1(disease3);
		admission.setDisType(dischargeType);

		when(admissionManagerMock.updateAdmission(admission)).thenReturn(admission);

		when(dischargeTypeManagerMock.isCodePresent(dischargeTypeCode)).thenReturn(true);

		AdmissionDTO admissionDTO = admissionMapper.map2DTO(admission);
		this.mockMvc
			.perform(
				post(request)
					.param("patientCode", patientCode.toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(Objects.requireNonNull(AdmissionHelper.asJsonString(admissionDTO))))
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
				.content(Objects.requireNonNull(AdmissionHelper.asJsonString(body))))
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

		List<PregnantTreatmentType> pregnancyTreatmentTypes = PregnantTreatmentTypeHelper.setupPregnantTreatmentTypeList(3);
		when(pregnancyTreatmentTypeManagerMock.getPregnantTreatmentType())
			.thenReturn(pregnancyTreatmentTypes);

		when(admissionManagerMock.updateAdmission(update))
			.thenReturn(update);

		MvcResult result = this.mockMvc
			.perform(put(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(AdmissionHelper.asJsonString(body))))
			.andDo(log())
			.andExpect(status().is2xxSuccessful())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

}
