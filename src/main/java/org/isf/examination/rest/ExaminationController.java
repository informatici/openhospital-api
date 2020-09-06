package org.isf.examination.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.exatype.rest.ExamTypeController;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class ExaminationController {

    private final Logger logger = LoggerFactory.getLogger(ExamTypeController.class);

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
        Patient patient = patientBrowserManager.getPatient(newPatientExamination.getPatientCode());
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

        Patient patient = patientBrowserManager.getPatient(dto.getPatientCode());
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

        Patient patient = patientBrowserManager.getPatient(patId);
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
