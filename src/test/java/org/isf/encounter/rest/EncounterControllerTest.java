/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter.rest;

import org.isf.admission.data.AdmissionHelper;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.admission.model.Admission;
import org.isf.conditioning.data.ConditioningHelper;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.encounter.data.EncounterHelper;
import org.isf.encounter.dto.EncounterDTO;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.mapper.EncounterMapper;
import org.isf.encounter.model.Encounter;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.medicalhistory.data.MedicalHistoryHelper;
import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.opd.data.OpdHelper;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.isf.shared.mapper.mappings.PatientMapping;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EncounterControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterControllerTest.class);

	@Mock
	protected EncounterBrowserManager encounterBrowserManagerMock;

	@Mock
	protected PatientBrowserManager patientBrowserManagerMock;

	@Mock
	protected ExaminationBrowserManager examinationBrowserManagerMock;

	@Mock
	protected OpdBrowserManager opdManagerMock;

	@Mock
	protected AdmissionBrowserManager admissionBrowserManagerMock;

	@Mock
	protected ConditioningBrowserManager browserManagerMock;

	@Mock
	protected MedicalHistoryBrowsingManager medicalHistoryBrowsingManager;

	@Mock
	protected OperationRowBrowserManager operationRowManager;

	protected OpdMapper opdMapper = new OpdMapper();
	protected AdmissionMapper admissionMapper = new AdmissionMapper();
	protected PatientExaminationMapper examinationMapper = new PatientExaminationMapper();
	private final EncounterMapper encounterMapper = new EncounterMapper();
	protected PatientMapper patientMapper = new PatientMapper();
	protected ConditioningMapper conditioningMapper = new ConditioningMapper();
	protected MedicalHistoryMapper medicalHistoryMapper = new MedicalHistoryMapper();
	protected OperationRowMapper opRowMapper = new OperationRowMapper();

	private MockMvc mockMvc;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new EncounterController(encounterBrowserManagerMock, encounterMapper, patientBrowserManagerMock, examinationBrowserManagerMock, examinationMapper, opdManagerMock, opdMapper, admissionMapper, admissionBrowserManagerMock, browserManagerMock, conditioningMapper, medicalHistoryBrowsingManager, medicalHistoryMapper, operationRowManager, opRowMapper))
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new BlobToByteArrayConverter());
		modelMapper.addConverter(new ByteArrayToBlobConverter());
		PatientMapping.addMapping(modelMapper);
		ReflectionTestUtils.setField(encounterMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(patientMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(opdMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(admissionMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(examinationMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(conditioningMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(medicalHistoryMapper, "modelMapper", modelMapper);
		ReflectionTestUtils.setField(opRowMapper, "modelMapper", modelMapper);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewEncounter_success() throws Exception {
		String request = "/encounters";

		EncounterDTO body = EncounterHelper.setup(encounterMapper);
		PatientDTO patientDTO = PatientHelper.setup(patientMapper);
		patientDTO.setCode(Double.valueOf(Math.random()).intValue());
		body.setPatient(patientDTO);
		Encounter encounter = encounterMapper.map2Model(body);

		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode()))
			.thenReturn(encounter.getPatient());

		when(encounterBrowserManagerMock.saveEncounter(any(Encounter.class)))
			.thenReturn(encounter);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isCreated())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testNewEncounter_patientNotFound() throws Exception {
		String request = "/encounters";

		EncounterDTO body = EncounterHelper.setup(encounterMapper);
		PatientDTO patientDTO = PatientHelper.setup(patientMapper);
		patientDTO.setCode(Double.valueOf(Math.random()).intValue());
		body.setPatient(patientDTO);

		when(patientBrowserManagerMock.getPatientById(body.getPatient().getCode()))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(body)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetEncountersByPatient_success() throws Exception {
		String request = "/encounters/{patientId}";
		int patientId = 123;

		List<Encounter> encounters = new ArrayList<>();
		encounters.add(encounterMapper.map2Model(EncounterHelper.setup(encounterMapper)));
		encounters.add(encounterMapper.map2Model(EncounterHelper.setup(encounterMapper)));

		when(encounterBrowserManagerMock.getEncountersByPatient(patientId))
			.thenReturn(encounters);

		MvcResult result = this.mockMvc
			.perform(get(request, patientId))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetEncountersByPatient_empty() throws Exception {
		String request = "/encounters/{patientId}";
		int patientId = 123;

		when(encounterBrowserManagerMock.getEncountersByPatient(patientId))
			.thenReturn(Collections.emptyList());

		MvcResult result = this.mockMvc
			.perform(get(request, patientId))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetCurrentEncounterByPatient_success() throws Exception {
		String request = "/encounters/current/{patientId}";
		int patientId = 123;

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));

		when(encounterBrowserManagerMock.getCurrentEncounter(patientId))
			.thenReturn(encounter);

		MvcResult result = this.mockMvc
			.perform(get(request, patientId))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").exists())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetCurrentEncounterByPatient_notFound() throws Exception {
		String request = "/encounters/current/{patientId}";
		int patientId = 123;

		when(encounterBrowserManagerMock.getCurrentEncounter(patientId))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, patientId))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(""))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateEncounter_success() throws Exception {
		String request = "/encounters/{code}";
		String existingCode = "ENC_001";

		EncounterDTO updateDTO = EncounterHelper.setup(encounterMapper);
		PatientDTO patientDTO = PatientHelper.setup(patientMapper);
		patientDTO.setCode(Double.valueOf(Math.random()).intValue());
		updateDTO.setPatient(patientDTO);
		updateDTO.setCode("ENC_002");

		Encounter existingEncounter = encounterMapper.map2Model(updateDTO);
		existingEncounter.setCode(existingCode);

		when(encounterBrowserManagerMock.getEncountersByCode(existingCode))
			.thenReturn(existingEncounter);

		when(encounterBrowserManagerMock.getEncountersByCode(updateDTO.getCode()))
			.thenReturn(null);

		when(encounterBrowserManagerMock.saveEncounter(any(Encounter.class)))
			.thenReturn(encounterMapper.map2Model(updateDTO));

		MvcResult result = this.mockMvc
			.perform(patch(request, existingCode)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(updateDTO)))
			)
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(updateDTO.getCode()))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateEncounter_notFound() throws Exception {
		String request = "/encounters/{code}";
		String nonExistentCode = "NOT_EXIST";

		EncounterDTO updateDTO = EncounterHelper.setup(encounterMapper);

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(patch(request, nonExistentCode)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(updateDTO)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testUpdateEncounter_codeAlreadyExists() throws Exception {
		String request = "/encounters/{code}";
		String existingCode = "ENC_001";

		EncounterDTO updateDTO = EncounterHelper.setup(encounterMapper);
		PatientDTO patientDTO = PatientHelper.setup(patientMapper);
		patientDTO.setCode(Double.valueOf(Math.random()).intValue());
		updateDTO.setPatient(patientDTO);
		updateDTO.setCode("EXISTING_CODE");

		Encounter existingEncounter = encounterMapper.map2Model(updateDTO);
		existingEncounter.setCode(existingCode);

		Encounter conflictingEncounter = new Encounter();
		conflictingEncounter.setCode("EXISTING_CODE");

		when(encounterBrowserManagerMock.getEncountersByCode(existingCode))
			.thenReturn(existingEncounter);

		when(encounterBrowserManagerMock.getEncountersByCode(updateDTO.getCode()))
			.thenReturn(conflictingEncounter);

		MvcResult result = this.mockMvc
			.perform(patch(request, existingCode)
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(EncounterHelper.asJsonString(updateDTO)))
			)
			.andDo(log())
			.andExpect(status().isBadRequest())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetOPDByEncounter_success() throws Exception {
		String request = "/encounters/{code}/opds";
		String encounterCode = "ENC_001";

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));

		OpdDTO opdDTO1 = opdMapper.map2DTO(OpdHelper.setup());
		OpdDTO opdDTO2 = opdMapper.map2DTO(OpdHelper.setup());
		Opd opd1 = opdMapper.map2Model(opdDTO1);
		Opd opd2 = opdMapper.map2Model(opdDTO2);
		List<Opd> opdList = Arrays.asList(opd1, opd2);

		when(encounterBrowserManagerMock.getEncountersByCode(encounterCode))
			.thenReturn(encounter);

		when(opdManagerMock.getOpdForEncounter(encounter))
			.thenReturn(opdList);

		MvcResult result = this.mockMvc
			.perform(get(request, encounterCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetOPDByEncounter_notFound() throws Exception {
		String request = "/encounters/{code}/opds";
		String nonExistentCode = "NOT_EXIST";

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, nonExistentCode))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetPatientExaminationsByEncounter_success() throws Exception {
		String request = "/encounters/{code}/examinations";
		String encounterCode = "ENC_001";

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));
		Patient patient1 = PatientHelper.setup();
		Patient patient2 = PatientHelper.setup();
		PatientExamination patientExamination1 = new TestPatientExamination().setup(patient1, false);
		PatientExamination patientExamination2 = new TestPatientExamination().setup(patient2, false);

		List<PatientExamination> examinations = Arrays.asList(patientExamination1, patientExamination2);

		when(encounterBrowserManagerMock.getEncountersByCode(encounterCode))
			.thenReturn(encounter);

		when(examinationBrowserManagerMock.getPatientExaminationsForEncounter(encounter))
			.thenReturn(examinations);

		MvcResult result = this.mockMvc
			.perform(get(request, encounterCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetPatientExaminationsByEncounter_notFound() throws Exception {
		String request = "/encounters/{code}/examinations";
		String nonExistentCode = "NOT_EXIST";

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, nonExistentCode))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetAdmissionsByEncounter_success() throws Exception {
		String request = "/encounters/{code}/admissions";
		String encounterCode = "ENC_001";

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));
		AdmissionDTO admissionDTO1 = admissionMapper.map2DTO(AdmissionHelper.setup());
		AdmissionDTO admissionDTO2 = admissionMapper.map2DTO(AdmissionHelper.setup());
		Admission admission1 = admissionMapper.map2Model(admissionDTO1);
		Admission admission2 = admissionMapper.map2Model(admissionDTO2);
		List<Admission> admissions = Arrays.asList(admission1, admission2);

		when(encounterBrowserManagerMock.getEncountersByCode(encounterCode))
			.thenReturn(encounter);

		when(admissionBrowserManagerMock.getAdmissionsByEncounter(encounter))
			.thenReturn(admissions);

		MvcResult result = this.mockMvc
			.perform(get(request, encounterCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetAdmissionsByEncounter_notFound() throws Exception {
		String request = "/encounters/{code}/admissions";
		String nonExistentCode = "NOT_EXIST";

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, nonExistentCode))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetConditioningsByEncounter_success() throws Exception {
		String request = "/encounters/{code}/conditionings";
		String encounterCode = "ENC_123";

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));
		ConditioningDTO conditioningDTO1 = conditioningMapper.map2DTO(ConditioningHelper.setup());
		ConditioningDTO conditioningDTO2 = conditioningMapper.map2DTO(ConditioningHelper.setup());
		Conditioning conditioning1 = conditioningMapper.map2Model(conditioningDTO1);
		Conditioning conditioning2 = conditioningMapper.map2Model(conditioningDTO2);
		conditioning1.setId(1);
		conditioning2.setId(2);
		List<Conditioning> conditionings = Arrays.asList(conditioning1, conditioning2);

		when(encounterBrowserManagerMock.getEncountersByCode(encounterCode))
			.thenReturn(encounter);
		when(browserManagerMock.getConditioningByPatientEncounter(encounter))
			.thenReturn(conditionings);

		MvcResult result = this.mockMvc
			.perform(get(request, encounterCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(conditioning1.getId()))
			.andExpect(jsonPath("$[1].id").value(conditioning2.getId()))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetConditioningsByEncounter_notFound() throws Exception {
		String request = "/encounters/{code}/conditionings";
		String nonExistentCode = "NOT_EXIST";

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, nonExistentCode))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}


	@Test
	void testGetMedicalhistoriesByEncounter_success() throws Exception {
		String request = "/encounters/{code}/medicalhistories";
		String encounterCode = "ENC_123";

		Encounter encounter = encounterMapper.map2Model(EncounterHelper.setup(encounterMapper));
		MedicalHistoryDTO medicalHistoryDTO1 = medicalHistoryMapper.map2DTO(MedicalHistoryHelper.setup());
		MedicalHistoryDTO medicalHistoryDTO2 = medicalHistoryMapper.map2DTO(MedicalHistoryHelper.setup());
		MedicalHistory medicalHistory1 = medicalHistoryMapper.map2Model(medicalHistoryDTO1);
		MedicalHistory medicalHistory2 = medicalHistoryMapper.map2Model(medicalHistoryDTO2);
		medicalHistory1.setId(1);
		medicalHistory2.setId(2);
		List<MedicalHistory> medicalHistories = Arrays.asList(medicalHistory1, medicalHistory2);

		when(encounterBrowserManagerMock.getEncountersByCode(encounterCode))
			.thenReturn(encounter);
		when(medicalHistoryBrowsingManager.getMedicalHistoriesForEncounter(encounter))
			.thenReturn(medicalHistories);

		MvcResult result = this.mockMvc
			.perform(get(request, encounterCode))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(medicalHistory1.getId()))
			.andExpect(jsonPath("$[1].id").value(medicalHistory2.getId()))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	void testGetMedicalhistoriesByEncounter_notFound() throws Exception {
		String request = "/encounters/{code}/medicalhistories";
		String nonExistentCode = "NOT_EXIST";

		when(encounterBrowserManagerMock.getEncountersByCode(nonExistentCode))
			.thenReturn(null);

		MvcResult result = this.mockMvc
			.perform(get(request, nonExistentCode))
			.andDo(log())
			.andExpect(status().isNotFound())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
}
