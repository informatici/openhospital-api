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

import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.mapper.DeliveryTypeMapper;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Delivery Type")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryTypeController.class);

	private final DeliveryTypeBrowserManager deliveryTypeManager;

	private final DeliveryTypeMapper deliveryTypeMapper;

	public DeliveryTypeController(DeliveryTypeBrowserManager deliveryTypeManager, DeliveryTypeMapper deliveryTypeMapper) {
		this.deliveryTypeManager = deliveryTypeManager;
		this.deliveryTypeMapper = deliveryTypeMapper;
	}

	/**
	 * Create a new {@link DeliveryType}.
	 *
	 * @param deliveryTypeDTO Delivery Type payload
	 * @return {@code true} if the {@link DeliveryType} has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create delivery type
	 */
	@PostMapping(value = "/deliverytypes")
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryTypeDTO newDeliveryType(@RequestBody DeliveryTypeDTO deliveryTypeDTO) throws OHServiceException {
		LOGGER.info("Create Delivery Type {}", deliveryTypeDTO.getCode());

		DeliveryType deliveryType = deliveryTypeManager.newDeliveryType(deliveryTypeMapper.map2Model(deliveryTypeDTO));

		if (deliveryType == null) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create delivery type"));
		}

		return deliveryTypeMapper.map2DTO(deliveryType);
	}

	/**
	 * Update the specified {@link DeliveryType}.
	 *
	 * @param deliveryTypeDTO Delivery type payload
	 * @return {@code true} if the {@link DeliveryType} has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update delivery type
	 */
	@PutMapping(value = "/deliverytypes")
	public DeliveryTypeDTO updateDeliveryTypes(
		@RequestBody DeliveryTypeDTO deliveryTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update Delivery Type code: {}", deliveryTypeDTO.getCode());
		if (!deliveryTypeManager.isCodePresent(deliveryTypeDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return deliveryTypeMapper.map2DTO(
				deliveryTypeManager.updateDeliveryType(deliveryTypeMapper.map2Model(deliveryTypeDTO))
			);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type is not updated."));
		}
	}

	/**
	 * Get all the available {@link DeliveryType}.
	 *
	 * @return a {@link List} of {@link DeliveryType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get delivery results
	 */
	@GetMapping(value = "/deliverytypes")
	public List<DeliveryTypeDTO> getDeliveryTypes() throws OHServiceException {
		LOGGER.info("Get all Delivery Types");
		return deliveryTypeMapper.map2DTOList(deliveryTypeManager.getDeliveryType());
	}

	/**
	 * Delete {@link DeliveryType} for specified code.
	 *
	 * @param code Delivery result code
	 * @return {@code true} if the {@link DeliveryType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete delivery type
	 */
	@DeleteMapping(value = "/deliverytypes/{code}")
	public boolean deleteDeliveryType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete Delivery Type code: {}", code);

		List<DeliveryType> deliveryTypes = deliveryTypeManager.getDeliveryType();
		List<DeliveryType> deliveryTypeFounds = deliveryTypes.stream().filter(ad -> ad.getCode().equals(code)).toList();

		if (deliveryTypeFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			deliveryTypeManager.deleteDeliveryType(deliveryTypeFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.error("Delete Delivery Type: {} failed.", code);
			throw new OHAPIException(new OHExceptionMessage("Delivery Type not deleted."));
		}
	}
}
