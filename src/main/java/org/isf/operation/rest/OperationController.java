package org.isf.operation.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.operation.dto.OperationDTO;
import org.isf.operation.dto.OperationRowDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.mapper.OperationMapper;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationController {

	@Autowired
	protected OperationBrowserManager operationManager;
	
	@Autowired
	protected AdmissionBrowserManager admissionManager;
	
	@Autowired
	protected OperationRowBrowserManager operationRowManager;
	
	@Autowired
	protected OpdBrowserManager opdManager;
	
	@Autowired
	protected OperationMapper mapper;
	
	@Autowired
	protected OpdMapper opdMapper;
	
	@Autowired
	protected OperationRowMapper opRowMapper;

	private final Logger logger = LoggerFactory.getLogger(OperationController.class);

	public OperationController(OperationBrowserManager operationManager, OperationMapper operationmapper) {
		this.operationManager = operationManager;
		this.mapper = operationmapper;
	}

	/**
	 * create a new {@link Operation}
	 * @param operationDTO
	 * @return <code>true</code> if the operation has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newOperation(@RequestBody OperationDTO operationDTO) throws OHServiceException {
		String code = operationDTO.getCode();
		logger.info("Create operation " + code);
		if(operationManager.descriptionControl(operationDTO.getDescription(), operationDTO.getType().getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "another operation has already been created with provided description and types!", OHSeverityLevel.ERROR));
		}
		boolean isCreated = operationManager.newOperation(mapper.map2Model(operationDTO));
		Operation operationCreated = operationManager.getOperationByCode(code);
		if (!isCreated || operationCreated == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(operationCreated.getCode());
	}

	/**
	 * Updates the specified {@link Operation}.
	 * @param operationDTO
	 * @return <code>true</code> if the operation has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateOperation(@RequestBody OperationDTO operationDTO)
			throws OHServiceException {
		logger.info("Update operations code:" + operationDTO.getCode());
		Operation operation = mapper.map2Model(operationDTO);
		if (!operationManager.codeControl(operation.getCode()))
			throw new OHAPIException(new OHExceptionMessage(null, "operation not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = operationManager.updateOperation(operation);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(operation.getCode());
	}

	/**
	 * get all the available {@link Operation}s.
	 * @return a {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationDTO>> getOperations() throws OHServiceException {
		logger.info("Get all operations ");
		List<Operation> operations = operationManager.getOperation();
		List<OperationDTO> operationDTOs = mapper.map2DTOList(operations);
		if (operationDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationDTOs);
		} else {
			return ResponseEntity.ok(operationDTOs);
		}
	}
	
	/**
	 * get the {@link Operation} with the specified code
	 * @return found operation
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperationDTO> getOperationByCode(@PathVariable String code) throws OHServiceException {
		logger.info("Get operation for provided code");
		Operation operation = operationManager.getOperationByCode(code);
		if (operation != null) {
			return ResponseEntity.ok(mapper.map2DTO(operation));
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
	}
	
	/**
	 * get {@link Operation}s whose type matches specified string description
	 * @return {@link List} of {@link Operation} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/filter/type", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationDTO>> getOperationsByDescription(@RequestParam String description) throws OHServiceException {
		logger.info("Get operations for provided type description");
		List<Operation> operations = operationManager.getOperation(description);
		List<OperationDTO> operationDTOs = mapper.map2DTOList(operations);
		if (operationDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationDTOs);
		} else {
			return ResponseEntity.ok(operationDTOs);
		}
	}

	/**
	 * Delete {@link Operation} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link Operation} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/operations/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOperation(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete operation code:" + code);
		boolean isDeleted = false;
		Operation operation = operationManager.getOperationByCode(code);
		if (operation != null) {
			isDeleted = operationManager.deleteOperation(operation);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * create a new {@link OperationRow}
	 * @param operationRowDTO
	 * @return <code>true</code> if the operation has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations/rows", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> newOperationRow(@RequestBody OperationRowDTO operationRowDTO) throws OHServiceException {
		int code = operationRowDTO.getId();
		logger.info("Create operation " + code);
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);
		boolean isCreated = operationRowManager.newOperationRow(opRow);
		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission()).stream().filter(op -> op.getId() == code)
				.collect(Collectors.toList());
		OperationRow opCreated = null;
		if (opRowFounds.size() > 0) opCreated = opRowFounds.get(0);
		if (!isCreated || opCreated == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation row is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(opCreated.getId());
	}
	
	/**
	 * Updates the specified {@link OperationRow}.
	 * @param operationRowDTO
	 * @return <code>true</code> if the operation row has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/operations/rows", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> updateOperationRow(@RequestBody OperationRowDTO operationRowDTO)
			throws OHServiceException {
		logger.info("Update operations row code:" + operationRowDTO.getId());
		OperationRow opRow = opRowMapper.map2Model(operationRowDTO);
		List<OperationRow> opRowFounds = operationRowManager.getOperationRowByAdmission(opRow.getAdmission()).stream().filter(op -> op.getId() == opRow.getId())
				.collect(Collectors.toList());
		if (opRowFounds.size() == 0)
			throw new OHAPIException(new OHExceptionMessage(null, "operation row not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = operationRowManager.updateOperationRow(opRow);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "operation is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(opRow.getId());
	}
	
	/**
	 * get {@link OperationRow}s for provided admission
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operations/rows/filterby/admission", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationRowDTO>> getOperationRowsByAdmt(@RequestParam("admissionId") int id) throws OHServiceException {
		logger.info("Get operations row for provided admission");
		Admission adm = admissionManager.getAdmission(id);
		List<OperationRow> operationRows = operationRowManager.getOperationRowByAdmission(adm);
		List<OperationRowDTO> operationRowDTOs = opRowMapper.map2DTOList(operationRows);
		if (operationRowDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationRowDTOs);
		} else {
			return ResponseEntity.ok(operationRowDTOs);
		}
	}

	/**
	 * get {@link OperationRow}s for provided opd
	 * @return {@link List} of {@link OperationRow} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operations/rows/filterby/opd", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationRowDTO>> getOperationRowsByOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		logger.info("Get operations row for provided opd");
		List<OperationRow> operationRows = operationRowManager.getOperationRowByOpd(opdMapper.map2Model(opdDTO));
		List<OperationRowDTO> operationRowDTOs = opRowMapper.map2DTOList(operationRows);
		if (operationRowDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationRowDTOs);
		} else {
			return ResponseEntity.ok(operationRowDTOs);
		}
	}
	
	/**
	 * Delete {@link OperationRow} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link OperationRow} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/operations/rows/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOperationRow(@PathVariable int code) throws OHServiceException {
		logger.info("Delete operation row code:" + code);
		OperationRow opRow = new OperationRow();
		opRow.setId(code);
		boolean isDeleted = operationRowManager.deleteOperationRow(opRow);
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation row is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isDeleted);
	}

	

}
