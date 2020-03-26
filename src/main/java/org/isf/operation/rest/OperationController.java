package org.isf.operation.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.operation.dto.OperationDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OperationController extends OHApiAbstractController<Operation, OperationDTO> {

    @Autowired
    protected OperationBrowserManager operationBrowserManager;

    private final Logger logger = LoggerFactory.getLogger(OperationController.class);

    public OperationController(OperationBrowserManager operationBrowserManager) {
        this.operationBrowserManager = operationBrowserManager;
    }

    // public ArrayList<Operation> getOperation() throws OHServiceException {
    // public Operation getOperationByCode(String code) throws OHServiceException {
    // public ArrayList<Operation> getOperation(String typecode) throws OHServiceException {

    @PostMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> newOperation(@RequestBody OperationDTO newOperation) throws OHServiceException {
        logger.info(String.format("Create operation [%d]", newOperation.getCode()));
        Boolean createOperation = operationBrowserManager.newOperation(toModel(newOperation));
        return ResponseEntity.status(HttpStatus.CREATED).body(createOperation);
    }

    // public boolean updateOperation(Operation operation) throws OHServiceException {
    // public boolean deleteOperation(Operation operation) throws OHServiceException {

    @Override
    protected Class<OperationDTO> getDTOClass() {
        return OperationDTO.class;
    }

    @Override
    protected Class<Operation> getModelClass() {
        return Operation.class;
    }
}
