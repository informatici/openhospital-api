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
package org.isf.admtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
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

@RestController(value = "/admissiontypes")
@Tag(name = "AdmissionTypes")
@SecurityRequirement(name = "bearerAuth")
public class AdmissionTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdmissionTypeController.class);

	@Autowired
	protected AdmissionTypeBrowserManager admtManager;
	
	@Autowired
	protected AdmissionTypeMapper mapper;

	public AdmissionTypeController(AdmissionTypeBrowserManager admtManager, AdmissionTypeMapper admissionTypemapper) {
		this.admtManager = admtManager;
		this.mapper = admissionTypemapper;
	}

	/**
	 * Create a new {@link AdmissionType}
	 * @param admissionTypeDTO
	 * @return {@code true} if the admission type has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> newAdmissionType(@RequestBody AdmissionTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();
		LOGGER.info("Create Admission Type {}", code);
		AdmissionType newAdmissionType = admtManager.newAdmissionType(mapper.map2Model(admissionTypeDTO));
		if (admtManager.isCodePresent(code)) {
			return ResponseEntity.badRequest().body(new OHExceptionMessage("The code of Admission Type is already used."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(newAdmissionType));
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 * @param admissionTypeDTO
	 * @return {@code true} if the admission type has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> updateAdmissionTypes(@RequestBody AdmissionTypeDTO admissionTypeDTO)
			throws OHServiceException {
		LOGGER.info("Update admissiontypes code: {}", admissionTypeDTO.getCode());
		AdmissionType admt = mapper.map2Model(admissionTypeDTO);
		if (!admtManager.isCodePresent(admt.getCode())) {
			return ResponseEntity.badRequest().body(new OHExceptionMessage("The Admission Type is not found."));
		}
		AdmissionType updatedAdmissionType = admtManager.updateAdmissionType(admt);
		return ResponseEntity.ok(mapper.map2DTO(updatedAdmissionType));
	}

	/**
	 * Get all the available {@link AdmissionType}s.
	 * @return a {@link List} of {@link AdmissionType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdmissionTypeDTO>> getAdmissionTypes() throws OHServiceException {
		LOGGER.info("Get all Admission Types ");
		List<AdmissionType> admissionTypes = admtManager.getAdmissionType();
		List<AdmissionTypeDTO> admissionTypeDTOs = mapper.map2DTOList(admissionTypes);
		if (admissionTypeDTOs.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	/**
	 * Delete {@link AdmissionType} for specified code.
	 * @param code
	 * @return {@code true} if the {@link AdmissionType} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/admissiontypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteAdmissionType(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete Admission Type code: {}", code);
		if (admtManager.isCodePresent(code)) {
			List<AdmissionType> admissionTypes = admtManager.getAdmissionType();
			List<AdmissionType> admtFounds = admissionTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (!admtFounds.isEmpty()) {
				admtManager.deleteAdmissionType(admtFounds.get(0));
			}
		} else {
			return ResponseEntity.badRequest().body("The Admission Type is not found.");
		}
		return ResponseEntity.ok(true);
	}

}
