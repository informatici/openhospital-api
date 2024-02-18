/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.dlvrtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.mapper.DeliveryTypeMapper;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value ="/deliverytype")
@Tag(name = "Delivery Type")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryTypeController.class);

	@Autowired
	protected DeliveryTypeBrowserManager dlvrtypeManager;
	
	@Autowired
	protected DeliveryTypeMapper deliveryTypeMapper;

	public DeliveryTypeController(DeliveryTypeBrowserManager dlvrtypeManager, DeliveryTypeMapper deliveryTypeMapper) {
		this.dlvrtypeManager = dlvrtypeManager;
		this.deliveryTypeMapper = deliveryTypeMapper;
	}

	/**
	 * Create a new {@link DeliveryType}.
	 * @param dlvrTypeDTO
	 * @return {@code true} if the {@link DeliveryType} has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/deliverytypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<DeliveryTypeDTO> newDeliveryType(@RequestBody DeliveryTypeDTO dlvrTypeDTO) throws OHServiceException {
		String code = dlvrTypeDTO.getCode();
		LOGGER.info("Create Delivery Type {}", code);
		dlvrtypeManager.newDeliveryType(deliveryTypeMapper.map2Model(dlvrTypeDTO));
		DeliveryType dlvrTypeCreated = null;
		List<DeliveryType> dlvrTypeFounds = dlvrtypeManager.getDeliveryType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (!dlvrTypeFounds.isEmpty()) {
			dlvrTypeCreated = dlvrTypeFounds.get(0);
		}
		if (dlvrTypeCreated == null) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(deliveryTypeMapper.map2DTO(dlvrTypeCreated));
	}

	/**
	 * Update the specified {@link DeliveryType}.
	 * @param dlvrTypeDTO
	 * @return {@code true} if the {@link DeliveryType} has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/deliverytypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<DeliveryTypeDTO> updateDeliveryTypes(@RequestBody DeliveryTypeDTO dlvrTypeDTO) throws OHServiceException {
		LOGGER.info("Update Delivery Type code: {}", dlvrTypeDTO.getCode());
		DeliveryType dlvrType = deliveryTypeMapper.map2Model(dlvrTypeDTO);
		if (!dlvrtypeManager.isCodePresent(dlvrType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type not found."));
		}
		try {
			dlvrtypeManager.updateDeliveryType(dlvrType);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type is not updated."));
		}
		return ResponseEntity.ok(deliveryTypeMapper.map2DTO(dlvrType));
	}

	/**
	 * Get all the available {@link DeliveryType}.
	 * @return a {@link List} of {@link DeliveryType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/deliverytypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DeliveryTypeDTO>> getDeliveryTypes() throws OHServiceException {
		LOGGER.info("Get all Delivery Types");
		List<DeliveryType> dlvrTypes = dlvrtypeManager.getDeliveryType();
		List<DeliveryTypeDTO> dlvrTypeDTOs = deliveryTypeMapper.map2DTOList(dlvrTypes);
		if (dlvrTypeDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dlvrTypeDTOs);
		} else {
			return ResponseEntity.ok(dlvrTypeDTOs);
		}
	}

	/**
	 * Delete {@link DeliveryType} for specified code.
	 * @param code
	 * @return {@code true} if the {@link DeliveryType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/deliverytypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteDeliveryType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete Delivery Type code: {}", code);
		if (dlvrtypeManager.isCodePresent(code)) {
			List<DeliveryType> dlvrTypes = dlvrtypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypeFounds = dlvrTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (!dlvrTypeFounds.isEmpty()) {
				dlvrtypeManager.deleteDeliveryType(dlvrTypeFounds.get(0));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(true);
	}

}
