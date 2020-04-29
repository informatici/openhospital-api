package org.isf.vactype.rest;

import java.util.ArrayList;
import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.vactype.dto.VaccineTypeDTO;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.mapper.VaccineTypeMapper;
import org.isf.vactype.model.VaccineType;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/vaccinetype", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class VaccineTypeController {

    private final Logger logger = LoggerFactory.getLogger(VaccineTypeController.class);

    @Autowired
    protected VaccineTypeBrowserManager vaccineTypeManager;
    
    @Autowired
    protected VaccineTypeMapper mapper;

    public VaccineTypeController(VaccineTypeBrowserManager vaccineTypeManager) {
        this.vaccineTypeManager = vaccineTypeManager;
    }

    /**
     * Get all the vaccines types
     *
     * @return
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccinetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineTypeDTO>> getVaccineType() throws OHServiceException {
        logger.info("Get vaccines type");
        ArrayList<VaccineType> vaccinesTypes = vaccineTypeManager.getVaccineType();
        List<VaccineTypeDTO> listVaccines = mapper.map2DTOList(vaccinesTypes);
        if (listVaccines.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    /**
     * Create new vaccine type
     *
     * @param newVaccineType
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/vaccinetype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newVaccineType(@RequestBody VaccineTypeDTO newVaccineType) throws OHServiceException {
        logger.info("Create vaccine type: " + newVaccineType.toString());
        boolean isCreated;
        try {
            isCreated = vaccineTypeManager.newVaccineType(mapper.map2Model(newVaccineType));
        }catch(OHDataIntegrityViolationException e){
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine type already present!", OHSeverityLevel.ERROR));
        }
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine type is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * Update vaccineType
     *
     * @param updateVaccineType
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PutMapping(value = "/vaccinetype/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateVaccineType(@RequestBody VaccineTypeDTO updateVaccineType) throws OHServiceException {
        logger.info("Update vaccine type: " + updateVaccineType.toString());
        boolean isUpdated = vaccineTypeManager.updateVaccineType(mapper.map2Model(updateVaccineType));
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine type is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);

    }

    /**
     * Delete vaccine type
     *
     * @param vaccineTypeToDelete
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/vaccinetype/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteVaccineType(@RequestBody VaccineTypeDTO vaccineTypeToDelete) throws OHServiceException {
        logger.info("Delete vaccine type: " + vaccineTypeToDelete.toString());
        boolean isDeleted = vaccineTypeManager.deleteVaccineType(mapper.map2Model(vaccineTypeToDelete));
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine type is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * Check if code is already use by other vaccine type
     *
     * @param code
     * @return true if it is already use, false otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccinetype/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkVaccineTypeCode(@PathVariable String code) throws OHServiceException {
        logger.info("Check vaccine type code: " + code);
        boolean check = vaccineTypeManager.codeControl(code);
        return ResponseEntity.ok(check);
    }
}
