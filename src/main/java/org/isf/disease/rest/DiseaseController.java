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
package org.isf.disease.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
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

@RestController(value = "/diseases")
@Tag(name = "Diseases")
@SecurityRequirement(name = "bearerAuth")
public class DiseaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseController.class);

	@Autowired
	protected DiseaseBrowserManager diseaseManager;
	
	@Autowired
	protected DiseaseMapper mapper;

	public DiseaseController(DiseaseBrowserManager diseaseManager, DiseaseMapper diseaseMapper) {
		this.diseaseManager = diseaseManager;
		this.mapper = diseaseMapper;
	}
	
	/**
	 * Gets all the stored {@link Disease} with ODP flag {@code true}.
	 * @return the stored diseases with ODP flag true.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/opd", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesOpd() throws OHServiceException {
        LOGGER.info("Get opd diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseOpd();
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting OPO diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets all the stored {@link Disease} with the specified typecode and flag ODP true.
	 * @param typeCode - the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/opd/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesOpdByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        LOGGER.info("Get opd diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseOpd(typeCode);
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting OPD diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets all the stored {@link Disease} with IPD_OUT flag {@code true}.
	 * @return the stored disease with IPD flag {@code true}.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/ipd/out", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdOut() throws OHServiceException {
        LOGGER.info("Get ipd out diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdOut();
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting IPD out diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets all the stored {@link Disease} with the specified typecode and the flag IPD_OUT {@code true}.
	 * @param typeCode - the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/ipd/out/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdOutByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        LOGGER.info("Get ipd out diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdOut(typeCode);
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting IPD out diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets all the stored {@link Disease} with IPD_IN flag {@code true}.
	 * @return the stored disease with IPD flag {@code true}.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/ipd/in", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdIn() throws OHServiceException {
        LOGGER.info("Get ipd-in diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdIn();
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting IPD-in diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets all the stored {@link Disease} with the specified typecode and the flag IPD_IN {@code true}.
	 * @param typeCode - the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/ipd/in/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdInByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        LOGGER.info("Get ipd-in diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdIn(typeCode);
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting IPD-in diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets both OPD and IPDs diseases.
	 * @return the stored diseases.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/both", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseases() throws OHServiceException {
        LOGGER.info("Get both ipd and opd diseases");
	    List<Disease> diseases = diseaseManager.getDisease();
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Retrieves all OPD and IPDs {@link Disease} with the specified typecode.
	 * @param typeCode - the filter typecode.
	 * @return all the diseases with the specified typecode.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/both/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseases(@PathVariable("typecode") String typeCode) throws OHServiceException {
        LOGGER.info("Get both ipd and opd diseases by type code");
	    List<Disease> diseases = diseaseManager.getDisease(typeCode);
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting diseases by type code."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Returns all diseases, deleted ones also
	 * @return the stored diseases.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getAllDiseases() throws OHServiceException {
        LOGGER.info("Get all diseases, deleted ones too");
	    List<Disease> diseases = diseaseManager.getDiseaseAll();
	    if (diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("Error getting all diseases."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Gets a {@link Disease} with the specified code.
	 * @param code - the disease code.
	 * @return the found disease.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/diseases/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> getDiseaseByCode(@PathVariable("code") String code) throws OHServiceException {
        LOGGER.info("Get disease by code");
        
	    Disease disease = diseaseManager.getDiseaseByCode(code);
	    if (disease != null) {
	    	return ResponseEntity.ok(mapper.map2DTO(disease));
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage("No disease found with the specified code."), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Stores the specified {@link Disease}.
	 * @param diseaseDTO - the disease to store.
	 * @return the stored disease
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> newDisease(@Valid @RequestBody DiseaseDTO diseaseDTO) throws OHServiceException {
		Disease disease = mapper.map2Model(diseaseDTO);
		if (diseaseManager.isCodePresent(disease.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Duplicated disease code."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (diseaseManager.descriptionControl(disease.getDescription(), disease.getType().getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Duplicated disease description for the same disease type."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			diseaseManager.newDisease(disease);
			return ResponseEntity.status(HttpStatus.CREATED).body(diseaseDTO);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Disease not created."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Updates the specified {@link Disease}.
	 * @param diseaseDTO - the disease to update.
	 * @return the updated disease
	 * @throws OHServiceException
	 */
	@PutMapping(value="/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> updateDisease(@Valid @RequestBody DiseaseDTO diseaseDTO) throws OHServiceException {
		Disease disease = mapper.map2Model(diseaseDTO);
		if (!diseaseManager.isCodePresent(disease.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Disease not found."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		disease.setLock(diseaseDTO.getLock());
		try {
			diseaseManager.updateDisease(disease);
			return ResponseEntity.ok(diseaseDTO);
		} catch (OHServiceException serviceException) {
        		throw new OHAPIException(new OHExceptionMessage("Disease not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
        	}
	}
	
	/**
	 * Mark as deleted the specified {@link Disease}.
	 * @param code - the code of the disease to mark delete.
	 * @return {@code true} if the disease has been marked, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/diseases/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Boolean>> deleteDisease(@PathVariable("code") String code) throws OHServiceException {
		Disease disease = diseaseManager.getDiseaseByCode(code);
		if (disease != null) {
			Map<String, Boolean> result = new HashMap<>();
			boolean isDeleted;
			try {
				diseaseManager.deleteDisease(disease);
				isDeleted = true;
			} catch (OHServiceException serviceException) {
				isDeleted = false;
			}
			result.put("deleted", isDeleted);
			return ResponseEntity.ok(result);
		} else {
			throw new OHAPIException(new OHExceptionMessage("No disease found with the specified code."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private ResponseEntity<List<DiseaseDTO>> computeResponse(List<Disease> diseases) {
		List<DiseaseDTO> diseasesDTO = mapper.map2DTOList(diseases);
        	if (diseasesDTO.isEmpty()) {
            		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseasesDTO);
        	} else {
            		return ResponseEntity.ok(diseasesDTO);
        	}
	}

}
