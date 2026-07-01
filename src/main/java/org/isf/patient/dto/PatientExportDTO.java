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
package org.isf.patient.dto;

import java.util.List;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.opd.dto.OpdDTO;
import org.isf.operation.dto.OperationRowDTO;
import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.therapy.dto.TherapyRowDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregate of a patient record and all the records connected to it, for GDPR Art. 20 (right to data portability) exports")
public class PatientExportDTO {

	@Schema(description = "The patient record")
	private PatientDTO patient;

	@Schema(description = "The admissions of the patient")
	private List<AdmissionDTO> admissions;

	@Schema(description = "The OPD episodes of the patient")
	private List<OpdDTO> opds;

	@Schema(description = "The laboratory exams of the patient")
	private List<LaboratoryDTO> laboratories;

	@Schema(description = "The therapies of the patient")
	private List<TherapyRowDTO> therapies;

	@Schema(description = "The operations of the patient")
	private List<OperationRowDTO> operations;

	@Schema(description = "The vaccines of the patient")
	private List<PatientVaccineDTO> vaccines;

	@Schema(description = "The examinations of the patient")
	private List<PatientExaminationDTO> examinations;

	@Schema(description = "The bills of the patient")
	private List<BillDTO> bills;

	@Schema(description = "The items of the patient's bills")
	private List<BillItemsDTO> billItems;

	@Schema(description = "The payments of the patient's bills")
	private List<BillPaymentsDTO> billPayments;

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public List<AdmissionDTO> getAdmissions() {
		return admissions;
	}

	public void setAdmissions(List<AdmissionDTO> admissions) {
		this.admissions = admissions;
	}

	public List<OpdDTO> getOpds() {
		return opds;
	}

	public void setOpds(List<OpdDTO> opds) {
		this.opds = opds;
	}

	public List<LaboratoryDTO> getLaboratories() {
		return laboratories;
	}

	public void setLaboratories(List<LaboratoryDTO> laboratories) {
		this.laboratories = laboratories;
	}

	public List<TherapyRowDTO> getTherapies() {
		return therapies;
	}

	public void setTherapies(List<TherapyRowDTO> therapies) {
		this.therapies = therapies;
	}

	public List<OperationRowDTO> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationRowDTO> operations) {
		this.operations = operations;
	}

	public List<PatientVaccineDTO> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<PatientVaccineDTO> vaccines) {
		this.vaccines = vaccines;
	}

	public List<PatientExaminationDTO> getExaminations() {
		return examinations;
	}

	public void setExaminations(List<PatientExaminationDTO> examinations) {
		this.examinations = examinations;
	}

	public List<BillDTO> getBills() {
		return bills;
	}

	public void setBills(List<BillDTO> bills) {
		this.bills = bills;
	}

	public List<BillItemsDTO> getBillItems() {
		return billItems;
	}

	public void setBillItems(List<BillItemsDTO> billItems) {
		this.billItems = billItems;
	}

	public List<BillPaymentsDTO> getBillPayments() {
		return billPayments;
	}

	public void setBillPayments(List<BillPaymentsDTO> billPayments) {
		this.billPayments = billPayments;
	}
}
