/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.visits.rest;

import java.util.List;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.mapper.VisitMapper;
import org.isf.visits.model.Visit;
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
@Tag(name = "Visit")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VisitsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisitsController.class);

	private final VisitManager visitManager;

	private final VisitMapper mapper;

	public VisitsController(VisitManager visitManager, VisitMapper visitMapper) {
		this.visitManager = visitManager;
		this.mapper = visitMapper;
	}

	/**
	 * Get all the visitors related to a patient.
	 *
	 * @param patID the id of the patient
	 * @return NO_CONTENT if there aren't visitors, {@code List<VaccineDTO>} otherwise
	 * @throws OHServiceException When failed to get patient visits
	 */
	@GetMapping("/visits/patient/{patID}")
	public List<VisitDTO> getVisit(@PathVariable("patID") int patID) throws OHServiceException {
		LOGGER.info("Get visit related to patId: {}", patID);

		return mapper.map2DTOList(visitManager.getVisits(patID));
	}

	/**
	 * Create a new visitor.
	 *
	 * @param newVisit Visit payload
	 * @return an error if there are some problem, the visitor id (Integer) otherwise
	 * @throws OHServiceException When failed to create visit
	 */
	@PostMapping("/visits")
	@ResponseStatus(HttpStatus.CREATED)
	public VisitDTO newVisit(@RequestBody VisitDTO newVisit) throws OHServiceException {
		LOGGER.info("Create Visit: {}", newVisit);
		return mapper.map2DTO(visitManager.newVisit(mapper.map2Model(newVisit)));
	}

	/**
	 * Create new visits.
	 *
	 * @param newVisits a list with all the visitors
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to create visits
	 */
	@PostMapping("/visits/insertList")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newVisits(@RequestBody List<VisitDTO> newVisits) throws OHServiceException {
		LOGGER.info("Create Visits");
		return visitManager.newVisits(mapper.map2ModelList(newVisits));
	}

	/**
	 * Delete all the visits related to a patient.
	 *
	 * @param patID the id of the patient
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to delete patient visit
	 */
	@DeleteMapping("/visits/delete/{patID}")
	public boolean deleteVisitsRelatedToPatient(
		@PathVariable("patID") int patID
	) throws OHServiceException {
		LOGGER.info("Delete Visit related to patId: {}", patID);

		return visitManager.deleteAllVisits(patID);
	}

	/**
	 * Update visit
	 *
	 * @param visitID the id of the visit
	 * @param updateVisit Visit payload
	 * @return an error message if there are some problem, ok otherwise
	 * @throws OHServiceException When failed to update the visit
	 */
	@PutMapping("/visits/{visitID}")
	public VisitDTO updateVisit(
		@PathVariable("visitID") int visitID, @RequestBody VisitDTO updateVisit
	) throws OHServiceException {
		LOGGER.info("Create Visits");

		Visit visit = visitManager.findVisit(visitID);
		if (visit == null || visit.getVisitID() != updateVisit.getVisitID()) {
			throw new OHAPIException(new OHExceptionMessage("Visit not found."), HttpStatus.NOT_FOUND);
		}

		Visit visitUp = mapper.map2Model(updateVisit);
		Visit visitUpdate = visitManager.newVisit(visitUp);
		if (visitUpdate == null) {
			throw new OHAPIException(new OHExceptionMessage("Visit not updated."));
		}

		return mapper.map2DTO(visitUpdate);
	}
}
