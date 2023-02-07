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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.opd.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.generaldata.MessageBundle;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
@Api(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpdController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpdController.class);

	@Autowired
	protected OpdBrowserManager opdManager;
	
	@Autowired
	protected OpdMapper mapper;
	
	@Autowired
	protected PatientBrowserManager patientManager;

	public OpdController(OpdBrowserManager opdManager, OpdMapper opdmapper, PatientBrowserManager patientManager) {
		this.opdManager = opdManager;
		this.mapper = opdmapper;
		this.patientManager = patientManager;
	}

	/**
	 * Create a new {@link Opd}.
	 * @param opdDTO
	 * @return the code of {@link Opd} stored
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpdDTO> newOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		int code = opdDTO.getCode();
		LOGGER.info("store Out patient {}", code);
		Patient patient = patientManager.getPatientById(opdDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}
		if (opdDTO.getNote() == " ") {
			throw new OHAPIException(new OHExceptionMessage(null, "not field is mandatory!", OHSeverityLevel.ERROR));
		}
		Opd opdToInsert = mapper.map2Model(opdDTO);
		opdToInsert.setPatient(patient);
		Opd isCreatedOpd = opdManager.newOpd(opdToInsert);
		if (isCreatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedOpd));
	}

	/**
	 * Updates the specified {@link Opd}.
	 * @param opdDTO
	 * @return the code of updated {@link Opd}
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/opds/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpdDTO> updateOpd(@PathVariable("code") int code, @RequestBody OpdDTO opdDTO)
			throws OHServiceException {
		LOGGER.info("Update opds code: {}", opdDTO.getCode());
		if (opdManager.getOpdById(code) == null) {	
			throw new OHAPIException(new OHExceptionMessage(null, "Opd not found!", OHSeverityLevel.ERROR));
		}

		if (opdDTO.getCode() != 0 && opdDTO.getCode() != code) {	
			throw new OHAPIException(new OHExceptionMessage(null, "Opd not found!", OHSeverityLevel.ERROR));
		}
		
		Patient patient = patientManager.getPatientById(opdDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}

		Opd opdToUpdate = mapper.map2Model(opdDTO);
		opdToUpdate.setLock(opdDTO.getLock());
		Opd updatedOpd = opdManager.updateOpd(opdToUpdate);
		if (updatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.OK).body(mapper.map2DTO(updatedOpd));
	}

	/**
	 * Get all {@link Opd}s for today or for the last week.
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdToDayOrWeek(@RequestParam(name="oneWeek", required=false) Boolean oneWeek) throws OHServiceException {
		LOGGER.info("Get all today or since one week opd");
		if (oneWeek == null) {
			oneWeek = false;
		}		
		List<Opd> opds = opdManager.getOpd(oneWeek);
		List<OpdDTO> opdDTOs = mapper.map2DTOList(opds);
		if (opdDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);
		} else {
			return ResponseEntity.ok(opdDTOs);
		}
	}
	
	/**
	 * Get all {@link Opd}s within specified date range.
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdByDates(
			@RequestParam(value = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dateFrom, 
			@RequestParam(value = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dateTo, 
			@RequestParam(value = "diseaseTypeCode", required = false) String diseaseTypeCode,
			@RequestParam(value = "diseaseCode", required = false) String diseaseCode,
			@RequestParam(value = "ageFrom", required = false, defaultValue = "0") Integer ageFrom, 
			@RequestParam(value = "ageTo", required = false, defaultValue = "200") Integer ageTo,
			@RequestParam(value = "sex", required = false, defaultValue = "A") char sex,
			@RequestParam(value = "newPatient", required = false, defaultValue = "A") char newPatient,
			@RequestParam(value = "patientCode", required = false, defaultValue = "0") Integer patientCode) throws OHServiceException {
		LOGGER.info("Get opd within specified dates");
		LOGGER.debug("dateFrom: {}", dateFrom);
		LOGGER.debug("dateTo: {}", dateTo);
		LOGGER.debug("diseaseTypeCode: {}", diseaseTypeCode);
		LOGGER.debug("diseaseCode: {}", diseaseCode);
		LOGGER.debug("ageFrom: {}", ageFrom);
		LOGGER.debug("ageTo: {}", ageTo);
		LOGGER.debug("sex: {}", sex);
		LOGGER.debug("newPatient: {}", newPatient);
		LOGGER.debug("patientCode: {}", patientCode);
		
		List<Opd> opds = null;
		if (patientCode != 0) {
			opds = opdManager.getOpdList(patientCode);
		} else {
			opds = opdManager.getOpd(null, MessageBundle.getMessage(diseaseTypeCode), MessageBundle.getMessage(diseaseCode), dateFrom, dateTo, ageFrom,  ageTo, sex, newPatient, null);
		}

		List<OpdDTO> opdDTOs = opds.stream().map(opd -> {
			return mapper.map2DTO(opd);
		}).collect(Collectors.toList());
		
		if (opdDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);
		} else {
			return ResponseEntity.ok(opdDTOs);
		}
	}
	
	/**
	 * Get all {@link Opd}s associated to specified patient CODE.
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/patient/{pcode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdByPatient(@PathVariable("pcode") int pcode) throws OHServiceException {
		LOGGER.info("Get opd associated to specified patient CODE: {}", pcode);

		List<Opd> opds = opdManager.getOpdList(pcode);
		List<OpdDTO> opdDTOs = new ArrayList<OpdDTO>();
		if (!opds.isEmpty()) {
			opdDTOs = opds.stream().map(opd -> {
				return mapper.map2DTO(opd);
			}).collect(Collectors.toList());
			return ResponseEntity.ok(opdDTOs);

		} else {

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);

		}
	}

	/**
	 * Delete {@link Opd} for specified code.
	 * @param code
	 * @return {@code true} if the {@link Opd} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/opds/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOpd(@PathVariable("code") int code) throws OHServiceException {
		LOGGER.info("Delete Opd code: {}", code);
		Opd toDelete = new Opd();
		toDelete.setCode(code);
		boolean isDeleted = opdManager.deleteOpd(toDelete);
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * Get the maximum progressive number within specified year or within current year if {@code 0}.
	 * @return the max progressive number
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/ProgYear/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getProgYear(@PathVariable("year") int year) throws OHServiceException {
		LOGGER.info("Get progressive number within specified year");
		int yProg = opdManager.getProgYear(year);
		return ResponseEntity.ok(yProg);
	}
	
	/**
	 * Get the last {@link Opd} in time associated with specified patient ID.
	 * @return last Opd associated with specified patient ID or {@code null}
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/last/{patientCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OpdDTO> getLastOpd(@PathVariable("patientCode") int patientCode) throws OHServiceException {
		LOGGER.info("Get the last opp for patient code: {}", patientCode);
		Opd lastOpd = opdManager.getLastOpd(patientCode);
		return ResponseEntity.ok(mapper.map2DTO(lastOpd));
	}
	
	/**
	 * Check if the given {@code opdNum} does already exist for the given {@code year}.
	 * @return {@code true} if the given number exists in year, {@code false} otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/check/progyear", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isExistOpdNum(@RequestParam("opdNum") int opdNum, @RequestParam("year") int year) throws OHServiceException {
		LOGGER.info("check if progYear: {}  already exist for year : {}", opdNum, year);
		Boolean isExist = opdManager.isExistOpdNum(opdNum, year);
		return ResponseEntity.ok(isExist);
	}

}
