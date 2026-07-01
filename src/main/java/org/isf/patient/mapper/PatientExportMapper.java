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
package org.isf.patient.mapper;

import org.isf.accounting.mapper.BillItemsMapper;
import org.isf.accounting.mapper.BillMapper;
import org.isf.accounting.mapper.BillPaymentsMapper;
import org.isf.admission.mapper.AdmissionMapper;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.opd.mapper.OpdMapper;
import org.isf.operation.mapper.OperationRowMapper;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.dto.PatientExport;
import org.isf.patient.dto.PatientExportDTO;
import org.isf.patvac.mapper.PatVacMapper;
import org.isf.therapy.mapper.TherapyRowMapper;
import org.springframework.stereotype.Component;

/**
 * Assembles a {@link PatientExportDTO} out of a {@link PatientExport} aggregate, delegating each
 * entity group to its existing mapper.
 */
@Component
public class PatientExportMapper {

	private final PatientMapper patientMapper;

	private final AdmissionMapper admissionMapper;

	private final OpdMapper opdMapper;

	private final LaboratoryMapper laboratoryMapper;

	private final TherapyRowMapper therapyRowMapper;

	private final OperationRowMapper operationRowMapper;

	private final PatVacMapper patVacMapper;

	private final PatientExaminationMapper patientExaminationMapper;

	private final BillMapper billMapper;

	private final BillItemsMapper billItemsMapper;

	private final BillPaymentsMapper billPaymentsMapper;

	public PatientExportMapper(PatientMapper patientMapper, AdmissionMapper admissionMapper, OpdMapper opdMapper, LaboratoryMapper laboratoryMapper,
		TherapyRowMapper therapyRowMapper, OperationRowMapper operationRowMapper, PatVacMapper patVacMapper,
		PatientExaminationMapper patientExaminationMapper, BillMapper billMapper, BillItemsMapper billItemsMapper, BillPaymentsMapper billPaymentsMapper) {
		this.patientMapper = patientMapper;
		this.admissionMapper = admissionMapper;
		this.opdMapper = opdMapper;
		this.laboratoryMapper = laboratoryMapper;
		this.therapyRowMapper = therapyRowMapper;
		this.operationRowMapper = operationRowMapper;
		this.patVacMapper = patVacMapper;
		this.patientExaminationMapper = patientExaminationMapper;
		this.billMapper = billMapper;
		this.billItemsMapper = billItemsMapper;
		this.billPaymentsMapper = billPaymentsMapper;
	}

	public PatientExportDTO map2DTO(PatientExport export) {
		PatientExportDTO dto = new PatientExportDTO();
		dto.setPatient(stripPhoto(patientMapper.map2DTO(export.getPatient())));
		dto.setAdmissions(admissionMapper.map2DTOList(export.getAdmissions()));
		dto.getAdmissions().forEach(admission -> stripPhoto(admission.getPatient()));
		dto.setOpds(opdMapper.map2DTOList(export.getOpds()));
		dto.setLaboratories(laboratoryMapper.map2DTOList(export.getLaboratories()));
		dto.setTherapies(therapyRowMapper.map2DTOList(export.getTherapies()));
		dto.getTherapies().forEach(therapy -> stripPhoto(therapy.getPatID()));
		dto.setOperations(operationRowMapper.map2DTOList(export.getOperations()));
		dto.getOperations().forEach(operation -> {
			if (operation.getAdmission() != null) {
				stripPhoto(operation.getAdmission().getPatient());
			}
			if (operation.getBill() != null) {
				stripPhoto(operation.getBill().getPatient());
			}
		});
		dto.setVaccines(patVacMapper.map2DTOList(export.getVaccines()));
		dto.getVaccines().forEach(vaccine -> stripPhoto(vaccine.getPatient()));
		dto.setExaminations(patientExaminationMapper.map2DTOList(export.getExaminations()));
		dto.setBills(billMapper.map2DTOList(export.getBills()));
		dto.getBills().forEach(bill -> stripPhoto(bill.getPatient()));
		dto.setBillItems(billItemsMapper.map2DTOList(export.getBillItems()));
		dto.setBillPayments(billPaymentsMapper.map2DTOList(export.getBillPayments()));
		return dto;
	}

	/**
	 * The profile photo is intentionally not part of the export (see AdminManual, GDPR - Data Portability).
	 */
	private PatientDTO stripPhoto(PatientDTO patientDTO) {
		if (patientDTO != null) {
			patientDTO.setBlobPhoto(null);
		}
		return patientDTO;
	}
}
