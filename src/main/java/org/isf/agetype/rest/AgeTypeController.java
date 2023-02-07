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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.agetype.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.mapper.AgeTypeMapper;
import org.isf.agetype.model.AgeType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value="/agetypes",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class AgeTypeController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AgeTypeController.class);

	@Autowired
	private AgeTypeBrowserManager ageTypeManager;
	
	@Autowired
	private AgeTypeMapper mapper;
	
	public AgeTypeController(AgeTypeBrowserManager ageTypeManager, AgeTypeMapper ageTypeMapper) {
		this.ageTypeManager = ageTypeManager;
		this.mapper = ageTypeMapper;
	}
	
	/**
	 * Get all the age types stored
	 * @return the list of age types found
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/agetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AgeTypeDTO>> getAllAgeTypes() throws OHServiceException {
		LOGGER.info("Get age types");
		List<AgeType> results = ageTypeManager.getAgeType();
		List<AgeTypeDTO> parsedResults = mapper.map2DTOList(results);
		if (!parsedResults.isEmpty()) {
			return ResponseEntity.ok(parsedResults);
        } else {
        	LOGGER.info("Empty age types list");
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(parsedResults);
        }
	}
	
	/**
	 * Update an age type
	 * @param ageTypeDTO - the age type to be updated
	 * @return {@link AgeTypeDTO} the updated age type
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/agetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<AgeTypeDTO> updateAgeType(@Valid @RequestBody AgeTypeDTO ageTypeDTO) throws OHServiceException {
		if (ageTypeDTO.getCode() == null || ageTypeDTO.getCode().trim().isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "The age type is not valid!", OHSeverityLevel.ERROR));
		}
		LOGGER.info("Update age type");
		AgeType ageType = mapper.map2Model(ageTypeDTO);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(ageType);
		if (ageTypeManager.updateAgeType(ageTypes)) {
			return ResponseEntity.ok(ageTypeDTO);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, 
					"The age type is not updated!", 
					OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Get the code of an age type whose ages range includes a given age
	 * @param age - the given age
	 * @return the code of the age type matching the given age
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/agetypes/code", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> getAgeTypeCodeByAge(@RequestParam("age") int age) throws OHServiceException {
		LOGGER.info("Get age type by age: {}", age);
		String result = ageTypeManager.getTypeByAge(age);
		Map<String, String> responseBody = new HashMap<>();
		if (result != null){
			responseBody.put("code", result);
			return ResponseEntity.ok(responseBody);
        } else {
        	LOGGER.info("No corresponding age code for the given age");
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseBody);
        }
	}
	
	/**
	 * Gets the {@link AgeType} from the code index.
	 * @param index the code index.
	 * @return the retrieved element.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/agetypes/{index}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AgeType> getAgeTypeByIndex(@PathVariable int index) throws OHServiceException {
		LOGGER.info("Get age type by index: {}", index);
		AgeType result = ageTypeManager.getTypeByCode(index);
		if (result != null){
			return ResponseEntity.ok(result);
        } else {
        	LOGGER.info("No corresponding age code for the given index");
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
	}
	
}
