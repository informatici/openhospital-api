package org.isf.dlvrrestype.rest;

import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
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
@Api(value = "/deliveryresulttype", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryResultTypeController {

	@Autowired
	protected DeliveryResultTypeBrowserManager dlvrrestManager;

	private final Logger logger = LoggerFactory.getLogger(DeliveryResultTypeController.class);

	public DeliveryResultTypeController(DeliveryResultTypeBrowserManager dlvrrestManager) {
		this.dlvrrestManager = dlvrrestManager;
	}

	@PostMapping(value = "/deliveryresulttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newDeliveryResultType(@RequestBody DeliveryResultTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();
		logger.info("Create Delivery result type " + code);
		boolean isCreated = dlvrrestManager.newDeliveryResultType(getObjectMapper().map(admissionTypeDTO, DeliveryResultType.class));
		DeliveryResultType admtCreated = null;
		List<DeliveryResultType> admtFounds = dlvrrestManager.getDeliveryResultType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (admtFounds.size() > 0)
			admtCreated = admtFounds.get(0);
		if (!isCreated || admtCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type is not created!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(admtCreated.getCode());
	}

	@PutMapping(value = "/deliveryresulttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateDeliveryResultTypet(@PathVariable String code,
			@RequestBody DeliveryResultTypeDTO admissionTypeDTO) throws OHServiceException {
		logger.info("Update deliveryresulttypes code:" + code);
		DeliveryResultType admt = getObjectMapper().map(admissionTypeDTO, DeliveryResultType.class);
		admt.setCode(code);
		if(!dlvrrestManager.codeControl(code)) 
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = dlvrrestManager.updateDeliveryResultType(admt);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type is not updated!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(admt.getCode());
	}

	@GetMapping(value = "/deliveryresulttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DeliveryResultTypeDTO>> getDeliveryResultTypes() throws OHServiceException {
		logger.info("Get all Delivery result types ");
		List<DeliveryResultType> admissionTypes = dlvrrestManager.getDeliveryResultType();
		List<DeliveryResultTypeDTO> admissionTypeDTOs = admissionTypes.stream()
				.map(admType -> getObjectMapper().map(admType, DeliveryResultTypeDTO.class)).collect(Collectors.toList());
		if (admissionTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admissionTypeDTOs);
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	@DeleteMapping(value = "/deliveryresulttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteDeliveryResultType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete Delivery result type code:" + code);
		boolean isDeleted = false;
		if (dlvrrestManager.codeControl(code)) {
			List<DeliveryResultType> admts = dlvrrestManager.getDeliveryResultType();
			List<DeliveryResultType> admtFounds = admts.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (admtFounds.size() > 0)
				isDeleted = dlvrrestManager.deleteDeliveryResultType(admtFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
