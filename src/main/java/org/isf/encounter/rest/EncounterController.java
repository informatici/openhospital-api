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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
	public ResponseEntity<EncounterDTO> createEncounter(@RequestBody EncounterDTO encounterDTO) throws OHServiceException {
		LOGGER.info("Create encounter with {}", encounterDTO.getCode());
		if (encounterDTO.getPatientCode() == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient code must not be null"));
		}
		if (encounterBrowserManager.getEncountersByCode(encounterDTO.getCode()) != null) {
			throw new OHAPIException(new OHExceptionMessage("The encounter code is already in use."));
		}

		Patient patient = patientBrowserManager.getPatientById(encounterDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found"));
		}

		Encounter encounter = encounterMapper.map2Model(encounterDTO);
		encounter.setStatus(EncounterStatus.OPEN);
		encounter = encounterBrowserManager.saveEncounter(encounter);			
		if (encounter == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create encounter"));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(encounterMapper.map2DTO(encounter));
	}

	@PatchMapping("/encounters/{code}/status")
	public ResponseEntity<EncounterDTO> updateEncounterStatus(@PathVariable String code) throws OHServiceException {
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
		return ResponseEntity.status(HttpStatus.OK).body(encounterMapper.map2DTO(encounter));
	}

	@GetMapping("/encounters/{patientId}")
	public ResponseEntity<List<EncounterDTO>> getEncountersByPatient(@PathVariable int patientId) throws OHServiceException {
		LOGGER.info("Get patient encounters  with code {}", patientId);
		List<Encounter> encounters = encounterBrowserManager.getEncountersByPatient(patientId);
		 return ResponseEntity.status(HttpStatus.OK).body(encounterMapper.map2DTOList(encounters));
	}

	@GetMapping("/encounters/current/{patientId}")
	public ResponseEntity<EncounterDTO> getCurrentEncounterByPatient(@PathVariable int patientId) throws OHServiceException {
		Encounter encounter = encounterBrowserManager.getCurrentEncounter(patientId);
		if (encounter == null) {
			return null;
		}
		return ResponseEntity.status(HttpStatus.OK).body(encounterMapper.map2DTO(encounter));
	}

	@PatchMapping("/encounters/{code}")
	public ResponseEntity<EncounterDTO> updateEncounterCode(@PathVariable String code, @RequestBody EncounterDTO encounter) throws OHServiceException {
		LOGGER.info("Update encounter with new code {}", encounter.getCode());
		Encounter encounterToUpdate = encounterBrowserManager.getEncountersByCode(code);
		if (encounterToUpdate == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found"));
		}

		if (!Objects.equals(encounter.getPatientCode(), encounterToUpdate.getPatientCode())) {
			throw new OHAPIException(new OHExceptionMessage("The encounter and the patient do not match."));
		}

		if (encounter.getStatus() == EncounterStatus.CLOSE) {
			throw new OHAPIException(new OHExceptionMessage("You cannot modify the code of a closed encounter."));
		}

		if (encounterBrowserManager.getEncountersByCode(encounter.getCode()) != null) {
			throw new OHAPIException(new OHExceptionMessage("The encounter code is already in use."));
		}

		encounterToUpdate.setCode(encounter.getCode());
		Encounter encounterUpdated = encounterBrowserManager.saveEncounter(encounterToUpdate);
		return ResponseEntity.status(HttpStatus.OK).body(encounterMapper.map2DTO(encounterUpdated));
	}
}
