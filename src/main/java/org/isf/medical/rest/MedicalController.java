package org.isf.medical.rest;

import java.util.List;

import javax.validation.Valid;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medical.dto.MedicalSortBy;
import org.isf.medical.mapper.MedicalMapper;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medtype.rest.MedicalTypeController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalController {
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeController.class);
	
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
		logger.info("Retrieving medical with code "+ code + " ...");
		Medical medical = medicalManager.getMedical(code);
		if(medical == null) {
			logger.info("Medical not found");
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		} else {
			logger.info("Medical retrieved sucessfully");
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
		logger.info("Retrieving all the medicals...");
		List<Medical> medicals;
		if(sortBy == null || sortBy == MedicalSortBy.NONE) {
			medicals = medicalManager.getMedicals();
		}
		else if(sortBy == MedicalSortBy.CODE) {
			medicals = medicalManager.getMedicalsSortedByCode();
		} else { //sortBy == MedicalSortBy.NAME
			medicals = medicalManager.getMedicalsSortedByName();
		}
		if(medicals == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Error while retrieving medicals!", OHSeverityLevel.ERROR));
		}
		List<MedicalDTO> mappedMedicals = mapper.map2DTOList(medicals);
		if(mappedMedicals.isEmpty()) {
			logger.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicals);
		} else {
			logger.info("Found " + mappedMedicals.size() + " medicals");
			return ResponseEntity.ok(mappedMedicals);
		}
	}
	
	/**
	 * Returns all the medicals with the specified criteria.
	 * @param description - the medical description
	 * @param type - the medical type
	 * @param critical - <code>true</code> to include only medicals under critical level
	 * @param nameSorted - if <code>true</code> return the list in alphabetical order
	 * @return
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/medicals/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalDTO>> filterMedicals(
			@RequestParam(name="desc", required=false) String description,
			@RequestParam(name="type", required=false) String type,
			@RequestParam(name="critical", defaultValue="false") boolean critical,
			@RequestParam(name="name_sorted", defaultValue="false") boolean nameSorted) throws OHServiceException {
		logger.info("Filtering the medicals...");
		List<Medical> medicals = null;
		if(description != null && type != null) {
			medicals = medicalManager.getMedicals(description, type, critical);
		}
		else if(description != null && type == null) {
			medicals = medicalManager.getMedicals(description);
		}
		else if(description == null && type != null) {
			medicals = medicalManager.getMedicals(type, nameSorted);
		}
		else if(nameSorted){
			medicals = medicalManager.getMedicals(null, nameSorted);
		}
		else {
			medicalManager.getMedicals();
		}
		
		if(medicals == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Error while retrieving medicals!", OHSeverityLevel.ERROR));
		}
		List<MedicalDTO> mappedMedicals = mapper.map2DTOList(medicals);
		if(mappedMedicals.isEmpty()) {
			logger.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicals);
		} else {
			logger.info("Found " + mappedMedicals.size() + " medicals");
			return ResponseEntity.ok(mappedMedicals);
		}
	}
	
	/**
	 * Saves the specified {@link Medical}
	 * @param medicalDTO - the medical to save
	 * @param ignoreSimilar - if <code>true</code>, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status <code>HttpStatus.CREATED</code> if the medical was created
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newMedical(
			@RequestBody MedicalDTO medicalDTO,
			@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar) throws OHServiceException {
		logger.info("Creating a new medical ...");
		boolean isCreated = medicalManager.newMedical(mapper.map2Model(medicalDTO), ignoreSimilar);
		if (!isCreated) {
			logger.info("Medical is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "Medical is not created!", OHSeverityLevel.ERROR));
        }
		logger.info("Medical successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Updates the specified {@link Medical}
	 * @param medicalDTO - the medical to update
	 * @param ignoreSimilar - if <code>true</code>, it ignore the warning "similarsFoundWarning"
	 * @return {@link ResponseEntity} with status <code>HttpStatus.OK</code> if the medical was updated
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/medicals", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateMedical(
			@RequestBody @Valid MedicalDTO medicalDTO,
			@RequestParam(name="ignore_similar", defaultValue="false") boolean ignoreSimilar) throws OHServiceException {
		logger.info("Updating a medical ...");
		boolean isUpdated = medicalManager.updateMedical(mapper.map2Model(medicalDTO), ignoreSimilar);
		if (!isUpdated) {
			logger.info("Medical is not updated!");
            throw new OHAPIException(new OHExceptionMessage(null, "Medical is not updated!", OHSeverityLevel.ERROR));
        }
		logger.info("Medical successfully updated!");
        return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	/**
	 * Deletes the specified medical.
	 * @param medical the medical to delete.
	 * @return <code>true</code> if the medical has been deleted.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/medicals/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedical(@PathVariable Integer code) throws OHServiceException {
		Medical medical = medicalManager.getMedical(code);
		if(medical == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		boolean isDeleted = medicalManager.deleteMedical(medical);
		return ResponseEntity.ok(isDeleted);
	}
}
