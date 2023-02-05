/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.stats.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.util.IOUtils;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class ReportsController {
	@Autowired
	private JasperReportsManager reportsManager;

	@GetMapping(value = "/reports/exams-list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> printExamsListPdf(HttpServletRequest request) throws OHServiceException, IOException {
		return getReport(reportsManager.getExamsListPdf(), request);
	}

	@GetMapping(value = "/reports/diseases-list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> printDiseasesListPdf(HttpServletRequest request) throws OHServiceException, IOException {
		return getReport(reportsManager.getDiseasesListPdf(), request);
	}

	private ResponseEntity<byte[]> getReport(JasperReportResultDto resultDto, HttpServletRequest request) throws OHServiceException, IOException {
		Path report = Paths.get(resultDto.getFilename()).normalize();
		Resource resource;
		try {
			resource = new UrlResource(report.toUri());
			if (!resource.exists()) {
				throw new OHAPIException(new OHExceptionMessage(null, "File not found", OHSeverityLevel.ERROR));
			}
		} catch (MalformedURLException e) {
			throw new OHAPIException(new OHExceptionMessage(null, "File not found", OHSeverityLevel.ERROR));
		}

		String contentType;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			throw new OHAPIException(new OHExceptionMessage(null, "Failed to load the file's type", OHSeverityLevel.ERROR));
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		byte[] out = IOUtils.toByteArray(resource.getInputStream());

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + resource.getFilename() + '"')
				.body(out);
	}
}
