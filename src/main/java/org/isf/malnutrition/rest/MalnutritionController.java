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
package org.isf.malnutrition.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.isf.malnutrition.dto.MalnutritionDTO;
import org.isf.malnutrition.manager.MalnutritionManager;
import org.isf.malnutrition.mapper.MalnutritionMapper;
import org.isf.malnutrition.model.Malnutrition;
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
@Tag(name = "Malnutritions")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MalnutritionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MalnutritionController.class);

	private final MalnutritionMapper mapper;

	private final MalnutritionManager manager;

	public MalnutritionController(MalnutritionMapper mapper, MalnutritionManager manager) {
		this.mapper = mapper;
		this.manager = manager;
	}

	/**
	 * Stores a new {@link Malnutrition}. The malnutrition object is updated with the generated id.
	 * @param malnutritionDTO the malnutrition to store.
	 * @return {@link ResponseEntity} with status {@code HttpStatus.CREATED} if the malnutrition has been stored
	 * @throws OHServiceException When failed to store the malnutrition
	 */
	@PostMapping(value = "/malnutritions")
	@ResponseStatus(HttpStatus.CREATED)
	public MalnutritionDTO newMalnutrition(
		@RequestBody @Valid MalnutritionDTO malnutritionDTO
	) throws OHServiceException{
		LOGGER.info("Creating a new malnutrition ...");
		Malnutrition isCreatedMalnutrition = manager.newMalnutrition(mapper.map2Model(malnutritionDTO));
		if (isCreatedMalnutrition == null) {
			LOGGER.info("Malnutrition is not created!");
			throw new OHAPIException(new OHExceptionMessage("Malnutrition not created."));
		}
		LOGGER.info("Malnutrition successfully created!");

		return mapper.map2DTO(isCreatedMalnutrition);
	}

	/**
	 * Retrieves all the {@link Malnutrition} associated to the given admission id.
	 * @param admissionID the admission id to use as filter.
	 * @return all the retrieved malnutrition.
	 * @throws OHServiceException When failed to get malnutrition
	 */
	@GetMapping(value = "/malnutritions/{id_admission}")
	public List<MalnutritionDTO> getMalnutrition(
		@PathVariable("id_admission") String admissionID
	) throws OHServiceException{
		LOGGER.info("Looking for malnutrition controls. Admission ID is {}", admissionID);

		List<Malnutrition> malnutritions = manager.getMalnutrition(admissionID);
		if (malnutritions == null) {
			throw new OHAPIException(new OHExceptionMessage("Error while retrieving malnutrition controls."));
		}

		return mapper.map2DTOList(malnutritions);
	}

	/**
	 * Returns the last {@link Malnutrition} entry for specified patient ID.
	 * @param patientID - the patient ID
	 * @return the last {@link Malnutrition} for specified patient ID.
	 * @throws OHServiceException When failed to get last malnutrition
	 */
	@GetMapping(value = "/malnutritions/last/{id_patient}")
	public MalnutritionDTO getLastMalnutrition(
		@PathVariable("id_patient") int patientID
	) throws OHServiceException {
		Malnutrition foundMalnutrition = manager.getLastMalnutrition(patientID);
		if (foundMalnutrition == null) {
			throw new OHAPIException(new OHExceptionMessage("No malnutrition found."));
		}

		return mapper.map2DTO(foundMalnutrition);
	}

	/**
	 * Updates the specified {@link Malnutrition}.
	 * @param malnutritionDTO the {@link Malnutrition} to update
	 * @return the updated {@link Malnutrition}
	 * @throws OHServiceException When failed to update malnutrition
	 */
	@PutMapping(value = "/malnutritions")
	public MalnutritionDTO updateMalnutrition(
		@RequestBody @Valid MalnutritionDTO malnutritionDTO
	) throws OHServiceException {
		return mapper.map2DTO(manager.updateMalnutrition(mapper.map2Model(malnutritionDTO)));
	}

	/**
	 * Deletes a given {@link Malnutrition}.
	 * @param code - the code of malnutrition to delete.
	 * @return {@code true} if the malnutrition has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete malnutrition
	 */
	@DeleteMapping(value = "/malnutritions")
	public boolean deleteMalnutrition(@RequestParam int code) throws OHServiceException{
		Malnutrition  malnutrition = manager.getMalnutrition(code);
		List<Malnutrition> malnutritions = manager.getMalnutrition(String.valueOf(malnutrition.getAdmission().getId()));
		List<Malnutrition> matchedMalnutritions = new ArrayList<>();
		if (malnutritions != null) {
			matchedMalnutritions = malnutritions
				.stream()
				.filter(item -> item.getCode() == code)
				.collect(Collectors.toList());
		}

		if (matchedMalnutritions.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Malnutrition control not found."));
		}

		try {
			manager.deleteMalnutrition(matchedMalnutritions.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Malnutrition not deleted."));
		}
	}
}
