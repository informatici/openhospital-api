package org.isf.ward.rest;

import java.util.ArrayList;
import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.dto.WardDTO;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.mapper.WardMapper;
import org.isf.ward.model.Ward;
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
@Api(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class WardController {

    private final Logger logger = LoggerFactory.getLogger(WardController.class);

    @Autowired
    protected WardBrowserManager wardManager;
    
    @Autowired
    protected WardMapper mapper;

    public WardController(WardBrowserManager wardManager) {
        this.wardManager = wardManager;
    }

    /**
     * Get all the wards
     *
     * @return NO_CONTENT if there aren't ward, List<WardDTO> otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WardDTO>> getWards() throws OHServiceException {
        logger.info("Get wards");
        ArrayList<Ward> wards = wardManager.getWards();
        List<WardDTO> listWard = mapper.map2DTOList(wards);
        if (listWard.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listWard);
        }
    }

    /**
     * Get all the wards with maternity flag <code>false</code>
     *
     * @return NO_CONTENT if there aren't ward, List<WardDTO> otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wardsNoMaternity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WardDTO>> getWardsNoMaternity() throws OHServiceException {
        logger.info("Get wards no maternity");
        ArrayList<Ward> wards = wardManager.getWardsNoMaternity();
        List<WardDTO> listWard = mapper.map2DTOList(wards);
        if (listWard.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listWard);
        }
    }

    /**
     * Get current number of patients inside ward
     *
     * @param wardDto
     * @return BAD_REQUEST if there are some problem, integer otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/occupation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getCurrentOccupation(@RequestBody WardDTO wardDto) throws OHServiceException {
        logger.info("Get current occupation ward code: " + wardDto.getCode());
        Ward ward = mapper.map2Model(wardDto);
        Integer numberOfPatients = wardManager.getCurrentOccupation(ward);
        if (numberOfPatients == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            return ResponseEntity.ok(numberOfPatients);
        }
    }

    /**
     * Create new Ward
     *
     * @param newWard
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newWard(@RequestBody WardDTO newWard) throws OHServiceException {
        logger.info("Create Ward: " + newWard);
        boolean isCreated = wardManager.newWard(mapper.map2Model(newWard));
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Ward is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * Update ward
     *
     * @param updateWard
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PutMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateWard(@RequestBody WardDTO updateWard) throws OHServiceException {
        logger.info("Update ward with code: " + updateWard.getCode());
        boolean isUpdated = wardManager.updateWard(mapper.map2Model(updateWard));
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Ward is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);

    }

    /**
     * Delete ward
     *
     * @param wardToDelete
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteWard(@RequestBody WardDTO wardToDelete) throws OHServiceException {
        logger.info("Delete Ward with code: " + wardToDelete.getCode());
        boolean isDeleted = wardManager.deleteWard(mapper.map2Model(wardToDelete));
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Wards is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * Check if code is already use by other ward
     *
     * @param code
     * @return true if it is already use, false otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkWardCode(@PathVariable String code) throws OHServiceException {
        logger.info("Check ward code: " + code);
        boolean check = wardManager.codeControl(code);
        return ResponseEntity.ok(check);
    }

    /**
     * Check if the Maternity Ward with code "M" exists or not.
     *
     * @param createIfNotExits if {@code true} it will create the missing {@link Ward} (with default values)
     *                         and will return {@link true}
     * @return <code>true</code> if the Maternity {@link Ward} exists, <code>false</code> otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/check/{createIfNotExist}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkWardMaternityCode(@PathVariable Boolean createIfNotExits) throws OHServiceException {
        logger.info("Check ward maternity code");
        boolean check = wardManager.maternityControl(createIfNotExits);
        return ResponseEntity.ok(check);
    }

}
