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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/vaccines")
@Tag(name = "Vaccines")
@SecurityRequirement(name = "bearerAuth")
public class VaccineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineController.class);

    @Autowired
    protected VaccineBrowserManager vaccineManager;
    
    @Autowired
    protected VaccineMapper mapper;

    public VaccineController(VaccineBrowserManager vaccineManager, VaccineMapper vaccineMapper) {
        this.vaccineManager = vaccineManager;
        this.mapper = vaccineMapper;
    }

    /**
     * Get all the vaccines.
     *
     * @return NO_CONTENT if there aren't any vaccines, {@code List<VaccineDTO>} otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccines() throws OHServiceException {
        LOGGER.info("Get vaccines");
        List<Vaccine> vaccines = vaccineManager.getVaccine();
        List<VaccineDTO> listVaccines = mapper.map2DTOList(vaccines);
        if (listVaccines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    /**
     * Get all the vaccines related to a vaccineType code.
     *
     * @param vaccineTypeCode of the vaccine
     * @return NO_CONTENT if there aren't vaccines related to the code, {@code List<VaccineDTO>} otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines/type-code/{vaccineTypeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccinesByVaccineTypeCode(@PathVariable String vaccineTypeCode) throws OHServiceException {
        LOGGER.info("Get vaccine by code: {}", vaccineTypeCode);
        List<Vaccine> vaccines = vaccineManager.getVaccine(vaccineTypeCode);
        List<VaccineDTO> listVaccines = mapper.map2DTOList(vaccines);
        if (listVaccines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    /**
     * Create a new vaccine.
     *
     * @param newVaccine
     * @return an error message if there is a problem, ok otherwise.
     * @throws OHServiceException
     */
    @PostMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VaccineDTO> newVaccine(@RequestBody VaccineDTO newVaccine) throws OHServiceException {
        LOGGER.info("Create vaccine: {}", newVaccine);
        Vaccine isCreatedVaccine;
        try {
             isCreatedVaccine = vaccineManager.newVaccine(mapper.map2Model(newVaccine));
        } catch (OHDataIntegrityViolationException e) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine type already present."));
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not created."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedVaccine));
    }

    /**
     * Update a vaccine.
     *
     * @param updateVaccine
     * @return an error message if there are some problems, ok otherwise.
     * @throws OHServiceException
     */
    @PutMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VaccineDTO> updateVaccine(@RequestBody VaccineDTO updateVaccine) throws OHServiceException {
        LOGGER.info("Update vaccine: {}", updateVaccine);
        Vaccine updatedVaccine;
        try {
            updatedVaccine = vaccineManager.updateVaccine(mapper.map2Model(updateVaccine));
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("Vaccine not updated."));
        }
        return ResponseEntity.ok(mapper.map2DTO(updatedVaccine));
    }

    /**
     * Delete a vaccine.
     *
     * @param code of the vaccine to delete
     * @return an error message if there are some problems, ok otherwise.
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/vaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteVaccine(@PathVariable("code") String code) throws OHServiceException {
        LOGGER.info("Delete vaccine code: {}", code);
        Vaccine vaccine = vaccineManager.findVaccine(code);
        if (vaccine != null) {
           try {
               vaccineManager.deleteVaccine(vaccine);
           } catch (OHServiceException serviceException) {
                throw new OHAPIException(new OHExceptionMessage("Vaccine not deleted."));
           }
           return ResponseEntity.ok(true);
        }
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    
    /**
     * Check if the code is already used by other vaccine.
     *
     * @param code
     * @return {@code true} if it is already used, {@code false} otherwise.
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkVaccineCode(@PathVariable String code) throws OHServiceException {
	    LOGGER.info("Check vaccine code: {}", code);
        boolean check = vaccineManager.isCodePresent(code);
        return ResponseEntity.ok(check);
    }
}
