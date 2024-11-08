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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Opds")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OpdController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpdController.class);

	// TODO: to centralize
	protected static final String DEFAULT_PAGE_SIZE = "80";

	private final OpdBrowserManager opdManager;

	private final OpdMapper mapper;

	private final PatientBrowserManager patientManager;

	private final OperationRowBrowserManager operationRowManager;

	private final OperationRowMapper opRowMapper;

	private final WardBrowserManager wardManager;

	public OpdController(
		OpdBrowserManager opdManager,
		OpdMapper opdmapper,
		PatientBrowserManager patientManager,
		OperationRowBrowserManager
			operationRowManager,
		OperationRowMapper opRowMapper,
		WardBrowserManager wardManager
	) {
		this.opdManager = opdManager;
		this.mapper = opdmapper;
		this.patientManager = patientManager;
		this.operationRowManager = operationRowManager;
		this.opRowMapper = opRowMapper;
		this.wardManager = wardManager;
	}

	/**
	 * Create a new {@link Opd}.
	 * @param opdDTO OPD payload
	 * @return the code of {@link Opd} stored
	 * @throws OHServiceException When failed to create OPD
	 */
	@PostMapping("/opds")
	@ResponseStatus(HttpStatus.CREATED)
	public OpdDTO newOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		LOGGER.info("store Out patient {}", opdDTO.getCode());
		Patient patient = patientManager.getPatientById(opdDTO.getPatientCode());

		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
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

		return mapper.map2DTO(isCreatedOpd);
	}

	/**
	 * Create a new {@link OpdWithOperationRowDTO}.
	 * @param opdWithOperationRowDTO OPD with Operation row payload
	 * @return the OpdWithOperationRowDTO stored
	 * @throws OHServiceException When failed to create OPD
	 */
	@PostMapping("/opds/rows")
	@ResponseStatus(HttpStatus.CREATED)
	public OpdWithOperationRowDTO newOpdWithOperationRow(
		@RequestBody OpdWithOperationRowDTO opdWithOperationRowDTO
	) throws OHServiceException {
		int code = opdWithOperationRowDTO.getOpdDTO().getCode();
		LOGGER.info("store Out patient {}", code);
		OpdWithOperationRowDTO opdWithOperationRow = new OpdWithOperationRowDTO();

		Patient patient = patientManager.getPatientById(opdWithOperationRowDTO.getOpdDTO().getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
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
		opdWithOperationRow.setOpdDTO(opdDTO);
		List<OperationRowDTO> listOp = new ArrayList<>();
		if (!opdWithOperationRowDTO.getOperationRows().isEmpty()) {
			for (OperationRowDTO operationRow : opdWithOperationRowDTO.getOperationRows()) {
				operationRow.setOpd(opdDTO);
				OperationRow createOpeRow = operationRowManager.newOperationRow(opRowMapper.map2Model(operationRow));
				listOp.add(opRowMapper.map2DTO(createOpeRow));
			}
		}
		opdWithOperationRow.setOperationRows(listOp);

		return opdWithOperationRow;
	}

	/**
	 * Updates the specified {@link Opd}.
	 * @param opdDTO OPD payload
	 * @return the code of updated {@link Opd}
	 * @throws OHServiceException When failed to update OPD
	 */
	@PutMapping("/opds/{code}")
	public OpdDTO updateOpd(@PathVariable("code") int code, @RequestBody OpdDTO opdDTO) throws OHServiceException {
		LOGGER.info("Update opds code: {}", opdDTO.getCode());
		if (opdManager.getOpdById(code).isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."), HttpStatus.NOT_FOUND);
		}

		if (opdDTO.getCode() != 0 && opdDTO.getCode() != code) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."), HttpStatus.NOT_FOUND);
		}

		Patient patient = patientManager.getPatientById(opdDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}

		Opd opdToUpdate = mapper.map2Model(opdDTO);
		opdToUpdate.setLock(opdDTO.getLock());
		Opd updatedOpd = opdManager.updateOpd(opdToUpdate);
		if (updatedOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not updated."));
		}

		return mapper.map2DTO(updatedOpd);
	}

	/**
	 * Updates the specified {@link OpdWithOperationRowDTO}.
	 * @param code OPD code
	 * @param opdWithOperationRowDTO OPD with Operation row payload
	 * @return the OpdWithOperationRowDTO updated
	 * @throws OHServiceException When failed to update OPD
	 */
	@PutMapping("/opds/rows/{code}")
	public OpdWithOperationRowDTO updateOpdWithOperationRow(
		@PathVariable("code") int code, @RequestBody OpdWithOperationRowDTO opdWithOperationRowDTO
	) throws OHServiceException {
		LOGGER.info("Update opds code: {}", code);
		OpdWithOperationRowDTO opdWithOperatioRow = new OpdWithOperationRowDTO();
		if (opdManager.getOpdById(code).isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."), HttpStatus.NOT_FOUND);
		}

		if (opdWithOperationRowDTO.getOpdDTO().getCode() != 0 && opdWithOperationRowDTO.getOpdDTO().getCode() != code) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."), HttpStatus.NOT_FOUND);
		}

		Patient patient = patientManager.getPatientById(opdWithOperationRowDTO.getOpdDTO().getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
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

		return opdWithOperatioRow;
	}

	/**
	 * Get all {@link Opd}s for today or for the last week.
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get OPD
	 */
	@GetMapping("/opds/weekly")
	public List<OpdDTO> getOpdToDayOrWeek(
		@RequestParam(name="oneWeek", required=false) Boolean oneWeek
	) throws OHServiceException {
		LOGGER.info("Get all today or since one week opd");
		if (oneWeek == null) {
			oneWeek = false;
		}

		return mapper.map2DTOList(opdManager.getOpd(oneWeek));
	}

	/**
	 * Get all {@link Opd}s within specified date range.
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get OPDs
	 */
	@GetMapping("/opds/search")
	public Page<OpdDTO> getOpdByDates(
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
		@RequestParam(value = "wardCode", required = false) String wardCode
	) throws OHServiceException {
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
			opdDTOs = opdsPaged.getData().stream().map(mapper::map2DTO).collect(Collectors.toList());
			opdPageable.setPageInfo(mapper.setParameterPageInfo(opdsPaged.getPageInfo()));
		} else {
			if (patientCode != 0) {
				opds = opdManager.getOpdList(patientCode);
			} else {
				opds = opdManager.getOpd(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, null);
			}
			opdDTOs = opds.stream().map(mapper::map2DTO).collect(Collectors.toList());
		}

		opdPageable.setData(opdDTOs);

		return opdPageable;
	}

	/**
	 * Get all {@link OpdWithOperationRowDTO}s associated to specified patient CODE.
	 * @return a {@link List} of {@link OpdWithOperationRowDTO} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get OPDs
	 */
	@GetMapping("/opds/patient/{pcode}")
	public List<OpdWithOperationRowDTO> getOpdByPatient(@PathVariable("pcode") int pcode) throws OHServiceException {
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

		}
		return opdWithOperations;
	}

	/**
	 * Delete {@link Opd} for specified code.
	 * @param code OPD code
	 * @return {@code true} if the {@link Opd} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete OPD
	 */
	@DeleteMapping("/opds/{code}")
	public boolean deleteOpd(@PathVariable("code") int code) throws OHServiceException {
		LOGGER.info("Delete Opd code: {}", code);

		Opd toDelete = new Opd();
		toDelete.setCode(code);

		try {
			opdManager.deleteOpd(toDelete);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Opd not deleted."));
		}
	}

	/**
	 * Get the maximum progressive number within specified year or within current year if {@code 0}.
	 * @return the max progressive number
	 * @throws OHServiceException When failed to get year progressive number
	 */
	@GetMapping("/opds/ProgYear/{year}")
	public Integer getProgressiveYear(@PathVariable("year") int year) throws OHServiceException {
		LOGGER.info("Get progressive number within specified year");

		return opdManager.getProgYear(year);
	}

	/**
	 * Get the last {@link Opd} in time associated with specified patient ID.
	 * @return last Opd associated with specified patient ID or {@code null}
	 * @throws OHServiceException When failed to get last OPD
	 */
	@GetMapping("/opds/last/{patientCode}")
	public OpdDTO getLastOpd(@PathVariable("patientCode") int patientCode) throws OHServiceException {
		LOGGER.info("Get the last opp for patient code: {}", patientCode);
		Opd lastOpd = opdManager.getLastOpd(patientCode);

		if (lastOpd == null) {
			throw new OHAPIException(new OHExceptionMessage("Opd not found."), HttpStatus.NOT_FOUND);
		}

		return mapper.map2DTO(lastOpd);
	}

	/**
	 * Check if the given {@code opdNum} does already exist for the given {@code year}.
	 * @return {@code true} if the given number exists in year, {@code false} otherwise
	 * @throws OHServiceException  When failed to check the OPD number
	 */
	@GetMapping("/opds/check/progyear")
	public boolean isExistOpdNum(@RequestParam("opdNum") int opdNum, @RequestParam("year") int year) throws OHServiceException {
		LOGGER.info("check if progYear: {}  already exist for year : {}", opdNum, year);
		return opdManager.isExistOpdNum(opdNum, year);
	}
}
