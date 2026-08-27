/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.rest;

import org.isf.patient.dto.PatientExport;
import org.isf.patient.dto.PatientExportDTO;
import org.isf.patient.manager.PatientExportManager;
import org.isf.patient.mapper.PatientExportMapper;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Patients")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientExportController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientExportController.class);

	private static final MediaType TEXT_CSV = MediaType.parseMediaType("text/csv");

	private final PatientExportManager patientExportManager;

	private final PatientExportMapper patientExportMapper;

	private final PatientExportCsv patientExportCsv;

	public PatientExportController(PatientExportManager patientExportManager, PatientExportMapper patientExportMapper, PatientExportCsv patientExportCsv) {
		this.patientExportManager = patientExportManager;
		this.patientExportMapper = patientExportMapper;
		this.patientExportCsv = patientExportCsv;
	}

	/**
	 * Export the {@link org.isf.patient.model.Patient} record and all the records connected to it, in an open
	 * format: JSON by default, CSV when the {@code Accept} header asks for {@code text/csv}.
	 *
	 * @param code the code of the patient to export
	 * @param accept the {@code Accept} header of the request
	 * @return the {@link PatientExportDTO} as JSON, or its CSV rendering
	 * @throws OHServiceException When failed to export the patient data
	 */
	@GetMapping(value = "/patients/{code}/export", produces = { MediaType.APPLICATION_JSON_VALUE, "text/csv" })
	@Operation(description = "GDPR Art. 20 - Right to data portability")
	@ApiResponse(responseCode = "200", content = {
		@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PatientExportDTO.class)),
		@Content(mediaType = "text/csv", schema = @Schema(type = "string"))
	})
	public ResponseEntity<?> exportPatientData(
		@PathVariable("code") int code,
		@Parameter(hidden = true) @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String accept
	) throws OHServiceException {
		LOGGER.info("Export patient data for code: '{}'.", code);
		PatientExport export = patientExportManager.exportPatientData(code);
		if (export == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}
		PatientExportDTO exportDTO = patientExportMapper.map2DTO(export);
		if (isCsvRequested(accept)) {
			String csv;
			try {
				csv = patientExportCsv.toCsv(exportDTO);
			} catch (JsonProcessingException jsonProcessingException) {
				LOGGER.error("Export patient data for code '{}' failed.", code, jsonProcessingException);
				throw new OHAPIException(new OHExceptionMessage("Patient data not exported."));
			}
			return ResponseEntity.ok()
				.contentType(TEXT_CSV)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patient_" + code + "_export.csv\"")
				.body(csv);
		}
		return ResponseEntity.ok(exportDTO);
	}

	/**
	 * Whether the {@code Accept} header asks for CSV. Media types are evaluated in header order:
	 * JSON (the default, also chosen for {@code Accept: *&#47;*} or no header) wins over a later CSV entry.
	 */
	private boolean isCsvRequested(String accept) {
		if (accept == null || accept.isBlank()) {
			return false;
		}
		try {
			for (MediaType mediaType : MediaType.parseMediaTypes(accept)) {
				if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
					return false;
				}
				if (mediaType.isCompatibleWith(TEXT_CSV)) {
					return true;
				}
			}
		} catch (InvalidMediaTypeException invalidMediaTypeException) {
			return false;
		}
		return false;
	}
}
