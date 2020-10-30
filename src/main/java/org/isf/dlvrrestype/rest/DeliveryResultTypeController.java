/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.dlvrrestype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.mapper.DeliveryResultTypeMapper;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(value = "/deliveryresulttype", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryResultTypeController {

	@Autowired
	protected DeliveryResultTypeBrowserManager dlvrrestManager;
	
	@Autowired
	protected DeliveryResultTypeMapper mapper;

	public DeliveryResultTypeController(DeliveryResultTypeBrowserManager dlvrrestManager, DeliveryResultTypeMapper deliveryResultTypeMapper) {
		this.dlvrrestManager = dlvrrestManager;
		this.mapper = deliveryResultTypeMapper;
	}

	/**
	 * Create a new {@link DeliveryResultType}.
	 * @param dlvrrestTypeDTO
	 * @return <code>true</code> if the {@link DeliveryResultType} has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/deliveryresulttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newDeliveryResultType(@RequestBody DeliveryResultTypeDTO dlvrrestTypeDTO)
			throws OHServiceException {
		String code = dlvrrestTypeDTO.getCode();
		log.info("Create Delivery result type {}", code);
		boolean isCreated = dlvrrestManager
				.newDeliveryResultType(mapper.map2Model(dlvrrestTypeDTO));
		DeliveryResultType dlvrrestTypeCreated = null;
		List<DeliveryResultType> dlvrrestTypeFounds = dlvrrestManager.getDeliveryResultType().stream()
				.filter(ad -> ad.getCode().equals(code)).collect(Collectors.toList());
		if (dlvrrestTypeFounds.size() > 0)
			dlvrrestTypeCreated = dlvrrestTypeFounds.get(0);
		if (!isCreated || dlvrrestTypeCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type is not created!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(dlvrrestTypeCreated.getCode());
	}

	/**
	 * Update the specified {@link DeliveryResultType}.
	 * @param dlvrrestTypeDTO
	 * @return <code>true</code> if the {@link DeliveryResultType} has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/deliveryresulttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateDeliveryResultTypet(@RequestBody DeliveryResultTypeDTO dlvrrestTypeDTO)
			throws OHServiceException {
		log.info("Update deliveryresulttypes code: {}", dlvrrestTypeDTO.getCode());
		DeliveryResultType dlvrrestType = mapper.map2Model(dlvrrestTypeDTO);
		if (!dlvrrestManager.codeControl(dlvrrestType.getCode()))
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = dlvrrestManager.updateDeliveryResultType(dlvrrestType);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "Delivery result type is not updated!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(dlvrrestType.getCode());
	}

	/**
	 * Get all the available {@link DeliveryResultType}s.
	 * @return a {@link List} of {@link DeliveryResultType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/deliveryresulttypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DeliveryResultTypeDTO>> getDeliveryResultTypes() throws OHServiceException {
		log.info("Get all Delivery result types ");
		List<DeliveryResultType> dlvrrestissionTypes = dlvrrestManager.getDeliveryResultType();
		List<DeliveryResultTypeDTO> dlvrrestTypeDTOs = mapper.map2DTOList(dlvrrestissionTypes);
		if (dlvrrestTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dlvrrestTypeDTOs);
		} else {
			return ResponseEntity.ok(dlvrrestTypeDTOs);
		}
	}
	
	/**
	 * Delete {@link DeliveryResultType} for the specified code.
	 * @param code
	 * @return <code>true</code> if the {@link DeliveryResultType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/deliveryresulttypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteDeliveryResultType(@PathVariable("code") String code)
			throws OHServiceException {
		log.info("Delete Delivery result type code: {}", code);
		boolean isDeleted = false;
		if (dlvrrestManager.codeControl(code)) {
			List<DeliveryResultType> dlvrrestTypes = dlvrrestManager.getDeliveryResultType();
			List<DeliveryResultType> dlvrrestTypeFounds = dlvrrestTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (dlvrrestTypeFounds.size() > 0)
				isDeleted = dlvrrestManager.deleteDeliveryResultType(dlvrrestTypeFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
