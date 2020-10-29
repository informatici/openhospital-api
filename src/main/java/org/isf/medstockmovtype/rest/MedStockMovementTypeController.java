/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medstockmovtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.mapper.MovementTypeMapper;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.rest.MedicalTypeController;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeController.class);
	
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
		logger.info("Retrieving all the movement types ...");
		List<MovementType> movementTypes = manager.getMedicaldsrstockmovType();
		List<MovementTypeDTO> mappedMvments = mapper.map2DTOList(movementTypes);
		if(mappedMvments.isEmpty()) {
			logger.info("No movement type found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMvments);
		} else {
			logger.info("Found " + mappedMvments.size() + " movement types");
			return ResponseEntity.ok(mappedMvments);
		}
	}
	
	/**
	 * Get the  {@link MovementType} by its code
	 * @param code - the code of the movement type.
	 * @return {@link MovementType}.
	 */
	@GetMapping(value = "/medstockmovementtype/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovementTypeDTO> getMovementType(@PathVariable("code") String code) throws OHServiceException {
		MovementType foundMvmntType = manager.getMovementType(code);
		if(foundMvmntType == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Movement type not found!", OHSeverityLevel.ERROR));
		}
		else {
			return ResponseEntity.ok(mapper.map2DTO(foundMvmntType));
		}
	}
	
	/**
	 * Save the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to save.
	 * @return {@link ResponseEntity} with status <code>HttpStatus.CREATED</code> if the medical stock movement type has been saved.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newMedicaldsrstockmovType(@RequestBody @Valid MovementTypeDTO medicaldsrstockmovType) throws OHServiceException {
		boolean isCreated = manager.newMedicaldsrstockmovType(mapper.map2Model(medicaldsrstockmovType));
		if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Movement type is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Updates the specified {@link MovementType}.
	 * @param medicaldsrstockmovTypeDTO the medical stock movement type to update.
	 * @return {@link ResponseEntity} with status <code>HttpStatus.OK</code> if the medical stock movement type has been updated.
	 * @throws OHServiceException 
	 */
	@PutMapping(value = "/medstockmovementtype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateMedicaldsrstockmovType(@RequestBody @Valid MovementTypeDTO medicaldsrstockmovTypeDTO) throws OHServiceException {
		MovementType medicaldsrstockmovType = mapper.map2Model(medicaldsrstockmovTypeDTO);
		if(!manager.codeControl(medicaldsrstockmovType.getCode())) 
			throw new OHAPIException(new OHExceptionMessage(null, "Movement type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = manager.updateMedicaldsrstockmovType(medicaldsrstockmovType);
		if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Movement type is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);
	}
	
	/**
	 * Checks if the specified movement type's code is already used.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medstockmovementtype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isCodeUsed(@PathVariable String code) throws OHServiceException {
		return ResponseEntity.ok(manager.codeControl(code));
	}
	
	/**
	 * Deletes the specified movement type.
	 * @param code - the code of the medical stock movement type to delete.
	 * @return <code>true</code> if the medical stock movement type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/medstockmovementtype/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedicaldsrstockmovType(@PathVariable("code") String code) throws OHServiceException {
		List<MovementType> machedMvmntTypes = manager.getMedicaldsrstockmovType()
				.stream()
				.filter(item -> item.getCode().equals(code))
				.collect(Collectors.toList());
		if (machedMvmntTypes.size() > 0)
			return ResponseEntity.ok(manager.deleteMedicaldsrstockmovType(machedMvmntTypes.get(0)));
		else 
			throw new OHAPIException(new OHExceptionMessage(null, "Movement type not found!", OHSeverityLevel.ERROR));
	}
	
}








