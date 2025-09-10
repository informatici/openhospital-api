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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.admission.model.Admission;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.encounter.dto.EncounterDTO;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.mapper.EncounterMapper;
import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;

import org.isf.patient.dto.PatientSTATUS;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Encounter")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EncounterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterController.class);

	private static final String DRAFT = LaboratoryStatus.draft.toString();

	private static final String OPEN = LaboratoryStatus.open.toString();

	private final EncounterBrowserManager encounterBrowserManager;
	private final EncounterMapper encounterMapper;
	private final PatientBrowserManager patientBrowserManager;
	private final ExaminationBrowserManager examinationBrowserManager;
	private final PatientExaminationMapper examinationMapper;
	private final OpdBrowserManager opdManager;
	private final OpdMapper opdMapper;
	private final AdmissionMapper admissionMapper;
	private final AdmissionBrowserManager admissionBrowserManager;
	private final ConditioningBrowserManager conditioningManager;
	private final ConditioningMapper conditioningMapper;
	private final MedicalHistoryBrowsingManager medicalHistoryManager;
	private final MedicalHistoryMapper medicalHistoryMapper;
	private final LabManager labManager;
	private final LaboratoryMapper laboratoryMapper;

	public EncounterController(
		EncounterBrowserManager encounterBrowserManager,
		EncounterMapper encounterMapper,
		PatientBrowserManager patientBrowserManager,

		ExaminationBrowserManager examinationBrowserManager,
		PatientExaminationMapper examinationMapper,
		OpdBrowserManager opdManager,
		OpdMapper opdMapper,
		AdmissionBrowserManager admissionBrowserManager,
		AdmissionMapper admissionMapper,
		ConditioningBrowserManager conditioningManager,
		ConditioningMapper conditioningMapper,
		MedicalHistoryBrowsingManager medicalHistoryManager,
		MedicalHistoryMapper medicalHistoryMapper,
		LabManager labManager,
		LaboratoryMapper laboratoryMapper

	) {
		this.encounterBrowserManager = encounterBrowserManager;
		this.encounterMapper = encounterMapper;
		this.patientBrowserManager = patientBrowserManager;
		this.examinationBrowserManager = examinationBrowserManager;
		this.examinationMapper = examinationMapper;
		this.opdManager = opdManager;
		this.opdMapper = opdMapper;
		this.admissionBrowserManager = admissionBrowserManager;
		this.admissionMapper = admissionMapper;
		this.conditioningManager = conditioningManager;
		this.conditioningMapper = conditioningMapper;
		this.medicalHistoryManager = medicalHistoryManager;
		this.medicalHistoryMapper = medicalHistoryMapper;
		this.labManager = labManager;
		this.laboratoryMapper = laboratoryMapper;
	}

	@PostMapping(value = "/encounters")
	@ResponseStatus(HttpStatus.CREATED)
	public EncounterDTO createEncounter(@RequestBody EncounterDTO encounterDTO) throws OHServiceException {
		LOGGER.info("Create encounter with {}", encounterDTO.getCode());
		if (encounterDTO.getPatient() == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient must not be null."));
		}
		if (encounterBrowserManager.getEncountersByCode(encounterDTO.getCode()) != null) {
			throw new OHAPIException(new OHExceptionMessage("The encounter code is already in use."));
		}

		Patient patient = patientBrowserManager.getPatientById(encounterDTO.getPatient().getCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Encounter encounter = encounterMapper.map2Model(encounterDTO);
		encounter.setPatient(patient);
		encounter.setStatus(EncounterStatus.ACTIVE);
		encounter = encounterBrowserManager.saveEncounter(encounter);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create encounter"));
		}

		return encounterMapper.map2DTO(encounter);
	}

	@GetMapping("/encounters/{patientId}")
	public List<EncounterDTO> getEncountersByPatient(@PathVariable int patientId) throws OHServiceException {
		LOGGER.info("Get patient encounters  with code {}", patientId);
		List<Encounter> encounters = encounterBrowserManager.getEncountersByPatient(patientId);
		return encounterMapper.map2DTOList(encounters);
	}

	@GetMapping("/encounters/current/{patientId}")
	public EncounterDTO getCurrentEncounterByPatient(@PathVariable int patientId) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getCurrentEncounter(patientId);
		if (encounter == null) {
			return null;
		}
		return encounterMapper.map2DTO(encounter);
	}

	@GetMapping("/encounters/{code}/opds")
	public List<OpdDTO> getOPDByEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}
		List<Opd> opdList = opdManager.getOpdForEncounter(encounter);
		return opdMapper.map2DTOList(opdList);
	}

	/**
	 * Retrieves the list of {@link PatientExaminationDTO} objects associated with a specific encounter,
	 * identified by its unique code.
	 *
	 * @param code the unique encounter code used to identify the encounter
	 * @return a {@link List} of {@link PatientExaminationDTO} objects associated with the given encounter
	 * @throws OHServiceException if an error occurs while retrieving patient examinations
	 * @throws OHAPIException if no encounter is found with the provided code
	 */
	@GetMapping("/encounters/{code}/examinations")
	public List<PatientExaminationDTO> getPatientExaminationsByEncounter(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Get patient examination By encounter code: {}", code);
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}
		List<PatientExamination> patientExaminationList = examinationBrowserManager.getPatientExaminationsForEncounter(encounter);
		return examinationMapper.map2DTOList(patientExaminationList);
	}

	@PatchMapping("/encounters/{code}")
	public EncounterDTO updateEncounter(@PathVariable String code, @RequestBody EncounterDTO encounter) throws OHServiceException {
		LOGGER.info("Update encounter with new code {}", encounter.getCode());
		Encounter encounterToUpdate = encounterBrowserManager.getEncountersByCode(code);
		if (encounterToUpdate == null || encounterToUpdate.getStatus() == EncounterStatus.CANCELLED) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found"));
		}

		if (encounter.getPatient() == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter most have patient property"));
		}

		Integer patientCode = encounter.getPatient().getCode();

		if (!Objects.equals(patientCode, encounterToUpdate.getPatient().getCode())) {
			throw new OHAPIException(new OHExceptionMessage("The encounter found is not for the patient with code "+patientCode));
		}

		if (encounterToUpdate.getClosedAt() != null) {
			throw new OHAPIException(new OHExceptionMessage("You cannot modify a closed encounter."));
		}

		Encounter encounterFound = encounterBrowserManager.getEncountersByCode(encounter.getCode());
		if (encounterFound != null && !Objects.equals(encounterFound.getCode(), encounterToUpdate.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("The encounter code is already in use."));
		}

		if (encounter.getStatus() == null) {
			encounter.setStatus(EncounterStatus.ACTIVE);
		}

		encounter.setPerformedAt(encounterToUpdate.getPerformedAt());

		Encounter encounterToUpdated = encounterMapper.map2Model(encounter);

		encounterBrowserManager.saveEncounter(encounterToUpdated);
		return encounter;
	}

	/**
	 * Retrieves the list of {@link OpdDTO} objects associated with a specific encounter,
	 * identified by its unique code.
	 *
	 * @param code the unique encounter code used to identify the encounter
	 * @return a {@link List} of {@link OpdDTO} objects associated with the given encounter
	 * @throws OHServiceException if an error occurs while retrieving patient examinations
	 * @throws OHAPIException if no encounter is found with the provided code
	 */
	@GetMapping("/encounters/{code}/conditionings")
	public List<ConditioningDTO> getConditioningByPatientEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}

		List<Conditioning> conditioningList = conditioningManager.getConditioningByPatientEncounter(encounter);

		return conditioningMapper.map2DTOList(conditioningList);
	}

	/**
	 * Retrieves the list of {@link MedicalHistoryDTO} objects associated with a specific encounter,
	 * identified by its unique code.
	 *
	 * @param code the unique encounter code used to identify the encounter
	 * @return a {@link List} of {@link MedicalHistoryDTO} objects associated with the given encounter
	 * @throws OHServiceException if an error occurs while retrieving patient examinations
	 * @throws OHAPIException if no encounter is found with the provided code
	 */
	@GetMapping("/encounters/{code}/medicalhistories")
	public List<MedicalHistoryDTO> getMedicalHistoriesEncounterByEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}
		List<MedicalHistory> medicalHistoryList = medicalHistoryManager.getMedicalHistoriesForEncounter(encounter);
		return medicalHistoryMapper.map2DTOList(medicalHistoryList);
	}

	@GetMapping("/encounters/{code}/admissions")
	public List<AdmissionDTO> getAdmissionsByEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}

		List<Admission> admissionList = admissionBrowserManager.getAdmissionsByEncounter(encounter);

		return admissionMapper.map2DTOList(admissionList);
	}

	/**
	 * Get all {@link LaboratoryDTO}s linked to the specified {@link Encounter}.
	 *
	 * @param code Encounter code
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exams for the encounter
	 */
	@GetMapping("/encounters/{code}/exams")
	public List<LaboratoryDTO> getLaboratoryByEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}

		List<Laboratory> LaboratoryList = labManager.getLaboratoryByEncounter(encounter).stream()
			.filter(e -> !DRAFT.equalsIgnoreCase(e.getStatus()) && !OPEN.equalsIgnoreCase(e.getStatus())).toList();

		return laboratoryMapper.map2DTOList(LaboratoryList);
	}

	/**
	 * Get all {@link LaboratoryDTO}s examRequest linked to the specified {@link Encounter}.
	 *
	 * @param code Encounter code
	 * @return the {@link List} of found {@link LaboratoryDTO} of examRequest or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exams for the encounter
	 */
	@GetMapping("/encounters/{code}/examRequest")
	public List<LaboratoryDTO> getLaboratoryExamRequestByEncounter(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code " + code), HttpStatus.NOT_FOUND);
		}

		List<Laboratory> LaboratoryList = labManager.getLaboratoryByEncounter(encounter).stream()
			.filter(e -> DRAFT.equalsIgnoreCase(e.getStatus()) || OPEN.equalsIgnoreCase(e.getStatus())).toList();

		return LaboratoryList.stream().map(lab -> {
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			return laboratoryDTO;
		}).collect(Collectors.toList());
	}
}
