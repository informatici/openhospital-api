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
package org.isf.medicalstock.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.dto.LotDTO;
import org.isf.medicalstock.dto.MovementDTO;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.mapper.LotMapper;
import org.isf.medicalstock.mapper.MovementMapper;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.ward.model.Ward;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Stock Movements")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StockMovementController {
	private final MovementMapper movMapper;

	private final LotMapper lotMapper;

	private final MovBrowserManager movManager;

	private final MovStockInsertingManager movInsertingManager;

	private final MedicalBrowsingManager medicalManager;

	public StockMovementController(
		MovementMapper movMapper,
		LotMapper lotMapper,
		MovBrowserManager movManager,
		MovStockInsertingManager movInsertingManager,
		MedicalBrowsingManager medicalManager
	) {
		this.movMapper = movMapper;
		this.lotMapper = lotMapper;
		this.movManager = movManager;
		this.movInsertingManager = movInsertingManager;
		this.medicalManager = medicalManager;
	}

	/**
	 * Insert a list of charging {@link Movement}s and related {@link Lot}s.
	 *
	 * @param movementDTOs - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * 		   if {@link null}, each movement must have a different referenceNumber
	 * @return <code>true</code> if the movements have been created, false or
	 * throw exception otherwise
	 * @throws OHServiceException When failed to create movements
	 */
	@PostMapping(value = "/stockmovements/charge")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newMultipleChargingMovements(
		@RequestBody List<MovementDTO> movementDTOs,
		@RequestParam(name="ref", required=true) String referenceNumber
	) throws OHServiceException {
		List<Movement> movements = new ArrayList<>(movMapper.map2ModelList(movementDTOs));
		movInsertingManager.newMultipleChargingMovements(movements, referenceNumber);

		return true;
	}

	/**
	 * Insert a list of discharging {@link Movement}s.
	 *
	 * @param movementDTOs - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * 		   if {@link null}, each movement must have a different referenceNumber
	 * @return <code>true</code> if the movements have been created, false or
	 * throw exception otherwise
	 * @throws OHServiceException When failed to create movements
	 */
	@PostMapping(value = "/stockmovements/discharge")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newMultipleDischargingMovements(
		@RequestBody List<MovementDTO> movementDTOs,
		@RequestParam(name="ref", required=true) String referenceNumber
	) throws OHServiceException {
		List<Movement> movements = new ArrayList<>(movMapper.map2ModelList(movementDTOs));
		movInsertingManager.newMultipleDischargingMovements(movements, referenceNumber);

		return true;
	}

	/**
	 * Retrieves all the {@link Movement}s.
	 * @return the retrieved movements.
	 * @throws OHServiceException When failed to get movements
	 */
	@GetMapping(value = "/stockmovements")
	public List<MovementDTO> getMovements() throws OHServiceException {
		return movMapper.map2DTOList(movManager.getMovements());
	}

	/**
	 * Retrieves all the movement associated to the specified {@link Ward}.
	 *
	 * @param wardId Ward code
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @return the retrieved movements.
	 * @throws OHServiceException When failed to get movements
	 */
	@GetMapping(value = "/stockmovements/filter/v1")
	public List<MovementDTO> getMovements(
		@RequestParam("ward_id") String wardId,
		@RequestParam("from") LocalDateTime dateFrom,
		@RequestParam("to") LocalDateTime dateTo
	) throws OHServiceException {
		return movMapper.map2DTOList(movManager.getMovements(wardId, dateFrom, dateTo));
	}

	/**
	 * Retrieves all the movement associated to the specified reference number.
	 * @param refNo Reference number
	 * @return the retrieved movements
	 * @throws OHServiceException When failed to get movements
	 */
	@GetMapping(value = "/stockmovements/{ref}")
	public List<MovementDTO> getMovements(@PathVariable("ref") String refNo) throws OHServiceException {
		return movMapper.map2DTOList(movManager.getMovementsByReference(refNo));
	}

	/**
	 * Retrieves all the {@link Movement}s with the specified criteria.
	 * @param medicalCode Medical code
	 * @param medicalType Medical type
	 * @param wardId Ward code
	 * @param movType Movement type
	 * @param movFrom Movement start date
	 * @param movTo Movement end date
	 * @param lotPrepFrom Lot preparation start date
	 * @param lotPrepTo Lot preparation end date
	 * @param lotDueFrom Lot expiration start date
	 * @param lotDueTo Lot expiration end date
	 * @return the retrieved movements.
	 * @throws OHServiceException When failed to get movement
	 */
	@GetMapping(value = "/stockmovements/filter/v2")
	public List<MovementDTO> getMovements(
		@RequestParam(name="med_code", required=false) Integer medicalCode,
		@RequestParam(name="med_type", required=false) String medicalType,
		@RequestParam(name="ward_id", required=false) String wardId,
		@RequestParam(name="mov_type", required=false) String movType,
		@RequestParam(name="mov_from", required=false) LocalDateTime movFrom,
		@RequestParam(name="mov_to", required=false) LocalDateTime movTo,
		@RequestParam(name="lot_prep_from", required=false) LocalDateTime lotPrepFrom,
		@RequestParam(name="lot_prep_to", required=false) LocalDateTime lotPrepTo,
		@RequestParam(name="lot_due_from", required=false) LocalDateTime lotDueFrom,
		@RequestParam(name="lot_due_to", required=false) LocalDateTime lotDueTo
	) throws OHServiceException {
		return movMapper.map2DTOList(
			movManager.getMovements(
				medicalCode, medicalType, wardId, movType, movFrom, movTo, lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo
			)
		);
	}

	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}, expiring first on top
	 * @param medCode Medical code
	 * @return the retrieved lots.
	 * @throws OHServiceException When failed to get lot movements
	 */
	@GetMapping(value = "/stockmovements/lot/{med_code}")
	public List<LotDTO> getLotByMedical(@PathVariable("med_code") int medCode) throws OHServiceException {
		Medical med = medicalManager.getMedical(medCode);
		if (med == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical not found."));
		}

		return lotMapper.map2DTOList(movInsertingManager.getLotByMedical(med));
	}

	/**
	 * Checks if the provided quantity is under the medical limits. 
	 * @param medCode Medical code
	 * @param specifiedQuantity Quantity to check
	 * @return {@code true} if is under the limit, false otherwise
	 * @throws OHServiceException When failed to check medical quantity
	 */
	@GetMapping(value = "/stockmovements/critical/check")
	public boolean alertCriticalQuantity(
		@RequestParam("med_code") int medCode,
		@RequestParam("qty") int specifiedQuantity
	) throws OHServiceException {
		Medical med = medicalManager.getMedical(medCode);
		if (med == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical not found."));
		}

		return movInsertingManager.alertCriticalQuantity(med, specifiedQuantity);
	}
}
