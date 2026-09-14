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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.isf.patient.dto.PatientExportDTO;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Renders a {@link PatientExportDTO} as CSV (RFC 4180 quoting rules). The output contains one section per
 * entity group: a {@code # <group>} marker line, a header row with the union of the keys of the group rows
 * (in first-occurrence order), one data row per record and a blank line between sections. Nested objects and
 * lists are serialized as compact JSON inside the field.
 */
@Component
public class PatientExportCsv {

	private static final String CRLF = "\r\n";

	private final ObjectMapper objectMapper;

	public PatientExportCsv(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public String toCsv(PatientExportDTO export) throws JsonProcessingException {
		StringBuilder csv = new StringBuilder();
		writeSection(csv, "patient", export.getPatient() == null ? List.of() : List.of(export.getPatient()));
		writeSection(csv, "admissions", export.getAdmissions());
		writeSection(csv, "opds", export.getOpds());
		writeSection(csv, "laboratories", export.getLaboratories());
		writeSection(csv, "therapies", export.getTherapies());
		writeSection(csv, "operations", export.getOperations());
		writeSection(csv, "vaccines", export.getVaccines());
		writeSection(csv, "examinations", export.getExaminations());
		writeSection(csv, "bills", export.getBills());
		writeSection(csv, "billItems", export.getBillItems());
		writeSection(csv, "billPayments", export.getBillPayments());
		return csv.toString();
	}

	private void writeSection(StringBuilder csv, String group, List<?> rows) throws JsonProcessingException {
		if (csv.length() > 0) {
			csv.append(CRLF);
		}
		csv.append("# ").append(group).append(CRLF);
		List<Map<String, Object>> flatRows = new ArrayList<>();
		Set<String> keys = new LinkedHashSet<>();
		if (rows != null) {
			for (Object row : rows) {
				Map<String, Object> flatRow = objectMapper.convertValue(row, new TypeReference<LinkedHashMap<String, Object>>() {
				});
				keys.addAll(flatRow.keySet());
				flatRows.add(flatRow);
			}
		}
		if (keys.isEmpty()) {
			return;
		}
		csv.append(String.join(",", keys.stream().map(this::escape).toList())).append(CRLF);
		for (Map<String, Object> flatRow : flatRows) {
			List<String> values = new ArrayList<>();
			for (String key : keys) {
				values.add(escape(render(flatRow.get(key))));
			}
			csv.append(String.join(",", values)).append(CRLF);
		}
	}

	private String render(Object value) throws JsonProcessingException {
		if (value == null) {
			return "";
		}
		if (value instanceof Map || value instanceof Collection) {
			return objectMapper.writeValueAsString(value);
		}
		return String.valueOf(value);
	}

	private String escape(String value) {
		String sanitized = value;
		// Neutralize CSV formula injection: spreadsheet applications interpret a cell starting with =, +, -,
		// @, tab or carriage return as a formula, so such values are prefixed with a single quote.
		if (!sanitized.isEmpty() && "=+-@\t\r".indexOf(sanitized.charAt(0)) >= 0) {
			sanitized = "'" + sanitized;
		}
		if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\r") || sanitized.contains("\n")) {
			return '"' + sanitized.replace("\"", "\"\"") + '"';
		}
		return sanitized;
	}
}
