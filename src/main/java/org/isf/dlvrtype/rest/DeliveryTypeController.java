package org.isf.dlvrtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.model.DeliveryType;
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
@Api(value = "/deliverytype", produces = "application/vnd.ohapi.app-v1+json")
public class DeliveryTypeController {

	@Autowired
	protected DeliveryTypeBrowserManager dlvrtypeManager;

	private final Logger logger = LoggerFactory.getLogger(DeliveryTypeController.class);

	public DeliveryTypeController(DeliveryTypeBrowserManager dlvrtypeManager) {
		this.dlvrtypeManager = dlvrtypeManager;
	}

	@PostMapping(value = "/deliverytypes", produces = "application/vnd.ohapi.app-v1+json")
	ResponseEntity<String> newDeliveryType(@RequestBody DeliveryTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();
		logger.info("Create Delivery type " + code);
		boolean isCreated = dlvrtypeManager.newDeliveryType(getObjectMapper().map(admissionTypeDTO, DeliveryType.class));
		DeliveryType admtCreated = null;
		List<DeliveryType> admtFounds = dlvrtypeManager.getDeliveryType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (admtFounds.size() > 0)
			admtCreated = admtFounds.get(0);
		if (!isCreated || admtCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(admtCreated.getCode());
	}

	@PutMapping(value = "/deliverytypes/{code}", produces = "application/vnd.ohapi.app-v1+json")
	ResponseEntity<String> updateDeliveryTypet(@PathVariable String code,
			@RequestBody DeliveryTypeDTO admissionTypeDTO) throws OHServiceException {
		logger.info("Update deliverytypes code:" + code);
		DeliveryType admt = getObjectMapper().map(admissionTypeDTO, DeliveryType.class);
		admt.setCode(code);
		if(!dlvrtypeManager.codeControl(code)) 
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = dlvrtypeManager.updateDeliveryType(admt);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(admt.getCode());
	}

	@GetMapping(value = "/deliverytypes", produces = "application/vnd.ohapi.app-v1+json")
	public ResponseEntity<List<DeliveryTypeDTO>> getDeliveryTypes() throws OHServiceException {
		logger.info("Get all Delivery types ");
		List<DeliveryType> admissionTypes = dlvrtypeManager.getDeliveryType();
		List<DeliveryTypeDTO> admissionTypeDTOs = admissionTypes.stream()
				.map(admType -> getObjectMapper().map(admType, DeliveryTypeDTO.class)).collect(Collectors.toList());
		if (admissionTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admissionTypeDTOs);
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	@DeleteMapping(value = "/deliverytypes/{code}", produces = "application/vnd.ohapi.app-v1+json")
	public ResponseEntity<Boolean> deleteDeliveryType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete Delivery type code:" + code);
		boolean isDeleted = false;
		if (dlvrtypeManager.codeControl(code)) {
			List<DeliveryType> admts = dlvrtypeManager.getDeliveryType();
			List<DeliveryType> admtFounds = admts.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (admtFounds.size() > 0)
				isDeleted = dlvrtypeManager.deleteDeliveryType(admtFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
