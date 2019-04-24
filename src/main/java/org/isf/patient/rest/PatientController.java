package org.isf.patient.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.responsebodyadvice.DTO;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;


@RestController
@Api(value="/patients",produces ="application/json")
public class PatientController {

	private static final String DEFAULT_PAGE_SIZE = "80";
	
	@Autowired
	protected PatientBrowserManager patientManager;
	
	private final Logger logger = LoggerFactory.getLogger(PatientController.class);
	
	public PatientController(PatientBrowserManager patientManager) {
		this.patientManager = patientManager;
	}

	@PostMapping(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	Patient newPatient(@RequestBody Patient newPatient) {
		try {
			logger.debug(newPatient.toString());
			patientManager.newPatient(newPatient);
			return newPatient;
		} catch (OHServiceException e) {
			logger.error("New patient error:", e.getMessages());
			for(OHExceptionMessage emsg: e.getMessages()) {
				logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "New Patient failed: "+ e.getMessages()
		          .stream()
		          .map(em -> em.getMessage())
		          .collect( Collectors.joining( "," ) ), 
		          e
		    );
	    }
	}
	
	@PutMapping(value = "/patients/{id}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	Patient updatePatient(@PathVariable String userId, @RequestBody Patient patient) {
		try {
			logger.debug(patient.toString());
			patientManager.updatePatient(patient);
			return patient;
		} catch (OHServiceException e) {
			logger.error("Update patient error:", e);
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Update Patient failed: " + e.getMessages()
		          .stream()
		          .map(em -> em.getMessage())
		          .collect( Collectors.joining( "," ) ), 
		          e);
	    }
	}
	
	@GetMapping(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
    public List<Patient> getPatients(
    		@RequestParam(value="page", required=false, defaultValue="0") Integer page,
    		@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size) {
        try {
        	return patientManager.getPatient(page, size);
		} catch (OHServiceException e) {
			logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }
	
	@GetMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
    public Patient getPatient(@PathVariable Integer code) {
        try {
        	return patientManager.getPatient(code);
		} catch (OHServiceException e) {
			logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }

	@GetMapping(value = "/patients/search", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
    public Patient searchPatient(
    		@RequestParam(value="name", defaultValue="") String name, 
    		@RequestParam(value="code", required=false) Integer code) {
        try {
        	Patient result = null;
        	if(code != null) {
        		result = patientManager.getPatient(code);
        	}else if (!name.equals("")) {
        		result = patientManager.getPatient(name);
        	}
        	return result;
		} catch (OHServiceException e) {
			logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }

	@DeleteMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	public void deletePatient(@PathVariable int code) {
		try {
			if(patientManager.getPatient(code) != null) {
				Patient p = new Patient();
				p.setCode(code);
				patientManager.deletePatient(p);
			}
		} catch (OHServiceException e) {
			logger.error("Delete patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Delete Patient failed", e);
		}
	}
}
