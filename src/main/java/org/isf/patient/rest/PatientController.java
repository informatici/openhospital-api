package org.isf.patient.rest;

import java.util.List;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.responsebodyadvice.DTO;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
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

import io.swagger.annotations.Api;


@RestController
@Api(value="/patients",produces ="application/vnd.ohapi.app-v1+json")
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
	Patient newPatient(@RequestBody Patient newPatient) throws OHServiceException {
		logger.debug(newPatient.toString());
		patientManager.newPatient(newPatient);
		return newPatient;
	}

	@PutMapping(value = "/patients/{id}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	Patient updatePatient(@PathVariable String userId, @RequestBody Patient patient) throws OHServiceException {
		logger.debug(patient.toString());
		patientManager.updatePatient(patient);
		return patient;
	}

	@GetMapping(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	public List<Patient> getPatients(
			@RequestParam(value="page", required=false, defaultValue="0") Integer page,
			@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
		return patientManager.getPatient(page, size);
	}

	@GetMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	public Patient getPatient(@PathVariable Integer code) throws OHServiceException {
		Patient patient = patientManager.getPatient(code);
		if (patient == null) {
			throw new OHAPIException(
				new OHExceptionMessage("Not Found", "Patient with code " + code + " not found", OHSeverityLevel.ERROR),
				HttpStatus.NOT_FOUND
			);
		}
		return patient;
	}


	@GetMapping(value = "/patients/search", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	public Patient searchPatient(
			@RequestParam(value="name", defaultValue="") String name, 
			@RequestParam(value="code", required=false) Integer code) throws OHServiceException {
		Patient result = null;
		if(code != null) {
			result = patientManager.getPatient(code);
		}else if (!name.equals("")) {
			result = patientManager.getPatient(name);
		}
		return result;
	}

	@DeleteMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@DTO(PatientDTO.class)
	public void deletePatient(@PathVariable int code) throws OHServiceException {
		if(patientManager.getPatient(code) != null) {
			Patient p = new Patient();
			p.setCode(code);
			patientManager.deletePatient(p);
		}
	}
}
