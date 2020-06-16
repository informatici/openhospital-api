package org.isf.medtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.medtype.dto.MedicalTypeDTO;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.mapper.MedicalTypeMapper;
import org.isf.medtype.model.MedicalType;
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

@RestController
@Api(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalTypeController {
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeController.class);
	
	@Autowired
	private MedicalTypeBrowserManager medicalTypeBrowserManager;
	@Autowired
	private MedicalTypeMapper medicalTypeMapper;
	
	/**
	 * Retrieves all the medical types.
	 * @return the found medical types.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalTypeDTO>> getMedicalTypes() throws OHServiceException {
		logger.info("Retrieving all the medical types ...");
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getMedicalType();
		List<MedicalTypeDTO> mappedMedicalTypes = medicalTypeMapper.map2DTOList(medicalTypes);
		if (mappedMedicalTypes.size() == 0) {
			logger.info("No medical type found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedicalTypes);
		} else {
			logger.info("Found " + mappedMedicalTypes.size() + " medical types");
			return ResponseEntity.ok(mappedMedicalTypes);
		}
	}
	
	/**
	 * Saves the specified medical type.
	 * @param medicalTypeDTO the medical type to save.
	 * @return {@link ResponseEntity} with status <code>HttpStatus.CREATED</code> if the medical type has been saved.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createMedicalType(@RequestBody MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		boolean isCreated = medicalTypeBrowserManager.newMedicalType(medicalTypeMapper.map2Model(medicalTypeDTO));
		if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Medical type is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Updates the specified medical type.
	 * @param medicalTypeDTO the medical type to update.
	 * @return {@link ResponseEntity} with status <code>HttpStatus.OK</code> if the medical type has been updated.
	 * @throws OHServiceException 
	 */
	@PutMapping(value = "/medicaltypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateMedicalType(@RequestBody MedicalTypeDTO medicalTypeDTO) throws OHServiceException {
		MedicalType medicalType = medicalTypeMapper.map2Model(medicalTypeDTO);
		if(!medicalTypeBrowserManager.codeControl(medicalType.getCode())) 
			throw new OHAPIException(new OHExceptionMessage(null, "Medical type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = medicalTypeBrowserManager.updateMedicalType(medicalType);
		if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Medical type is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);
	}
	
	/**
	 * Checks if the specified medical type code is already used.
	 * @param code - the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicaltypes/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isCodeUsed(@PathVariable String code) throws OHServiceException {
		return ResponseEntity.ok(medicalTypeBrowserManager.codeControl(code));
	}

	/**
	 * Deletes the specified medical type.
	 * @param code - the code of the medical type to delete.
	 * @return <code>true</code> if the medical type has been deleted.
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/medicaltypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMedicalType(@PathVariable("code") String code) throws OHServiceException {
		List<MedicalType> machedMedicalTypes = medicalTypeBrowserManager.getMedicalType()
				.stream()
				.filter(item -> item.getCode().equals(code))
				.collect(Collectors.toList());
		if (machedMedicalTypes.size() > 0)
			return ResponseEntity.ok(medicalTypeBrowserManager.deleteMedicalType(machedMedicalTypes.get(0)));
		else 
			throw new OHAPIException(new OHExceptionMessage(null, "Medical type not found!", OHSeverityLevel.ERROR));
	}
}
