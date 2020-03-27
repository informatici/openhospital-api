package org.isf.operation.rest;

import org.isf.operation.dto.OperationDTO;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.opetype.model.OperationType;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;

import java.util.ArrayList;
import java.util.List;

public class OperationController extends OHApiAbstractController<Operation, OperationDTO> {

    protected OperationBrowserManager operationBrowserManager;

    /**
     * return the list of {@link Operation}s
     *
     * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    public List<OperationDTO> getOperation() throws OHServiceException {
        return toDTOList(operationBrowserManager.getOperation());
    }

    /**
     * return the {@link Operation} with the specified code
     * @param code
     * @return
     * @throws OHServiceException
     */
    public OperationDTO getOperationByCode(String code) throws OHServiceException {
        return toDTO(operationBrowserManager.getOperationByCode(code));
    }

    /**
     * return the {@link Operation}s whose type matches specified string
     *
     * @param typeDescription - a type description
     * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
     * @throws OHServiceException
     */
    public List<OperationDTO> getOperation(String typecode) throws OHServiceException {
        return toDTOList(operationBrowserManager.getOperation(typecode));
    }

    /**
     * insert an {@link Operation} in the DB
     *
     * @param operation - the {@link Operation} to insert
     * @return <code>true</code> if the operation has been inserted, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    public boolean newOperation(OperationDTO operation) throws OHServiceException {
        return operationBrowserManager.newOperation(toModel(operation));
    }

    /**
     * updates an {@link Operation} in the DB
     *
     * @param operation - the {@link Operation} to update
     * @return <code>true</code> if the item has been updated. <code>false</code> other
     * @throws OHServiceException
     */
    public boolean updateOperation(OperationDTO operation) throws OHServiceException {
        // the user has confirmed he wants to overwrite the record
        return operationBrowserManager.updateOperation(toModel(operation));
    }

    /**
     * Delete a {@link Operation} in the DB
     * @param operation - the {@link Operation} to delete
     * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    public boolean deleteOperation(OperationDTO operation) throws OHServiceException {
        return operationBrowserManager.deleteOperation(toModel(operation));
    }

    /**
     * checks if an {@link Operation} code has already been used
     * @param code - the code
     * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    public boolean codeControl(String code) throws OHServiceException {
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
    public boolean descriptionControl(String description, String typeCode) throws OHServiceException {
        return operationBrowserManager.descriptionControl(description,typeCode);
    }

    @Override
    protected Class<OperationDTO> getDTOClass() {
        return OperationDTO.class;
    }

    @Override
    protected Class getModelClass() {
        return Operation.class;
    }
}
