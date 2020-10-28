package org.isf.patient.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;


@RestController
@Api(value="/patients",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class PatientController {

	protected static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
	protected PatientBrowserManager patientManager;
    @Autowired
	protected PatientMapper patientMapper;

	private final Logger logger = LoggerFactory.getLogger(PatientController.class);

	public PatientController(PatientBrowserManager patientManager, PatientMapper patientMapper) {
		this.patientManager = patientManager;
		this.patientMapper = patientMapper;
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
        logger.info("Create patient "  + name);
        newPatient.setCode(null);
        boolean isCreated = patientManager.newPatient(patientMapper.map2Model(newPatient));
        Patient patient = patientManager.getPatient(name);
        if(!isCreated || patient == null){
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(patient.getCode());
	}

	@PutMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> updatePatient(@PathVariable int code, @RequestBody PatientDTO updatePatient) throws OHServiceException {
        logger.info("Update patient code:"  +  code);
        Patient patient = patientMapper.map2Model(updatePatient);
        patient.setCode(code);
        boolean isUpdated = patientManager.updatePatient(patient);
        if(!isUpdated){
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(patient.getCode());
	}

	@GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> getPatients(
			@RequestParam(value="page", required=false, defaultValue="0") Integer page,
			@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
        logger.info("Get patients page:"  +  page + " size:" + size);
	    ArrayList<Patient> patients = patientManager.getPatient(page, size);
        List<PatientDTO> patientDTOS = patientMapper.map2DTOList(patients);
        if(patientDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patientDTOS);
        }else{
            return ResponseEntity.ok(patientDTOS);
        }
	}

	@GetMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientDTO> getPatient(@PathVariable Integer code) throws OHServiceException {
        logger.info("Get patient code:"  +  code);
		Patient patient = patientManager.getPatient(code);
		if (patient == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(patientMapper.map2DTO(patient));
	}

    @GetMapping(value = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientDTO>> searchPatient(
            @RequestParam(value="firstName", defaultValue="", required = false) String firstName,
            @RequestParam(value="secondName", defaultValue="", required = false) String secondName,
            @RequestParam(value="birthDate", defaultValue="", required = false) String birthDate,
            @RequestParam(value="address", defaultValue="", required = false) String address
    ) throws OHServiceException {


        List<Patient> patientList = null;

        Map<String, Object> params = new HashMap<String, Object>();
        if (firstName != null && !firstName.isEmpty()) {
            params.put("firstName", firstName);
        }
        if (secondName != null && !secondName.isEmpty()) {
            params.put("secondName", secondName);
        }
        if (birthDate != null && !birthDate.isEmpty()) {
            try {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date birthDateDate = df.parse(birthDate);
                params.put("birthDate", birthDateDate);

            } catch (Exception e) {
                // TODO: fixme
            }
        }
        if (address != null && !address.isEmpty()) {
            params.put("address", address);
        }


        if (params.entrySet().size() > 0) {
            patientList = patientManager.getPatients(params);
        }
        if (patientList == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(patientMapper.map2DTOList(patientList));
    }


	@DeleteMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deletePatient(@PathVariable int code) throws OHServiceException {
        logger.info("Delete patient code:"  +  code);
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
