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
		encounter.setStatus(EncounterStatus.OPEN);
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

	@PatchMapping("/encounters/{code}")
	public EncounterDTO updateEncounter(@PathVariable String code, @RequestBody EncounterDTO encounter) throws OHServiceException {
		LOGGER.info("Update encounter with new code {}", encounter.getCode());
		Encounter encounterToUpdate = encounterBrowserManager.getEncountersByCode(code);
		if (encounterToUpdate == null) {
			throw new OHAPIException(new OHExceptionMessage("Encounter not found"));
		}

		if (!Objects.equals(encounter.getPatient().getCode(), encounterToUpdate.getPatient().getCode())) {
			throw new OHAPIException(new OHExceptionMessage("The encounter and the patient do not match."));
		}

		Encounter encounterFound = encounterBrowserManager.getEncountersByCode(encounter.getCode());
		if (encounterFound != null && !Objects.equals(encounterFound.getCode(), encounterToUpdate.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("The encounter code is already in use."));
		}

		if (encounter.getStatus() == null) {
			encounter.setStatus(EncounterStatus.OPEN);
		}

		encounter.setPerformedAt(encounterToUpdate.getPerformedAt());

		Encounter encounterToUpdated = encounterMapper.map2Model(encounter);

		encounterBrowserManager.saveEncounter(encounterToUpdated);
		return encounter;
	}
}
