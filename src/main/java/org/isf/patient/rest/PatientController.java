package org.isf.patient.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.responsebodyadvice.DTO;
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

@RestController
public class PatientController {

	@Autowired
	protected PatientBrowserManager patientManager;
	private final Logger logger = LoggerFactory.getLogger(PatientController.class);
	
	public PatientController(PatientBrowserManager patientManager) {
		super();
		this.patientManager = patientManager;
	}

	@PostMapping("/patient")
	@DTO(PatientDTO.class)
	Patient newPatient(@RequestBody Patient newPatient) {
		try {
			this.logger.debug(newPatient.toString());
			this.patientManager.newPatient(newPatient);
			return newPatient;
		} catch (OHServiceException e) {
			this.logger.error("New patient error:", e.getMessages());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
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
	
	@PutMapping("/patient/{id}")
	@DTO(PatientDTO.class)
	Patient updatePatient(@PathVariable String userId, @RequestBody Patient patient) {
		try {
			this.logger.debug(patient.toString());
			this.patientManager.updatePatient(patient);
			return patient;
		} catch (OHServiceException e) {
			this.logger.error("Update patient error:", e);
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
	
	@GetMapping("/patient")
	@DTO(PatientDTO.class)
    public List<Patient> getPatients(
    		@RequestParam(value="start", required=false) Integer start,
    		@RequestParam(value="hits", required=false) Integer hits) {
        try {
        	return this.patientManager.getPatient();
		} catch (OHServiceException e) {
			this.logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }
	
	@GetMapping("/patient/{code}")
	@DTO(PatientDTO.class)
    public Patient getPatient(@PathVariable Integer code) {
        try {
        	return this.patientManager.getPatient(code);
		} catch (OHServiceException e) {
			this.logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }

	@GetMapping("/patient/search")
	@DTO(PatientDTO.class)
    public Patient searchPatient(
    		@RequestParam(value="name", defaultValue="") String name, 
    		@RequestParam(value="code", required=false) Integer code) {
        try {
        	Patient result = null;
        	if(code != null) {
        		result = this.patientManager.getPatient(code);
        	}else if (!name.equals("")) {
        		result = this.patientManager.getPatient(name);
        	}
        	return result;
		} catch (OHServiceException e) {
			this.logger.error("Get patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Get Patient failed", e);
		}
    }

	@DeleteMapping("/patient/{code}")
	@DTO(PatientDTO.class)
	public void deletePatient(@PathVariable int code) {
		try {
			if(this.patientManager.getPatient(code) != null) {
				Patient p = new Patient();
				p.setCode(code);
				this.patientManager.deletePatient(p);
			}
		} catch (OHServiceException e) {
			this.logger.error("Delete patient error:", e.getCause());
			for(OHExceptionMessage emsg: e.getMessages()) {
				this.logger.error(emsg.getMessage());
			}
			throw new ResponseStatusException(
		          HttpStatus.INTERNAL_SERVER_ERROR, "Delete Patient failed", e);
		}
	}
}
