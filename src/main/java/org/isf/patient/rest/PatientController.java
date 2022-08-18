/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
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

@RestController
@Api(value="/patients",produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatientController.class);

	protected static final String DEFAULT_PAGE_SIZE = "80";

	@Autowired
	protected PatientBrowserManager patientManager;
	
	@Autowired
	protected  AdmissionBrowserManager admissionBrowserManager = new AdmissionBrowserManager();

	@Autowired
	protected PatientMapper patientMapper;

	public PatientController(PatientBrowserManager patientManager, PatientMapper patientMapper) {
		this.patientManager = patientManager;
		this.patientMapper = patientMapper;
	}

    /**
     * Create new {@link Patient}.
     * @param newPatient
     * @return
     * @throws OHServiceException
     */
	@PostMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PatientDTO> newPatient(@RequestBody PatientDTO newPatient) throws OHServiceException {
        String name = StringUtils.isEmpty(newPatient.getName()) ? newPatient.getFirstName() + " " + newPatient.getSecondName() : newPatient.getName();
		LOGGER.info("Create patient {}", name);
		Patient patient = patientMapper.map2Model(newPatient);
        Patient pat = patientManager.savePatient(patient);
        if(pat == null){
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(patientMapper.map2DTO(patient));
	}

	@PutMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PatientDTO> updatePatient(@PathVariable int code, @RequestBody PatientDTO updatePatient) throws OHServiceException {
		LOGGER.info("Update patient code: {}", code);
		if (!updatePatient.getCode().equals(code)) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient code mismatch", OHSeverityLevel.ERROR));
		}
		Patient patientRead = patientManager.getPatientById(code);
		if (patientRead == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}
		Patient updatePatientModel = patientMapper.map2Model(updatePatient);
		updatePatientModel.setLock(patientRead.getLock());
		Patient patient = patientManager.savePatient(updatePatientModel);
		if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(patientMapper.map2DTO(patient));
	}

	@GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> getPatients(
			@RequestParam(value="page", required=false, defaultValue="0") Integer page,
			@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
		LOGGER.info("Get patients page: {}  size: {}", page, size);
		List<Patient> patients = patientManager.getPatient(page, size);
        List<PatientDTO> patientDTOS = patientMapper.map2DTOList(patients);
        if(patientDTOS.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patientDTOS);
        }else{
            return ResponseEntity.ok(patientDTOS);
        }
	}

	@GetMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientDTO> getPatient(@PathVariable("code") int code) throws OHServiceException {
		LOGGER.info("Get patient code: {}", code);
		Patient patient = patientManager.getPatientById(code);
		if (patient == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		Admission admission = admissionBrowserManager.getCurrentAdmission(patient);
		Boolean status = admission != null ? true : false;
		return ResponseEntity.ok(patientMapper.map2DTOWS(patient, status));
	}

	@GetMapping(value = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientDTO>> searchPatient(
			@RequestParam(value="firstName", defaultValue="", required = false) String firstName,
			@RequestParam(value="secondName", defaultValue="", required = false) String secondName,
			@RequestParam(value="birthDate", defaultValue="", required = false) String birthDate,
			@RequestParam(value="address", defaultValue="", required = false) String address
	) throws OHServiceException {

		List<PatientDTO> patientListDTO = new ArrayList<PatientDTO>();
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
		for(Patient patient : patientList) {
			Admission admission = admissionBrowserManager.getCurrentAdmission(patient);
			Boolean status = admission != null ? true : false;
			patientListDTO.add(patientMapper.map2DTOWS(patient, status));
		}
		
		return ResponseEntity.ok(patientListDTO);
	}

	@GetMapping(value = "/patients/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientDTO> getPatientAll(@RequestParam Integer code) throws OHServiceException {
		LOGGER.info("get patient for provided code even if logically deleted: {}", code);
        Patient patient = patientManager.getPatientAll(code);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(patientMapper.map2DTO(patient));
	}
	
	@GetMapping(value = "/patients/nextcode", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getPatientNextCode() throws OHServiceException {
        LOGGER.info("get patient next code");
        int nextCode = patientManager.getNextPatientCode();
        return ResponseEntity.ok(nextCode);
	}

	@DeleteMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePatient(@PathVariable int code) throws OHServiceException {
		LOGGER.info("Delete patient code: {}", code);
        Patient patient = patientManager.getPatientById(code);
        boolean isDeleted = false;
        if (patient != null) {
            isDeleted = patientManager.deletePatient(patient);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(isDeleted);
    }
	
	@GetMapping(value = "/patients/merge", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> mergePatients(@RequestParam Integer mergedcode, @RequestParam Integer code2) throws OHServiceException {
		LOGGER.info("merge patient for code {} in patient for code {}", code2, mergedcode);
        Patient mergedPatient = patientManager.getPatientById(mergedcode);
        Patient patient2 = patientManager.getPatientById(code2);
        if(mergedPatient == null || patient2 == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        boolean merged = patientManager.mergePatient(mergedPatient, patient2);
        if(!merged) {
        	throw new OHAPIException(new OHExceptionMessage(null, "Patients are not merged!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(merged);
	}
	
	@GetMapping(value = "/patients/cities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getPatientCities() throws OHServiceException {
		LOGGER.info("get all cities of patient");
        return ResponseEntity.ok(patientManager.getCities());
	}
}
