package org.isf.disctype.rest;

import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.model.DischargeType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@Api(value = "/dischargetype", produces = "application/vnd.ohapi.app-v1+json")
public class DischargeTypeController {

	@Autowired
	protected DischargeTypeBrowserManager discTypeManager;

	private final Logger logger = LoggerFactory.getLogger(DischargeTypeController.class);

	public DischargeTypeController(DischargeTypeBrowserManager discTypeManager) {
		this.discTypeManager = discTypeManager;
	}

	@PostMapping(value = "/dischargetypes", produces = "application/vnd.ohapi.app-v1+json")
	ResponseEntity<String> newDischargeType(@RequestBody DischargeTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();
		logger.info("Create discharge type " + code);
		boolean isCreated = discTypeManager.newDischargeType(getObjectMapper().map(admissionTypeDTO, DischargeType.class));
		DischargeType admtCreated = null;
		List<DischargeType> admtFounds = discTypeManager.getDischargeType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (admtFounds.size() > 0)
			admtCreated = admtFounds.get(0);
		if (!isCreated || admtCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(admtCreated.getCode());
	}

	@PutMapping(value = "/dischargetypes/{code}", produces = "application/vnd.ohapi.app-v1+json")
	ResponseEntity<String> updateDischargeTypet(@PathVariable String code,
			@RequestBody DischargeTypeDTO admissionTypeDTO) throws OHServiceException {
		logger.info("Update dischargetypes code:" + code);
		DischargeType admt = getObjectMapper().map(admissionTypeDTO, DischargeType.class);
		admt.setCode(code);
		if(!discTypeManager.codeControl(code)) 
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = discTypeManager.updateDischargeType(admt);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "discharge type is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(admt.getCode());
	}

	@GetMapping(value = "/dischargetypes", produces = "application/vnd.ohapi.app-v1+json")
	public ResponseEntity<List<DischargeTypeDTO>> getDischargeTypes() throws OHServiceException {
		logger.info("Get all discharge types ");
		List<DischargeType> admissionTypes = discTypeManager.getDischargeType();
		List<DischargeTypeDTO> admissionTypeDTOs = admissionTypes.stream()
				.map(admType -> getObjectMapper().map(admType, DischargeTypeDTO.class)).collect(Collectors.toList());
		if (admissionTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admissionTypeDTOs);
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	@DeleteMapping(value = "/dischargetypes/{code}", produces = "application/vnd.ohapi.app-v1+json")
	public ResponseEntity<Boolean> deleteDischargeType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete discharge type code:" + code);
		boolean isDeleted = false;
		if (discTypeManager.codeControl(code)) {
			List<DischargeType> admts = discTypeManager.getDischargeType();
			List<DischargeType> admtFounds = admts.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (admtFounds.size() > 0)
				isDeleted = discTypeManager.deleteDischargeType(admtFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
