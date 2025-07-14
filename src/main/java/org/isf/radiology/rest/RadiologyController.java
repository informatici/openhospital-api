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
package org.isf.radiology.rest;

import java.util.Base64;
import java.util.List;

import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.StudyResponse;
import org.isf.orthanc.service.OrthancAPIClientService;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.radiology.dto.InstancePreviewDTO;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHInternalServerException;
import org.isf.utils.exception.OHNotFoundException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Silevester D.
 * @since 1.15
 */
@RestController
@Tag(name = "Radiology")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RadiologyController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RadiologyController.class);

	private final OrthancAPIClientService orthancAPIClientService;
	private final PatientBrowserManager patientManager;

	public RadiologyController(OrthancAPIClientService orthancAPIClientService, PatientBrowserManager patientManager) {
		this.orthancAPIClientService = orthancAPIClientService;
		this.patientManager = patientManager;
	}

	/**
	 * Get patient studies
	 * @return the list of studies related to the given patient.
	 * @throws OHServiceException When failed to connect with ORTHANC server
	 */
	@GetMapping("/radiology/patients/{id}/studies")
	public List<StudyResponse> getPatientStudiesById(@PathVariable("id") String patientId) throws OHServiceException {
		LOGGER.info("Retrieving studies for patient with ID {}", patientId);
		Patient patient = patientManager.getPatientById(Integer.parseInt(patientId));

		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient with ID " + patientId + " not found."), HttpStatus.NOT_FOUND);
		}

		return orthancAPIClientService.getPatientStudiesById(patientId);
	}

	/**
	 * Get study's series
	 * @return the list of series related to the given study.
	 * @throws OHServiceException When failed to connect with ORTHANC server
	 */
	@GetMapping("/radiology/studies/{id}/series")
	public List<SeriesResponse> getStudySeriesById(@PathVariable("id") String studyId) throws OHServiceException {
		LOGGER.info("Retrieving series for study with ID {}", studyId);

		try {
			return orthancAPIClientService.getStudySeries(studyId);
		} catch (OHNotFoundException e) {
			throw new OHAPIException(new OHExceptionMessage("Study with ID " + studyId + " not found."), HttpStatus.NOT_FOUND);
		} catch (OHInternalServerException e) {
			throw new OHAPIException(new OHExceptionMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get series' instances
	 * @return the list of instances related to the given series.
	 * @throws OHServiceException When failed to connect with ORTHANC server
	 */
	@GetMapping("/radiology/series/{id}/instances")
	public List<InstanceResponse> getSeriesInstancesById(@PathVariable("id") String seriesId) throws OHServiceException {
		LOGGER.info("Retrieving instances for series with ID {}", seriesId);

		try {
			return orthancAPIClientService.getSeriesInstances(seriesId);
		} catch (OHNotFoundException e) {
			throw new OHAPIException(new OHExceptionMessage("Series with ID " + seriesId + " not found."), HttpStatus.NOT_FOUND);
		} catch (OHInternalServerException e) {
			throw new OHAPIException(new OHExceptionMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get instance preview
	 * @return a Base64 PNG image representing the instance preview
	 * @throws OHServiceException When failed to connect with ORTHANC server
	 */
	@GetMapping(value = "/radiology/instances/{id}/preview")
	public InstancePreviewDTO getInstancePreview(@PathVariable("id") String instanceId) throws OHServiceException {
		LOGGER.info("Downloading preview for instance with ID {}", instanceId);

		return new InstancePreviewDTO(Base64.getEncoder().encodeToString(orthancAPIClientService.getInstancePreview(instanceId)));
	}
}
