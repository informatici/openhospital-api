/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
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
        labToInsert.setCode(null);
        labToInsert.setLock(0);
        ArrayList<String> labRows = new ArrayList<>();
        if (labRow != null) {
            labRows = new ArrayList<String>(labRow);
        }
        
        if (labToInsert.getDate() == null) {
        	labToInsert.setDate(LocalDateTime.now());
        }

        boolean inserted = laboratoryManager.newLaboratory(labToInsert, labRows);
        
		if (!inserted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not created!", OHSeverityLevel.ERROR));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}

	@PostMapping(value = "/laboratories/insertList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newLaboratory2(@RequestBody List<LabWithRowsDTO> labsWithRows) throws OHServiceException {

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
		Optional<Laboratory> lab = laboratoryManager.getLaboratory(code);
    	Laboratory labToDelete = null;
        if (lab.isPresent()) {
        	labToDelete = lab.get();
        }else {
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
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
        	
			return ResponseEntity.ok(labList.stream().map(lab -> {
				LaboratoryDTO labDTO = laboratoryMapper.map2DTO(lab);
//				if (lab.getExamDate() != null) {
//					Instant instant = lab.getExamDate().atZone(ZoneId.systemDefault()).toInstant();
//					Date date = Date.from(instant);
//					labDTO.setExamDate(date);
//				}
//				if (lab.getLabDate() != null) {
//					Instant instant2 = lab.getLabDate().atZone(ZoneId.systemDefault()).toInstant();
//					Date date2 = Date.from(instant2);
//					labDTO.setRegistrationDate(date2);
//				}
				return labDTO;
			}).collect(Collectors.toList()));
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
        } else {
			return ResponseEntity.ok(labList.stream().map(lab -> {
				LaboratoryDTO labDTO = laboratoryMapper.map2DTO(lab);
//				if (lab.getExamDate() != null) {
//					Instant instant = lab.getExamDate().atZone(ZoneId.systemDefault()).toInstant();
//					Date date = Date.from(instant);
//					labDTO.setExamDate(date);
//				}
//				if (lab.getLabDate() != null) {
//					Instant instant2 = lab.getLabDate().atZone(ZoneId.systemDefault()).toInstant();
//					Date date2 = Date.from(instant2);
//					labDTO.setRegistrationDate(date2);
//				}
				return labDTO;
			}).collect(Collectors.toList()));
        }
    }

    @GetMapping(value = "/laboratories/materials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getMaterials() throws OHServiceException {
        List<String> materialList = laboratoryManager.getMaterialList();
        if (materialList == null || materialList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(materialList);
        }
    }

	@GetMapping(value = "/laboratories/exams", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryForPrintDTO>> getLaboratoryForPrint(
			@RequestParam String examName,
			@RequestParam(value = "dateFrom") LocalDateTime dateFrom,
			@RequestParam(value = "dateTo") LocalDateTime dateTo,
			@RequestParam(value = "patientCode",required = false, defaultValue = "0") int patientCode) throws OHServiceException {
		
		Patient patient = null ;
//    	LocalDateTime dateF = null;
//		if(dateFrom != null) {
//			dateF  = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//		}
//		
//		LocalDateTime dateT = null;
//		if(dateTo != null) {
//			dateT  = dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//		}

		if (patientCode != 0) {
			patient = patientBrowserManager.getPatientById(patientCode);
			if (patient == null || laboratoryManager.getLaboratory(patient) == null)
				throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<LaboratoryForPrint> laboratoryForPrintList = laboratoryManager.getLaboratoryForPrint(examName, dateFrom, dateTo, patient);
		if (laboratoryForPrintList == null || laboratoryForPrintList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(laboratoryForPrintList.stream().map(lab -> {
				LaboratoryForPrintDTO laboratoryForPrintDTO = laboratoryForPrintMapper.map2DTO(lab);
//				Instant instant = lab.getDate().atZone(ZoneId.systemDefault()).toInstant();
//				Date date = Date.from(instant);
//				laboratoryForPrintDTO.setDate(date);
				return laboratoryForPrintDTO;
			}).collect(Collectors.toList()));
		}
	}
	
	@GetMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LaboratoryDTO> getExamById(@PathVariable Integer code) throws OHServiceException {
		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		Laboratory lab = null;
		if (labo.isPresent()) {
			lab = labo.get();
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
//		Instant instant = lab.getDate().atZone(ZoneId.systemDefault()).toInstant();
//		Date date = Date.from(instant);
//		LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
//		laboratoryDTO.setExamDate(date);

		return ResponseEntity.ok(laboratoryMapper.map2DTO(lab));
	}
	
	@GetMapping(value = "/laboratories/exams/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LabWithRowsDTO> getExamWithRowsById(@PathVariable Integer code) throws OHServiceException {
    	LabWithRowsDTO lab = new LabWithRowsDTO();
    	Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
    	List<String> labDescription = new ArrayList<String>();
        Laboratory laboratory = null;
        if (labo.isPresent()) {
        	laboratory = labo.get();
        } else {
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        LaboratoryDTO labDTO = laboratoryMapper.map2DTO(laboratory);
//        if(laboratory.getDate()!=null) {
//        	Instant instant1 = laboratory.getDate().atZone(ZoneId.systemDefault()).toInstant();
//          	Date date1 = (Date) Date.from(instant1);
//          	labDTO.setExamDate(date1);
//        }
        lab.setLaboratoryDTO(labDTO);
        
        if (laboratory.getExam().getProcedure() == 2) {
        	List<LaboratoryRow> labDes = laboratoryManager.getLaboratoryRowList(laboratory.getCode());
        	if(!labDes.isEmpty()) {
        		for(LaboratoryRow laboratoryRow : labDes) {
        			labDescription.add(laboratoryRow.getDescription());
        		}
        	}
        	
		}
        lab.setLaboratoryRowList(labDescription);
        return  ResponseEntity.ok(lab);
    }
 
}
