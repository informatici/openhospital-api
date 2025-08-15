package org.isf.encounter.rest;

import java.util.List;

import org.isf.encounter.dto.EncounterDTO;
import org.isf.encounter.mapper.EncounterMapper;
import org.isf.encouter.manager.EncounterBrowserManager;
import org.isf.encouter.model.Encounter;
import org.isf.encouter.model.EncounterStatus;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

	public EncounterController(EncounterBrowserManager encounterBrowserManager, EncounterMapper encounterMapper) {
		this.encounterBrowserManager = encounterBrowserManager;
		this.encounterMapper = encounterMapper;
	}

	@PostMapping(value = "/encounters")
	public EncounterDTO createEncounter(@Valid @RequestBody EncounterDTO encounterDTO) throws OHServiceException {
		LOGGER.info("Create encounter {}", encounterDTO.getCode());
		Encounter encounter = encounterMapper.map2Model(encounterDTO);
		encounter = encounterBrowserManager.saveEncounter(encounter);			
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create encounter"));
		}
		return encounterMapper.map2DTO(encounter);
	}

	@PutMapping(value = "/encounters/{code}")
	public EncounterDTO updateEncounter(@PathVariable String code, @Valid @RequestBody EncounterDTO encounterDTO) throws OHServiceException {
		LOGGER.info("Update Encounter with code: '{}'.", code);
		if (!encounterDTO.getCode().equals(code)) {
			throw new OHAPIException(new OHExceptionMessage("Encounter code mismatch."));
		}
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code :" + code), HttpStatus.NOT_FOUND);
		}
		encounter = encounterMapper.map2Model(encounterDTO);
		encounter = encounterBrowserManager.saveEncounter(encounter);			
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to update encounter"));
		}
		return encounterMapper.map2DTO(encounter);
	}

	@PatchMapping("/encounters/{id}/status")
	public EncounterDTO updateEncounterStatus(@PathVariable String code, @RequestParam String status) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found with code :" + code), HttpStatus.NOT_FOUND);
		}
		encounter.setStatus(EncounterStatus.valueOf(status));
		encounter = encounterBrowserManager.saveEncounter(encounter);
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to update encounter"));
		}
		return encounterMapper.map2DTO(encounter);
	}

	@GetMapping("/encounters/{patientId}")
	public List<EncounterDTO> getEncountersByPatient(@PathVariable int patientId) throws OHServiceException {
		 List<Encounter> encounterDTOs = encounterBrowserManager.getEncountersByPatient(patientId);
		 return encounterDTOs.stream().map(e-> {
			 return encounterMapper.map2DTO(e);
		 }).toList();
	}
}
