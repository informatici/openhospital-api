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
package org.isf.distype.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.mapper.DiseaseTypeMapper;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
@Tag(name = "Disease Types")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DiseaseTypeController {

	private final DiseaseTypeBrowserManager diseaseTypeManager;

	private final DiseaseTypeMapper mapper;

	public DiseaseTypeController(DiseaseTypeBrowserManager diseaseTypeManager, DiseaseTypeMapper diseaseTypeMapper) {
		this.diseaseTypeManager = diseaseTypeManager;
		this.mapper = diseaseTypeMapper;
	}

	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHServiceException When failed to get disease type
	 */
	@GetMapping(value = "/diseasetypes")
	public List<DiseaseTypeDTO> getAllDiseaseTypes() throws OHServiceException {
		return mapper.map2DTOList(diseaseTypeManager.getDiseaseType());
	}

	/**
	 * Create a new {@link DiseaseType}.
	 * @param diseaseTypeDTO Disease type payload
	 * @return the disease type created
	 * @throws OHServiceException - in case of duplicated code or in case of error
	 */
	@PostMapping(value = "/diseasetypes")
	@ResponseStatus(HttpStatus.CREATED)
	public DiseaseTypeDTO newDiseaseType(
		@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO
	) throws OHServiceException {
		DiseaseType diseaseType = mapper.map2Model(diseaseTypeDTO);
		if (diseaseTypeManager.isCodePresent(diseaseType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Specified Disease Type code is already used."));
		}

		try {
			return mapper.map2DTO(diseaseTypeManager.newDiseaseType(diseaseType));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create disease type."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseTypeDTO - the disease type to update.
	 * @return the updated disease type
	 * @throws OHServiceException When failed to update disease type
	 */
	@PutMapping(value = "/diseasetypes")
	public DiseaseTypeDTO updateDiseaseType(@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO) throws OHServiceException {
		DiseaseType diseaseType = mapper.map2Model(diseaseTypeDTO);
		if (!diseaseTypeManager.isCodePresent(diseaseType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Disease Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return mapper.map2DTO(diseaseTypeManager.updateDiseaseType(diseaseType));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Disease Type not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param code - the code of the disease type to remove.
	 * @return {@code true} if the disease has been removed, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete disease type
	 */
	@DeleteMapping(value = "/diseasetypes/{code}")
	public boolean deleteDiseaseType(@PathVariable String code) throws OHServiceException {
		DiseaseType diseaseType = diseaseTypeManager.getDiseaseType(code);
		if (diseaseType == null) {
			throw new OHAPIException(new OHExceptionMessage("No Disease Type found with the given code."), HttpStatus.NOT_FOUND);
		}

		try {
			diseaseTypeManager.deleteDiseaseType(diseaseType);
			return true;
		} catch (OHServiceException e) {
			return false;
		}
	}
}
