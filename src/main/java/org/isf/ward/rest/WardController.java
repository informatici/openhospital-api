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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.ward.rest;

import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.ward.dto.WardDTO;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.mapper.WardMapper;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Wards")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class WardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WardController.class);

    private final WardBrowserManager wardManager;

    private final WardMapper mapper;

    public WardController(WardBrowserManager wardManager, WardMapper wardMapper) {
        this.wardManager = wardManager;
        this.mapper =  wardMapper;
    }

    /**
     * Get all the {@link Ward}s.
     *
     * @return NO_CONTENT if there aren't wards, {@code List<WardDTO>} otherwise
     * @throws OHServiceException When failed to get wards
     */
    @GetMapping(value = "/wards")
    public List<WardDTO> getWards() throws OHServiceException {
        LOGGER.info("Get wards");

        return mapper.map2DTOList(wardManager.getWards());
    }

    /**
     * Get all the {@link Ward}s with maternity flag {@code false}.
     *
     * @return NO_CONTENT if there aren't wards, {@code List<WardDTO>} otherwise
     * @throws OHServiceException When failed to get wards
     */
    @GetMapping(value = "/wardsNoMaternity")
    public List<WardDTO> getWardsNoMaternity() throws OHServiceException {
        LOGGER.info("Get wards no maternity");

        return mapper.map2DTOList(wardManager.getWardsNoMaternity());
    }

    /**
     * Get current number of patients inside a {@link Ward}.
     *
     * @param code Ward code
     * @return BAD_REQUEST if there are some problem, integer otherwise
     * @throws OHServiceException When failed to get the number of occupants
     */
    @GetMapping(value = "/wards/occupation/{code}")
    public Integer getCurrentOccupation(@PathVariable String code) throws OHServiceException {
        LOGGER.info("Get current occupation ward code: {}", code);
        Ward ward = wardManager.findWard(code);

        return wardManager.getCurrentOccupation(ward);
    }

    /**
     * Create a new {@link Ward}.
     *
     * @param newWard Ward payload
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException When failed to create ward
     */
    @PostMapping(value = "/wards")
    @ResponseStatus(HttpStatus.CREATED)
    public WardDTO newWard(@RequestBody WardDTO newWard) throws OHServiceException {
        LOGGER.info("Create Ward: {}", newWard);
        Ward wardCreated = wardManager.newWard(mapper.map2Model(newWard));
        if (wardCreated == null) {
            throw new OHAPIException(new OHExceptionMessage("Ward not created."));
        }

        return mapper.map2DTO(wardCreated);
    }

    /**
     * Update a specified {@link Ward}.
     *
     * @param updateWard Ward payload
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException When
     */
    @PutMapping(value = "/wards")
    public WardDTO updateWard(@RequestBody WardDTO updateWard) throws OHServiceException {
        LOGGER.info("Update ward with code: {}", updateWard.getCode());
        Ward ward = mapper.map2Model(updateWard);
        ward.setLock(updateWard.getLock());
        Ward wardUpdated = wardManager.updateWard(ward);
        if (wardUpdated == null) {
            throw new OHAPIException(new OHExceptionMessage("Ward not updated."));
        }

        return mapper.map2DTO(wardUpdated);

    }

    /**
     * Delete a {@link Ward}.
     *
     * @param code the ward to delete
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException When failed to delete ward
     */
    @DeleteMapping(value = "/wards/{code}")
    public boolean deleteWard(@PathVariable String code) throws OHServiceException {
        LOGGER.info("Delete Ward with code: {}", code);
        Ward ward = wardManager.findWard(code);
        if (ward == null) {
            throw new OHAPIException(new OHExceptionMessage("Ward not found."), HttpStatus.NOT_FOUND);
        }

        try {
            wardManager.deleteWard(ward);
            return true;
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Ward not deleted."));
        }
    }

    /**
     * Check if the code is already used by other {@link Ward}.
     *
     * @param code Ward code
     * @return {@code true} if it is already used, false otherwise
     * @throws OHServiceException When failed to check the ward code
     */
    @GetMapping(value = "/wards/check/{code}")
    public boolean checkWardCode(
        @PathVariable(value = "code") String code
    ) throws OHServiceException {
        LOGGER.info("Check ward code: {}", code);
        return wardManager.isCodePresent(code);
    }

    /**
     * Check if the Maternity {@link Ward} with code "M" exists or not.
     *
     * @param createIfNotExist if {@code true} it will create the missing {@link Ward} (with default values)
     *                         and will return {@link true}
     * @return {@code true} if the Maternity {@link Ward} exists, {@code false} otherwise.
     * @throws OHServiceException When failed to check the ward
     */
    @GetMapping(value = "/wards/check/maternity/{createIfNotExist}")
    public boolean checkWardMaternityCode(
        @PathVariable Boolean createIfNotExist
    ) throws OHServiceException {
        LOGGER.info("Check ward maternity code");
        return wardManager.maternityControl(createIfNotExist);
    }
}
