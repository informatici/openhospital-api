package org.isf.pregtreattype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.mapper.PregnantTreatmentTypeMapper;
import org.isf.pregtreattype.model.PregnantTreatmentType;
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
@Api(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class PregnantTreatmentTypeController {

	@Autowired
	protected PregnantTreatmentTypeBrowserManager pregTreatTypeManager;
	
	@Autowired
	protected PregnantTreatmentTypeMapper mapper;

	private final Logger logger = LoggerFactory.getLogger(PregnantTreatmentTypeController.class);

	public PregnantTreatmentTypeController(PregnantTreatmentTypeBrowserManager pregTreatTypeManager, PregnantTreatmentTypeMapper pregnantTreatmentTypemapper) {
		this.pregTreatTypeManager = pregTreatTypeManager;
		this.mapper = pregnantTreatmentTypemapper;
	}

	/**
	 * create a new {@link PregnantTreatmentType}
	 * @param pregnantTreatmentTypeDTO
	 * @return <code>true</code> if the pregnant treatment type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newPregnantTreatmentType(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO) throws OHServiceException {
		String code = pregnantTreatmentTypeDTO.getCode();
		logger.info("Create pregnant treatment Type " + code);
		boolean isCreated = pregTreatTypeManager.newPregnantTreatmentType(mapper.map2Model(pregnantTreatmentTypeDTO));
		PregnantTreatmentType pregTreatTypeCreated = null;
		List<PregnantTreatmentType> pregTreatTypeFounds = pregTreatTypeManager.getPregnantTreatmentType().stream().filter(pregtreattype -> pregtreattype.getCode().equals(code))
				.collect(Collectors.toList());
		if (pregTreatTypeFounds.size() > 0)
			pregTreatTypeCreated = pregTreatTypeFounds.get(0);
		if (!isCreated || pregTreatTypeCreated == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "pregnant treatment Type is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(pregTreatTypeCreated.getCode());
	}

	/**
	 * Updates the specified {@link PregnantTreatmentType}.
	 * @param pregnantTreatmentTypeDTO
	 * @return <code>true</code> if the pregnant treatment type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updatePregnantTreatmentTypet(@RequestBody PregnantTreatmentTypeDTO pregnantTreatmentTypeDTO)
			throws OHServiceException {
		logger.info("Update pregnanttreatmenttypes code:" + pregnantTreatmentTypeDTO.getCode());
		PregnantTreatmentType pregTreatType = mapper.map2Model(pregnantTreatmentTypeDTO);
		if (!pregTreatTypeManager.codeControl(pregTreatType.getCode()))
			throw new OHAPIException(new OHExceptionMessage(null, "pregnantTreatment Type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = pregTreatTypeManager.updatePregnantTreatmentType(pregTreatType);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "pregnantTreatment Type is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(pregTreatType.getCode());
	}

	/**
	 * get all the available {@link PregnantTreatmentType}s.
	 * @return a {@link List} of {@link PregnantTreatmentType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pregnanttreatmenttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PregnantTreatmentTypeDTO>> getPregnantTreatmentTypes() throws OHServiceException {
		logger.info("Get all pregnantTreatment Types ");
		List<PregnantTreatmentType> pregnantTreatmentTypes = pregTreatTypeManager.getPregnantTreatmentType();
		List<PregnantTreatmentTypeDTO> pregnantTreatmentTypeDTOs = mapper.map2DTOList(pregnantTreatmentTypes);
		if (pregnantTreatmentTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(pregnantTreatmentTypeDTOs);
		} else {
			return ResponseEntity.ok(pregnantTreatmentTypeDTOs);
		}
	}

	/**
	 * Delete {@link PregnantTreatmentType} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link PregnantTreatmentType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pregnanttreatmenttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePregnantTreatmentType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete pregnantTreatment Type code:" + code);
		boolean isDeleted = false;
		if (pregTreatTypeManager.codeControl(code)) {
			List<PregnantTreatmentType> pregTreatTypes = pregTreatTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTreatTypeFounds = pregTreatTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (pregTreatTypeFounds.size() > 0)
				isDeleted = pregTreatTypeManager.deletePregnantTreatmentType(pregTreatTypeFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
