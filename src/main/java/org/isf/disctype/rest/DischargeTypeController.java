package org.isf.disctype.rest;

import io.swagger.annotations.Api;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.model.DischargeType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "/dischargetype", produces = MediaType.APPLICATION_JSON_VALUE)
public class DischargeTypeController {

	@Autowired
	protected DischargeTypeBrowserManager discTypeManager;

	@Autowired
	protected ModelMapper modelMapper;

	private final Logger logger = LoggerFactory.getLogger(DischargeTypeController.class);

	public DischargeTypeController(DischargeTypeBrowserManager discTypeManager) {
		this.discTypeManager = discTypeManager;
	}

	/**
	 * create a new {@link DischargeType}
	 * @param dischTypeDTO
	 * @return <code>true</code> if the {@link DischargeType} has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newDischargeType(@RequestBody DischargeTypeDTO dischTypeDTO) throws OHServiceException {
		String code = dischTypeDTO.getCode();
		logger.info("Create discharge type " + code);
		boolean isCreated = discTypeManager.newDischargeType(modelMapper.map(dischTypeDTO, DischargeType.class));
		DischargeType dischTypeCreated = null;
		List<DischargeType> dischTypeFounds = discTypeManager.getDischargeType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (dischTypeFounds.size() > 0)
			dischTypeCreated = dischTypeFounds.get(0);
		if (!isCreated || dischTypeCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type is not created!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(dischTypeCreated.getCode());
	}

	/**
	 * update the specified {@link DischargeType}
	 * @param dischTypeDTO
	 * @return <code>true</code> if the {@link DischargeType} has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateDischargeTypet(@RequestBody DischargeTypeDTO dischTypeDTO) throws OHServiceException {
		logger.info("Update dischargetypes code:" + dischTypeDTO.getCode());
		DischargeType dischType = modelMapper.map(dischTypeDTO, DischargeType.class);
		if(!discTypeManager.codeControl(dischTypeDTO.getCode())) 
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = discTypeManager.updateDischargeType(dischType);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type is not updated!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(dischType.getCode());
	}

	/**
	 * get all the available {@link DischargeType}
	 * @return a {@link List} of {@link DischargeType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DischargeTypeDTO>> getDischargeTypes() throws OHServiceException {
		logger.info("Get all discharge types ");
		List<DischargeType> dischTypes = discTypeManager.getDischargeType();
		List<DischargeTypeDTO> dischTypeDTOs = dischTypes.stream()
				.map(dischType -> modelMapper.map(dischType, DischargeTypeDTO.class)).collect(Collectors.toList());
		if (dischTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dischTypeDTOs);
		} else {
			return ResponseEntity.ok(dischTypeDTOs);
		}
	}

	/**
	 * Delete {@link DischargeType} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link DischargeType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/dischargetypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteDischargeType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete discharge type code:" + code);
		boolean isDeleted = false;
		if (discTypeManager.codeControl(code)) {
			List<DischargeType> dischTypes = discTypeManager.getDischargeType();
			List<DischargeType> dischTypeFounds = dischTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (dischTypeFounds.size() > 0)
				isDeleted = discTypeManager.deleteDischargeType(dischTypeFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
