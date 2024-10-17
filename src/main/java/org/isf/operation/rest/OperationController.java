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
package org.isf.operation.rest;

import java.util.List;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.mapper.OpdMapper;
import org.isf.operation.dto.OperationDTO;
import org.isf.operation.dto.OperationRowDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.mapper.OperationMapper;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.opetype.model.OperationType;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Operations")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationController.class);

	private final OperationBrowserManager operationManager;

	private final AdmissionBrowserManager admissionManager;

	private final OperationRowBrowserManager operationRowManager;

	private final PatientBrowserManager patientBrowserManager;

	private final OperationMapper mapper;

	private final OpdMapper opdMapper;

	private final OperationRowMapper opRowMapper;

	public OperationController(
		OperationBrowserManager operationManager,
		AdmissionBrowserManager admissionManager,
		OperationRowBrowserManager operationRowManager,
		PatientBrowserManager patientBrowserManager,
		OperationMapper mapper, OpdMapper opdMapper,
		OperationRowMapper opRowMapper
	) {
		this.operationManager = operationManager;
		this.admissionManager = admissionManager;
		this.operationRowManager = operationRowManager;
		this.patientBrowserManager = patientBrowserManager;
		this.mapper = mapper;
		this.opdMapper = opdMapper;
		this.opRowMapper = opRowMapper;
	}
	/**
	 * Create a new {@link Operation}.
	 *
	 * @param operationDTO Operation payload
	 * @return {@code true} if the operation has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create operation
	 */
	@PostMapping("/operations")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationDTO newOperation(@RequestBody OperationDTO operationDTO) throws OHServiceException {
		String code = operationDTO.getCode();
		LOGGER.info("Create operation {}.", code);
		if (operationManager.descriptionControl(operationDTO.getDescription(), operationDTO.getType().getCode())) {
			throw new OHAPIException(new OHExceptionMessage(
				"Another operation already created with provided description and types."
			));
		}

		Operation isCreatedOperation = operationManager.newOperation(mapper.map2Model(operationDTO));
		if (isCreatedOperation == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation not created."));
		}

		return mapper.map2DTO(isCreatedOperation);
	}

	/**
	 * Updates the specified {@link Operation}.
	 *
	 * @param operationDTO Operation payload
	 * @return {@code true} if the operation has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update operation
	 */
	@PutMapping("/operations/{code}")
	public OperationDTO updateOperation(
		@PathVariable String code, @RequestBody OperationDTO operationDTO
	) throws OHServiceException {
		LOGGER.info("Update operations code: {}.", operationDTO.getCode());
		Operation operation = mapper.map2Model(operationDTO);
		if (!operationManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Operation not found."));
		}
		operation.setLock(operationDTO.getLock());
		Operation isUpdatedOperation = operationManager.updateOperation(operation);
		if (isUpdatedOperation == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation not updated."));
		}

		return mapper.map2DTO(isUpdatedOperation);
	}

	/**
	 * Get all the available {@link Operation}s.
	 *
	 * @return a {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get operations
	 */
	@GetMapping("/operations")
	public List<OperationDTO> getOperations() throws OHServiceException {
		LOGGER.info("Get all operations.");

		return mapper.map2DTOList(operationManager.getOperation());
	}

	/**
	 * Get the {@link Operation} with the specified code.
	 *
	 * @return found operation
	 * @throws OHServiceException When failed get the operation
	 */
	@GetMapping("/operations/{code}")
	public OperationDTO getOperationByCode(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Get operation for provided code: {}.", code);
		Operation operation = operationManager.getOperationByCode(code);

		if (operation == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation not found."), HttpStatus.NOT_FOUND);
		}

		return mapper.map2DTO(operation);
	}

	/**
	 * Get all {@link Operation}s whose {@link OperationType}'s description matches specified string.
	 *
	 * @return {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed get operations
	 */
	@GetMapping("/operations/search/type")
	public List<OperationDTO> getOperationByTypeDescription(
		@RequestParam String typeDescription
	) throws OHServiceException {
		LOGGER.info("Get operations for provided type description: {}.", typeDescription);

		return mapper.map2DTOList(operationManager.getOperationByTypeDescription(typeDescription));
	}

	/**
	 * Delete {@link Operation} for specified code.
	 *
	 * @param code Operation code
	 * @return {@code true} if the {@link Operation} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete operation
	 */
	@DeleteMapping("/operations/{code}")
	public boolean deleteOperation(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete operation code: {}.", code);
		Operation operation = operationManager.getOperationByCode(code);

		if (operation == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation not deleted."), HttpStatus.NOT_FOUND);
		}

		try {
			operationManager.deleteOperation(operation);
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.error("Delete Operation: {} failed.", code);
			throw new OHAPIException(new OHExceptionMessage("Operation not deleted."));
		}
	}

	/**
	 * Create a new {@link OperationRow}.
	 *
	 * @param operationRowDTO Operation Row payload
	 * @return {@code true} if the operation has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to save operation row
	 */
	@PostMapping("/operations/rows")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationRowDTO newOperationRow(@RequestBody OperationRowDTO operationRowDTO) throws OHServiceException {
		int code = operationRowDTO.getAdmission().getId();
		LOGGER.info("Create operation: {}.", code);

		if (operationRowDTO.getAdmission() == null && operationRowDTO.getOpd() == null) {
			throw new OHAPIException(new OHExceptionMessage("At least one field between admission and Opd is required."));
		}
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);

		OperationRow createOpeRow = operationRowManager.newOperationRow(opRow);
		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission())
			.stream()
			.filter(op -> op.getAdmission().getId() == code)
			.toList();

		OperationRow opCreated = null;
		if (!opRowFounds.isEmpty()) {
			opCreated = opRowFounds.get(0);
		}

		if (createOpeRow == null || opCreated == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation row not created."));
		}

		return opRowMapper.map2DTO(opCreated);
	}

	/**
	 * Updates the specified {@link OperationRow}.
	 *
	 * @param operationRowDTO Operation Row payload
	 * @return {@code true} if the operation row has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update operation row
	 */
	@PutMapping("/operations/rows")
	public Integer updateOperationRow(@RequestBody OperationRowDTO operationRowDTO) throws OHServiceException {
		LOGGER.info("Update operations row code: {}.", operationRowDTO.getId());
		if (operationRowDTO.getAdmission() == null && operationRowDTO.getOpd() == null) {
			throw new OHAPIException(new OHExceptionMessage("At least one field between admission and Opd is required."));
		}
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);

		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission())
			.stream()
			.filter(op -> op.getId() == opRow.getId())
			.toList();

		if (opRowFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Operation row not found."), HttpStatus.NOT_FOUND);
		}
		OperationRow updateOpeRow = operationRowManager.updateOperationRow(opRow);
		if (updateOpeRow == null) {
			throw new OHAPIException(new OHExceptionMessage("Operation not updated."));
		}

		return opRow.getId();
	}

	/**
	 * Get {@link OperationRow}s for specified admission.
	 *
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get operations
	 */
	@GetMapping("/operations/rows/search/admission")
	public List<OperationRowDTO> getOperationRowsByAdmt(@RequestParam("admissionId") int id) throws OHServiceException {
		LOGGER.info("Get operations row for provided admission.");
		Admission adm = admissionManager.getAdmission(id);

		return opRowMapper.map2DTOList(operationRowManager.getOperationRowByAdmission(adm));
	}

	/**
	 * Get {@link OperationRow}s for specified patient.
	 *
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get operations
	 */
	@GetMapping("/operations/rows/search/patient")
	public List<OperationRowDTO> getOperationRowsByPatient(@RequestParam int patientCode) throws OHServiceException {
		LOGGER.info("Get operations row for provided patient.");
		Patient patient = patientBrowserManager.getPatientById(patientCode);

		return opRowMapper.map2DTOList(operationRowManager.getOperationRowByPatientCode(patient));
	}

	/**
	 * Get {@link OperationRow}s for specified {@link OpdDTO}.
	 *
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get operation rows
	 */
	@PostMapping("/operations/rows/search/opd")
	public List<OperationRowDTO> getOperationRowsByOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		LOGGER.info("Get operations row for provided opd.");

		return opRowMapper.map2DTOList(operationRowManager.getOperationRowByOpd(opdMapper.map2Model(opdDTO)));
	}

	/**
	 * Delete the {@link OperationRow} with the specified code.
	 *
	 * @param code Operation Row code
	 * @return {@code true} if the {@link OperationRow} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete operation row
	 */
	@DeleteMapping("/operations/rows/{code}")
	public boolean deleteOperationRow(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Delete operation row code: {}.", code);
		OperationRow opRow = new OperationRow();
		opRow.setId(code);

		try {
			operationRowManager.deleteOperationRow(opRow);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Operation row not deleted."));
		}
	}
}
