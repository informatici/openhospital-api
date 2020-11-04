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

import java.util.List;

import org.isf.examination.dto.PatientExaminationDTO;
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
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
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

        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PutMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateExamination(@PathVariable Integer id, @RequestBody PatientExaminationDTO dto) throws OHServiceException {
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
        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/examinations/defaultPatientExamination", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getDefaultPatientExamination(@RequestParam Integer patId) throws OHServiceException {

        Patient patient = patientBrowserManager.getPatientById(patId);
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not exists!", OHSeverityLevel.ERROR));
        }
        PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(examinationBrowserManager.getDefaultPatientExamination(patient));
        if (patientExaminationDTO == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
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
            return ResponseEntity.ok(patientExaminationDTO);
        }
    }

    @GetMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getByID(@PathVariable Integer id) throws OHServiceException {

        PatientExamination patientExamination = examinationBrowserManager.getByID(id);

        if (patientExamination == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(patientExaminationMapper.map2DTO(patientExamination));
        }
    }

    @GetMapping(value = "/examinations/lastByPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getLastByPatientId(@PathVariable Integer patId) throws OHServiceException {

        PatientExamination patientExamination = examinationBrowserManager.getLastByPatID(patId);

        if (patientExamination == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(patientExaminationMapper.map2DTO(patientExamination));
        }
    }

    @GetMapping(value = "/examinations/lastNByPatId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientExaminationDTO>> getLastNByPatID(@RequestParam Integer limit, @RequestParam Integer patId) throws OHServiceException {

        List<PatientExamination> patientExaminationList = examinationBrowserManager.getLastNByPatID(patId, limit);

        if (patientExaminationList == null || patientExaminationList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(patientExaminationMapper.map2DTOList(patientExaminationList));
        }
    }

    @GetMapping(value = "/examinations/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientExaminationDTO>> getByPatientId(@PathVariable Integer patId) throws OHServiceException {

        List<PatientExamination> patientExamination = examinationBrowserManager.getByPatID(patId);

        if (patientExamination == null || patientExamination.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(patientExaminationMapper.map2DTOList(patientExamination));
        }
    }
}
