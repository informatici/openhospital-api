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
package org.isf.medical.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medical.dto.MedicalSortBy;
import org.isf.medical.mapper.MedicalMapper;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Medicals")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalController.class);

	private final MedicalBrowsingManager medicalManager;

	private final MedicalMapper mapper;

	public MedicalController(MedicalBrowsingManager medicalManager, MedicalMapper mapper) {
		this.medicalManager = medicalManager;
		this.mapper = mapper;
	}
	/**
	 * Returns the requested medical.
	 * @param code the medical code.
	 * @return the retrieved medical.
	 * @throws OHServiceException When failed to get medical
	 */
	@GetMapping(value = "/medicals/{code}")
	public MedicalDTO getMedical(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Retrieving medical with code {} ...", code);
		Medical medical = medicalManager.getMedical(code);
		if (medical == null) {
			LOGGER.info("Medical not found.");
			throw new OHAPIException(new OHExceptionMessage("Medical not found."));
		}

		LOGGER.info("Medical {} retrieved successfully.", medical.getDescription());

		return mapper.map2DTO(medical);
	}

	/**
	 * Returns all the medicals.
	 * @param sortBy - specifies by which property the medicals should be sorted
	 * @return all the medicals.
	 * @throws OHServiceException When failed to get medicals
	 */
	@GetMapping(value = "/medicals")
	public List<MedicalDTO> getMedicals(
		@RequestParam(name="sort_by", required=false) MedicalSortBy sortBy
	) throws OHServiceException {
		LOGGER.info("Retrieving all the medicals...");

		List<Medical> medicals;
		if (sortBy == null || sortBy == MedicalSortBy.NONE) {
			medicals = medicalManager.getMedicals();
		} else if (sortBy == MedicalSortBy.CODE) {
			medicals = medicalManager.getMedicalsSortedByCode();
		} else { //sortBy == MedicalSortBy.NAME
			medicals = medicalManager.getMedicalsSortedByName();
		}

		if (medicals == null) {
			throw new OHAPIException(new OHExceptionMessage("Error while retrieving medicals."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return mapper.map2DTOList(medicals);
	}

	/**
	 * Returns all the medicals with the specified criteria.
	 * @param description - the medical description
	 * @param type - the medical type
	 * @param critical - {@code true} to include only medicals under critical level
	 * @param nameSorted - if {@code true} return the list in alphabetical order
	 * @return The filtered list of medicals
	 * @throws OHServiceException When failed to get medicals
	 */
	@GetMapping(value = "/medicals/filter")
	public List<MedicalDTO> filterMedicals(
		@RequestParam(name="desc", required=false) String description,
		@RequestParam(name="type", required=false) String type,
		@RequestParam(name="critical", defaultValue="false") boolean critical,
		@RequestParam(name="name_sorted", defaultValue="false") boolean nameSorted
	) throws OHServiceException {
		LOGGER.info("Filtering the medicals...");
		List<Medical> medicals;
		if (description != null && type != null) {
			medicals = medicalManager.getMedicals(description, type, critical);
		} else if (description != null) {
			medicals = medicalManager.getMedicals(description);
		} else if (type != null) {
			medicals = medicalManager.getMedicals(type, nameSorted);
		} else if (nameSorted){
			medicals = medicalManager.getMedicals(null, true);
		} else {
			medicals = medicalManager.getMedicals();
		}

		if (medicals == null) {
			throw new OHAPIException(new OHExceptionMessage("Error while retrieving medicals."));
		}

		return mapper.map2DTOList(medicals);
	}

	/**
	 * Saves the specified {@link Medical}.
	 * @param medicalDTO - the medical to save
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the medical was created
	 * @throws OHServiceException When failed to save the medical
	 */
	@PostMapping(value = "/medicals")
	@ResponseStatus(HttpStatus.CREATED)
	public MedicalDTO newMedical(
		@RequestBody MedicalDTO medicalDTO,
		@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar
	) throws OHServiceException {
		LOGGER.info("Creating a new medical ...");
		try {
			LOGGER.info("Medical successfully created.");
			return mapper.map2DTO(medicalManager.newMedical(mapper.map2Model(medicalDTO), ignoreSimilar));
		} catch (OHServiceException serviceException) {
			LOGGER.info("Medical is not created.");
			throw new OHAPIException(new OHExceptionMessage("Medical not created."));
		}
	}

	/**
	 * Updates the specified {@link Medical}.
	 * @param medicalDTO - the medical to update
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical was updated
	 * @throws OHServiceException When failed to update medical
	 */
	@PutMapping(value = "/medicals")
	public MedicalDTO updateMedical(
		@RequestBody @Valid MedicalDTO medicalDTO,
		@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar
	) throws OHServiceException {
		LOGGER.info("Updating a medical ...");
		try {
			LOGGER.info("Medical successfully updated.");
			return mapper.map2DTO(medicalManager.updateMedical(mapper.map2Model(medicalDTO), ignoreSimilar));
		} catch (OHServiceException serviceException) {
			LOGGER.info("Medical is not updated.");
			throw new OHAPIException(new OHExceptionMessage("Medical not updated."));
		}
	}

	/**
	 * Deletes the specified {@link Medical}.
	 * @param code the medical to delete.
	 * @return {@code true} if the medical has been deleted.
	 * @throws OHServiceException When failed to delete medical
	 */
	@DeleteMapping(value = "/medicals/{code}")
	public boolean deleteMedical(@PathVariable Integer code) throws OHServiceException {
		Medical medical = medicalManager.getMedical(code);
		if (medical == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical not found."), HttpStatus.NOT_FOUND);
		}
		try {
			medicalManager.deleteMedical(medical);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Medical not deleted"));
		}
	}
}
