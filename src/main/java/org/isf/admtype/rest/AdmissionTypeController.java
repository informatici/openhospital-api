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
package org.isf.admtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@Tag(name = "AdmissionTypes")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AdmissionTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdmissionTypeController.class);

	private final AdmissionTypeBrowserManager admissionTypeManager;

	private final AdmissionTypeMapper mapper;

	public AdmissionTypeController(
		AdmissionTypeBrowserManager admissionTypeBrowserManager, AdmissionTypeMapper admissionTypemapper
	) {
		this.admissionTypeManager = admissionTypeBrowserManager;
		this.mapper = admissionTypemapper;
	}

	/**
	 * Create a new {@link AdmissionType}
	 * 
	 * @param admissionTypeDTO Admission Type payload
	 * @return {@code true} if the admission type has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create admission type
	 */
	@PostMapping(value = "/admissiontypes")
	@ResponseStatus(HttpStatus.CREATED)
	public AdmissionTypeDTO newAdmissionType(@RequestBody AdmissionTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();

		LOGGER.info("Create Admission Type {}", code);

		AdmissionType newAdmissionType = admissionTypeManager.newAdmissionType(mapper.map2Model(admissionTypeDTO));
		if (!admissionTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Admission Type is not created."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return mapper.map2DTO(newAdmissionType);
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 * 
	 * @param admissionTypeDTO Admission Type payload
	 * @return {@code true} if the admission type has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update the admission type
	 */
	@PutMapping(value = "/admissiontypes")
	public AdmissionTypeDTO updateAdmissionTypes(
		@RequestBody AdmissionTypeDTO admissionTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update admission types code: {}", admissionTypeDTO.getCode());

		AdmissionType admissionType = mapper.map2Model(admissionTypeDTO);
		if (!admissionTypeManager.isCodePresent(admissionType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Admission Type not found."));
		}

		return mapper.map2DTO(admissionTypeManager.updateAdmissionType(admissionType));
	}

	/**
	 * Get all the available {@link AdmissionType}s.
	 * 
	 * @return a {@link List} of {@link AdmissionType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get admission types
	 */
	@GetMapping(value = "/admissiontypes")
	public ResponseEntity<List<AdmissionTypeDTO>> getAdmissionTypes() throws OHServiceException {
		LOGGER.info("Get all Admission Types ");

		List<AdmissionType> admissionTypes = admissionTypeManager.getAdmissionType();
		List<AdmissionTypeDTO> admissionTypeDTOs = mapper.map2DTOList(admissionTypes);

		if (admissionTypeDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admissionTypeDTOs);
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	/**
	 * Delete {@link AdmissionType} for specified code.
	 * 
	 * @param code Admission Type Code
	 * @return {@code true} if the {@link AdmissionType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete admission type
	 */
	@DeleteMapping(value = "/admissiontypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean deleteAdmissionType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete Admission Type code: {}", code);

		if (!admissionTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Admission type not found with ID :" + code), HttpStatus.NOT_FOUND);
		}

		List<AdmissionType> admissionTypes = admissionTypeManager.getAdmissionType();
		List<AdmissionType> admissionTypesFound = admissionTypes.stream().filter(ad -> ad.getCode().equals(code)).toList();

		if (!admissionTypesFound.isEmpty()) {
			try {
				admissionTypeManager.deleteAdmissionType(admissionTypesFound.get(0));
			} catch (OHServiceException serviceException) {
				LOGGER.error("Delete Admission: {} failed.", code);
				throw new OHAPIException(new OHExceptionMessage("Admission not deleted."));
			}
		}

		return true;
	}
}
