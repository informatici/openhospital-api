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
package org.isf.medicalhistory.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "MedicalHistory")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalHistoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalHistoryController.class);

	private final MedicalHistoryBrowsingManager medicalHistoryBrowsingManager;
	private final MedicalHistoryMapper medicalHistoryMapper;
	private final PatientBrowserManager patientBrowserManager;

	public MedicalHistoryController(MedicalHistoryBrowsingManager manager, MedicalHistoryMapper medicalHistoryMapper, PatientBrowserManager patientBrowserManager) {
		this.medicalHistoryBrowsingManager = manager;
		this.medicalHistoryMapper = medicalHistoryMapper;
		this.patientBrowserManager = patientBrowserManager;
	}

	@GetMapping(value = "/medicalhistories/{id}")
	public ResponseEntity<MedicalHistoryDTO> getMedicalHistoryById(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Get medical history {}", id);
		MedicalHistory mh = medicalHistoryBrowsingManager.getMedicalHistoryById(id);
		if (mh == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical history not found with id: " + id), HttpStatus.NOT_FOUND);
		}
		MedicalHistoryDTO medicalHistoryDTO = medicalHistoryMapper.map2DTO(mh);
		return ResponseEntity.status(HttpStatus.OK).body(medicalHistoryDTO);
	}

	@GetMapping(value = "/medicalhistories/patient/{patientCode}")
	public ResponseEntity<MedicalHistoryDTO> getMedicalHistoryByPatientCode(@PathVariable Integer patientCode) throws OHServiceException {
		LOGGER.info("Get medical histories for patient code {}", patientCode);
		MedicalHistory history = medicalHistoryBrowsingManager.getMedicalHistoriesByPatientCode(patientCode);
		if (history == null) {
			throw new OHAPIException(new OHExceptionMessage("No medical history found for patient code: " + patientCode),HttpStatus.NOT_FOUND);
		}
		MedicalHistoryDTO medicalHistoryDTOs = medicalHistoryMapper.map2DTO(history);
		return ResponseEntity.status(HttpStatus.OK).body(medicalHistoryDTOs);
	}

	@PostMapping(value = "/medicalhistories")
	public ResponseEntity<MedicalHistoryDTO> createMedicalHistory(@Valid @RequestBody MedicalHistoryDTO dto) throws OHServiceException {
		LOGGER.info("Create medical history for the patient: {}", dto.getPatient().getCode());
		PatientDTO patientDTO = dto.getPatient();
		Patient patient = patientBrowserManager.getPatientById(patientDTO.getCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}
		MedicalHistory mh = medicalHistoryMapper.map2Model(dto);
		MedicalHistoryDTO saved = medicalHistoryMapper.map2DTO(medicalHistoryBrowsingManager.add(mh));
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PutMapping(value = "/medicalhistories/{id}")
	public ResponseEntity<MedicalHistoryDTO> updateMedicalHistory(@PathVariable Integer id, @Valid @RequestBody MedicalHistoryDTO dto) throws OHServiceException {
		LOGGER.info("Update medical history {}", id);
		PatientDTO patientDTO = dto.getPatient();
		if (!dto.getId().equals(id)) {
			throw new OHAPIException(new OHExceptionMessage("Medical history code mismatch."));
		}
		MedicalHistory mh = medicalHistoryBrowsingManager.getMedicalHistoryById(id);
		if (mh == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical history not found with id: " + id), HttpStatus.NOT_FOUND);
		}
		Patient patient = patientBrowserManager.getPatientById(patientDTO.getCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}
		MedicalHistoryDTO medicalHistoryDTO = medicalHistoryMapper.map2DTO(medicalHistoryBrowsingManager.update(medicalHistoryMapper.map2Model(dto)));
		return ResponseEntity.status(HttpStatus.OK).body(medicalHistoryDTO);
	}
}
