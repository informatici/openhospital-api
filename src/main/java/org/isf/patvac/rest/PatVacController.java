/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patvac.rest;

import java.time.LocalDate;
import java.util.List;

import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.patvac.manager.PatVacManager;
import org.isf.patvac.mapper.PatVacMapper;
import org.isf.patvac.model.PatientVaccine;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/patientvaccines", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatVacController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatVacController.class);

	@Autowired
	protected PatVacManager patVacManager;
	
	@Autowired
	protected PatVacMapper mapper;

	public PatVacController(PatVacManager patVacManager, PatVacMapper patientVaccinemapper) {
		this.patVacManager = patVacManager;
		this.mapper = patientVaccinemapper;
	}

	/**
	 * Create a new {@link PatientVaccine}.
	 * @param patientVaccineDTO
	 * @return {@code true} if the operation type has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/patientvaccines", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PatientVaccineDTO> newPatientVaccine(@RequestBody PatientVaccineDTO patientVaccineDTO) throws OHServiceException {
		int code = patientVaccineDTO.getCode();
		LOGGER.info("Create patient vaccine {}", code);
		PatientVaccine isCreatedPatientVaccine = patVacManager.newPatientVaccine(mapper.map2Model(patientVaccineDTO));
		if (isCreatedPatientVaccine == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "patient vaccine is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedPatientVaccine));
	}

	/**
	 * Updates the specified {@link PatientVaccine}.
	 * @param patientVaccineDTO
	 * @return {@code true} if the operation type has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/patientvaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PatientVaccineDTO> updatePatientVaccinet(@PathVariable Integer code, @RequestBody PatientVaccineDTO patientVaccineDTO)
			throws OHServiceException {
		LOGGER.info("Update patientvaccines code: {}", patientVaccineDTO.getCode());
		PatientVaccine patvac = mapper.map2Model(patientVaccineDTO);
		patvac.setLock(patientVaccineDTO.getLock());
		PatientVaccine isUpdatedPatientVaccine = patVacManager.updatePatientVaccine(mapper.map2Model(patientVaccineDTO));
		if (isUpdatedPatientVaccine == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "patient vaccine is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(mapper.map2DTO(isUpdatedPatientVaccine));
	}

	/**
	 * Get all the {@link PatientVaccine}s for today or in the last week.
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/patientvaccines/week", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientVaccineDTO>> getPatientVaccines(@RequestParam(required=false) Boolean oneWeek) throws OHServiceException {
		LOGGER.info("Get the all patient vaccine of to day or one week");
		if (oneWeek == null) {
			oneWeek = false;
		}
		List<PatientVaccine> patientVaccines = patVacManager.getPatientVaccine(oneWeek);
		List<PatientVaccineDTO> patientVaccineDTOs = mapper.map2DTOList(patientVaccines);
		if (patientVaccineDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patientVaccineDTOs);
		} else {
			return ResponseEntity.ok(patientVaccineDTOs);
		}
	}
	
	/**
	 * Get all {@link PatientVaccine}s within {@code dateFrom} and {@code dateTo}.
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/patientvaccines/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientVaccineDTO>> getPatientVaccinesByDatesRanges(@RequestParam String vaccineTypeCode, @RequestParam String vaccineCode, 
			@RequestParam LocalDate dateFrom, @RequestParam LocalDate dateTo, @RequestParam char sex, @RequestParam int ageFrom, @RequestParam int ageTo) throws OHServiceException {
		LOGGER.info("filter patient vaccine by dates ranges");

		List<PatientVaccine> patientVaccines = patVacManager.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom.atStartOfDay(), dateTo.atStartOfDay(), sex, ageFrom, ageTo);
		List<PatientVaccineDTO> patientVaccineDTOs = mapper.map2DTOList(patientVaccines);
		if (patientVaccineDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patientVaccineDTOs);
		} else {
			return ResponseEntity.ok(patientVaccineDTOs);
		}
	}
	
	/**
	 * Get the maximum progressive number within specified year or within current year if {@code 0}.
	 * @return {@code int} - the progressive number in the year
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/patientvaccines/progyear/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getProgYear(@PathVariable int year) throws OHServiceException {
		LOGGER.info("Get progressive number within specified year");
		int yProg = patVacManager.getProgYear(year);
		return ResponseEntity.ok(yProg);
	}

	/**
	 * Delete {@link PatientVaccine} for specified code.
	 * @param code
	 * @return {@code true} if the {@link PatientVaccine} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/patientvaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePatientVaccine(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Delete patient vaccine code: {}", code);
		PatientVaccine patVac = new PatientVaccine();
		patVac.setCode(code);
		boolean isDeleted = patVacManager.deletePatientVaccine(patVac);
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isDeleted);
	}

}
