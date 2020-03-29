package org.isf.pregtreattype.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PregnantTreatmentTypeController extends OHApiAbstractController<PregnantTreatmentType, PregnantTreatmentTypeDTO> {
	
	@Autowired
	private PregnantTreatmentTypeBrowserManager manager;

	private final Logger logger = LoggerFactory.getLogger(PregnantTreatmentTypeController.class);
	
	/**
	 * return the list of {@link PregnantTreatmentType}s
	 * 
	 * @return the list of {@link PregnantTreatmentType}s
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PregnantTreatmentTypeDTO>> getPregnantTreatmentType() throws OHServiceException {
		logger.info(String.format("getPregnantTreatmentType"));
        return ResponseEntity.status(HttpStatus.FOUND).body(toDTOList(manager.getPregnantTreatmentType()));
	}
	
	/**
	 * insert a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newPregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
		logger.info(String.format("newPregnantTreatmentType code [%s]"), pregnantTreatmentType.getCode());
        return manager.newPregnantTreatmentType(toModel(pregnantTreatmentType))
				? ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	/**
	 * update a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@PatchMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updatePregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
		logger.info(String.format("updatePregnantTreatmentType code [%s]"), pregnantTreatmentType.getCode());
        return manager.updatePregnantTreatmentType(toModel(pregnantTreatmentType))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}
	
	/**
	 * delete a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/pregtreattype", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentType) throws OHServiceException {
		logger.info(String.format("deletePregnantTreatmentType code [%s]"), pregnantTreatmentType.getCode());
        return manager.deletePregnantTreatmentType(toModel(pregnantTreatmentType))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}
	
	/**
	 * check if the code is already in use
	 * 
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/pregtreattype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> codeControl(@PathVariable String code) throws OHServiceException {
		logger.info(String.format("codeControl code [%s]"), code);
        return manager.codeControl(code)
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	@Override
	protected Class<PregnantTreatmentTypeDTO> getDTOClass() {
		return PregnantTreatmentTypeDTO.class;
	}

	@Override
	protected Class<PregnantTreatmentType> getModelClass() {
		return PregnantTreatmentType.class;
	}
}
