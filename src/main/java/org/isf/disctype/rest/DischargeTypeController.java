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
package org.isf.disctype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.mapper.DischargeTypeMapper;
import org.isf.disctype.model.DischargeType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "DischargeType")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DischargeTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DischargeTypeController.class);

	private final DischargeTypeBrowserManager discTypeManager;

	private final DischargeTypeMapper mapper;

	public DischargeTypeController(DischargeTypeBrowserManager discTypeManager, DischargeTypeMapper dischargeTypemapper) {
		this.discTypeManager = discTypeManager;
		this.mapper = dischargeTypemapper;
	}

	/**
	 * Create a new {@link DischargeType}
	 * 
	 * @param dischargeTypeDTO Discharge Type payload
	 * @return {@code true} if the {@link DischargeType} has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create discharge type
	 */
	@PostMapping(value = "/dischargetypes")
	@ResponseStatus(HttpStatus.CREATED)
	public DischargeTypeDTO newDischargeType(@RequestBody DischargeTypeDTO dischargeTypeDTO) throws OHServiceException {
		String code = dischargeTypeDTO.getCode();
		LOGGER.info("Create discharge type {}", code);

		DischargeType newDischargeType = discTypeManager.newDischargeType(mapper.map2Model(dischargeTypeDTO));
		if (!discTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Discharge Type is not created."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return mapper.map2DTO(newDischargeType);
	}

	/**
	 * Update the specified {@link DischargeType}
	 * 
	 * @param dischargeTypeDTO Discharge type payload
	 * @return {@code true} if the {@link DischargeType} has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update discharge type
	 */
	@PutMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public DischargeTypeDTO updateDischargeType(
		@RequestBody DischargeTypeDTO dischargeTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update discharge type with code: {}", dischargeTypeDTO.getCode());

		DischargeType dischargeType = mapper.map2Model(dischargeTypeDTO);
		if (!discTypeManager.isCodePresent(dischargeTypeDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Discharge Type not found."));
		}

		DischargeType updatedDischargeType = discTypeManager.updateDischargeType(dischargeType);
		if (!discTypeManager.isCodePresent(updatedDischargeType.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Discharge Type is not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return mapper.map2DTO(dischargeType);
	}

	/**
	 * Get all the available {@link DischargeType}s
	 * 
	 * @return a {@link List} of {@link DischargeType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get discharge types
	 */
	@GetMapping(value = "/dischargetypes")
	public ResponseEntity<List<DischargeTypeDTO>> getDischargeTypes() throws OHServiceException {
		LOGGER.info("Get all discharge types ");

		List<DischargeType> dischargeTypes = discTypeManager.getDischargeType();
		if (dischargeTypes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of());
		}

		return ResponseEntity.ok(mapper.map2DTOList(dischargeTypes));
	}

	/**
	 * Delete {@link DischargeType} for the specified code.
	 * 
	 * @param code Discharge type code
	 * @return {@code true} if the {@link DischargeType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete discharge type
	 */
	@DeleteMapping(value = "/dischargetypes/{code}")
	public boolean deleteDischargeType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete discharge type code: {}", code);

		if (!discTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Discharge type not found with code :" + code), HttpStatus.NOT_FOUND);
		}

		List<DischargeType> dischargeTypes = discTypeManager.getDischargeType();
		List<DischargeType> dischargeTypeFounds = dischargeTypes.stream().filter(ad -> ad.getCode().equals(code)).toList();

		if (!dischargeTypeFounds.isEmpty()) {
			try {
				discTypeManager.deleteDischargeType(dischargeTypeFounds.get(0));
			} catch (OHServiceException serviceException) {
				LOGGER.error("Delete discharge type: {} failed.", code);
				throw new OHAPIException(new OHExceptionMessage("Discharge type not deleted."));
			}
		}

		return true;
	}
}
