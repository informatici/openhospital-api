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
package org.isf.opd.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.dto.OpdWithOperationRowDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;
import org.isf.operation.dto.OperationRowDTO;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.operation.model.OperationRow;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.pagination.Page;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/opds")
@Tag(name = "Opds")
@SecurityRequirement(name = "bearerAuth")
public class OpdController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpdController.class);
	
	// TODO: to centralize
	protected static final String DEFAULT_PAGE_SIZE = "80";

	@Autowired
	protected OpdBrowserManager opdManager;
	
	@Autowired
	protected OpdMapper mapper;
	
	@Autowired
	protected PatientBrowserManager patientManager;
	
	@Autowired
	protected OperationRowBrowserManager operationRowManager;
	
	@Autowired
	protected OperationRowMapper opRowMapper;
	
	@Autowired
    protected WardBrowserManager wardManager;
	
	@Autowired
    protected DiseaseTypeBrowserManager diseaseTypeManager;

	public OpdController(OpdBrowserManager opdManager, OpdMapper opdmapper, PatientBrowserManager patientManager, OperationRowBrowserManager 
			operationRowManager, OperationRowMapper opRowMapper, WardBrowserManager wardManager, DiseaseTypeBrowserManager diseaseTypeManager) {
		this.opdManager = opdManager;
		this.mapper = opdmapper;
		this.patientManager = patientManager;
		this.operationRowManager = operationRowManager;
		this.opRowMapper = opRowMapper;
		this.wardManager = wardManager;
		this.diseaseTypeManager = diseaseTypeManager;
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
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}
		if (" ".equals(opdDTO.getNote())) {
			throw new OHAPIException(new OHExceptionMessage("Note field mandatory."));
		}
		Opd opdToInsert = mapper.map2Model(opdDTO);
		opdToInsert.setPatient(patient);
		Opd isCreatedOpd = opdManager.newOpd(opdToInsert);
		if (isCreatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedOpd));
	}

	/**
	 * Create a new {@link OpdWithOperationRowDTO}.
	 * @param opdWithOperationRowDTO
	 * @return the OpdWithOperationRowDTO stored
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/opds/rows", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpdWithOperationRowDTO> newOpdWithOperationRow(@RequestBody OpdWithOperationRowDTO opdWithOperationRowDTO) throws OHServiceException {
		int code = opdWithOperationRowDTO.getOpdDTO().getCode();
		LOGGER.info("store Out patient {}", code);
		OpdWithOperationRowDTO opdWithOperatioRow = new OpdWithOperationRowDTO();
		Patient patient = patientManager.getPatientById(opdWithOperationRowDTO.getOpdDTO().getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}
		if (" ".equals(opdWithOperationRowDTO.getOpdDTO().getNote())) {
			throw new OHAPIException(new OHExceptionMessage("Note field mandatory."));
		}
		Opd opdToInsert = mapper.map2Model(opdWithOperationRowDTO.getOpdDTO());
		opdToInsert.setPatient(patient);
		Opd isCreatedOpd = opdManager.newOpd(opdToInsert);
		if (isCreatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not created."));
		}
		OpdDTO opdDTO = mapper.map2DTO(isCreatedOpd);
		opdWithOperatioRow.setOpdDTO(opdDTO);
		List<OperationRowDTO> listOp = new ArrayList<>();
		if (!opdWithOperationRowDTO.getOperationRows().isEmpty()) {
			for (OperationRowDTO operationRow : opdWithOperationRowDTO.getOperationRows()) {
				operationRow.setOpd(opdDTO);
				OperationRow createOpeRow = operationRowManager.newOperationRow(opRowMapper.map2Model(operationRow));
				listOp.add(opRowMapper.map2DTO(createOpeRow));
			}
		}
		opdWithOperatioRow.setOperationRows(listOp);
		return ResponseEntity.status(HttpStatus.CREATED).body(opdWithOperatioRow);
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
		if (opdManager.getOpdById(code).isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."));
		}

		if (opdDTO.getCode() != 0 && opdDTO.getCode() != code) {	
			throw new OHAPIException(new OHExceptionMessage("Opd not found."));
		}
		
		Patient patient = patientManager.getPatientById(opdDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Opd opdToUpdate = mapper.map2Model(opdDTO);
		opdToUpdate.setLock(opdDTO.getLock());
		Opd updatedOpd = opdManager.updateOpd(opdToUpdate);
		if (updatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not updated."));
		}
		return ResponseEntity.status(HttpStatus.OK).body(mapper.map2DTO(updatedOpd));
	}

	/**
	 * Updates the specified {@link OpdWithOperationRowDTO}.
	 * @param code
	 * @param opdWithOperationRowDTO
	 * @return the OpdWithOperationRowDTO updated
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/opds/rows/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpdWithOperationRowDTO> updateOpdWithOperationRow(@PathVariable("code") int code, @RequestBody OpdWithOperationRowDTO opdWithOperationRowDTO)
			throws OHServiceException {
		LOGGER.info("Update opds code: {}", code);
		OpdWithOperationRowDTO opdWithOperatioRow = new OpdWithOperationRowDTO();
		if (opdManager.getOpdById(code).isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."));
		}

		if (opdWithOperationRowDTO.getOpdDTO().getCode() != 0 && opdWithOperationRowDTO.getOpdDTO().getCode() != code) {	
			throw new OHAPIException(new OHExceptionMessage("Opd not found."));
		}
		
		Patient patient = patientManager.getPatientById(opdWithOperationRowDTO.getOpdDTO().getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Opd opdToUpdate = mapper.map2Model(opdWithOperationRowDTO.getOpdDTO());
		opdToUpdate.setLock(opdWithOperationRowDTO.getOpdDTO().getLock());
		Opd updatedOpd = opdManager.updateOpd(opdToUpdate);
		if (updatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not updated."));
		}
		OpdDTO opdDTO = mapper.map2DTO(updatedOpd);
		opdWithOperatioRow.setOpdDTO(opdDTO);
		List<OperationRowDTO> listOpeRow = new ArrayList<>();
		if (!opdWithOperationRowDTO.getOperationRows().isEmpty()) {
			for (OperationRowDTO operationRow : opdWithOperationRowDTO.getOperationRows()) {
				operationRow.setOpd(opdDTO);
				OperationRow updateOpeRow;
				if (operationRow.getId() == 0) {
					updateOpeRow = operationRowManager.newOperationRow(opRowMapper.map2Model(operationRow));
				} else {
					updateOpeRow = operationRowManager.updateOperationRow(opRowMapper.map2Model(operationRow));
				}
				
				listOpeRow.add(opRowMapper.map2DTO(updateOpeRow));
			}
		}
		opdWithOperatioRow.setOperationRows(listOpeRow);
		return ResponseEntity.status(HttpStatus.OK).body(opdWithOperatioRow);
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
	public ResponseEntity<Page<OpdDTO>> getOpdByDates(
			@RequestParam(value = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Schema(implementation = String.class) LocalDate dateFrom, 
			@RequestParam(value = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Schema(implementation = String.class) LocalDate dateTo, 
			@RequestParam(value = "diseaseTypeCode", required = false) String diseaseTypeCode,
			@RequestParam(value = "diseaseCode", required = false) String diseaseCode,
			@RequestParam(value = "ageFrom", required = false, defaultValue = "0") Integer ageFrom, 
			@RequestParam(value = "ageTo", required = false, defaultValue = "200") Integer ageTo,
			@RequestParam(value = "sex", required = false, defaultValue = "A") char sex,
			@RequestParam(value = "newPatient", required = false, defaultValue = "A") char newPatient,
			@RequestParam(value = "patientCode", required = false, defaultValue = "0") Integer patientCode,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "paged", required = false, defaultValue = "false") boolean paged,
			@RequestParam(value = "wardCode", required = false) String wardCode) throws OHServiceException {
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
		LOGGER.debug("page: {}", page);
		LOGGER.debug("size: {}", size);
		LOGGER.debug("paged: {}", paged);
		LOGGER.debug("wardCode: {}", wardCode);

		Page<OpdDTO> opdPageable = new Page<>();
		List<OpdDTO> opdDTOs;
		List<Opd> opds;
		Ward ward = null;
		if (wardCode != null) {
			ward = wardManager.findWard(wardCode);
		}
		if (paged) {
			PagedResponse<Opd> opdsPaged = opdManager.getOpdPageable(ward, diseaseTypeCode, MessageBundle.getMessage(diseaseTypeCode), dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, page,size);
			opdDTOs = opdsPaged.getData().stream().map(opd -> mapper.map2DTO(opd)).collect(Collectors.toList());
			opdPageable.setPageInfo(mapper.setParameterPageInfo(opdsPaged.getPageInfo()));
		} else {
			if (patientCode != 0) {
				opds = opdManager.getOpdList(patientCode);
			} else {
				opds = opdManager.getOpd(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, null);
			}
			opdDTOs = opds.stream().map(opd -> mapper.map2DTO(opd)).collect(Collectors.toList());
		}
		opdPageable.setData(opdDTOs);
		if (opdDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdPageable);
		} else {
			return ResponseEntity.ok(opdPageable);
		}
	}
	
	/**
	 * Get all {@link OpdWithOperationRowDTO}s associated to specified patient CODE.
	 * @return a {@link List} of {@link OpdWithOperationRowDTO} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/patient/{pcode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdWithOperationRowDTO>> getOpdByPatient(@PathVariable("pcode") int pcode) throws OHServiceException {
		LOGGER.info("Get opd associated to specified patient CODE: {}", pcode);

		List<Opd> opds = opdManager.getOpdList(pcode);
		List<OpdWithOperationRowDTO> opdWithOperations = new ArrayList<>();
		if (!opds.isEmpty()) {
			opdWithOperations = opds.stream().map(opd -> {
				OpdWithOperationRowDTO opRows = new OpdWithOperationRowDTO();
				opRows.setOpdDTO(mapper.map2DTO(opd));
				List<OperationRow> listOp = new ArrayList<>();
				try {
					listOp = operationRowManager.getOperationRowByOpd(opd);
				} catch (OHServiceException e) {
					// TODO Auto-generated catch block
					LOGGER.error("Unable to get the List of operation associate to this Opd");
				}
				if (!listOp.isEmpty()) {
					opRows.setOperationRows(opRowMapper.map2DTOList(listOp));
				} else {
					opRows.setOperationRows(new ArrayList<>());
				}
					
				return opRows;
			}).collect(Collectors.toList());
			return ResponseEntity.ok(opdWithOperations);

		} else {

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdWithOperations);

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
		try {
			opdManager.deleteOpd(toDelete);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Opd not deleted."));
		}
		return ResponseEntity.ok(true);
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
		if (lastOpd == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(mapper.map2DTO(lastOpd));
		}
		
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
