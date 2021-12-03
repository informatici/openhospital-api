package org.isf.therapy.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medical.mapper.MedicalMapper;
import org.isf.medicals.model.Medical;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.therapy.dto.TherapyDTO;
import org.isf.therapy.dto.TherapyRowDTO;
import org.isf.therapy.manager.TherapyManager;
import org.isf.therapy.mapper.TherapyMapper;
import org.isf.therapy.mapper.TherapyRowMapper;
import org.isf.therapy.model.Therapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/therapies", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class TherapyController {
	private final Logger logger = LoggerFactory.getLogger(TherapyController.class);
	@Autowired
	private TherapyManager manager;
	@Autowired
	private TherapyMapper therapyMapper;
	@Autowired
	private TherapyRowMapper therapyRowMapper;
	@Autowired
	private MedicalMapper medicalMapper;
	@Autowired
	private PatientMapper patientMapper;
	@Autowired
	protected PatientBrowserManager patientBrowserManager;
	
	/**
	 * Creates a new therapy for related Patient
	 * @param thRowDTO - the therapy
	 * @return the created therapy
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/therapies", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TherapyRowDTO> newTherapy(@RequestBody TherapyRowDTO thRowDTO) throws OHServiceException {

		Patient patient = patientBrowserManager.getPatient(thRowDTO.getPatID().getCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}
		thRowDTO.setPatID(patientMapper.map2DTO(patient));

		TherapyRow thRow = therapyRowMapper.map2Model(thRowDTO);
		thRow = manager.newTherapy(thRow);
		return ResponseEntity.status(HttpStatus.CREATED).body(therapyRowMapper.map2DTO(thRow));
	}
	
	/**
	 * Replaces all therapies for related Patient
	 * @param thRowDTOs - the list of therapies
	 * @return <code>true</code> if the rows has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/therapies/replace", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> replaceTherapies(@RequestBody @Valid List<TherapyRowDTO> thRowDTOs) throws OHServiceException {
		ArrayList<TherapyRow> therapies = (ArrayList<TherapyRow>)therapyRowMapper.map2ModelList(thRowDTOs);
		boolean done = manager.newTherapies(therapies);
		if(done) {
			return ResponseEntity.status(HttpStatus.CREATED).body(done);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Therapies are not replaced!", OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * Deletes all therapies for specified Patient Code 
	 * @param code - the Patient Code
	 * @return <code>true</code> if the therapies have been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/therapies/{code_patient}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteAllTherapies(@PathVariable("code_patient") Integer code) throws OHServiceException {
		boolean done = manager.deleteAllTherapies(code);
		if(done) {
			return ResponseEntity.ok(done);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Therapies are not deleted!", OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * Gets the medicals that are not available for the specified list of therapies
	 * @param therapyDTOs - the list of therapies
	 * @return the list of medicals out of stock
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/therapies/meds-out-of-stock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalDTO>> getMedicalsOutOfStock(@RequestBody List<TherapyDTO> therapyDTOs) throws OHServiceException {
		ArrayList<Therapy> therapyRows = (ArrayList<Therapy>) therapyMapper.map2ModelList(therapyDTOs);
		ArrayList<Medical> meds = manager.getMedicalsOutOfStock(therapyRows);
		List<MedicalDTO> mappedMeds = medicalMapper.map2DTOList(meds);
		if(mappedMeds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMeds);
		} else {
			logger.info("Found " + mappedMeds.size() + " medicals");
			return ResponseEntity.ok(mappedMeds);
		}
	}
	
	/**
	 * Gets the list of therapies for specified Patient ID
	 * @param patientID - the Patient ID
	 * @return the list of therapies of the patient or all the therapies if <code>0</code> is passed
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/therapies/{code_patient}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TherapyRowDTO>> getTherapyRows(@PathVariable("code_patient") Integer patientID) throws OHServiceException {
		List<TherapyRow> thRows = manager.getTherapyRows(patientID);
		List<TherapyRowDTO> mappedThRows = therapyRowMapper.map2DTOList(thRows);
		if(mappedThRows.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedThRows);
		} else {
			logger.info("Found " + mappedThRows.size() + " therapies");
			return ResponseEntity.ok(mappedThRows);
		}
	}
	
	/**
	 * Gets a list of therapies from a list of therapyRows (DB records)
	 * @param thRowDTOs - the list of therapyRows
	 * @return the list of therapies
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/therapies/from-rows", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TherapyDTO>> getTherapies(@RequestBody @Valid List<TherapyRowDTO> thRowDTOs) throws OHServiceException {
		ArrayList<TherapyRow> thRows = (ArrayList<TherapyRow>) therapyRowMapper.map2ModelList(thRowDTOs);
		List<Therapy> therapies = manager.getTherapies(thRows);
		List<TherapyDTO> mappedTherapies = therapies != null? therapyMapper.map2DTOList(therapies) : null;
		if(mappedTherapies == null || mappedTherapies.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedTherapies);
		} else {
			logger.info("Found " + mappedTherapies.size() + " therapies");
			return ResponseEntity.ok(mappedTherapies);
		}
	}
	
	/**
	 * Gets therapy from a therapyRow (DB record)
	 * @param thRowDTO - the therapyRow
	 * @return the therapy
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/therapies/from-row", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TherapyDTO> getTherapy(@RequestBody @Valid TherapyRowDTO thRowDTO) throws OHServiceException {
		TherapyRow thRow = therapyRowMapper.map2Model(thRowDTO);
		TherapyDTO mappedTherapy = therapyMapper.map2DTO(manager.createTherapy(thRow));
		return ResponseEntity.ok(mappedTherapy);
	}
}
