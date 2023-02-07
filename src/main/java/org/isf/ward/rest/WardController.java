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
package org.isf.ward.rest;

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
@Api(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class WardController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WardController.class);

    @Autowired
    protected WardBrowserManager wardManager;
    
    @Autowired
    protected WardMapper mapper;

    public WardController(WardBrowserManager wardManager, WardMapper wardMapper) {
        this.wardManager = wardManager;
        this.mapper =  wardMapper;
    }

    /**
     * Get all the wards.
     *
     * @return NO_CONTENT if there aren't wards, {@code List<WardDTO>} otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WardDTO>> getWards() throws OHServiceException {
        LOGGER.info("Get wards");
        List<Ward> wards = wardManager.getWards();
        List<WardDTO> listWard = mapper.map2DTOList(wards);
        if (listWard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listWard);
        }
    }

    /**
     * Get all the wards with maternity flag {@code false}.
     *
     * @return NO_CONTENT if there aren't wards, {@code List<WardDTO>} otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wardsNoMaternity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WardDTO>> getWardsNoMaternity() throws OHServiceException {
        LOGGER.info("Get wards no maternity");
        List<Ward> wards = wardManager.getWardsNoMaternity();
        List<WardDTO> listWard = mapper.map2DTOList(wards);
        if (listWard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listWard);
        }
    }

    /**
     * Get current number of patients inside a ward.
     *
     * @param code
     * @return BAD_REQUEST if there are some problem, integer otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/occupation/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getCurrentOccupation(@PathVariable String code) throws OHServiceException {
    	LOGGER.info("Get current occupation ward code: {}", code);
        Ward ward = wardManager.findWard(code);
        int numberOfPatients = wardManager.getCurrentOccupation(ward);
        if (numberOfPatients == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            return ResponseEntity.ok(numberOfPatients);
        }
    }

    /**
     * Create a new ward.
     *
     * @param newWard
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WardDTO> newWard(@RequestBody WardDTO newWard) throws OHServiceException {
	    LOGGER.info("Create Ward: {}", newWard);
        Ward wardCreated = wardManager.newWard(mapper.map2Model(newWard));
        if (wardCreated == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Ward is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(wardCreated));
    }

    /**
     * Update a specified ward.
     *
     * @param updateWard
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PutMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WardDTO> updateWard(@RequestBody WardDTO updateWard) throws OHServiceException {
	    LOGGER.info("Update ward with code: {}", updateWard.getCode());
	    Ward ward = mapper.map2Model(updateWard);
	    ward.setLock(updateWard.getLock());
        Ward wardUpdated = wardManager.updateWard(ward);
        if (wardUpdated == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Ward is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(mapper.map2DTO(wardUpdated));

    }

    /**
     * Delete a ward.
     *
     * @param code the ward to delete
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/wards/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteWard(@PathVariable String code) throws OHServiceException {
        LOGGER.info("Delete Ward with code: {}", code);
        boolean isDeleted;
        Ward ward = wardManager.findWard(code);
        if (ward != null) {
            isDeleted = wardManager.deleteWard(ward);
            if (!isDeleted) {
                throw new OHAPIException(new OHExceptionMessage(null, "Wards is not deleted!", OHSeverityLevel.ERROR));
            }
            return ResponseEntity.ok(isDeleted);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    /**
     * Check if the code is already used by other ward.
     *
     * @param code
     * @return true if it is already use, false otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkWardCode(@PathVariable(value = "code") String code) throws OHServiceException {
	    LOGGER.info("Check ward code: {}", code);
        boolean check = wardManager.isCodePresent(code);
        return ResponseEntity.ok(check);
    }

    /**
     * Check if the Maternity Ward with code "M" exists or not.
     *
     * @param createIfNotExist if {@code true} it will create the missing {@link Ward} (with default values)
     *                         and will return {@link true}
     * @return {@code true} if the Maternity {@link Ward} exists, {@code false} otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/wards/check/maternity/{createIfNotExist}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkWardMaternityCode(@PathVariable Boolean createIfNotExist) throws OHServiceException {
        LOGGER.info("Check ward maternity code");
        boolean check = wardManager.maternityControl(createIfNotExist);
        return ResponseEntity.ok(check);
    }

}
