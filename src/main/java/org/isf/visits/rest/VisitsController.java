package org.isf.visits.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.model.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value = "/visit", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class VisitsController {

    private final Logger logger = LoggerFactory.getLogger(VisitsController.class);

    @Autowired
    protected VisitManager visitManager;

    public VisitsController(VisitManager visitManager) {
        this.visitManager = visitManager;
    }

    /**
     * Get all the visitors related to a patient
     *
     * @param patID the id of the patient
     * @return NO_CONTENT if there aren't visitors, List<VisitDTO> otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/visit/{patID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VisitDTO>> getVisit(@PathVariable int patID) throws OHServiceException {
        logger.info("Get visit related to patId: " + patID);
        ArrayList<Visit> visit = visitManager.getVisits(patID);
        List<VisitDTO> listVisit = visit.stream().map(it -> getObjectMapper().map(it, VisitDTO.class)).collect(Collectors.toList());
        if (listVisit.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listVisit);
        }
    }

    /**
     * Create new visitor
     *
     * @param newVisit
     * @return an error if there are some problem, the visitor id (Integer) otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/visit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> newVisit(@RequestBody VisitDTO newVisit) throws OHServiceException {
        logger.info("Create Visit: " + newVisit);
        int visitId = visitManager.newVisit(getObjectMapper().map(newVisit, Visit.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(newVisit.getVisitID());
    }

    /**
     * Create new visitors
     *
     * @param newVisits a list with all the visitors
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/visits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newVisits(@RequestBody List<VisitDTO> newVisits) throws OHServiceException {
        logger.info("Create Visits");
        ArrayList<Visit> listVisits = new ArrayList<Visit>();
        for (VisitDTO visit : newVisits) {
            listVisits.add(getObjectMapper().map(visit, Visit.class));
        }
        boolean areCreated = visitManager.newVisits(listVisits);
        if (!areCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Visits are not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * Delete all the visits related to a patient
     *
     * @param patID the id of the patient
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/visit/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteVisitsRelatedToPatient(@PathVariable int patID) throws OHServiceException {
        logger.info("Delete Visit related to patId: " + patID);
        boolean areDeleted = visitManager.deleteAllVisits(patID);
        if (!areDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Visits are not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);
    }

}
