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
package org.isf.admtype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.mapper.AdmissionTypeMapper;
import org.isf.admtype.model.AdmissionType;
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
@Api(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdmissionTypeController {

	@Autowired
	protected AdmissionTypeBrowserManager admtManager;
	
	@Autowired
	protected AdmissionTypeMapper mapper;

	private final Logger logger = LoggerFactory.getLogger(AdmissionTypeController.class);

	public AdmissionTypeController(AdmissionTypeBrowserManager admtManager, AdmissionTypeMapper admissionTypemapper) {
		this.admtManager = admtManager;
		this.mapper = admissionTypemapper;
	}

	/**
	 * create a new {@link AdmissionType}
	 * @param admissionTypeDTO
	 * @return <code>true</code> if the admission type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newAdmissionType(@RequestBody AdmissionTypeDTO admissionTypeDTO) throws OHServiceException {
		String code = admissionTypeDTO.getCode();
		logger.info("Create Admission Type " + code);
		boolean isCreated = admtManager.newAdmissionType(mapper.map2Model(admissionTypeDTO));
		AdmissionType admtCreated = null;
		List<AdmissionType> admtFounds = admtManager.getAdmissionType().stream().filter(ad -> ad.getCode().equals(code))
				.collect(Collectors.toList());
		if (admtFounds.size() > 0)
			admtCreated = admtFounds.get(0);
		if (!isCreated || admtCreated == null) {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Admission Type is not created!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(admtCreated.getCode());
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 * @param admissionTypeDTO
	 * @return <code>true</code> if the admission type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateAdmissionTypet(@RequestBody AdmissionTypeDTO admissionTypeDTO)
			throws OHServiceException {
		logger.info("Update admissiontypes code:" + admissionTypeDTO.getCode());
		AdmissionType admt = mapper.map2Model(admissionTypeDTO);
		if (!admtManager.codeControl(admt.getCode()))
			throw new OHAPIException(new OHExceptionMessage(null, "Admission Type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = admtManager.updateAdmissionType(admt);
		if (!isUpdated)
			throw new OHAPIException(
					new OHExceptionMessage(null, "Admission Type is not updated!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(admt.getCode());
	}

	/**
	 * get all the available {@link AdmissionType}s.
	 * @return a {@link List} of {@link AdmissionType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissiontypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdmissionTypeDTO>> getAdmissionTypes() throws OHServiceException {
		logger.info("Get all Admission Types ");
		List<AdmissionType> admissionTypes = admtManager.getAdmissionType();
		List<AdmissionTypeDTO> admissionTypeDTOs = mapper.map2DTOList(admissionTypes);
		if (admissionTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admissionTypeDTOs);
		} else {
			return ResponseEntity.ok(admissionTypeDTOs);
		}
	}

	/**
	 * Delete {@link AdmissionType} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link AdmissionType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/admissiontypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteAdmissionType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete Admission Type code:" + code);
		boolean isDeleted = false;
		if (admtManager.codeControl(code)) {
			List<AdmissionType> admts = admtManager.getAdmissionType();
			List<AdmissionType> admtFounds = admts.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (admtFounds.size() > 0)
				isDeleted = admtManager.deleteAdmissionType(admtFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
