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
package org.isf.vactype.rest;

import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.mapper.VaccineTypeMapper;
import org.isf.vactype.model.VaccineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Vaccine Type")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VaccineTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VaccineTypeController.class);

	private final VaccineTypeBrowserManager vaccineTypeManager;

	private final VaccineTypeMapper mapper;

	public VaccineTypeController(
		VaccineTypeBrowserManager vaccineTypeManager,
		VaccineTypeMapper vaccineTypeMapper
	) {
		this.vaccineTypeManager = vaccineTypeManager;
		this.mapper = vaccineTypeMapper;
	}

	/**
	 * Get all the vaccine types.
	 *
	 * @return The list of vaccine types
	 * @throws OHServiceException When failed to get vaccine types
	 */
	@GetMapping("/vaccinetypes")
	public List<VaccineTypeDTO> getVaccineType() throws OHServiceException {
		LOGGER.info("Get vaccine types.");
		return mapper.map2DTOList(vaccineTypeManager.getVaccineType());
	}

	/**
	 * Create a new vaccine type.
	 *
	 * @param newVaccineType VaccineType payload
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to create vaccine type
	 */
	@PostMapping("/vaccinetypes")
	@ResponseStatus(HttpStatus.CREATED)
	public VaccineTypeDTO newVaccineType(@RequestBody VaccineTypeDTO newVaccineType) throws OHServiceException {
		LOGGER.info("Create vaccine type: {}", newVaccineType);
		try {
			return mapper.map2DTO(vaccineTypeManager.newVaccineType(mapper.map2Model(newVaccineType)));
		} catch (OHDataIntegrityViolationException e) {
			throw new OHAPIException(new OHExceptionMessage("Vaccine Type already present."));
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage("Vaccine Type not created."));
		}
	}

	/**
	 * Update a vaccine type.
	 *
	 * @param updateVaccineType VaccineType payload
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to update vaccine type
	 */
	@PutMapping("/vaccinetypes")
	public VaccineTypeDTO updateVaccineType(@RequestBody VaccineTypeDTO updateVaccineType) throws OHServiceException {
		LOGGER.info("Update vaccine type: {}", updateVaccineType);

		try {
			return mapper.map2DTO(vaccineTypeManager.updateVaccineType(mapper.map2Model(updateVaccineType)));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Vaccine Type not updated."));
		}
	}

	/**
	 * Delete a vaccine type.
	 *
	 * @param code the vaccineType to delete
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to delete vaccine type
	 */
	@DeleteMapping("/vaccinetypes/{code}")
	public boolean deleteVaccineType(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Delete vaccine type code: {}", code);

		VaccineType vaccineType = vaccineTypeManager.findVaccineType(code);
		if (vaccineType == null) {
			throw new OHAPIException(new OHExceptionMessage("Vaccine Type not found."), HttpStatus.NOT_FOUND);
		}
		try {
			vaccineTypeManager.deleteVaccineType(vaccineType);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Vaccine Type not deleted."));
		}
	}

	/**
	 * Check if the code is already used by other vaccine type.
	 *
	 * @param code Vaccine type code
	 * @return {@code true} if it is already used, {@code false} otherwise
	 * @throws OHServiceException When failed to check the vaccine type code
	 */
	@GetMapping("/vaccinetypes/check/{code}")
	public boolean checkVaccineTypeCode(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Check vaccine type code: {}", code);
		return vaccineTypeManager.isCodePresent(code);
	}
}
