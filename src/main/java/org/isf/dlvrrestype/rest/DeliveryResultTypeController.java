/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dlvrrestype.rest;

import java.util.List;

import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.mapper.DeliveryResultTypeMapper;
import org.isf.dlvrrestype.model.DeliveryResultType;
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
@Tag(name = "Delivery Result Type")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryResultTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryResultTypeController.class);

	private final DeliveryResultTypeBrowserManager deliveryResultTypeManager;

	private final DeliveryResultTypeMapper mapper;

	public DeliveryResultTypeController(
		DeliveryResultTypeBrowserManager deliveryResultTypeBrowserManager, DeliveryResultTypeMapper deliveryResultTypeMapper
	) {
		this.deliveryResultTypeManager = deliveryResultTypeBrowserManager;
		this.mapper = deliveryResultTypeMapper;
	}

	/**
	 * Create a new {@link DeliveryResultType}.
	 * @param deliveryResultTypeDTO Delivery result type payload
	 * @return {@code true} if the {@link DeliveryResultType} has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create delivery result type
	 */
	@PostMapping(value = "/deliveryresulttypes")
	@ResponseStatus(HttpStatus.CREATED)
	DeliveryResultTypeDTO newDeliveryResultType(
		@RequestBody DeliveryResultTypeDTO deliveryResultTypeDTO
	) throws OHServiceException {
		LOGGER.info("Create Delivery Result Type {}", deliveryResultTypeDTO.getCode());

		return mapper.map2DTO(deliveryResultTypeManager.newDeliveryResultType(mapper.map2Model(deliveryResultTypeDTO)));
	}

	/**
	 * Update the specified {@link DeliveryResultType}.
	 * @param deliveryResultTypeDTO Delivery result type payload
	 * @return {@code true} if the {@link DeliveryResultType} has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update delivery result type
	 */
	@PutMapping(value = "/deliveryresulttypes")
	public DeliveryResultTypeDTO updateDeliveryResultTypes(
		@RequestBody DeliveryResultTypeDTO deliveryResultTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update Delivery Result Type code: {}", deliveryResultTypeDTO.getCode());
		if (!deliveryResultTypeManager.isCodePresent(deliveryResultTypeDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Result Type not found."), HttpStatus.NOT_FOUND);
		}

		DeliveryResultType deliveryResultType = mapper.map2Model(deliveryResultTypeDTO);
		try {
			return mapper.map2DTO(deliveryResultTypeManager.updateDeliveryResultType(deliveryResultType));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(
				new OHExceptionMessage("Delivery Result Type is not updated."), HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}

	/**
	 * Get all the available {@link DeliveryResultType}s.
	 * @return a {@link List} of {@link DeliveryResultType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get delivery result types
	 */
	@GetMapping(value = "/deliveryresulttypes")
	public List<DeliveryResultTypeDTO> getDeliveryResultTypes() throws OHServiceException {
		LOGGER.info("Get all Delivery Result Types.");
		return mapper.map2DTOList(deliveryResultTypeManager.getDeliveryResultType());
	}

	/**
	 * Delete {@link DeliveryResultType} for the specified code.
	 * @param code Delivery result type code
	 * @return {@code true} if the {@link DeliveryResultType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete delivery result type
	 */
	@DeleteMapping(value = "/deliveryresulttypes/{code}")
	public boolean deleteDeliveryResultType(
		@PathVariable("code") String code
	) throws OHServiceException {
		LOGGER.info("Delete Delivery Result Type code: {}", code);

		List<DeliveryResultType> deliveryResultTypes = deliveryResultTypeManager.getDeliveryResultType();
		List<DeliveryResultType> deliveryResultTypeFounds = deliveryResultTypes
			.stream()
			.filter(ad -> ad.getCode().equals(code))
			.toList();

		if (deliveryResultTypeFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Delivery Result Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			deliveryResultTypeManager.deleteDeliveryResultType(deliveryResultTypeFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(
				new OHExceptionMessage("Delivery Result Type is not deleted."), HttpStatus.INTERNAL_SERVER_ERROR
			);
		}
	}
}
