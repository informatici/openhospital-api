/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.visits.rest;

import java.util.ArrayList;
import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.mapper.VisitMapper;
import org.isf.visits.model.Visit;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/visit", produces = MediaType.APPLICATION_JSON_VALUE)
public class VisitsController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(VisitsController.class);

    @Autowired
    protected VisitManager visitManager;
    
    @Autowired
    protected VisitMapper mapper;

    public VisitsController(VisitManager visitManager, VisitMapper visitMapper) {
        this.visitManager = visitManager;
        this.mapper = visitMapper;
    }

    /**
     * Get all the visitors related to a patient.
     *
     * @param patID the id of the patient
     * @return NO_CONTENT if there aren't visitors, {@code List<VaccineDTO>} otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/visit/{patID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VisitDTO>> getVisit(@PathVariable int patID) throws OHServiceException {
        LOGGER.info("Get visit related to patId: {}", patID);
        List<Visit> visit = visitManager.getVisits(patID);
        List<VisitDTO> listVisit = mapper.map2DTOList(visit);
        if (listVisit.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listVisit);
        }
    }

    /**
     * Create a new visitor.
     *
     * @param newVisit
     * @return an error if there are some problem, the visitor id (Integer) otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/visit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> newVisit(@RequestBody VisitDTO newVisit) throws OHServiceException {
	    LOGGER.info("Create Visit: {}", newVisit);
        Visit visit = visitManager.newVisit(mapper.map2Model(newVisit));
        return ResponseEntity.status(HttpStatus.CREATED).body(visit.getVisitID()); //TODO: verify if it's correct
    }

    /**
     * Create new visitors.
     *
     * @param newVisits a list with all the visitors
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/visits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newVisits(@RequestBody List<VisitDTO> newVisits) throws OHServiceException {
        LOGGER.info("Create Visits");
        ArrayList<Visit> listVisits = (ArrayList<Visit>) mapper.map2ModelList(newVisits);
        boolean areCreated = visitManager.newVisits(listVisits);
        if (!areCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Visits are not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(areCreated);
    }

    /**
     * Delete all the visits related to a patient.
     *
     * @param patID the id of the patient
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/visit/{patID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteVisitsRelatedToPatient(@PathVariable int patID) throws OHServiceException {
	    LOGGER.info("Delete Visit related to patId: {}", patID);
        boolean areDeleted = visitManager.deleteAllVisits(patID);
        if (!areDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Visits are not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }

}
