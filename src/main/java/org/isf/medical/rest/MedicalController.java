/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medical.rest;

import java.util.List;

import javax.validation.Valid;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medical.dto.MedicalSortBy;
import org.isf.medical.mapper.MedicalMapper;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MedicalController.class);

	@Autowired
	private MedicalBrowsingManager medicalManager;
	
	@Autowired
	private MedicalMapper mapper;
	
	/**
	 * Returns the requested medical.
	 * @param code the medical code.
	 * @return the retrieved medical.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicals/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalDTO> getMedical(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Retrieving medical with code {} ...", code);
		Medical medical = medicalManager.getMedical(code);
		if (medical == null) {
			LOGGER.info("Medical not found");
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		} else {
			LOGGER.info("Medical retrieved successfully");
			return ResponseEntity.ok(mapper.map2DTO(medical));
		}
	}
	
	/**
	 * Returns all the medicals.
	 * @param sortBy - specifies by which property the medicals should be sorted
	 * @return all the medicals.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalDTO>> getMedicals(@RequestParam(name="sort_by", required=false) MedicalSortBy sortBy) 
			throws OHServiceException {
		LOGGER.info("Retrieving all the medicals...");
		List<Medical> medicals;
		if (sortBy == null || sortBy == MedicalSortBy.NONE) {
			medicals = medicalManager.getMedicals();
		}
		else if (sortBy == MedicalSortBy.CODE) {
			medicals = medicalManager.getMedicalsSortedByCode();
		} else { //sortBy == MedicalSortBy.NAME
			medicals = medicalManager.getMedicalsSortedByName();
		}
		if (medicals == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Error while retrieving medicals!", OHSeverityLevel.ERROR));
		}
		List<MedicalDTO> mappedMedicals = mapper.map2DTOList(medicals);
		if (mappedMedicals.isEmpty()) {
			LOGGER.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicals);
		} else {
			LOGGER.info("Found {} medicals", mappedMedicals.size());
			return ResponseEntity.ok(mappedMedicals);
		}
	}
	
	/**
	 * Returns all the medicals with the specified criteria.
	 * @param description - the medical description
	 * @param type - the medical type
	 * @param critical - {@code true} to include only medicals under critical level
	 * @param nameSorted - if {@code true} return the list in alphabetical order
	 * @return
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicals/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalDTO>> filterMedicals(
			@RequestParam(name="desc", required=false) String description,
			@RequestParam(name="type", required=false) String type,
			@RequestParam(name="critical", defaultValue="false") boolean critical,
			@RequestParam(name="name_sorted", defaultValue="false") boolean nameSorted) throws OHServiceException {
		LOGGER.info("Filtering the medicals...");
		List<Medical> medicals = null;
		if (description != null && type != null) {
			medicals = medicalManager.getMedicals(description, type, critical);
		}
		else if (description != null && type == null) {
			medicals = medicalManager.getMedicals(description);
		}
		else if (description == null && type != null) {
			medicals = medicalManager.getMedicals(type, nameSorted);
		}
		else if (nameSorted){
			medicals = medicalManager.getMedicals(null, nameSorted);
		}
		else {
			medicalManager.getMedicals();
		}
		
		if (medicals == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Error while retrieving medicals!", OHSeverityLevel.ERROR));
		}
		List<MedicalDTO> mappedMedicals = mapper.map2DTOList(medicals);
		if (mappedMedicals.isEmpty()) {
			LOGGER.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicals);
		} else {
			LOGGER.info("Found {} medicals", mappedMedicals.size());
			return ResponseEntity.ok(mappedMedicals);
		}
	}
	
	/**
	 * Saves the specified {@link Medical}.
	 * @param medicalDTO - the medical to save
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the medical was created
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalDTO> newMedical(
			@RequestBody MedicalDTO medicalDTO,
			@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar) throws OHServiceException {
		LOGGER.info("Creating a new medical ...");
		Medical isCreatedMedical = medicalManager.newMedical(mapper.map2Model(medicalDTO), ignoreSimilar);
		if (isCreatedMedical == null) {
			LOGGER.info("Medical is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "Medical is not created!", OHSeverityLevel.ERROR));
        }
		LOGGER.info("Medical successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedMedical));
	}
	
	/**
	 * Updates the specified {@link Medical}.
	 * @param medicalDTO - the medical to update
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status {@code HttpStatus.OK} if the medical was updated
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalDTO> updateMedical(
			@RequestBody @Valid MedicalDTO medicalDTO,
			@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar) throws OHServiceException {
		LOGGER.info("Updating a medical ...");
		Medical isUpdatedMedical = medicalManager.updateMedical(mapper.map2Model(medicalDTO), ignoreSimilar);
		if (isUpdatedMedical == null) {
			LOGGER.info("Medical is not updated!");
            throw new OHAPIException(new OHExceptionMessage(null, "Medical is not updated!", OHSeverityLevel.ERROR));
        }
		LOGGER.info("Medical successfully updated!");
        return ResponseEntity.status(HttpStatus.OK).body(mapper.map2DTO(isUpdatedMedical));
	}
	
	/**
	 * Deletes the specified {@link Medical}.
	 * @param code the medical to delete.
	 * @return {@code true} if the medical has been deleted.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/medicals/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedical(@PathVariable Integer code) throws OHServiceException {
		Medical medical = medicalManager.getMedical(code);
		if (medical == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		boolean isDeleted = medicalManager.deleteMedical(medical);
		return ResponseEntity.ok(isDeleted);
	}
}
