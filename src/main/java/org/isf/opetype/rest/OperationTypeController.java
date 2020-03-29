package org.isf.opetype.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.manager.OperationTypeBrowserManager;
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
@Api(value = "/opetypes", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OperationTypeController extends OHApiAbstractController<OperationType, OperationTypeDTO> {

    @Autowired
    private OperationTypeBrowserManager operationTypeBrowserManager;

    private final Logger logger = LoggerFactory.getLogger(OperationTypeController.class);

    /**
     * return the list of {@link OperationType}s
     *
     * @return the list of {@link OperationType}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    @GetMapping(value = "/opetypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OperationTypeDTO>> getOperationType() throws OHServiceException {
        logger.info(String.format("getOperationType"));
        List<OperationType> operationTypeList = operationTypeBrowserManager.getOperationType();
        return ResponseEntity.status(HttpStatus.FOUND).body(toDTOList(operationTypeList));
    }

    /**
     * insert an {@link OperationType} in the DB
     *
     * @param operationType - the {@link OperationType} to insert
     * @return <code>true</code> if the {@link OperationType} has been inserted, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @PostMapping( value = "/opetypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        logger.info(String.format("newOperationType [%s]", operationType.getCode()));
        return operationTypeBrowserManager.newOperationType(toModel(operationType))
                ? ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * update an {@link OperationType}
     *
     * @param operationType - the {@link OperationType} to update
     * @return <code>true</code> if the {@link OperationType} has been updated, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @PatchMapping( value="/opetypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        logger.info(String.format("updateOperationType [%s]", operationType.getCode()));
        return operationTypeBrowserManager.updateOperationType(toModel(operationType))
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * delete an {@link OperationType}
     *
     * @param operationType - the {@link OperationType} to delete
     * @return <code>true</code> if the {@link OperationType} has been delete, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @DeleteMapping(value="/opetypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        logger.info(String.format("deleteOperationType [%s]", operationType.getCode()));
        return operationTypeBrowserManager.deleteOperationType(toModel(operationType))
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * checks if an {@link OperationType} code has already been used
     *
     * @param code - the code
     * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value="/opetypes/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> codeControl(@PathVariable String code) throws OHServiceException {
        logger.info(String.format("OperationType code [%s]", code));
        return operationTypeBrowserManager.codeControl(code)
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    @Override
    protected Class<OperationTypeDTO> getDTOClass() {
        return OperationTypeDTO.class;
    }

    @Override
    protected Class<OperationType> getModelClass() {
        return OperationType.class;
    }
}
