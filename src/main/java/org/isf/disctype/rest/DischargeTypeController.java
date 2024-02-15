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
package org.isf.disctype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.mapper.DischargeTypeMapper;
import org.isf.disctype.model.DischargeType;
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

@RestController(value = "/dischargetype")
@Tag(name = "DischargeType")
@SecurityRequirement(name = "bearerAuth")
public class DischargeTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DischargeTypeController.class);

	@Autowired
	protected DischargeTypeBrowserManager discTypeManager;
	
	@Autowired
	protected DischargeTypeMapper mapper;

	public DischargeTypeController(DischargeTypeBrowserManager discTypeManager, DischargeTypeMapper dischargeTypemapper) {
		this.discTypeManager = discTypeManager;
		this.mapper = dischargeTypemapper;
	}

	/**
	 * Create a new {@link DischargeType}
	 * @param dischTypeDTO
	 * @return {@code true} if the {@link DischargeType} has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> newDischargeType(@RequestBody DischargeTypeDTO dischTypeDTO) throws OHServiceException {
		String code = dischTypeDTO.getCode();
		LOGGER.info("Create discharge type {}", code);
		DischargeType newDischargeType = discTypeManager.newDischargeType(mapper.map2Model(dischTypeDTO));
		if (!discTypeManager.isCodePresent(code)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(newDischargeType));
	}

	/**
	 * Update the specified {@link DischargeType}
	 * @param dischTypeDTO
	 * @return {@code true} if the {@link DischargeType} has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> updateDischargeType(@RequestBody DischargeTypeDTO dischTypeDTO) throws OHServiceException {
		LOGGER.info("Update discharge type with code: {}", dischTypeDTO.getCode());
		DischargeType dischType = mapper.map2Model(dischTypeDTO);
		if (!discTypeManager.isCodePresent(dischTypeDTO.getCode())) {
			return ResponseEntity.badRequest().body(null);
		}
		DischargeType updatedDischargeType = discTypeManager.updateDischargeType(dischType);
		if (!discTypeManager.isCodePresent(updatedDischargeType.getCode())) {
			return ResponseEntity.badRequest().body(new OHExceptionMessage("Discharge Type not found."));
		}
		return ResponseEntity.ok(mapper.map2DTO(dischType));
	}

	/**
	 * Get all the available {@link DischargeType}s
	 * @return a {@link List} of {@link DischargeType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/dischargetypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DischargeTypeDTO>> getDischargeTypes() throws OHServiceException {
		LOGGER.info("Get all discharge types ");
		List<DischargeType> dischTypes = discTypeManager.getDischargeType();
		List<DischargeTypeDTO> dischTypeDTOs = mapper.map2DTOList(dischTypes);
		if (dischTypeDTOs.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(dischTypeDTOs);
		}
	}

	/**
	 * Delete {@link DischargeType} for the specified code.
	 * @param code
	 * @return {@code true} if the {@link DischargeType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/dischargetypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteDischargeType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete discharge type code: {}", code);
		if (discTypeManager.isCodePresent(code)) {
			List<DischargeType> dischTypes = discTypeManager.getDischargeType();
			List<DischargeType> dischTypeFounds = dischTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (!dischTypeFounds.isEmpty()) {
				discTypeManager.deleteDischargeType(dischTypeFounds.get(0));
			}
		} else {
			return ResponseEntity.badRequest().body("Discharge Type not found.");
		}
		return ResponseEntity.ok(true);
	}

}
