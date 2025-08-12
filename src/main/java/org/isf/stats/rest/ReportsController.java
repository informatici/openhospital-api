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
package org.isf.stats.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.poi.util.IOUtils;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.model.PatientExamination;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reports")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class ReportsController {

	private final JasperReportsManager reportsManager;
	private final ExaminationBrowserManager examinationBrowserManager;
	private final PatientBrowserManager patientBrowserManager;

	public ReportsController(JasperReportsManager reportsManager, ExaminationBrowserManager examinationBrowserManager, PatientBrowserManager patientBrowserManager) {
		this.reportsManager = reportsManager;
		this.examinationBrowserManager = examinationBrowserManager;
		this.patientBrowserManager = patientBrowserManager;
	}

	@GetMapping("/reports/exams-list")
	public ResponseEntity<Resource> printExamsListPdf(HttpServletRequest request) throws OHServiceException, IOException {
		return getReport(reportsManager.getExamsListPdf(), request);
	}

	@GetMapping("/reports/diseases-list")
	public ResponseEntity<Resource> printDiseasesListPdf(HttpServletRequest request) throws OHServiceException, IOException {
		return getReport(reportsManager.getDiseasesListPdf(), request);
	}
	
	@GetMapping("/reports/patientexamination/{examinationId}")
	public ResponseEntity<Resource> printPatientExaminationPdf(@PathVariable("examinationId") int examinationId, HttpServletRequest request) throws OHServiceException, IOException {
		PatientExamination patientExamination = examinationBrowserManager.getByID(examinationId);
		if (patientExamination == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient examination not found."), HttpStatus.NOT_FOUND);
		}
		int patId = patientExamination.getPatient().getCode();
	    return getReport(reportsManager.getGenericReportPatientExaminationPdf(patId, examinationId, request.getLocale()), request);
	}
	
	@GetMapping("/reports/patientexamrequest/{patientId}")
	public ResponseEntity<Resource> printPatientExamRequestPdf(@PathVariable("patientId") int patientId, HttpServletRequest request) throws OHServiceException, IOException {
		Patient patient = patientBrowserManager.getPatientById(patientId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}
	    return getReport(reportsManager.getGenericReportPatientExamRequestPdf(patientId, request.getLocale()), request);
	}

	private ResponseEntity<Resource> getReport(
		JasperReportResultDto resultDto, HttpServletRequest request
	) throws OHServiceException, IOException {
		Path report = Paths.get(resultDto.getFilename()).normalize();
		Resource resource;
		try {
			resource = new UrlResource(report.toUri());
			if (!resource.exists()) {
				throw new OHAPIException(new OHExceptionMessage("File not found."));
			}
		} catch (MalformedURLException e) {
			throw new OHAPIException(new OHExceptionMessage("File not found."));
		}

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + resource.getFilename() + '"')
			.body(resource);
	}
}
