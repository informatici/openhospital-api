/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.dto.PatientExportDTO;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class PatientExportCsvTest {

	private final PatientExportCsv patientExportCsv = new PatientExportCsv(new ObjectMapper());

	@Test
	void quotesFieldsContainingSeparatorsQuotesAndNewlines() throws Exception {
		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setFirstName("John, \"JJ\"\nJunior");
		patientDTO.setSecondName("Doe");
		PatientExportDTO export = new PatientExportDTO();
		export.setPatient(patientDTO);

		String csv = patientExportCsv.toCsv(export);

		assertThat(csv).contains("\"John, \"\"JJ\"\"\nJunior\"");
		assertThat(csv).contains("Doe");
	}

	@Test
	void writesOneSectionPerEntityGroup() throws Exception {
		PatientExportDTO export = new PatientExportDTO();
		export.setPatient(new PatientDTO());
		export.setAdmissions(List.of());

		String csv = patientExportCsv.toCsv(export);

		assertThat(csv).contains("# patient");
		assertThat(csv).contains("# admissions");
		assertThat(csv).contains("# opds");
		assertThat(csv).contains("# laboratories");
		assertThat(csv).contains("# therapies");
		assertThat(csv).contains("# operations");
		assertThat(csv).contains("# vaccines");
		assertThat(csv).contains("# examinations");
		assertThat(csv).contains("# bills");
		assertThat(csv).contains("# billItems");
		assertThat(csv).contains("# billPayments");
	}

	@Test
	void neutralizesCsvFormulaInjection() throws Exception {
		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setFirstName("=1+2");
		patientDTO.setSecondName("+SUM(A1)");
		PatientExportDTO export = new PatientExportDTO();
		export.setPatient(patientDTO);

		String csv = patientExportCsv.toCsv(export);

		assertThat(csv).contains("'=1+2");
		assertThat(csv).contains("'+SUM(A1)");
	}
}
