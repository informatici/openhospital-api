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
package org.isf.medtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.medtype.dto.MedicalTypeDTO;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.mapper.MedicalTypeMapper;
import org.isf.medtype.model.MedicalType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
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
@Api(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalTypeController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MedicalTypeController.class);

	@Autowired
	private MedicalTypeBrowserManager medicalTypeBrowserManager;

	@Autowired
	private MedicalTypeMapper medicalTypeMapper;
	
	/**
	 * Retrieves all the medical types.
	 * @return the found medical types.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalTypeDTO>> getMedicalTypes() throws OHServiceException {
		LOGGER.info("Retrieving all the medical types ...");
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getMedicalType();
		List<MedicalTypeDTO> mappedMedicalTypes = medicalTypeMapper.map2DTOList(medicalTypes);
		if (mappedMedicalTypes.isEmpty()) {
			LOGGER.info("No medical type found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicalTypes);
		} else {
			LOGGER.info("Found {} medical types", mappedMedicalTypes.size());
			return ResponseEntity.ok(mappedMedicalTypes);
		}
	}
	
	/**
	 * Saves the specified medical type.
	 * @param medicalTypeDTO the medical type to save.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the medical type has been saved.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalTypeDTO> createMedicalType(@RequestBody @Valid MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		MedicalType isCreatedMedicalType = medicalTypeBrowserManager.newMedicalType(medicalTypeMapper.map2Model(medicalTypeDTO));
		if (isCreatedMedicalType == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Medical type is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalTypeMapper.map2DTO(isCreatedMedicalType));
	}
	
	/**
	 * Updates the specified medical type.
	 * @param medicalTypeDTO the medical type to update.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical type has been updated.
	 * @throws OHServiceException 
	 */
	@PutMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalTypeDTO> updateMedicalType(@RequestBody @Valid MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		MedicalType medicalType = medicalTypeMapper.map2Model(medicalTypeDTO);
		if (!medicalTypeBrowserManager.isCodePresent(medicalType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical type not found!", OHSeverityLevel.ERROR));
		}
		MedicalType isUpdatedMedicalType = medicalTypeBrowserManager.updateMedicalType(medicalType);
		if (isUpdatedMedicalType == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Medical type is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(medicalTypeMapper.map2DTO(isUpdatedMedicalType));
	}
	
	/**
	 * Checks if the specified medical type code is already used.
	 * @param code - the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicaltypes/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isCodeUsed(@PathVariable String code) throws OHServiceException {
		return ResponseEntity.ok(medicalTypeBrowserManager.isCodePresent(code));
	}

	/**
	 * Deletes the specified medical type.
	 * @param code - the code of the medical type to delete.
	 * @return {@code true} if the medical type has been deleted.
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/medicaltypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedicalType(@PathVariable("code") String code) throws OHServiceException {
		List<MedicalType> machedMedicalTypes = medicalTypeBrowserManager.getMedicalType()
				.stream()
				.filter(item -> item.getCode().equals(code))
				.collect(Collectors.toList());
		if (!machedMedicalTypes.isEmpty()) {
			return ResponseEntity.ok(medicalTypeBrowserManager.deleteMedicalType(machedMedicalTypes.get(0)));
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical type not found!", OHSeverityLevel.ERROR));
		}
	}
}
