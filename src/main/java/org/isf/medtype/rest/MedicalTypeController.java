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
package org.isf.medtype.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.medtype.dto.MedicalTypeDTO;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.mapper.MedicalTypeMapper;
import org.isf.medtype.model.MedicalType;
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
@Tag(name = "Medical Types")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalTypeController.class);

	private final MedicalTypeBrowserManager medicalTypeBrowserManager;

	private final MedicalTypeMapper medicalTypeMapper;

	public MedicalTypeController(
		MedicalTypeBrowserManager medicalTypeBrowserManager, MedicalTypeMapper medicalTypeMapper
	) {
		this.medicalTypeBrowserManager = medicalTypeBrowserManager;
		this.medicalTypeMapper = medicalTypeMapper;
	}

	/**
	 * Retrieves all the medical types.
	 * @return the found medical types.
	 * @throws OHServiceException When failed to get medical types
	 */
	@GetMapping(value = "/medicaltypes")
	public List<MedicalTypeDTO> getMedicalTypes() throws OHServiceException {
		LOGGER.info("Retrieving all the medical types ...");
		return medicalTypeMapper.map2DTOList(medicalTypeBrowserManager.getMedicalType());
	}

	/**
	 * Saves the specified medical type.
	 * @param medicalTypeDTO the medical type to save.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the medical type has been saved.
	 * @throws OHServiceException When failed to create medical type
	 */
	@PostMapping(value = "/medicaltypes")
	@ResponseStatus(HttpStatus.CREATED)
	public MedicalTypeDTO createMedicalType(@RequestBody @Valid MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		MedicalType isCreatedMedicalType = medicalTypeBrowserManager.newMedicalType(
			medicalTypeMapper.map2Model(medicalTypeDTO)
		);

		if (isCreatedMedicalType == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical type not created."));
		}

		return medicalTypeMapper.map2DTO(isCreatedMedicalType);
	}

	/**
	 * Updates the specified medical type.
	 * @param medicalTypeDTO the medical type to update.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical type has been updated.
	 * @throws OHServiceException When failed to update medical type
	 */
	@PutMapping(value = "/medicaltypes")
	public MedicalTypeDTO updateMedicalType(@RequestBody @Valid MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		MedicalType medicalType = medicalTypeMapper.map2Model(medicalTypeDTO);
		if (!medicalTypeBrowserManager.isCodePresent(medicalType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Medical type not found."), HttpStatus.NOT_FOUND);
		}

		MedicalType isUpdatedMedicalType = medicalTypeBrowserManager.updateMedicalType(medicalType);

		if (isUpdatedMedicalType == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical type not updated."));
		}

		return medicalTypeMapper.map2DTO(isUpdatedMedicalType);
	}

	/**
	 * Checks if the specified medical type code is already used.
	 * @param code - the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException When failed to check if medical type code is used
	 */
	@GetMapping(value = "/medicaltypes/check/{code}")
	public boolean isCodeUsed(@PathVariable String code) throws OHServiceException {
		return medicalTypeBrowserManager.isCodePresent(code);
	}

	/**
	 * Deletes the specified medical type.
	 * @param code - the code of the medical type to delete.
	 * @return {@code true} if the medical type has been deleted.
	 * @throws OHServiceException When failed to delete medical type
	 */
	@DeleteMapping(value = "/medicaltypes/{code}")
	public boolean deleteMedicalType(@PathVariable("code") String code) throws OHServiceException {
		List<MedicalType> matchedMedicalTypes = medicalTypeBrowserManager.getMedicalType()
			.stream()
			.filter(item -> item.getCode().equals(code)).toList();

		if (matchedMedicalTypes.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Medical type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			medicalTypeBrowserManager.deleteMedicalType(matchedMedicalTypes.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Medical type not deleted."));
		}
	}
}
