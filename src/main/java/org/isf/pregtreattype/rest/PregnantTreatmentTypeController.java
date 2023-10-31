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
import java.util.stream.Collectors;

import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.mapper.PregnantTreatmentTypeMapper;
import org.isf.pregtreattype.model.PregnantTreatmentType;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/pregnanttreatmenttypes")
@Tag(name = "Pregnant Treatment Types")
@SecurityRequirement(name = "bearerAuth")
public class PregnantTreatmentTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PregnantTreatmentTypeController.class);

	@Autowired
	protected PregnantTreatmentTypeBrowserManager pregTreatTypeManager;
	
	@Autowired
	protected PregnantTreatmentTypeMapper mapper;

	public PregnantTreatmentTypeController(PregnantTreatmentTypeBrowserManager pregTreatTypeManager, PregnantTreatmentTypeMapper pregnantTreatmentTypemapper) {
		this.pregTreatTypeManager = pregTreatTypeManager;
		this.mapper = pregnantTreatmentTypemapper;
	}

	/**
	 * Create a new {@link PregnantTreatmentType}.
	 * @param pregnantTreatmentTypeDTO
	 * @return the newly stored {@link PregnantTreatmentType} object.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PregnantTreatmentTypeDTO> newPregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO) throws OHServiceException {
		String code = pregnantTreatmentTypeDTO.getCode();
		LOGGER.info("Create PregnantTreatmentType {}", code);
		PregnantTreatmentType isCreatedPregnantTreatmentType = pregTreatTypeManager.newPregnantTreatmentType(mapper.map2Model(pregnantTreatmentTypeDTO));
		if (isCreatedPregnantTreatmentType == null) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedPregnantTreatmentType));
	}

	/**
	 * Updates the specified {@link PregnantTreatmentType}.
	 * @param pregnantTreatmentTypeDTO
	 * @return the updated {@link PregnantTreatmentType} object.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pregnanttreatmenttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PregnantTreatmentTypeDTO> updatePregnantTreatmentTypes(@PathVariable String code, @RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO)
			throws OHServiceException {
		LOGGER.info("Update PregnantTreatmentType code: {}", pregnantTreatmentTypeDTO.getCode());
		PregnantTreatmentType pregTreatType = mapper.map2Model(pregnantTreatmentTypeDTO);
		if (!pregTreatTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not found."));
		}
		PregnantTreatmentType isUpdatedPregnantTreatmentType = pregTreatTypeManager.updatePregnantTreatmentType(pregTreatType);
		if (isUpdatedPregnantTreatmentType == null) {
			throw new OHAPIException(new OHExceptionMessage("Pregnant Treatment Type not updated."));
		}
		return ResponseEntity.ok(mapper.map2DTO(isUpdatedPregnantTreatmentType));
	}

	/**
	 * Get all the available {@link PregnantTreatmentType}s.
	 * @return a {@link List} of {@link PregnantTreatmentType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PregnantTreatmentTypeDTO>> getPregnantTreatmentTypes() throws OHServiceException {
		LOGGER.info("Get all pregnantTreatment Types ");
		List<PregnantTreatmentType> pregnantTreatmentTypes = pregTreatTypeManager.getPregnantTreatmentType();
		List<PregnantTreatmentTypeDTO> pregnantTreatmentTypeDTOs = mapper.map2DTOList(pregnantTreatmentTypes);
		if (pregnantTreatmentTypeDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(pregnantTreatmentTypeDTOs);
		} else {
			return ResponseEntity.ok(pregnantTreatmentTypeDTOs);
		}
	}

	/**
	 * Delete the {@link PregnantTreatmentType} for the specified code.
	 * @param code
	 * @return {@code true} if the {@link PregnantTreatmentType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pregnanttreatmenttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePregnantTreatmentType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete PregnantTreatment Type code: {}", code);
		if (pregTreatTypeManager.isCodePresent(code)) {
			List<PregnantTreatmentType> pregTreatTypes = pregTreatTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTreatTypeFounds = pregTreatTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (!pregTreatTypeFounds.isEmpty()) {
				try {
					pregTreatTypeManager.deletePregnantTreatmentType(pregTreatTypeFounds.get(0));
				} catch (OHServiceException serviceException) {
					LOGGER.info("Delete PregnantTreatment Type code: {} failed.", code);
					return ResponseEntity.ok(false);
				}
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(true);
	}

}
