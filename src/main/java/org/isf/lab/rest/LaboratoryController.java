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
package org.isf.lab.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.lab.dto.LabWithRowsDTO;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.dto.LaboratoryForPrintDTO;
import org.isf.lab.dto.LaboratoryRowDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryForPrintMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.mapper.LaboratoryRowMapper;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
public class LaboratoryController {

	@Autowired
	protected LabManager laboratoryManager;

	@Autowired
	protected ExamBrowsingManager examManager;

	@Autowired
	private PatientBrowserManager patientBrowserManager;

	@Autowired
	private LaboratoryMapper laboratoryMapper;

	@Autowired
	private LaboratoryRowMapper laboratoryRowMapper;

	@Autowired
	private LaboratoryForPrintMapper laboratoryForPrintMapper;

	public LaboratoryController(LabManager laboratoryManager, PatientBrowserManager patientBrowserManager, ExamBrowsingManager examManager,
			LaboratoryMapper laboratoryMapper, LaboratoryRowMapper laboratoryRowMapper, LaboratoryForPrintMapper laboratoryForPrintMapper) {
		this.laboratoryManager = laboratoryManager;
		this.patientBrowserManager = patientBrowserManager;
		this.examManager = examManager;
		this.laboratoryMapper = laboratoryMapper;
		this.laboratoryRowMapper = laboratoryRowMapper;
		this.laboratoryForPrintMapper = laboratoryForPrintMapper;
	}

	@PostMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newLaboratory(@RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {

		LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
		List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

		Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}

		Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
		if (exam == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
		}

		Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
		labToInsert.setExam(exam);
		labToInsert.setPatient(patient);

		List<String> labRows = new ArrayList<>();
		if (labRow != null) {
			labRows = new ArrayList<>(labRow);
		}

		boolean inserted = laboratoryManager.newLaboratory(labToInsert, labRows);

		if (!inserted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not created!", OHSeverityLevel.ERROR));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}

	@PostMapping(value = "/laboratories/insertList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity newLaboratory2(@RequestBody List<LabWithRowsDTO> labsWithRows) throws OHServiceException {

		List<Laboratory> labsToInsert = new ArrayList<>();
		List<List<LaboratoryRow>> labsRowsToInsert = new ArrayList<>();

		for (LabWithRowsDTO labWithRowsDTO : labsWithRows) {
			LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
			Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
			}

			Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
			if (exam == null) {
				throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
			}

			Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
			labToInsert.setExam(exam);
			labToInsert.setPatient(patient);

			labsToInsert.add(labToInsert);

			if (labWithRowsDTO.getLaboratoryRowList() != null) {
				List<LaboratoryRow> labRowToInsert = new ArrayList<>();
				for (String rowDescription : labWithRowsDTO.getLaboratoryRowList()) {
					labRowToInsert.add(laboratoryRowMapper.map2Model(new LaboratoryRowDTO(rowDescription, laboratoryDTO)));
				}
				if (!labRowToInsert.isEmpty()) {
					labsRowsToInsert.add(labRowToInsert);
				}
			}
		}

		boolean inserted = laboratoryManager.newLaboratory2(labsToInsert, labsRowsToInsert);

		if (!inserted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}

	@PutMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateLaboratory(@PathVariable Integer code, @RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {

		LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
		List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

		if (!code.equals(laboratoryDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory code mismatch!", OHSeverityLevel.ERROR));
		}

		if (laboratoryManager.getLaboratory().stream().noneMatch(l -> l.getCode().equals(code))) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory Not Found!", OHSeverityLevel.ERROR));
		}
		Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}

		Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
		if (exam == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
		}

		Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
		labToInsert.setExam(exam);
		labToInsert.setPatient(patient);

		List<String> labRows = new ArrayList<>();
		if (labRow != null) {
			labRows = new ArrayList<>(labRow);
		}

		boolean updated = laboratoryManager.updateLaboratory(labToInsert, labRows);

		if (!updated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(true);
	}

	@DeleteMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteExam(@PathVariable Integer code) throws OHServiceException {
		Laboratory labToDelete = laboratoryManager.getLaboratory().stream().filter(l -> l.getCode().equals(code)).findFirst().orElse(null);
		if (labToDelete == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory Not Found!", OHSeverityLevel.ERROR));
		}
		if (!laboratoryManager.deleteLaboratory(labToDelete)) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(true);
	}

	@GetMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryDTO>> getLaboratory() throws OHServiceException {
		List<Laboratory> labList = laboratoryManager.getLaboratory();
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(laboratoryMapper.map2DTOList(labList));
		}
	}

	@GetMapping(value = "/laboratories/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryDTO>> getLaboratory(@PathVariable Integer patId) throws OHServiceException {
		Patient patient = patientBrowserManager.getPatientById(patId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}

		List<Laboratory> labList = laboratoryManager.getLaboratory(patient);
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(laboratoryMapper.map2DTOList(labList));
	}

	@GetMapping(value = "/laboratories/materials", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getMaterials() throws OHServiceException {
		List<String> materialList = laboratoryManager.getMaterialList();
		if (materialList == null || materialList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(materialList);
	}

	@GetMapping(value = "/laboratories/exams", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryForPrintDTO>> getLaboratoryForPrint(@RequestParam String examName,
			@RequestParam(value = "dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateFrom,
			@RequestParam(value = "dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateTo) throws OHServiceException {

		List<LaboratoryForPrint> laboratoryForPrintList = laboratoryManager.getLaboratoryForPrint(examName, dateFrom, dateTo);
		if (laboratoryForPrintList == null || laboratoryForPrintList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(laboratoryForPrintMapper.map2DTOList(laboratoryForPrintList));
	}

}
