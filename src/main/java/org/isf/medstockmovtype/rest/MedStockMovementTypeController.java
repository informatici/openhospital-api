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
package org.isf.medstockmovtype.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.mapper.MovementTypeMapper;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.shared.FormatErrorMessage;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedStockMovementTypeController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MedStockMovementTypeController.class);

	@Autowired
	private MovementTypeMapper mapper;
	
	@Autowired
	private MedicaldsrstockmovTypeBrowserManager manager;
	
	/**
	 * Returns all the medical stock movement types.
	 * @return all the medical stock movement types.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementTypeDTO>> getMedicaldsrstockmovType() throws OHServiceException {
		LOGGER.info("Retrieving all the movement types ...");
		List<MovementType> movementTypes = new ArrayList<>();
		try {
			movementTypes = manager.getMedicaldsrstockmovType();
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		List<MovementTypeDTO> mappedMovements = mapper.map2DTOList(movementTypes);
		if (mappedMovements.isEmpty()) {
			LOGGER.info("No movement type found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovements);
		} else {
			LOGGER.info("Found {} movement types", mappedMovements.size());
			return ResponseEntity.ok(mappedMovements);
		}
	}
	
	/**
	 * Get the {@link MovementType} by its code.
	 * @param code - the code of the movement type.
	 * @return {@link MovementType}.
	 */
	@GetMapping(value = "/medstockmovementtype/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovementTypeDTO> getMovementType(@PathVariable("code") String code) throws OHServiceException {
		MovementType foundMvmntType;
		try {
			foundMvmntType = manager.getMovementType(code);
		} catch (Exception e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessage())));
		}
		if (foundMvmntType == null) {
			throw new OHAPIException(new OHExceptionMessage("medicalstockmovementtype.notfound"));
		}
		else {
			return ResponseEntity.ok(mapper.map2DTO(foundMvmntType));
		}
	}
	
	/**
	 * Save the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to save.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the medical stock movement type has been saved.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovementTypeDTO> newMedicaldsrstockmovType(@RequestBody @Valid MovementTypeDTO medicaldsrstockmovType) throws OHServiceException {
		MovementType isCreatedMovementType = manager.newMedicaldsrstockmovType(mapper.map2Model(medicaldsrstockmovType));
		try {
			isCreatedMovementType = manager.newMedicaldsrstockmovType(mapper.map2Model(medicaldsrstockmovType));
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (isCreatedMovementType == null) {
            throw new OHAPIException(new OHExceptionMessage("medicalstockmovementtype.notcreated"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedMovementType));
	}
	
	/**
	 * Updates the specified {@link MovementType}.
	 * @param medicaldsrstockmovTypeDTO the medical stock movement type to update.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical stock movement type has been updated.
	 * @throws OHServiceException 
	 */
	@PutMapping(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovementTypeDTO> updateMedicaldsrstockmovType(@RequestBody @Valid MovementTypeDTO medicaldsrstockmovTypeDTO) throws OHServiceException {
		MovementType medicaldsrstockmovType = mapper.map2Model(medicaldsrstockmovTypeDTO);
		boolean isPresent;
		try {
			isPresent = manager.isCodePresent(medicaldsrstockmovType.getCode());
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (!isPresent) {
			throw new OHAPIException(new OHExceptionMessage("medicalstockmovementtype.notfound"));
		}
		MovementType isUpdatedMovementType;
		try {
			isUpdatedMovementType = manager.updateMedicaldsrstockmovType(medicaldsrstockmovType);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (isUpdatedMovementType == null) {
            throw new OHAPIException(new OHExceptionMessage("medicalstockmovementtype.notupdated"));
        }
        return ResponseEntity.ok(mapper.map2DTO(isUpdatedMovementType));
	}
	
	/**
	 * Checks if the specified movement type's code is already used.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medstockmovementtype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isCodeUsed(@PathVariable String code) throws OHServiceException {
		boolean isPresent;
		try {
			isPresent = manager.isCodePresent(code);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		return ResponseEntity.ok(isPresent);
	}
	
	/**
	 * Deletes the specified movement type.
	 * @param code - the code of the medical stock movement type to delete.
	 * @return {@code true} if the medical stock movement type has been deleted, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/medstockmovementtype/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedicaldsrstockmovType(@PathVariable("code") String code) throws OHServiceException {
		List<MovementType> matchedMvmntTypes = new ArrayList<>();
		try {
			matchedMvmntTypes = manager.getMedicaldsrstockmovType()
					.stream()
					.filter(item -> item.getCode().equals(code))
					.collect(Collectors.toList());
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (!matchedMvmntTypes.isEmpty()) {
			return ResponseEntity.ok(manager.deleteMedicaldsrstockmovType(matchedMvmntTypes.get(0)));
		} else {
			throw new OHAPIException(new OHExceptionMessage("medicalstockmovementtype.notfound"));
		}
	}
	
}
