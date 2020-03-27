package org.isf.operation.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.operation.dto.OperationDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.opetype.model.OperationType;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OperationController extends OHApiAbstractController<Operation, OperationDTO> {

    @Autowired
    protected OperationBrowserManager operationBrowserManager;

    private final Logger logger = LoggerFactory.getLogger(OperationController.class);

    /**
     * create a new {@link OperationDTO}
     *
     * @param newOperation
     * @return
     * @throws OHServiceException
     */
    @PostMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> newOperation(@RequestBody OperationDTO newOperation) throws OHServiceException {
        logger.info(String.format("newOperation [%d]", newOperation.getCode()));
        Boolean createOperation = operationBrowserManager.newOperation(toModel(newOperation));
        return ResponseEntity.status(HttpStatus.CREATED).body(createOperation);
    }

    /**
     * return the list of {@link OperationDTO}s
     *
     * @return the list of {@link OperationDTO}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    @GetMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OperationDTO> getOperation() throws OHServiceException {
        logger.info(String.format("getOperation"));
        return toDTOList(operationBrowserManager.getOperation());
    }

    /**
     * return the {@link OperationDTO} with the specified code
     *
     * @param code
     * @return the {@link OperationDTO} by code.
     * @throws OHServiceException
     */
    @GetMapping(value = "/operations/operation/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OperationDTO getOperationByCode(String code) throws OHServiceException {
        logger.info(String.format("getOperationByCode [%s]", code));
        return toDTO(operationBrowserManager.getOperationByCode(code));
    }

    /**
     * return the {@link Operation}s whose type matches specified string
     *
     * @param typecode - a type description
     * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    @GetMapping(value = "/operationsByTypecode/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OperationDTO> getOperation(String typecode) throws OHServiceException {
        logger.info(String.format("getOperation by typecode [%s]", typecode));
        return toDTOList(operationBrowserManager.getOperation(typecode));
    }

    /**
     * updates an {@link Operation} in the DB
     *
     * @param operation - the {@link OperationDTO} to update
     * @return <code>true</code> if the item has been updated. <code>false</code> other
     * @throws OHServiceException
     */
    @PatchMapping(value = "/operations", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateOperation(OperationDTO operation) throws OHServiceException {
        logger.info(String.format("updateOperation code [%s]", operation.getCode()));
        // the user has confirmed he wants to overwrite the record
        return operationBrowserManager.updateOperation(toModel(operation));
    }

    /**
     * Delete a {@link Operation} in the DB
     *
     * @param operation - the {@link OperationDTO} to delete
     * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @DeleteMapping(value="/operations", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteOperation(OperationDTO operation) throws OHServiceException {
        logger.info(String.format("deleteOperation code [%s]", operation.getCode()));
        return operationBrowserManager.deleteOperation(toModel(operation));
    }

    /**
     * checks if an {@link Operation} code has already been use
     *
     * @param code - the code
     * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/codeControl/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean codeControl(String code) throws OHServiceException {
        logger.info(String.format("codeControl [%s]", code));
        return operationBrowserManager.codeControl(code);
    }

    /**
     * checks if an {@link Operation} description has already been used within the specified {@link OperationType}
     *
     * @param description - the {@link Operation} description
     * @param typeCode - the {@link OperationType} code
     * @return <code>true</code> if the description is already in use, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/descriptionControl/{description}/{typeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean descriptionControl(String description, String typeCode) throws OHServiceException {
        logger.info(String.format("descriptionControl description [%s] typeCode [%s]", description, typeCode));
        return operationBrowserManager.descriptionControl(description,typeCode);
    }

    @Override
    protected Class<OperationDTO> getDTOClass() {
        return OperationDTO.class;
    }

    @Override
    protected Class<Operation> getModelClass() {
        return Operation.class;
    }
}
