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
package org.isf.vaccine.rest;

import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.mapper.VaccineMapper;
import org.isf.vaccine.model.Vaccine;
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
@Tag(name = "Vaccines")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VaccineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineController.class);

    private final VaccineBrowserManager vaccineManager;

    private final VaccineMapper mapper;

    public VaccineController(VaccineBrowserManager vaccineManager, VaccineMapper vaccineMapper) {
        this.vaccineManager = vaccineManager;
        this.mapper = vaccineMapper;
    }

    /**
     * Get all the vaccines.
     *
     * @return List of vaccines.
     * @throws OHServiceException When failed to get vaccine
     */
    @GetMapping("/vaccines")
    public List<VaccineDTO> getVaccines() throws OHServiceException {
        LOGGER.info("Get vaccines");

        return mapper.map2DTOList(vaccineManager.getVaccine());
    }

    /**
     * Get all the vaccines related to a vaccineType code.
     *
     * @param vaccineTypeCode of the vaccine
     * @return The list of vaccines related to the supplied vaccine type code
     * @throws OHServiceException When failed to get vaccines
     */
    @GetMapping("/vaccines/type-code/{vaccineTypeCode}")
    public List<VaccineDTO> getVaccinesByVaccineTypeCode(@PathVariable String vaccineTypeCode) throws OHServiceException {
        LOGGER.info("Get vaccine by code: {}", vaccineTypeCode);

        return mapper.map2DTOList(vaccineManager.getVaccine(vaccineTypeCode));
    }

    /**
     * Create a new vaccine.
     *
     * @param newVaccine Vaccine payload
     * @return an error message if there is a problem, ok otherwise.
     * @throws OHServiceException When failed to create the vaccine
     */
    @PostMapping("/vaccines")
    @ResponseStatus(HttpStatus.CREATED)
    public VaccineDTO newVaccine(@RequestBody VaccineDTO newVaccine) throws OHServiceException {
        LOGGER.info("Create vaccine: {}", newVaccine);
        try {
            return mapper.map2DTO(vaccineManager.newVaccine(mapper.map2Model(newVaccine)));
        } catch (OHDataIntegrityViolationException e) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine type already present."));
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not created."));
        }
    }

    /**
     * Update a vaccine.
     *
     * @param updateVaccine Vaccine payload
     * @return an error message if there are some problems, ok otherwise.
     * @throws OHServiceException When failed to update vaccine
     */
    @PutMapping("/vaccines")
    public VaccineDTO updateVaccine(@RequestBody VaccineDTO updateVaccine) throws OHServiceException {
        LOGGER.info("Update vaccine: {}", updateVaccine);

        try {
            return mapper.map2DTO(vaccineManager.updateVaccine(mapper.map2Model(updateVaccine)));
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not updated."));
        }
    }

    /**
     * Delete a vaccine.
     *
     * @param code of the vaccine to delete
     * @return an error message if there are some problems, ok otherwise.
     * @throws OHServiceException When failed to delete vaccine
     */
    @DeleteMapping("/vaccines/{code}")
    public boolean deleteVaccine(@PathVariable("code") String code) throws OHServiceException {
        LOGGER.info("Delete vaccine code: {}", code);
        Vaccine vaccine = vaccineManager.findVaccine(code);
        if (vaccine == null) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not found."), HttpStatus.NOT_FOUND);
        }

        try {
            vaccineManager.deleteVaccine(vaccine);
            return true;
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not deleted."));
        }
    }

    /**
     * Check if the code is already used by other vaccine.
     *
     * @param code Vaccine code
     * @return {@code true} if it is already used, {@code false} otherwise.
     * @throws OHServiceException When failed to check vaccine code
     */
    @GetMapping("/vaccines/check/{code}")
    public boolean checkVaccineCode(@PathVariable String code) throws OHServiceException {
        LOGGER.info("Check vaccine code: {}", code);

        return vaccineManager.isCodePresent(code);
    }
}
