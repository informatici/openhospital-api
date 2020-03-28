package org.isf.patient.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.isf.patient.dto.PatientDTO;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Slf4j
@Api(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PatientController {

    private static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
    protected PatientBrowserManager patientManager;

    public PatientController(PatientBrowserManager patientManager) {
        this.patientManager = patientManager;
    }

    /**
     * Create new Patient
     * @param newPatient
     * @return
     * @throws OHServiceException
     */
    @PostMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> newPatient(@RequestBody PatientDTO newPatient) throws OHServiceException {
        String name = StringUtils.isEmpty(newPatient.getName()) ? newPatient.getFirstName() + " " + newPatient.getSecondName() : newPatient.getName();
        log.info("Create patient " + name);
        boolean isCreated = patientManager.newPatient(getObjectMapper().map(newPatient, Patient.class));
        Patient patient = patientManager.getPatient(name);
        if (!isCreated || patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(patient.getCode());
    }

    @PutMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> updatePatient(@PathVariable int code, @RequestBody PatientDTO updatePatient) throws OHServiceException {
        log.info("Update patient code:" + code);
        Patient patient = getObjectMapper().map(updatePatient, Patient.class);
        patient.setCode(code);
        boolean isUpdated = patientManager.updatePatient(patient);
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(patient.getCode());
    }

    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientDTO>> getPatients(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
        log.info("Get patients page:" + page + " size:" + size);
        ArrayList<Patient> patients = patientManager.getPatient(page, size);
        List<PatientDTO> patientDTOS = patients.stream().map(it -> getObjectMapper().map(it, PatientDTO.class)).collect(Collectors.toList());
        if (patientDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patientDTOS);
        } else {
            return ResponseEntity.ok(patientDTOS);
        }
    }

    @GetMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Integer code) throws OHServiceException {
        log.info("Get patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(getObjectMapper().map(patient, PatientDTO.class));
    }


    @GetMapping(value = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> searchPatient(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "code", required = false) Integer code) throws OHServiceException {
        log.info("Search patient name:" + name + " code:" + code);
        Patient patient = null;
        if (code != null) {
            patient = patientManager.getPatient(code);
        } else if (!name.equals("")) {
            patient = patientManager.getPatient(name);
        }
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(getObjectMapper().map(patient, PatientDTO.class));
    }

    @DeleteMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deletePatient(@PathVariable int code) throws OHServiceException {
        log.info("Delete patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        boolean isDeleted = false;
        if (patient != null) {
            isDeleted = patientManager.deletePatient(patient);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not deleted!", OHSeverityLevel.ERROR));
        }
        return (ResponseEntity) ResponseEntity.ok(isDeleted);
    }
}
