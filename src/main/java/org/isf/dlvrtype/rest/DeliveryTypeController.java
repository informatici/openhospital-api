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
@Api(value = "/deliverytype", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryTypeController {

	@Autowired
	protected DeliveryTypeBrowserManager dlvrtypeManager;

	private final Logger logger = LoggerFactory.getLogger(DeliveryTypeController.class);

	public DeliveryTypeController(DeliveryTypeBrowserManager dlvrtypeManager) {
		this.dlvrtypeManager = dlvrtypeManager;
	}

	/**
	 * create a new {@link DeliveryType}
	 * @param dlvrTypeDTO
	 * @return <code>true</code> if the {@link DeliveryType} has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/deliverytypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newDeliveryType(@RequestBody DeliveryTypeDTO dlvrTypeDTO) throws OHServiceException {
		String code = dlvrTypeDTO.getCode();
		logger.info("Create Delivery type " + code);
		boolean isCreated = dlvrtypeManager.newDeliveryType(getObjectMapper().map(dlvrTypeDTO, DeliveryType.class));
		DeliveryType dlvrTypeCreated = null;
		List<DeliveryType> dlvrTypeFounds = dlvrtypeManager.getDeliveryType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (dlvrTypeFounds.size() > 0)
			dlvrTypeCreated = dlvrTypeFounds.get(0);
		if (!isCreated || dlvrTypeCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(dlvrTypeCreated.getCode());
	}

	/**
	 * update the specified {@link DeliveryType}
	 * @param dlvrTypeDTO
	 * @return <code>true</code> if the {@link DeliveryType} has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/deliverytypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateDeliveryTypet(@RequestBody DeliveryTypeDTO dlvrTypeDTO) throws OHServiceException {
		logger.info("Update deliverytypes code:" + dlvrTypeDTO.getCode());
		DeliveryType dlvrType = getObjectMapper().map(dlvrTypeDTO, DeliveryType.class);
		if(!dlvrtypeManager.codeControl(dlvrType.getCode())) 
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = dlvrtypeManager.updateDeliveryType(dlvrType);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery type is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(dlvrType.getCode());
	}

	/**
	 * get all the available {@link DeliveryType}
	 * @return a {@link List} of {@link DeliveryType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/deliverytypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DeliveryTypeDTO>> getDeliveryTypes() throws OHServiceException {
		logger.info("Get all Delivery types ");
		List<DeliveryType> dlvrTypes = dlvrtypeManager.getDeliveryType();
		List<DeliveryTypeDTO> dlvrTypeDTOs = dlvrTypes.stream()
				.map(dlvrType -> getObjectMapper().map(dlvrType, DeliveryTypeDTO.class)).collect(Collectors.toList());
		if (dlvrTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dlvrTypeDTOs);
		} else {
			return ResponseEntity.ok(dlvrTypeDTOs);
		}
	}

	/**
	 * Delete {@link DeliveryType} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link DeliveryType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/deliverytypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteDeliveryType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete Delivery type code:" + code);
		boolean isDeleted = false;
		if (dlvrtypeManager.codeControl(code)) {
			List<DeliveryType> dlvrTypes = dlvrtypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypeFounds = dlvrTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (dlvrTypeFounds.size() > 0)
				isDeleted = dlvrtypeManager.deleteDeliveryType(dlvrTypeFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
