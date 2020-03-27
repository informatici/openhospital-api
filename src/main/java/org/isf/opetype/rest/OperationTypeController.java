package org.isf.opetype.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.model.OperationType;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/opetype", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OperationTypeController extends OHApiAbstractController<OperationType, OperationTypeDTO> {

    @Autowired
    private OperationTypeBrowserManager operationTypeBrowserManager;

    /**
     * return the list of {@link OperationType}s
     *
     * @return the list of {@link OperationType}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    @GetMapping(value = "/opetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OperationTypeDTO>> getOperationType() throws OHServiceException {

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
    @PostMapping( value = "/opetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        Boolean created = operationTypeBrowserManager.newOperationType(toModel(operationType));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * update an {@link OperationType}
     *
     * @param operationType - the {@link OperationType} to update
     * @return <code>true</code> if the {@link OperationType} has been updated, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @PatchMapping( value="/opetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        Boolean updated = operationTypeBrowserManager.updateOperationType(toModel(operationType));
        return ResponseEntity.ok().body(updated);
    }

    /**
     * delete an {@link OperationType}
     *
     * @param operationType - the {@link OperationType} to delete
     * @return <code>true</code> if the {@link OperationType} has been delete, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @DeleteMapping(value="/opetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteOperationType(@RequestBody OperationTypeDTO operationType) throws OHServiceException {
        Boolean deleted = operationTypeBrowserManager.deleteOperationType(toModel(operationType));
        return ResponseEntity.ok().body(deleted);
    }

    /**
     * checks if an {@link OperationType} code has already been used
     *
     * @param code - the code
     * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value="/opetype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> codeControl(@PathVariable String code) throws OHServiceException {
        Boolean alreadyUsed = operationTypeBrowserManager.codeControl(code);
        return ResponseEntity.ok().body(alreadyUsed);
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
