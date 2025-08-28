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
package org.isf.conditioning.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
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

import java.util.List;

@RestController
@Tag(name = "Conditioning")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditioningController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditioningController.class);

	private final ConditioningBrowserManager browserManager;
	private final ConditioningMapper conditioningMapper;
	private final UserBrowsingManager userBrowsingManager;
	private final PatientBrowserManager patientBrowserManager;

	public ConditioningController(ConditioningBrowserManager browserManager, ConditioningMapper conditioningMapper, UserBrowsingManager userBrowsingManager, PatientBrowserManager patientBrowserManager) {
		this.browserManager = browserManager;
		this.conditioningMapper = conditioningMapper;
		this.userBrowsingManager = userBrowsingManager;
		this.patientBrowserManager = patientBrowserManager;
	}

	/**
	 * Inserts a new conditioning.
	 *
	 * @param conditioningDTO the conditioning to insert.
	 * @return {@code true} if the conditioning has been successfully inserted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping("/conditioning")
	public ResponseEntity<ConditioningDTO> newConditioning(@RequestBody @Valid ConditioningDTO conditioningDTO) throws OHServiceException {

		Conditioning newConditioning = conditioningMapper.map2Model(conditioningDTO);

		if (conditioningDTO.getPatient() != null) {
			Patient patient = patientBrowserManager.getPatientById(conditioningDTO.getPatient().getCode());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
			}
			newConditioning.setPatient(patient);
		} else {
			throw new OHAPIException(new OHExceptionMessage("Patient is required."), HttpStatus.BAD_REQUEST);
		}

		if (conditioningDTO.getPerformBy() != null) {
			if (!userBrowsingManager.isUserNamePresent(conditioningDTO.getPerformBy().getUserName())) {
				throw new OHAPIException(new OHExceptionMessage("User not found."), HttpStatus.NOT_FOUND);
			}
		} else {
			throw new OHAPIException(new OHExceptionMessage("User is required."), HttpStatus.BAD_REQUEST);
		}

		browserManager.validateConditioning(newConditioning);

		Conditioning savedConditioning = browserManager.updateConditioning(newConditioning);
		if (savedConditioning == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not save."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(conditioningMapper.map2DTO(savedConditioning));
	}

	/**
	 * Retrieve all existing {@link Conditioning} records.
	 *
	 * @return a list of {@link ConditioningDTO} objects, empty if none found
	 * @throws OHServiceException When the retrieval operation fails
	 */
	@GetMapping("/conditioning/{patientCode}")
	public ResponseEntity<List<ConditioningDTO>> getConditioningByPatientCode(@PathVariable("patientCode") int patientCode) throws OHServiceException {
		LOGGER.info("get conditioning by patient code : {}", patientCode);

		List<Conditioning> conditioningList = browserManager.getConditioningByPatientCode(patientCode);
		if (conditioningList == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not found."), HttpStatus.NOT_FOUND);
		}
		List<ConditioningDTO> conditioningDTOS = conditioningList.stream()
			.map(conditioningMapper::map2DTO)
			.toList();
		return ResponseEntity.ok(conditioningDTOS);
	}

	/**
	 * Update an existing {@link Conditioning}.
	 *
	 * @param updateConditioningDTO Conditioning data to update
	 * @return updated {@link ConditioningDTO} if successful, or error message if not found or invalid
	 * @throws OHServiceException When the update operation fails
	 */
	@PutMapping("/conditioning/{id}")
	public ResponseEntity<ConditioningDTO> updateConditioning(@PathVariable("id") int id, @RequestBody @Valid ConditioningDTO updateConditioningDTO)
		throws OHServiceException {

		if (id != updateConditioningDTO.getId()) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning does not match."), HttpStatus.BAD_REQUEST);
		}
		Conditioning old = browserManager.getConditioningById(id);
		if (old == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not found with id "+ id), HttpStatus.NOT_FOUND);
		}

		Conditioning updateConditioning = conditioningMapper.map2Model(updateConditioningDTO);

		if (updateConditioningDTO.getPerformBy() != null) {
			User user = browserManager.getConditioningById(updateConditioningDTO.getId()).getPerformBy();
			if (user == null) {
				throw new OHAPIException(new OHExceptionMessage("User not found."), HttpStatus.NOT_FOUND);
			}
			updateConditioning.setPerformBy(user);
		} else {
			throw new OHAPIException(new OHExceptionMessage("User is required."), HttpStatus.BAD_REQUEST);
		}

		if (updateConditioningDTO.getPatient() != null) {
			Patient patient = browserManager.getConditioningById(updateConditioningDTO.getId()).getPatient();
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
			}
			updateConditioning.setPatient(patient);
		} else {
			throw new OHAPIException(new OHExceptionMessage("Patient is required."), HttpStatus.BAD_REQUEST);
		}

		browserManager.validateConditioning(updateConditioning);

		Conditioning updatedConditioning = browserManager.updateConditioning(updateConditioning);
		if (updatedConditioning == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok(conditioningMapper.map2DTO(updatedConditioning));
	}
}
