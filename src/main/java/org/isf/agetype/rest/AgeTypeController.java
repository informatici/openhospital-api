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
package org.isf.agetype.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.mapper.AgeTypeMapper;
import org.isf.agetype.model.AgeType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "AgeTypes")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AgeTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AgeTypeController.class);

	private final AgeTypeBrowserManager ageTypeManager;

	private final AgeTypeMapper mapper;

	public AgeTypeController(AgeTypeBrowserManager ageTypeManager, AgeTypeMapper ageTypeMapper) {
		this.ageTypeManager = ageTypeManager;
		this.mapper = ageTypeMapper;
	}

	/**
	 * Get all the age types stored
	 *
	 * @return the list of age types found
	 * @throws OHServiceException When failed to get age types
	 */
	@GetMapping(value = "/agetypes")
	public List<AgeTypeDTO> getAllAgeTypes() throws OHServiceException {
		LOGGER.info("Get age types");

		return mapper.map2DTOList(ageTypeManager.getAgeType());
	}

	/**
	 * Update an age type
	 * @param ageTypeDTOs - the list of age types to be updated
	 * @return {@link AgeTypeDTO} the updated age type
	 * @throws OHServiceException When failed to update age type
	 */
	@PutMapping(value = "/agetypes")
	public List<AgeTypeDTO> updateAgeType(@Valid @RequestBody List<AgeTypeDTO> ageTypeDTOs) throws OHServiceException {

		ageTypeDTOs.forEach(ageTypeDTO -> {
			try {
				if (ageTypeDTO.getCode() == null || ageTypeDTO.getCode().trim().isEmpty() || ageTypeManager.getTypeByCode(ageTypeDTO.getCode()) == null) {
					try {
						throw new OHAPIException(new OHExceptionMessage("The age type with code "+ageTypeDTO.getCode()+" is not valid."));
					} catch (OHAPIException e) {
						throw new RuntimeException(e);
					}
				}
			} catch (OHServiceException e) {
				throw new RuntimeException(e);
			}
		});

		LOGGER.info("Updating age types");
		List<AgeType> ageTypes = mapper.map2ModelList(ageTypeDTOs);

		try {
			return mapper.map2DTOList(ageTypeManager.updateAgeType(ageTypes));
		} catch (OHServiceException ex) {
			throw new OHAPIException(
				new OHExceptionMessage("Unable to update age types. Please check that you've correctly set values"),
				HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}

	/**
	 * Get the code of an age type whose ages range includes a given age
	 * @param age - the given age
	 * @return the code of the age type matching the given age
	 * @throws OHServiceException When failed to get age type
	 */
	@GetMapping(value = "/agetypes/code")
	public Map<String, String> getAgeTypeCodeByAge(@RequestParam("age") int age) throws OHServiceException {
		LOGGER.info("Get age type by age: {}", age);

		String result = ageTypeManager.getTypeByAge(age);
		Map<String, String> responseBody = new HashMap<>();

		if (result != null){
			responseBody.put("code", result);
		} else {
			LOGGER.info("No corresponding age code for the given age");
		}

		return responseBody;
	}

	/**
	 * Gets the {@link AgeType} from the code index.
	 * @param index the code index.
	 * @return the retrieved element.
	 * @throws OHServiceException When failed to get age type
	 */
	@GetMapping(value = "/agetypes/{index}")
	public AgeTypeDTO getAgeTypeByIndex(@PathVariable int index) throws OHServiceException {
		LOGGER.info("Get age type by index: {}", index);
		AgeType result = ageTypeManager.getTypeByCode(index);

		if (result == null){
			LOGGER.info("No corresponding age code for the given index");
			throw new OHAPIException(new OHExceptionMessage("Age type not found with index :" + index), HttpStatus.NOT_FOUND);
		}

		return mapper.map2DTO(result);
	}
}
