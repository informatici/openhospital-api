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
package org.isf.opetype.rest;

import java.util.List;

import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.mapper.OperationTypeMapper;
import org.isf.opetype.model.OperationType;
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
@Tag(name = "Operations Types")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationTypeController.class);

	private final OperationTypeBrowserManager opeTypeManager;

	private final OperationTypeMapper mapper;

	public OperationTypeController(OperationTypeBrowserManager opeTypeManager, OperationTypeMapper operationTypemapper) {
		this.opeTypeManager = opeTypeManager;
		this.mapper = operationTypemapper;
	}

	/**
	 * Create a new {@link OperationType}.
	 *
	 * @param operationTypeDTO Operation Type payload
	 * @return the newly stored {@link OperationType} object.
	 * @throws OHServiceException When failed to create operation type
	 */
	@PostMapping("/operationtypes")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationTypeDTO newOperationType(@RequestBody OperationTypeDTO operationTypeDTO) throws OHServiceException {
		String code = operationTypeDTO.getCode();
		LOGGER.info("Create Operation Type {}", code);

		try {
			return mapper.map2DTO(opeTypeManager.newOperationType(mapper.map2Model(operationTypeDTO)));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Operation Type not created."));
		}
	}

	/**
	 * Updates the specified {@link OperationType}.
	 *
	 * @param operationTypeDTO Operation Type payload
	 * @return the newly updated {@link OperationType} object.
	 * @throws OHServiceException When failed to update operation type
	 */
	@PutMapping("/operationtypes/{code}")
	public OperationTypeDTO updateOperationTypes(
		@PathVariable String code, @RequestBody OperationTypeDTO operationTypeDTO
	) throws OHServiceException {
		LOGGER.info("Update operationtypes code: {}", operationTypeDTO.getCode());

		OperationType opeType = mapper.map2Model(operationTypeDTO);
		if (!opeTypeManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Operation Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return mapper.map2DTO(opeTypeManager.updateOperationType(opeType));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Operation Type not updated."));
		}
	}

	/**
	 * Get all the available {@link OperationType}s.
	 *
	 * @return a {@link List} of {@link OperationType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get operation types
	 */
	@GetMapping("/operationtypes")
	public List<OperationTypeDTO> getOperationTypes() throws OHServiceException {
		LOGGER.info("Get all operation Types ");

		return mapper.map2DTOList(opeTypeManager.getOperationType());
	}

	/**
	 * Delete {@link OperationType} with the specified code.
	 *
	 * @param code Operation Type code
	 * @return {@code true} if the {@link OperationType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete operation type
	 */
	@DeleteMapping("/operationtypes/{code}")
	public boolean deleteOperationType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete Operation Type code: {}", code);
		List<OperationType> opeTypes = opeTypeManager.getOperationType();
		List<OperationType> opeTypeFounds = opeTypes.stream().filter(ad -> ad.getCode().equals(code)).toList();
		if (opeTypeFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Operation Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			opeTypeManager.deleteOperationType(opeTypeFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.error("Delete Operation Type code: {} failed.", code);
			throw new OHAPIException(new OHExceptionMessage("Operation Type not deleted."));
		}
	}
}
