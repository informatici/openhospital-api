package org.isf.agetype.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.agetype.dto.AgeTypeDTO;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.model.AgeType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value="/agetypes",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="basicAuth")})
public class AgeTypeController {
	
	@Autowired
	private AgeTypeBrowserManager ageTypeManager;
	
	private final Logger logger = LoggerFactory.getLogger(AgeTypeController.class);
	
	public AgeTypeController(AgeTypeBrowserManager ageTypeManager) {
		this.ageTypeManager = ageTypeManager;
	}
	
	/**
	 * Get all the age types stored
	 * @return the list of age types found
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/agetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AgeTypeDTO>> getAllAgeTypes() throws OHServiceException {
		logger.info("Get age types");
		List<AgeType> results = ageTypeManager.getAgeType();
		List<AgeTypeDTO> parsedResults = results.stream().map(item -> getObjectMapper().map(item, AgeTypeDTO.class)).collect(Collectors.toList());
		if(parsedResults.size() > 0){
			return ResponseEntity.ok(parsedResults);
        }else{
        	logger.info("Empty age types list");
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
		if(ageTypeDTO.getCode() == null || ageTypeDTO.getCode().trim().isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "The age type is not valid!", OHSeverityLevel.ERROR));
		}
		logger.info("Update age type");
		AgeType ageType = getObjectMapper().map(ageTypeDTO, AgeType.class);
		ArrayList<AgeType> ageTypes = new ArrayList<AgeType>();
		ageTypes.add(ageType);
		if(ageTypeManager.updateAgeType(ageTypes)) {
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
		logger.info("Get age type by age: " + age);
		String result = ageTypeManager.getTypeByAge(age);
		Map<String, String> responseBody = new HashMap<String, String>();
		if(result != null){
			responseBody.put("code", result);
			return ResponseEntity.ok(responseBody);
        }else{
        	logger.info("No corresponding age code for the given age");
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
		logger.info("Get age type by index: " + index);
		AgeType result = ageTypeManager.getTypeByCode(index);
		if(result != null){
			return ResponseEntity.ok(result);
        }else{
        	logger.info("No corresponding age code for the given index");
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
	}
	
}
