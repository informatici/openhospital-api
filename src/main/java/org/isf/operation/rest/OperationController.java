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
package org.isf.operation.rest;

import java.util.List;
import java.util.stream.Collectors;

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
@Api(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OperationController.class);

	@Autowired
	protected OperationBrowserManager operationManager;
	
	@Autowired
	protected AdmissionBrowserManager admissionManager;
	
	@Autowired
	protected OperationRowBrowserManager operationRowManager;
	
	@Autowired
	protected PatientBrowserManager patientBrowserManager;
	
	@Autowired
	protected OperationMapper mapper;
	
	@Autowired
	protected OpdMapper opdMapper;
	
	@Autowired
	protected OperationRowMapper opRowMapper;

	public OperationController(OperationBrowserManager operationManager, OperationMapper operationmapper) {
		this.operationManager = operationManager;
		this.mapper = operationmapper;
	}

	/**
	 * Create a new {@link Operation}.
	 * @param operationDTO
	 * @return {@code true} if the operation has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OperationDTO> newOperation(@RequestBody OperationDTO operationDTO) throws OHServiceException {
		String code = operationDTO.getCode();
		LOGGER.info("Create operation {}", code);
		if (operationManager.descriptionControl(operationDTO.getDescription(), operationDTO.getType().getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "another operation has already been created with provided description and types!", OHSeverityLevel.ERROR));
		}
		Operation isCreatedOperation = operationManager.newOperation(mapper.map2Model(operationDTO));
		if (isCreatedOperation == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedOperation));
	}

	/**
	 * Updates the specified {@link Operation}.
	 * @param operationDTO
	 * @return {@code true} if the operation has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/operations/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OperationDTO> updateOperation(@PathVariable String code, @RequestBody OperationDTO operationDTO)
			throws OHServiceException {
		LOGGER.info("Update operations code: {}", operationDTO.getCode());
		Operation operation = mapper.map2Model(operationDTO);
		if (!operationManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation not found!", OHSeverityLevel.ERROR));
		}
		operation.setLock(operationDTO.getLock());
		Operation isUpdatedOperation = operationManager.updateOperation(operation);
		if (isUpdatedOperation == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(mapper.map2DTO(isUpdatedOperation));
	}

	/**
	 * Get all the available {@link Operation}s.
	 * @return a {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationDTO>> getOperations() throws OHServiceException {
		LOGGER.info("Get all operations ");
		List<Operation> operations = operationManager.getOperation();
		List<OperationDTO> operationDTOs = mapper.map2DTOList(operations);
		if (operationDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationDTOs);
		} else {
			return ResponseEntity.ok(operationDTOs);
		}
	}
	
	/**
	 * Get the {@link Operation} with the specified code.
	 * @return found operation
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperationDTO> getOperationByCode(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Get operation for provided code");
		Operation operation = operationManager.getOperationByCode(code);
		if (operation != null) {
			return ResponseEntity.ok(mapper.map2DTO(operation));
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
	}
	
	/**
	 * Get all {@link Operation}s whose {@link OperationType}'s description matches specified string.
	 * @return {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/search/type", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationDTO>> getOperationByTypeDescription(@RequestParam String typeDescription) throws OHServiceException {
		LOGGER.info("Get operations for provided type description");
		List<Operation> operations = operationManager.getOperationByTypeDescription(typeDescription);
		List<OperationDTO> operationDTOs = mapper.map2DTOList(operations);
		if (operationDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationDTOs);
		} else {
			return ResponseEntity.ok(operationDTOs);
		}
	}

	/**
	 * Delete {@link Operation} for specified code.
	 * @param code
	 * @return {@code true} if the {@link Operation} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/operations/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOperation(@PathVariable("code") String code) throws OHServiceException {
		LOGGER.info("Delete operation code: {}", code);
		boolean isDeleted;
		Operation operation = operationManager.getOperationByCode(code);
		if (operation != null) {
			isDeleted = operationManager.deleteOperation(operation);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * Create a new {@link OperationRow}.
	 * @param operationRowDTO
	 * @return {@code true} if the operation has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations/rows", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OperationRowDTO> newOperationRow(@RequestBody OperationRowDTO operationRowDTO) throws OHServiceException {
		int code = operationRowDTO.getAdmission().getId();
		LOGGER.info("Create operation {}", code);
		if (operationRowDTO.getAdmission() == null && operationRowDTO.getOpd() == null) {
			   throw new OHAPIException(new OHExceptionMessage(null, "At least one field between admission and Opd is required!", OHSeverityLevel.ERROR));
		}
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);
		
		boolean isCreated = operationRowManager.newOperationRow(opRow);
		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission()).stream().filter(op -> op.getAdmission().getId() == code)
				.collect(Collectors.toList());
		OperationRow opCreated = null;
		if (!opRowFounds.isEmpty()) {
			opCreated = opRowFounds.get(0);
		}
		if (!isCreated || opCreated == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation row is not created!", OHSeverityLevel.ERROR));
		}
		OperationRowDTO opR =  opRowMapper.map2DTO(opCreated);
		return ResponseEntity.status(HttpStatus.CREATED).body(opR);
	}
	
	/**
	 * Updates the specified {@link OperationRow}.
	 * 
	 * @param operationRowDTO
	 * @return {@code true} if the operation row has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/operations/rows", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> updateOperationRow(@RequestBody OperationRowDTO operationRowDTO) throws OHServiceException {
		LOGGER.info("Update operations row code: {}", operationRowDTO.getId());
		if (operationRowDTO.getAdmission() == null && operationRowDTO.getOpd() == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "At least one field between admission and Opd is required!", OHSeverityLevel.ERROR));
		}
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);

		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission()).stream().filter(op -> op.getId() == opRow.getId())
						.collect(Collectors.toList());
		if (opRowFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation row not found!", OHSeverityLevel.ERROR));
		}
		boolean isUpdated = operationRowManager.updateOperationRow(opRow);
		if (!isUpdated) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(opRow.getId());
	}
	
	/**
	 * Get {@link OperationRow}s for specified admission.
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/rows/search/admission", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationRowDTO>> getOperationRowsByAdmt(@RequestParam("admissionId") int id) throws OHServiceException {
		LOGGER.info("Get operations row for provided admission");
		Admission adm = admissionManager.getAdmission(id);
		List<OperationRow> operationRows = operationRowManager.getOperationRowByAdmission(adm);
		List<OperationRowDTO> operationRowDTOs = operationRows.stream().map(operation -> {
			OperationRowDTO opR = opRowMapper.map2DTO(operation);
			return opR;
		}).collect(Collectors.toList());
		if (operationRowDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationRowDTOs);
		} else {
			return ResponseEntity.ok(operationRowDTOs);
		}
	}

	/**
	 * Get {@link OperationRow}s for specified patient.
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/rows/search/patient", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationRowDTO>> getOperationRowsByPatient(@RequestParam int patientCode) throws OHServiceException {
		LOGGER.info("Get operations row for provided patient");
		Patient patient = patientBrowserManager.getPatientById(patientCode);
		List<OperationRow> operationRows = operationRowManager.getOperationRowByPatientCode(patient);
		List<OperationRowDTO> operationRowDTOs = operationRows.stream().map(operation -> {
			return opRowMapper.map2DTO(operation);
		}).collect(Collectors.toList());
		if (operationRowDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationRowDTOs);
		} else {
			return ResponseEntity.ok(operationRowDTOs);
		}
	}
	
	/**
	 * Get {@link OperationRow}s for specified {@link OpdDTO}.
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations/rows/search/opd", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationRowDTO>> getOperationRowsByOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		LOGGER.info("Get operations row for provided opd");
		List<OperationRow> operationRows = operationRowManager.getOperationRowByOpd(opdMapper.map2Model(opdDTO));
		List<OperationRowDTO> operationRowDTOs = operationRows.stream().map(operation -> {
			return opRowMapper.map2DTO(operation);
		}).collect(Collectors.toList());
		
		if (operationRowDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationRowDTOs);
		} else {
			return ResponseEntity.ok(operationRowDTOs);
		}
	}
	
	/**
	 * Delete the {@link OperationRow} with the specified code.
	 * @param code
	 * @return {@code true} if the {@link OperationRow} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/operations/rows/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOperationRow(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Delete operation row code: {}", code);
		OperationRow opRow = new OperationRow();
		opRow.setId(code);
		boolean isDeleted = operationRowManager.deleteOperationRow(opRow);
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation row is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isDeleted);
	}
}
