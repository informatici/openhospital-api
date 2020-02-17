package org.isf.patient.rest;

import java.util.List;

import javax.validation.Valid;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.controller.SuccessDto;
import org.isf.shared.exceptions.OHAPINotFoundException;
import org.isf.shared.responsebodyadvice.DTO;
import org.isf.utils.exception.OHServiceException;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
public class PatientController {

	private static final String DEFAULT_PAGE_SIZE = "80";

	private static final ModelMapper modelMapper = new ModelMapper();

	@Autowired
	protected PatientBrowserManager patientManager;

	private final Logger logger = LoggerFactory.getLogger(PatientController.class);

	@PostMapping(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.CREATED)
	@DTO(PatientDTO.class)
	public Patient newPatient(@RequestBody @Valid PatientDTO newPatient) throws OHServiceException {
		logger.debug(newPatient.toString());
		Patient patient = modelMapper.map(newPatient, Patient.class);
		patientManager.newPatient(patient);
		return patient;
	}

	@PutMapping(value = "/patients/{id}", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.OK)
	@DTO(PatientDTO.class)
	public Patient updatePatient(@PathVariable String userId, @RequestBody @Valid PatientDTO patient) throws OHServiceException {
		logger.debug(patient.toString());
		Patient updatePatient = modelMapper.map(patient, Patient.class);
		patientManager.updatePatient(updatePatient);
		return updatePatient;
	}

	@GetMapping(value = "/patients", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.OK)
	@DTO(PatientDTO.class)
	public List<Patient> getPatients(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
		return patientManager.getPatient(page, size);
	}

	@GetMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.OK)
	@DTO(PatientDTO.class)
	public Patient getPatient(@PathVariable Integer code) throws OHServiceException, OHAPINotFoundException {
		Patient patient = patientManager.getPatient(code);
		if (patient == null) {
			throw new OHAPINotFoundException("Patient not found");
		}
		return patient;
	}

	@GetMapping(value = "/patients/search", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.OK)
	@DTO(PatientDTO.class)
	public Patient searchPatient(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "code", required = false) Integer code) throws OHServiceException, OHAPINotFoundException {
		Patient patient = null;
		if (code != null) {
			patient = patientManager.getPatient(code);
		} else if (!name.equals("")) {
			patient = patientManager.getPatient(name);
		}
		if (patient == null) {
			throw new OHAPINotFoundException("Patient not found");
		}
		return patient;
	}

	@DeleteMapping(value = "/patients/{code}", produces = "application/vnd.ohapi.app-v1+json")
	@ResponseStatus(HttpStatus.OK)
	public SuccessDto deletePatient(@PathVariable int code) throws OHServiceException {
		if (patientManager.getPatient(code) != null) {
			Patient p = new Patient();
			p.setCode(code);
			patientManager.deletePatient(p);
		}
		return new SuccessDto();
	}
}
