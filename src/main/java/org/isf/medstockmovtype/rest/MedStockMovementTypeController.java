/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medstockmovtype.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.medstockmovtype.manager.MedicalDsrStockMovementTypeBrowserManager;
import org.isf.medstockmovtype.mapper.MovementTypeMapper;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Medical Stock Movement Type")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MedStockMovementTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedStockMovementTypeController.class);

	private final MovementTypeMapper mapper;

	private final MedicalDsrStockMovementTypeBrowserManager manager;

	public MedStockMovementTypeController(MovementTypeMapper mapper, MedicalDsrStockMovementTypeBrowserManager manager) {
		this.mapper = mapper;
		this.manager = manager;
	}

	/**
	 * Returns all the medical stock movement types.
	 *
	 * @return all the medical stock movement types.
	 * @throws OHServiceException When failed to get movement types
	 */
	@GetMapping(value = "/medstockmovementtypes")
	public List<MovementTypeDTO> getMedicalDsrStockMovementType() throws OHServiceException {
		LOGGER.info("Retrieving all the movement types ...");
		return mapper.map2DTOList(manager.getMedicalDsrStockMovementType());
	}

	/**
	 * Get the {@link MovementType} by its code.
	 * @param code - the code of the movement type.
	 * @return {@link MovementType}.
	 */
	@GetMapping(value = "/medstockmovementtypes/{code}")
	public MovementTypeDTO getMovementType(@PathVariable("code") String code) throws OHServiceException {
		MovementType movementType = manager.getMovementType(code);
		if (movementType == null) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not found."), HttpStatus.NOT_FOUND);
		}

		return mapper.map2DTO(movementType);
	}

	/**
	 * Save the specified {@link MovementType}.
	 *
	 * @param medicalDsrStockMovementType the medical stock movement type to save.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if
	 * the medical stock movement type has been saved.
	 * @throws OHServiceException When failed to create movement type
	 */
	@PostMapping(value = "/medstockmovementtypes")
	@ResponseStatus(HttpStatus.CREATED)
	public MovementTypeDTO newMedicalDsrStockMovementType(
		@RequestBody @Valid MovementTypeDTO medicalDsrStockMovementType
	) throws OHServiceException {
		try {
			MovementType isCreatedMovementType = manager.newMedicalDsrStockMovementType(mapper.map2Model(medicalDsrStockMovementType));
			return mapper.map2DTO(isCreatedMovementType);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not created."));
		}
	}

	/**
	 * Updates the specified {@link MovementType}.
	 *
	 * @param medicalDsrStockMovementTypeDTO the medical stock movement type to update.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical
	 * stock movement type has been updated.
	 * @throws OHServiceException When failed to update movement type
	 */
	@PutMapping(value = "/medstockmovementtypes")
	public MovementTypeDTO updateMedicalDsrStockMovementType(
		@RequestBody @Valid MovementTypeDTO medicalDsrStockMovementTypeDTO
	) throws OHServiceException {
		MovementType medicalDsrStockMovementType = mapper.map2Model(medicalDsrStockMovementTypeDTO);
		if (!manager.isCodePresent(medicalDsrStockMovementType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return mapper.map2DTO(manager.updateMedicalDsrStockMovementType(medicalDsrStockMovementType));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not updated."));
		}
	}

	/**
	 * Checks if the specified movement type's code is already used.
	 *
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException When failed to check movement type code
	 */
	@GetMapping(value = "/medstockmovementtypes/check/{code}")
	public boolean isCodeUsed(@PathVariable String code) throws OHServiceException {
		return manager.isCodePresent(code);
	}

	/**
	 * Deletes the specified movement type.
	 *
	 * @param code - the code of the medical stock movement type to delete.
	 * @return {@code true} if the medical stock movement type has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete movement type
	 */
	@DeleteMapping(value = "/medstockmovementtypes/{code}")
	public boolean deleteMedicalDsrStockMovementType(
		@PathVariable("code") String code
	) throws OHServiceException {
		List<MovementType> matchedMovementTypes = manager.getMedicalDsrStockMovementType()
			.stream()
			.filter(item -> item.getCode().equals(code)).toList();
		if (matchedMovementTypes.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not found."), HttpStatus.NOT_FOUND);
		}
		try {
			manager.deleteMedicalDsrStockMovementType(matchedMovementTypes.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Movement type not deleted."));
		}
	}
}
