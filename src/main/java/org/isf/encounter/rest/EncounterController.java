package org.isf.encounter.rest;

import java.util.List;

import org.isf.encounter.dto.EncounterDTO;
import org.isf.encounter.mapper.EncounterMapper;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Encounter")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EncounterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterController.class);
	
	private final EncounterBrowserManager encounterBrowserManager;
	private final EncounterMapper encounterMapper;
	private final PatientBrowserManager patientBrowserManager;

	public EncounterController(EncounterBrowserManager encounterBrowserManager,
							   EncounterMapper encounterMapper,
							   PatientBrowserManager patientBrowserManager
							   ) {
		this.encounterBrowserManager = encounterBrowserManager;
		this.encounterMapper = encounterMapper;
		this.patientBrowserManager = patientBrowserManager;
	}

	@PostMapping(value = "/encounters")
	@ResponseStatus(HttpStatus.CREATED)
	public EncounterDTO createEncounter(@RequestBody EncounterDTO encounterDTO) throws OHServiceException {
		LOGGER.info("Create encounter with {}", encounterDTO.getCode());
		if (encounterDTO.getPatientCode() == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient code must not be null"));
		}

		Patient patient = patientBrowserManager.getPatientById(encounterDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found"));
		}

		Encounter encounter = encounterMapper.map2Model(encounterDTO);
		encounter = encounterBrowserManager.saveEncounter(encounter);			
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create encounter"));
		}

		return encounterMapper.map2DTO(encounter);
	}

	@PatchMapping("/encounters/{code}/status")
	public EncounterDTO updateEncounterStatus(@PathVariable String code) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code :" + code), HttpStatus.NOT_FOUND);
		}
		if (encounter.getStatus().toString().equals(EncounterStatus.OPEN.toString())) {
			encounter.setStatus(EncounterStatus.CLOSE);
		} else {
			encounter.setStatus(EncounterStatus.OPEN);
		}
		encounter = encounterBrowserManager.saveEncounter(encounter);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to update encounter"));
		}
		return encounterMapper.map2DTO(encounter);
	}

	@GetMapping("/encounters/{patientId}")
	public List<EncounterDTO> getEncountersByPatient(@PathVariable int patientId) throws OHServiceException {
		 List<Encounter> encounters = encounterBrowserManager.getEncountersByPatient(patientId);
		 return encounterMapper.map2DTOList(encounters);
	}

	@GetMapping("/encounters/current/{patientId}")
	public EncounterDTO getCurrentEncounterByPatient(@PathVariable int patientId) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getCurrentEncounter(patientId);
		return encounterMapper.map2DTO(encounter);
	}

	@PatchMapping("/encounters/{code}")
	public EncounterDTO updateEncounterCode(@PathVariable String code, @RequestBody @Valid String newCode) throws OHServiceException {
		LOGGER.info("Update encounter with new code {}", newCode);
		Encounter encounterToUpdate = encounterBrowserManager.getEncountersByCode(code);
		if (encounterToUpdate == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found"));
		}

		if (newCode == null) {
			throw new OHAPIException(new OHExceptionMessage("New code should not be empty"));
		}

		encounterToUpdate.setCode(newCode);
		Encounter encounterUpdated = encounterBrowserManager.saveEncounter(encounterToUpdate);
		return encounterMapper.map2DTO(encounterUpdated);
	}
}
