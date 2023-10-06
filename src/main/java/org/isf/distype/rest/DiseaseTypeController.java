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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.mapper.DiseaseTypeMapper;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

@RestController(value = "/diseasetypes")
@Tag(name = "Disease Types")
@SecurityRequirement(name = "bearerAuth")
public class DiseaseTypeController {

	@Autowired
	private DiseaseTypeBrowserManager diseaseTypeManager;
	
	@Autowired
	protected DiseaseTypeMapper mapper;

	public DiseaseTypeController(DiseaseTypeBrowserManager diseaseTypeManager, DiseaseTypeMapper diseaseTypeMapper) {
		this.diseaseTypeManager = diseaseTypeManager;
		this.mapper = diseaseTypeMapper;
	}
	
	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseTypeDTO>> getAllDiseaseTypes() throws OHServiceException {
		List<DiseaseType> results = diseaseTypeManager.getDiseaseType();
		List<DiseaseTypeDTO> parsedResults=mapper.map2DTOList(results);
		if (!parsedResults.isEmpty()) {
			return ResponseEntity.ok(parsedResults);
        } else {
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(parsedResults);
        }
	}	
	
	/**
	 * Create a new {@link DiseaseType}.
	 * @param diseaseTypeDTO
	 * @return the disease type created
	 * @throws OHServiceException - in case of duplicated code or in case of error
	 */
	@PostMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseTypeDTO> newDiseaseType(@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO) throws OHServiceException {
        DiseaseType diseaseType = mapper.map2Model(diseaseTypeDTO);
        if (diseaseTypeManager.isCodePresent(diseaseType.getCode())) {
        	throw new OHAPIException(new OHExceptionMessage("Specified Disease Type code is already used."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
	        diseaseTypeManager.newDiseaseType(diseaseType);
        } catch (OHServiceException serviceException) {
	        throw new OHAPIException(new OHExceptionMessage("Disease Type is not created."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(diseaseTypeDTO);
	}
	
	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseTypeDTO - the disease type to update.
	 * @return the updated disease type
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseTypeDTO> updateDiseaseType(@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO) throws OHServiceException {
		DiseaseType diseaseType = mapper.map2Model(diseaseTypeDTO);
		if (!diseaseTypeManager.isCodePresent(diseaseType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Disease Type not found."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			diseaseTypeManager.updateDiseaseType(diseaseType);
			return ResponseEntity.ok(diseaseTypeDTO);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Disease Type not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param code - the code of the disease type to remove.
	 * @return {@code true} if the disease has been removed, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/diseasetypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Boolean>> deleteDiseaseType(@PathVariable String code) throws OHServiceException {
		Optional<DiseaseType> optDiseaseType = diseaseTypeManager.getDiseaseType()
				.stream()
				.filter(item -> item.getCode().equals(code))
				.findFirst();
		if (optDiseaseType.isPresent()) {
			try {
				diseaseTypeManager.deleteDiseaseType(optDiseaseType.get());
				Map<String, Boolean> result = new HashMap<>(1);
				result.put("deleted", true);
				return ResponseEntity.ok(result);
			} catch (OHServiceException serviceException) {
				throw new OHAPIException(new OHExceptionMessage("Disease Type not deleted."), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			throw new OHAPIException(new OHExceptionMessage("No Disease Type found with the given code."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
