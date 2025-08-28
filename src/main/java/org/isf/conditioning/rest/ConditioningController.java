/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.isf.admission.model.Admission;
import org.isf.admission.rest.AdmissionController;
import org.isf.conditioning.dto.ConditioningDTO;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.mapper.ConditioningMapper;
import org.isf.conditioning.model.Conditioning;
import org.isf.conditioning.service.ConditioningOperations;
import org.isf.menu.model.User;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Conditioning")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditioningController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditioningController.class);

	private final ConditioningBrowserManager browserManager;
	private final ConditioningMapper conditioningMapper;

	public ConditioningController(ConditioningBrowserManager browserManager, ConditioningMapper conditioningMapper) {
		this.browserManager = browserManager;
		this.conditioningMapper = conditioningMapper;
	}

	/**
	 * Update an existing {@link Conditioning}.
	 *
	 * @param updateConditioningDTO Conditioning data to update
	 * @return updated {@link ConditioningDTO} if successful, or error message if not found or invalid
	 * @throws OHServiceException When the update operation fails
	 */
	@PutMapping("/conditioning/{id}")
	public ResponseEntity<ConditioningDTO> updateConditioning(@PathVariable("id") int id, @RequestBody @Valid ConditioningDTO updateConditioningDTO)
		throws OHServiceException {

		if(id != updateConditioningDTO.getId()){
			throw new OHAPIException(new OHExceptionMessage("Conditioning does not match."));
		}
		Conditioning old = browserManager.getConditioning(updateConditioningDTO.getId());
		if (old == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not found."));
		}

		Conditioning updateConditioning = conditioningMapper.map2Model(updateConditioningDTO);

		if (updateConditioningDTO.getPerformById() != null) {
			User user = browserManager.getConditioning(updateConditioningDTO.getId()).getPerformBy();
			if (user == null) {
				throw new OHAPIException(new OHExceptionMessage("User (performBy) not found."));
			}
			updateConditioning.setPerformBy(user);
		} else {
			throw new OHAPIException(new OHExceptionMessage("performBy field is required."));
		}

		browserManager.validateConditioning(updateConditioning);

		Conditioning updatedConditioning = browserManager.updateConditioning(updateConditioning);
		if (updatedConditioning == null) {
			throw new OHAPIException(new OHExceptionMessage("Conditioning not updated."));
		}

		return ResponseEntity.ok(conditioningMapper.map2DTO(updatedConditioning));
	}
}
