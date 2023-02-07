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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medicalstockward.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.dto.MedicalWardDTO;
import org.isf.medicalstockward.dto.MovementWardDTO;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.mapper.MedicalWardMapper;
import org.isf.medicalstockward.mapper.MovementWardMapper;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/medicalstockward", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalStockWardController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MedicalStockWardController.class);

	@Autowired
	private MedicalWardMapper medicalWardMapper;

	@Autowired
	private MovementWardMapper movementWardMapper;

	@Autowired
	private MovWardBrowserManager movWardBrowserManager;

	@Autowired
	private MedicalBrowsingManager medicalManager;

	@Autowired
	private WardBrowserManager wardManager;

	/**
	 * Gets all the {@link MedicalWard}s associated to the specified ward.
	 *
	 * @param wardId the ward id.
	 * @return the retrieved {@link MedicalWard}s.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicalstockward/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalWardDTO>> getMedicalsWard(@PathVariable("ward_code") char wardId) throws OHServiceException {
		List<MedicalWard> medWards = movWardBrowserManager.getMedicalsWard(wardId, true); //FIXME: provide provision for boolean ,false?
		List<MedicalWardDTO> mappedMedWards = medicalWardMapper.map2DTOList(medWards);
		if (mappedMedWards.isEmpty()) {
			LOGGER.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedWards);
		} else {
			LOGGER.info("Found {} medicals", mappedMedWards.size());
			return ResponseEntity.ok(mappedMedWards);
		}
	}

	/**
	 * Gets the current quantity for the specified {@link Medical} and specified {@link Ward}.
	 *
	 * @param wardId - if {@code null} the quantity is counted for the whole hospital
	 * @param medicalId - the {@link Medical} to check.
	 * @return the total quantity.
	 * @throws OHServiceException if an error occurs retrieving the quantity.
	 */
	@GetMapping(value = "/medicalstockward/current/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getCurrentQuantityInWard(
			@PathVariable("ward_code") String wardId,
			@RequestParam("med_id") int medicalId) throws OHServiceException {
		Medical medical = medicalManager.getMedical(medicalId);
		if (medical == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		List<Ward> wards = wardManager.getWards().stream().filter(w -> w.getCode().equals(wardId)).collect(Collectors.toList());
		if (wards == null || wards.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(movWardBrowserManager.getCurrentQuantityInWard(wards.get(0), medical));
	}

	/**
	 * Gets all the {@link MovementWard}s.
	 *
	 * @return all the retrieved movements ward.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getMovementWard() throws OHServiceException {
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movWardBrowserManager.getMovementWard());
		if (mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		} else {
			return ResponseEntity.ok(mappedMovs);
		}
	}

	/**
	 * Gets all the movement ward with the specified criteria.
	 *
	 * @param wardId the ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return all the retrieved movements.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicalstockward/movements/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getMovementWard(
			@PathVariable("ward_code") String wardId,
			@RequestParam("from") LocalDate dateFrom,
			@RequestParam("to") LocalDate dateTo) throws OHServiceException {

		LocalDateTime dateFromTime = null;
		if (dateFrom != null) {
			dateFromTime = dateFrom.atStartOfDay();
		}

		LocalDateTime dateToTime = null;
		if (dateTo != null) {
			dateToTime = dateTo.atStartOfDay();
		}

		List<MovementWard> movs = movWardBrowserManager.getMovementWard(wardId, dateFromTime, dateToTime);
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movs);
		if (mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		} else {
			return ResponseEntity.ok(mappedMovs);
		}
	}

	/**
	 * Gets all the movement wards with the specified criteria.
	 *
	 * @param idwardTo the target ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return all the retrieved movements.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicalstockward/movements/to/{target_ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getWardMovementsToWard(
			@PathVariable("target_ward_code") String idwardTo,
			@RequestParam("from") LocalDateTime dateFrom,
			@RequestParam("to") LocalDateTime dateTo) throws OHServiceException {

		List<MovementWard> movs = movWardBrowserManager.getWardMovementsToWard(idwardTo, dateFrom, dateTo);
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movs);
		if (mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		}
		return ResponseEntity.ok(mappedMovs);
	}

	/**
	 * Persists the specified movement.
	 *
	 * @param newMovementDTO the movement to persist.
	 * @return {@code true} if the movement has been persisted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMovementWard(@Valid @RequestBody MovementWardDTO newMovementDTO) throws OHServiceException {
		MovementWard newMovement = movementWardMapper.map2Model(newMovementDTO);
		movWardBrowserManager.newMovementWard(newMovement);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	/**
	 * Persists the specified movements.
	 *
	 * @param newMovementDTOs the movements to persist.
	 * @return {@code true} if the movements have been persisted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/medicalstockward/movements/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMovementWard(@Valid @RequestBody List<MovementWardDTO> newMovementDTOs) throws OHServiceException {
		List<MovementWard> newMovements = new ArrayList<>();
		newMovements.addAll(movementWardMapper.map2ModelList(newMovementDTOs));
		movWardBrowserManager.newMovementWard(newMovements);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	/**
	 * Updates the specified {@link MovementWard}.
	 *
	 * @param movementWardDTO the movement ward to update.
	 * @return {@code true} if the movement has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateMovementWard(@Valid @RequestBody MovementWardDTO movementWardDTO) throws OHServiceException {
		MovementWard movementWard = movementWardMapper.map2Model(movementWardDTO);
		boolean isPresent = movWardBrowserManager.getMovementWard().stream().anyMatch(mov -> mov.getCode() == movementWard.getCode());
		if (!isPresent) {
			throw new OHAPIException(new OHExceptionMessage(null, "Movement ward not found!", OHSeverityLevel.ERROR));
		}

		boolean isUpdated = movWardBrowserManager.updateMovementWard(movementWard);
		if (!isUpdated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Movement ward is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isUpdated);
	}

}
