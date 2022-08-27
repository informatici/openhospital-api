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
package org.isf.examination.rest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.enumeration.Ausculation;
import org.isf.examination.enumeration.Bowel;
import org.isf.examination.enumeration.Diurese;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExaminationController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExaminationController.class);

	@Autowired
    protected ExaminationBrowserManager examinationBrowserManager;

    @Autowired
    private PatientExaminationMapper patientExaminationMapper;

    @Autowired
    private PatientBrowserManager patientBrowserManager;

    public ExaminationController(ExaminationBrowserManager examinationBrowserManager, PatientExaminationMapper patientExaminationMapper, PatientBrowserManager patientBrowserManager) {
        this.examinationBrowserManager = examinationBrowserManager;
        this.patientExaminationMapper = patientExaminationMapper;
        this.patientBrowserManager = patientBrowserManager;
    }

    @PostMapping(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newPatientExamination(@RequestBody PatientExaminationDTO newPatientExamination) throws OHServiceException {
        Patient patient = patientBrowserManager.getPatientById(newPatientExamination.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not exists!", OHSeverityLevel.ERROR));
        }

        PatientExamination patientExamination = patientExaminationMapper.map2Model(newPatientExamination);
        patientExamination.setPatient(patient);
        if(newPatientExamination.getPex_auscultation() != null) {
        	patientExamination.setPex_auscultation(newPatientExamination.getPex_auscultation().name());
        }
        if(newPatientExamination.getPex_bowel_desc() != null) {
        	patientExamination.setPex_bowel_desc(newPatientExamination.getPex_bowel_desc().name());
        }
        if(newPatientExamination.getPex_diuresis_desc() != null) {
        	patientExamination.setPex_diuresis_desc(newPatientExamination.getPex_diuresis_desc().name());
        }
        LocalDateTime date = newPatientExamination.getPex_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        patientExamination.setPex_date(date);
        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PutMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> updateExamination(@PathVariable Integer id, @RequestBody PatientExaminationDTO dto) throws OHServiceException {
        if (dto.getPex_ID() != id) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient examination id mismatch", OHSeverityLevel.ERROR));
        }
        if (examinationBrowserManager.getByID(id) == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient Examination not Found!", OHSeverityLevel.WARNING));
        }

        Patient patient = patientBrowserManager.getPatientById(dto.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not exists!", OHSeverityLevel.ERROR));
        }

        PatientExamination patientExamination = patientExaminationMapper.map2Model(dto);
        patientExamination.setPatient(patient);
        patientExamination.setPex_date(dto.getPex_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        if(dto.getPex_auscultation() != null) {
        	patientExamination.setPex_auscultation(dto.getPex_auscultation().name());
        }
        if(dto.getPex_bowel_desc() != null) {
        	patientExamination.setPex_bowel_desc(dto.getPex_bowel_desc().name());
        }
        if(dto.getPex_diuresis_desc() != null) {
        	patientExamination.setPex_diuresis_desc(dto.getPex_diuresis_desc().name());
        }
        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/examinations/defaultPatientExamination", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getDefaultPatientExamination(@RequestParam Integer patId) throws OHServiceException {

        Patient patient = patientBrowserManager.getPatientById(patId);
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not exists!", OHSeverityLevel.ERROR));
        }
        PatientExamination patientExamination = examinationBrowserManager.getDefaultPatientExamination(patient);
        PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(patientExamination);
        if (patientExaminationDTO == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        	Instant instant = patientExamination.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
    		Date date = (Date) Date.from(instant);
    		patientExaminationDTO.setPex_date(date);
    		if(patientExamination.getPex_auscultation() != null) {
    			patientExaminationDTO.setPex_auscultation(Ausculation.valueOf(patientExamination.getPex_auscultation()));
            }
            if(patientExamination.getPex_bowel_desc() != null) {
            	patientExaminationDTO.setPex_bowel_desc(Bowel.valueOf(patientExamination.getPex_bowel_desc()));
            }
            if(patientExamination.getPex_diuresis_desc() != null) {
            	patientExaminationDTO.setPex_diuresis_desc(Diurese.valueOf(patientExamination.getPex_diuresis_desc()));
            }
            return ResponseEntity.ok(patientExaminationDTO);
        }
    }

    @GetMapping(value = "/examinations/fromLastPatientExamination/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getFromLastPatientExamination(@PathVariable Integer id) throws OHServiceException {

        PatientExamination lastPatientExamination = examinationBrowserManager.getByID(id);
        PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(examinationBrowserManager.getFromLastPatientExamination(lastPatientExamination));
        if (patientExaminationDTO == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        	Instant instant = lastPatientExamination.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
    		Date date = (Date) Date.from(instant);
    		patientExaminationDTO.setPex_date(date);
    		if(lastPatientExamination.getPex_auscultation() != null) {
    			patientExaminationDTO.setPex_auscultation(Ausculation.valueOf(lastPatientExamination.getPex_auscultation()));
            }
            if(lastPatientExamination.getPex_bowel_desc() != null) {
            	patientExaminationDTO.setPex_bowel_desc(Bowel.valueOf(lastPatientExamination.getPex_bowel_desc()));
            }
            if(lastPatientExamination.getPex_diuresis_desc() != null) {
            	patientExaminationDTO.setPex_diuresis_desc(Diurese.valueOf(lastPatientExamination.getPex_diuresis_desc()));
            }
            return ResponseEntity.ok(patientExaminationDTO);
        }
    }

    @GetMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getByID(@PathVariable Integer id) throws OHServiceException {

        PatientExamination patientExamination = examinationBrowserManager.getByID(id);

        if (patientExamination == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        	PatientExaminationDTO patienE = patientExaminationMapper.map2DTO(patientExamination);
        	Instant instant = patientExamination.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
    		Date date = (Date) Date.from(instant);
    		patienE.setPex_date(date);
    		if(patientExamination.getPex_auscultation() != null) {
    			patienE.setPex_auscultation(Ausculation.valueOf(patientExamination.getPex_auscultation()));
            }
            if(patientExamination.getPex_bowel_desc() != null) {
            	patienE.setPex_bowel_desc(Bowel.valueOf(patientExamination.getPex_bowel_desc()));
            }
            if(patientExamination.getPex_diuresis_desc() != null) {
            	patienE.setPex_diuresis_desc(Diurese.valueOf(patientExamination.getPex_diuresis_desc()));
            }
            return ResponseEntity.ok(patienE);
            
        }
    }

    @GetMapping(value = "/examinations/lastByPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getLastByPatientId(@PathVariable Integer patId) throws OHServiceException {

        PatientExamination patientExamination = examinationBrowserManager.getLastByPatID(patId);

        if (patientExamination == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        	PatientExaminationDTO patienE = patientExaminationMapper.map2DTO(patientExamination);
        	Instant instant = patientExamination.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
    		Date date = (Date) Date.from(instant);
    		patienE.setPex_date(date);
    		if(patientExamination.getPex_auscultation() != null) {
    			patienE.setPex_auscultation(Ausculation.valueOf(patientExamination.getPex_auscultation()));
            }
            if(patientExamination.getPex_bowel_desc() != null) {
            	patienE.setPex_bowel_desc(Bowel.valueOf(patientExamination.getPex_bowel_desc()));
            }
            if(patientExamination.getPex_diuresis_desc() != null) {
            	patienE.setPex_diuresis_desc(Diurese.valueOf(patientExamination.getPex_diuresis_desc()));
            }
            return ResponseEntity.ok(patienE);
        }
    }

    @GetMapping(value = "/examinations/lastNByPatId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientExaminationDTO>> getLastNByPatID(@RequestParam Integer limit, @RequestParam Integer patId) throws OHServiceException {

        List<PatientExamination> patientExaminationList = examinationBrowserManager.getLastNByPatID(patId, limit);

        if (patientExaminationList == null || patientExaminationList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        	List<PatientExaminationDTO> patientExamList=patientExaminationList.stream().map(pat -> {
        		PatientExaminationDTO patienE = patientExaminationMapper.map2DTO(pat);
            	Instant instant = pat.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
        		Date date = (Date) Date.from(instant);
        		patienE.setPex_date(date);
        		if(pat.getPex_auscultation() != null) {
        			patienE.setPex_auscultation(Ausculation.valueOf(pat.getPex_auscultation()));
                }
                if(pat.getPex_bowel_desc() != null) {
                	patienE.setPex_bowel_desc(Bowel.valueOf(pat.getPex_bowel_desc()));
                }
                if(pat.getPex_diuresis_desc() != null) {
                	patienE.setPex_diuresis_desc(Diurese.valueOf(pat.getPex_diuresis_desc()));
                }
        		return patienE;
        	}).collect(Collectors.toList());
            return ResponseEntity.ok(patientExamList);
        }
    }

    @GetMapping(value = "/examinations/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientExaminationDTO>> getByPatientId(@PathVariable Integer patId) throws OHServiceException {

        List<PatientExamination> patientExamination = examinationBrowserManager.getByPatID(patId);
        List<PatientExaminationDTO> listePExam = new ArrayList<PatientExaminationDTO>();
        if (patientExamination == null || patientExamination.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
        		listePExam = patientExamination.stream().map(pat -> {
            		PatientExaminationDTO patienE = patientExaminationMapper.map2DTO(pat);
                	Instant instant = pat.getPex_date().atZone(ZoneId.systemDefault()).toInstant();
            		Date date = (Date) Date.from(instant);
            		patienE.setPex_date(date);
            		if(pat.getPex_auscultation() != null) {
            			patienE.setPex_auscultation(Ausculation.valueOf(pat.getPex_auscultation()));
                    }
                    if(pat.getPex_bowel_desc() != null) {
                    	patienE.setPex_bowel_desc(Bowel.valueOf(pat.getPex_bowel_desc()));
                    }
                    if(pat.getPex_diuresis_desc() != null) {
                    	patienE.setPex_diuresis_desc(Diurese.valueOf(pat.getPex_diuresis_desc()));
                    }
            		return patienE;
            	}).collect(Collectors.toList());
        		
        	}
            return ResponseEntity.ok(listePExam);
    }
}
