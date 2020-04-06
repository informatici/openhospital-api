package org.isf.distype.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.distype.dto.DiseaseTypeDTO;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value="/diseasetypes",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="basicAuth")})
public class DiseaseTypeController {
	@Autowired
	private DiseaseTypeBrowserManager diseaseTypeManager;
	
	private final Logger logger = LoggerFactory.getLogger(DiseaseTypeController.class);

	public DiseaseTypeController(DiseaseTypeBrowserManager diseaseTypeManager) {
		this.diseaseTypeManager = diseaseTypeManager;
	}
	
	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseTypeDTO>> getAllDiseaseTypes() throws OHServiceException {
		List<DiseaseType> results = diseaseTypeManager.getDiseaseType();
		List<DiseaseTypeDTO> parsedResults= results.stream()
												.map(item -> getObjectMapper().map(item, DiseaseTypeDTO.class))
												.collect(Collectors.toList());
		if(parsedResults.size() > 0){
			return ResponseEntity.ok(parsedResults);
        }else{
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(parsedResults);
        }
	}	
	
	/**
	 * Create a new disease type.
	 * @param diseaseTypeDTO
	 * @return the disease type created
	 * @throws OHServiceException - in case of duplicated code or in case of error
	 */
	@PostMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseTypeDTO> newDiseaseType(@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO) throws OHServiceException {
        DiseaseType diseaseType = getObjectMapper().map(diseaseTypeDTO, DiseaseType.class);
        logger.info("creating new disease type with code `"+ diseaseType.getCode() + "`");
        if(diseaseTypeManager.codeControl(diseaseType.getCode())) {
        	throw new OHAPIException(new OHExceptionMessage(null, 
        			"specified code is already used!", 
        			OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if(diseaseTypeManager.newDiseaseType(diseaseType)) {
        	return ResponseEntity.status(HttpStatus.CREATED).body(diseaseTypeDTO);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, 
        			"disease type is not created!", 
        			OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseTypeDTO - the disease type to update.
	 * @return the updated disease type
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/diseasetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseTypeDTO> updateDiseaseType(@Valid @RequestBody DiseaseTypeDTO diseaseTypeDTO) throws OHServiceException {
        DiseaseType diseaseType = getObjectMapper().map(diseaseTypeDTO, DiseaseType.class);
        if(!diseaseTypeManager.codeControl(diseaseType.getCode())) {
        	throw new OHAPIException(new OHExceptionMessage(null, 
        			"disease type not found!", 
        			OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if(diseaseTypeManager.updateDiseaseType(diseaseType)) {
        	return ResponseEntity.ok(diseaseTypeDTO);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, 
        			"disease type is not updated!", 
        			OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param code - the code of the disease type to remove.
	 * @return <code>true</code> if the disease has been removed, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/diseasetypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Boolean>> deleteDiseaseType(@PathVariable String code) throws OHServiceException {
		Optional<DiseaseType> optDiseaseType = diseaseTypeManager.getDiseaseType()
				.stream()
				.filter(item -> item.getCode().equals(code))
				.findFirst();
		if(optDiseaseType.isPresent()) {
			boolean deleted = diseaseTypeManager.deleteDiseaseType(optDiseaseType.get());
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			result.put("deleted", deleted);
			return ResponseEntity.ok(result);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, 
					"No disease type found with the given code!", 
					OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
