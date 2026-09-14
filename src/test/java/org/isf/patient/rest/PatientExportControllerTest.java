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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.isf.OpenHospitalApiApplication;
import org.isf.accounting.TestBillItems;
import org.isf.accounting.TestBillPayments;
import org.isf.accounting.data.BillHelper;
import org.isf.accounting.model.Bill;
import org.isf.admission.data.AdmissionHelper;
import org.isf.examination.TestPatientExamination;
import org.isf.lab.data.LaboratoryHelper;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medtype.TestMedicalType;
import org.isf.opd.data.OpdHelper;
import org.isf.operation.TestOperationRow;
import org.isf.operation.data.OperationHelper;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.dto.PatientExport;
import org.isf.patient.manager.PatientExportManager;
import org.isf.patient.mapper.PatientExportMapper;
import org.isf.patient.model.Patient;
import org.isf.patvac.TestPatientVaccine;
import org.isf.therapy.TestTherapy;
import org.isf.utils.exception.OHException;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.TestVaccineType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
class PatientExportControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientExportControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PatientExportMapper patientExportMapper;

	@MockitoBean
	private PatientExportManager patientExportManager;

	@Test
	@WithMockUser(username = "admin", authorities = { "patient.export" })
	@DisplayName("Export patient data as JSON")
	void exportPatientDataAsJson() throws Exception {
		PatientExport export = setupPatientExport();

		when(patientExportManager.exportPatientData(anyInt())).thenReturn(export);

		var result = mvc.perform(
				get("/patients/{code}/export", 1))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(patientExportMapper.map2DTO(export))))
			.andExpect(jsonPath("$.patient").exists())
			.andExpect(jsonPath("$.patient.blobPhoto").isEmpty())
			.andExpect(jsonPath("$.admissions").isNotEmpty())
			.andExpect(jsonPath("$.opds").isNotEmpty())
			.andExpect(jsonPath("$.laboratories").isNotEmpty())
			.andExpect(jsonPath("$.therapies").isNotEmpty())
			.andExpect(jsonPath("$.operations").isNotEmpty())
			.andExpect(jsonPath("$.vaccines").isNotEmpty())
			.andExpect(jsonPath("$.vaccines[0].vaccine").exists())
			.andExpect(jsonPath("$.examinations").isNotEmpty())
			.andExpect(jsonPath("$.bills").isNotEmpty())
			.andExpect(jsonPath("$.billItems").isNotEmpty())
			.andExpect(jsonPath("$.billPayments").isNotEmpty())
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "patient.export" })
	@DisplayName("Export patient data as JSON when the Accept header allows any media type")
	void exportPatientDataAsJsonWhenAcceptingAnyMediaType() throws Exception {
		when(patientExportManager.exportPatientData(anyInt())).thenReturn(setupPatientExport());

		mvc.perform(
				get("/patients/{code}/export", 1).header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "patient.export" })
	@DisplayName("Export patient data as CSV")
	void exportPatientDataAsCsv() throws Exception {
		when(patientExportManager.exportPatientData(anyInt())).thenReturn(setupPatientExport());

		var result = mvc.perform(
				get("/patients/{code}/export", 1).header(HttpHeaders.ACCEPT, "text/csv"))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/csv"))
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patient_1_export.csv\""))
			.andExpect(content().string(containsString("# patient")))
			.andExpect(content().string(containsString("# admissions")))
			.andExpect(content().string(containsString("# opds")))
			.andExpect(content().string(containsString("# laboratories")))
			.andExpect(content().string(containsString("# therapies")))
			.andExpect(content().string(containsString("# operations")))
			.andExpect(content().string(containsString("# vaccines")))
			.andExpect(content().string(containsString("# examinations")))
			.andExpect(content().string(containsString("# bills")))
			.andExpect(content().string(containsString("# billItems")))
			.andExpect(content().string(containsString("# billPayments")))
			.andReturn();

		LOGGER.debug("result: {}", result);
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "patient.export" })
	@DisplayName("Export patient data as CSV when the Accept header carries media type parameters")
	void exportPatientDataAsCsvWithMediaTypeParameters() throws Exception {
		when(patientExportManager.exportPatientData(anyInt())).thenReturn(setupPatientExport());

		mvc.perform(
				get("/patients/{code}/export", 1).header(HttpHeaders.ACCEPT, "text/csv;q=0.9"))
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith("text/csv"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "patients.read" })
	@DisplayName("Should fail to export patient data without the patient.export permission")
	void shouldFailToExportPatientDataWithoutPermission() throws Exception {
		mvc.perform(
				get("/patients/{code}/export", 1))
			.andDo(log())
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "patient.export" })
	@DisplayName("Should fail to export patient data when the patient is not found")
	void shouldFailToExportPatientDataWhenPatientNotFound() throws Exception {
		when(patientExportManager.exportPatientData(anyInt())).thenReturn(null);

		mvc.perform(
				get("/patients/{code}/export", 1))
			.andDo(log())
			.andExpect(status().isNotFound());
	}

	private PatientExport setupPatientExport() throws OHException {
		Patient patient = PatientHelper.setup();
		patient.setCode(1);
		PatientExport export = new PatientExport();
		export.setPatient(patient);
		export.setAdmissions(List.of(AdmissionHelper.setup()));
		export.setOpds(List.of(OpdHelper.setup()));
		export.setLaboratories(List.of(LaboratoryHelper.setup()));
		Medical medical = new TestMedical().setup(new TestMedicalType().setup(false), false);
		medical.setCode(1);
		export.setTherapies(List.of(new TestTherapy().setup(patient, medical, true)));
		export.setOperations(List.of(new TestOperationRow().setup(OperationHelper.setup(), true)));
		Vaccine vaccine = new TestVaccine().setup(new TestVaccineType().setup(false), false);
		export.setVaccines(List.of(new TestPatientVaccine().setup(patient, vaccine, true)));
		export.setExaminations(List.of(new TestPatientExamination().setup(patient, true)));
		Bill bill = BillHelper.setup(1);
		export.setBills(List.of(bill));
		export.setBillItems(List.of(new TestBillItems().setup(bill, true)));
		export.setBillPayments(List.of(new TestBillPayments().setup(bill, true)));
		return export;
	}
}
