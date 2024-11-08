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
package org.isf.pregtreattype.rest;

import java.util.List;

import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.mapper.PregnantTreatmentTypeMapper;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
@Tag(name = "Pregnant Treatment Types")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PregnantTreatmentTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PregnantTreatmentTypeController.class);

	private final PregnantTreatmentTypeBrowserManager pregnantTreatmentTypeManager;

	private final PregnantTreatmentTypeMapper mapper;

	public PregnantTreatmentTypeController(
		PregnantTreatmentTypeBrowserManager pregnantTreatmentTypeManager,
		PregnantTreatmentTypeMapper pregnantTreatmentTypemapper
	) {
		this.pregnantTreatmentTypeManager = pregnantTreatmentTypeManager;
		this.mapper = pregnantTreatmentTypemapper;
	}

	/**
	 * Create a new {@link PregnantTreatmentType}.
	 *
	 * @param pregnantTreatmentTypeDTO Pregnant Treatment Type payload
	 * @return the newly stored {@link PregnantTreatmentType} object.
	 * @throws OHServiceException When failed to create Pregnant Treatment Type
	 */
	@PostMapping(value = "/pregnanttreatmenttypes")
	@ResponseStatus(HttpStatus.CREATED)
	public PregnantTreatmentTypeDTO newPregnantTreatmentType(
		@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO
	) throws OHServiceException {
		LOGGER.info("Create PregnantTreatmentType {}", pregnantTreatmentTypeDTO.getCode());

		PregnantTreatmentType isCreatedPregnantTreatmentType = pregnantTreatmentTypeManager.newPregnantTreatmentType(
			mapper.map2Model(pregnantTreatmentTypeDTO)
		);

		if (isCreatedPregnantTreatmentType == null) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not created."));
		}

		return mapper.map2DTO(isCreatedPregnantTreatmentType);
	}

	/**
	 * Updates the specified {@link PregnantTreatmentType}.
	 *
	 * @param pregnantTreatmentTypeDTO Pregnant Treatment Type payload
	 * @return the updated {@link PregnantTreatmentType} object.
	 * @throws OHServiceException When failed to update Pregnant Treatment Type
	 */
	@PutMapping(value = "/pregnanttreatmenttypes/{code}")
	public PregnantTreatmentTypeDTO updatePregnantTreatmentTypes(
		@PathVariable String code,
		@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update PregnantTreatmentType code: {}", pregnantTreatmentTypeDTO.getCode());
		PregnantTreatmentType pregTreatType = mapper.map2Model(pregnantTreatmentTypeDTO);
		if (!pregnantTreatmentTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not found."), HttpStatus.NOT_FOUND);
		}
		PregnantTreatmentType isUpdatedPregnantTreatmentType = pregnantTreatmentTypeManager.updatePregnantTreatmentType(pregTreatType);
		if (isUpdatedPregnantTreatmentType == null) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not updated."));
		}

		return mapper.map2DTO(isUpdatedPregnantTreatmentType);
	}

	/**
	 * Get all the available {@link PregnantTreatmentType}s.
	 *
	 * @return a {@link List} of {@link PregnantTreatmentType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get Pregnant Treatment Types
	 */
	@GetMapping(value = "/pregnanttreatmenttypes")
	public List<PregnantTreatmentTypeDTO> getPregnantTreatmentTypes() throws OHServiceException {
		LOGGER.info("Get all pregnantTreatment Types ");

		List<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeManager.getPregnantTreatmentType();

		return mapper.map2DTOList(pregnantTreatmentTypes);
	}

	/**
	 * Delete the {@link PregnantTreatmentType} for the specified code.
	 *
	 * @param code Pregnant Treatment Type code
	 * @return {@code true} if the {@link PregnantTreatmentType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete Pregnant Treatment Type
	 */
	@DeleteMapping(value = "/pregnanttreatmenttypes/{code}")
	public boolean deletePregnantTreatmentType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete PregnantTreatment Type code: {}", code);
		List<PregnantTreatmentType> pregTreatTypes = pregnantTreatmentTypeManager.getPregnantTreatmentType();
		List<PregnantTreatmentType> pregTreatTypeFounds = pregTreatTypes
			.stream()
			.filter(ad -> ad.getCode().equals(code))
			.toList();

		if (pregTreatTypeFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			pregnantTreatmentTypeManager.deletePregnantTreatmentType(pregTreatTypeFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.error("Delete PregnantTreatment Type: {} failed.", code);
			throw new OHAPIException(new OHExceptionMessage("PregnantTreatment Type not deleted."));
		}
	}
}
