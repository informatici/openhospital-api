package org.isf.admission.rest;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.dto.AdmittedPatientDTO;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Api(value = "/admissions", produces = "application/vnd.ohapi.app-v1+json")
public class AdmissionController {

	@Autowired
	private AdmissionBrowserManager admissionManager;

	@Autowired
	private PatientBrowserManager patientManager;

	@Autowired
	private WardBrowserManager wardManager;

	@Autowired
	private DiseaseBrowserManager diseaseManager;

	@Autowired
	private OperationBrowserManager operationManager;

	@Autowired
	private PregnantTreatmentTypeBrowserManager pregTraitTypeManager;

	@Autowired
	private DeliveryTypeBrowserManager dlvrTypeManager;

	@Autowired
	private DeliveryResultTypeBrowserManager dlvrrestTypeManager;

	private final Logger logger = LoggerFactory.getLogger(AdmissionController.class);

	public AdmissionController(AdmissionBrowserManager admissionManager) {
		this.admissionManager = admissionManager;
	}

	/**
	 * Get {@link Admission} for the specified id.
	 * 
	 * @param id
	 * @return the {@link Admission} found or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdmissionDTO> getAdmissions(@PathVariable int id) throws OHServiceException {
		logger.info("Get admission by id:" + id);
		Admission admission = admissionManager.getAdmission(id);
		if (admission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		System.out.println("admissiontype code" + admission.getAdmType().getCode());
		return ResponseEntity.ok(getObjectMapper().map(admission, AdmissionDTO.class));
	}

	/**
	 * Get the only one admission without Admission date for the specified patient.
	 * 
	 * @param patientCode
	 * @return found {@link Admission}, N0_CONTENT if there is no {@link Admission}
	 *         found or message error.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions/current", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdmissionDTO> getCurrentAdmission(@RequestParam("patientcode") Integer patientCode)
			throws OHServiceException {
		logger.info("Get admission by patient code:" + patientCode);
		Patient patient = patientManager.getPatient(patientCode);
		if (patient == null)
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		Admission admission = admissionManager.getCurrentAdmission(patient);
		if (admission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(getObjectMapper().map(admission, AdmissionDTO.class));
	}

	/**
	 * get all admitted {@link Patient} based on the applied filters.
	 * @param searchTerms
	 * @param admissionRange
	 * @param dischargeRange
	 * @return the {@link List} of found {@link Patient} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions/admittedPatients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdmittedPatientDTO>> getAdmittedPatients(
			@RequestParam(name = "searchterms", defaultValue = "", required = false) String searchTerms,
			@RequestParam(name = "admissionrange", required = false) GregorianCalendar[] admissionRange,
			@RequestParam(name = "dischargerange", required = false) GregorianCalendar[] dischargeRange)
			throws OHServiceException {
		logger.info("Get admitted patients search terms:" + searchTerms);
		
		List<AdmittedPatientDTO> Amittedpatients = admissionManager
				.getAdmittedPatients(admissionRange, dischargeRange, searchTerms).stream()
				.map(admPt -> getObjectMapper().map(admPt, AdmittedPatientDTO.class)).collect(Collectors.toList());

		if (Amittedpatients.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		
		return ResponseEntity.ok(Amittedpatients);
	}

	/**
	 * get all the {@link Admission} for the specified {@link Patient} code.
	 * @param patientCode
	 * @return the {@link List} of found {@link Admission} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdmissionDTO> getPatientAdmissions(@RequestParam("patientcode") Integer patientCode)
			throws OHServiceException {
		logger.info("Get patient admissions by patient code:" + patientCode);
		Patient patient = patientManager.getPatient(patientCode);
		if (patient == null)
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		List<AdmissionDTO> admissionsDTOs = admissionManager.getAdmissions(patient).stream()
				.map(adm -> getObjectMapper().map(adm, AdmissionDTO.class)).collect(Collectors.toList());

		if (admissionsDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(admissionsDTOs.get(0));
	}

	/**
	 * get the next prog in the year for specified {@link Ward} code.
	 * @param wardId
	 * @return the next prog.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions/getNextProgressiveIdInYear", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getNextYProg(@RequestParam("wardcode") String wardCode)
			throws OHServiceException {
		logger.info("get the next prog in the year for ward code:" + wardCode);
		
		if (wardCode.trim().isEmpty() || !wardManager.codeControl(wardCode)) {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward not found for code:" + wardCode, OHSeverityLevel.ERROR));
		}
		
		return ResponseEntity.ok(admissionManager.getNextYProg(wardCode));
	}
	
	/**
	 * get the number of used bed for the specified {@link Ward} code.
	 * @param wardCode
	 * @return the number of used beds.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/admissions/getBedsOccupationInWard", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getUsedWardBed(@RequestParam("wardid") String wardCode) throws OHServiceException {
		logger.info("Counts the number of used bed for ward code:" + wardCode);

		if (wardCode.trim().isEmpty() || !wardManager.codeControl(wardCode)) {
			throw new OHAPIException( new OHExceptionMessage(null, "Ward not found for code:" + wardCode, OHSeverityLevel.ERROR));
		}

		return ResponseEntity.ok(admissionManager.getUsedWardBed(wardCode));
	}
	
	/**
	 * Set an {@link Admission} record to deleted.
	 * @param id
	 * @return <code>true</code> if the record has been set to delete.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/admissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteAdmissionType(@PathVariable int id) throws OHServiceException {
		logger.info("setting admission to deleted:" + id);
		boolean isDeleted = false;
		Admission admission = admissionManager.getAdmission(id);
		if (admission != null) {
			isDeleted = admissionManager.setDeleted(id);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

	/**
	 * Create a new {@link Admission}.
	 * @param newAdmissionDTO
	 * @return the generated id or <code>null</code> for the created {@link Admission}.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/admissions", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> newAdmissions(@Valid @RequestBody AdmissionDTO newAdmissionDTO)
			throws OHServiceException {

		Admission newAdmission = getObjectMapper().map(newAdmissionDTO, Admission.class);

		if (newAdmissionDTO.getWard() != null && newAdmissionDTO.getWard().getCode() != null
				&& !newAdmissionDTO.getWard().getCode().trim().isEmpty()) {
			List<Ward> wards = wardManager.getWards().stream()
					.filter(w -> w.getCode().equals(newAdmissionDTO.getWard().getCode())).collect(Collectors.toList());
			if (wards.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setWard(wards.get(0));
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward field is required!", OHSeverityLevel.ERROR));
		}

		if (newAdmissionDTO.getAdmType() != null && newAdmissionDTO.getAdmType().getCode() != null
				&& !newAdmissionDTO.getAdmType().getCode().trim().isEmpty()) {
			List<AdmissionType> types = admissionManager.getAdmissionType().stream()
					.filter(admt -> admt.getCode().equals(newAdmissionDTO.getAdmType().getCode()))
					.collect(Collectors.toList());
			if (types.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Admission type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setAdmType(types.get(0));
		} else {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Admition type field is required!", OHSeverityLevel.ERROR));
		}

		if (newAdmissionDTO.getPatient() != null && newAdmissionDTO.getPatient().getCode() != null) {
			Patient patient = patientManager.getPatient(newAdmissionDTO.getPatient().getCode());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setPatient(patient);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient field is required!", OHSeverityLevel.ERROR));
		}
		List<Disease> diseases = null;
		if (newAdmissionDTO.getDiseaseIn() != null && newAdmissionDTO.getDiseaseIn().getCode() > 0 ) {
			diseases = diseaseManager.getDisease();
			List<Disease> dIns = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == newAdmissionDTO.getDiseaseIn().getCode())
					.collect(Collectors.toList());
			if (dIns.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease in not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseIn(dIns.get(0));
		} 
		
		if (newAdmissionDTO.getDiseaseOut1() != null && newAdmissionDTO.getDiseaseOut1().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut1s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == newAdmissionDTO.getDiseaseOut1().getCode())
					.collect(Collectors.toList());
			if (dOut1s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 1 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut1(dOut1s.get(0));
		} 
		
		if (newAdmissionDTO.getDiseaseOut2() != null && newAdmissionDTO.getDiseaseOut2().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut2s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == newAdmissionDTO.getDiseaseOut2().getCode() )
					.collect(Collectors.toList());
			if (dOut2s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 2 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut2(dOut2s.get(0));
		} 
		
		if (newAdmissionDTO.getDiseaseOut3() != null && newAdmissionDTO.getDiseaseOut3().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut3s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == newAdmissionDTO.getDiseaseOut3().getCode())
					.collect(Collectors.toList());
			if (dOut3s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 3 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut3(dOut3s.get(0));
		} 
	
		if (newAdmissionDTO.getOperation() != null && newAdmissionDTO.getOperation().getCode() != null && !newAdmissionDTO.getOperation().getCode().trim().isEmpty()) {
			List<Operation> operations = operationManager.getOperation();
			List<Operation> opFounds = operations.stream()
					.filter(op -> op.getCode().equals(newAdmissionDTO.getOperation().getCode()))
					.collect(Collectors.toList());
			if (opFounds.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Operation not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setOperation(opFounds.get(0));
		}

		if (newAdmissionDTO.getDisType() != null && newAdmissionDTO.getDisType().getCode() != null && !newAdmissionDTO.getDisType().getCode().trim().isEmpty()) {
			List<DischargeType> disTypes = admissionManager.getDischargeType();
			List<DischargeType> disTypesF = disTypes.stream()
					.filter(dtp -> dtp.getCode().equals(newAdmissionDTO.getDisType().getCode()))
					.collect(Collectors.toList());
			if (disTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Discharge type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDisType(disTypesF.get(0));
		}

		if (newAdmissionDTO.getPregTreatmentType() != null && newAdmissionDTO.getPregTreatmentType().getCode() != null && !newAdmissionDTO.getPregTreatmentType().getCode().trim().isEmpty()) {
			List<PregnantTreatmentType> pregTTypes = pregTraitTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTTypesF = pregTTypes.stream()
					.filter(pregtt -> pregtt.getCode().equals(newAdmissionDTO.getPregTreatmentType().getCode()))
					.collect(Collectors.toList());
			if (pregTTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Pregnant treatment type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setPregTreatmentType(pregTTypesF.get(0));
		}

		if (newAdmissionDTO.getDeliveryType() != null && newAdmissionDTO.getDeliveryType().getCode() != null && !newAdmissionDTO.getDeliveryType().getCode().trim().isEmpty()) {
			List<DeliveryType> dlvrTypes = dlvrTypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypesF = dlvrTypes.stream()
					.filter(dlvrType -> dlvrType.getCode().equals(newAdmissionDTO.getDeliveryType().getCode()))
					.collect(Collectors.toList());
			if (dlvrTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDeliveryType(dlvrTypesF.get(0));
		}

		if (newAdmissionDTO.getDeliveryResult() != null && newAdmissionDTO.getDeliveryResult().getCode() != null && !newAdmissionDTO.getDeliveryResult().getCode().trim().isEmpty()) {
			List<DeliveryResultType> dlvrrestTypes = dlvrrestTypeManager.getDeliveryResultType();
			List<DeliveryResultType> dlvrrestTypesF = dlvrrestTypes.stream()
					.filter(dlvrrestType -> dlvrrestType.getCode().equals(newAdmissionDTO.getDeliveryResult().getCode()))
					.collect(Collectors.toList());
			if (dlvrrestTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery result type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDeliveryResult(dlvrrestTypesF.get(0));
		}

		String name = StringUtils.isEmpty(newAdmission.getPatient().getName())
				? newAdmission.getPatient().getFirstName() + " " + newAdmission.getPatient().getSecondName()
				: newAdmission.getPatient().getName();
		logger.info("Create admission for patient " + name);
		Integer aId = admissionManager.newAdmissionReturnKey(newAdmission);
		if (aId != null && aId.intValue() > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(aId);
		}
		throw new OHAPIException(new OHExceptionMessage(null, "Admission is not created!", OHSeverityLevel.ERROR));
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param updAdmissionCUDTO
	 * @param id
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/admissions", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> updateAdmissions(@RequestBody AdmissionDTO updAdmissionDTO) throws OHServiceException {
		
		Admission old = admissionManager.getAdmission(updAdmissionDTO.getId());
		if (old == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Admission not found!", OHSeverityLevel.ERROR));
		}
		Admission updAdmission = getObjectMapper().map(updAdmissionDTO, Admission.class);

		if (updAdmissionDTO.getWard() != null && updAdmissionDTO.getWard().getCode() != null
				&& !updAdmissionDTO.getWard().getCode().trim().isEmpty()) {
			List<Ward> wards = wardManager.getWards().stream()
					.filter(w -> w.getCode().equals(updAdmissionDTO.getWard().getCode())).collect(Collectors.toList());
			if (wards.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setWard(wards.get(0));
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward field is required!", OHSeverityLevel.ERROR));
		}

		if (updAdmissionDTO.getAdmType() != null && updAdmissionDTO.getAdmType().getCode() != null
				&& !updAdmissionDTO.getAdmType().getCode().trim().isEmpty()) {
			List<AdmissionType> types = admissionManager.getAdmissionType().stream()
					.filter(admt -> admt.getCode().equals(updAdmissionDTO.getAdmType().getCode()))
					.collect(Collectors.toList());
			if (types.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Admission type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setAdmType(types.get(0));
		} else {
			throw new OHAPIException(
					new OHExceptionMessage(null, "Admition type field is required!", OHSeverityLevel.ERROR));
		}

		if (updAdmissionDTO.getPatient() != null && updAdmissionDTO.getPatient().getCode() != null) {
			Patient patient = patientManager.getPatient(updAdmissionDTO.getPatient().getCode());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setPatient(patient);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient field is required!", OHSeverityLevel.ERROR));
		}
		List<Disease> diseases = null;
		if (updAdmissionDTO.getDiseaseIn() != null && updAdmissionDTO.getDiseaseIn().getCode() > 0 ) {
			diseases = diseaseManager.getDisease();
			List<Disease> dIns = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == updAdmissionDTO.getDiseaseIn().getCode())
					.collect(Collectors.toList());
			if (dIns.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease in not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseIn(dIns.get(0));
		} 
		
		if (updAdmissionDTO.getDiseaseOut1() != null && updAdmissionDTO.getDiseaseOut1().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut1s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == updAdmissionDTO.getDiseaseOut1().getCode())
					.collect(Collectors.toList());
			if (dOut1s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 1 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut1(dOut1s.get(0));
		} 
		
		if (updAdmissionDTO.getDiseaseOut2() != null && updAdmissionDTO.getDiseaseOut2().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut2s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == updAdmissionDTO.getDiseaseOut2().getCode() )
					.collect(Collectors.toList());
			if (dOut2s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 2 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut2(dOut2s.get(0));
		} 
		
		if (updAdmissionDTO.getDiseaseOut3() != null && updAdmissionDTO.getDiseaseOut3().getCode() > 0) {
			if (diseases == null) diseases = diseaseManager.getDisease();
			List<Disease> dOut3s = diseases.stream()
					.filter(d -> Integer.parseInt(d.getCode())  == updAdmissionDTO.getDiseaseOut3().getCode())
					.collect(Collectors.toList());
			if (dOut3s.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease out 3 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut3(dOut3s.get(0));
		} 
	
		if (updAdmissionDTO.getOperation() != null && updAdmissionDTO.getOperation().getCode() != null && !updAdmissionDTO.getOperation().getCode().trim().isEmpty()) {
			List<Operation> operations = operationManager.getOperation();
			List<Operation> opFounds = operations.stream()
					.filter(op -> op.getCode().equals(updAdmissionDTO.getOperation().getCode()))
					.collect(Collectors.toList());
			if (opFounds.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Operation not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setOperation(opFounds.get(0));
		}

		if (updAdmissionDTO.getDisType() != null && updAdmissionDTO.getDisType().getCode() != null && !updAdmissionDTO.getDisType().getCode().trim().isEmpty()) {
			List<DischargeType> disTypes = admissionManager.getDischargeType();
			List<DischargeType> disTypesF = disTypes.stream()
					.filter(dtp -> dtp.getCode().equals(updAdmissionDTO.getDisType().getCode()))
					.collect(Collectors.toList());
			if (disTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Discharge type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDisType(disTypesF.get(0));
		}

		if (updAdmissionDTO.getPregTreatmentType() != null && updAdmissionDTO.getPregTreatmentType().getCode() != null && !updAdmissionDTO.getPregTreatmentType().getCode().trim().isEmpty()) {
			List<PregnantTreatmentType> pregTTypes = pregTraitTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTTypesF = pregTTypes.stream()
					.filter(pregtt -> pregtt.getCode().equals(updAdmissionDTO.getPregTreatmentType().getCode()))
					.collect(Collectors.toList());
			if (pregTTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Pregnant treatment type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setPregTreatmentType(pregTTypesF.get(0));
		}

		if (updAdmissionDTO.getDeliveryType() != null && updAdmissionDTO.getDeliveryType().getCode() != null && !updAdmissionDTO.getDeliveryType().getCode().trim().isEmpty()) {
			List<DeliveryType> dlvrTypes = dlvrTypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypesF = dlvrTypes.stream()
					.filter(dlvrType -> dlvrType.getCode().equals(updAdmissionDTO.getDeliveryType().getCode()))
					.collect(Collectors.toList());
			if (dlvrTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDeliveryType(dlvrTypesF.get(0));
		}

		if (updAdmissionDTO.getDeliveryResult() != null && updAdmissionDTO.getDeliveryResult().getCode() != null && !updAdmissionDTO.getDeliveryResult().getCode().trim().isEmpty()) {
			List<DeliveryResultType> dlvrrestTypes = dlvrrestTypeManager.getDeliveryResultType();
			List<DeliveryResultType> dlvrrestTypesF = dlvrrestTypes.stream()
					.filter(dlvrrestType -> dlvrrestType.getCode().equals(updAdmissionDTO.getDeliveryResult().getCode()))
					.collect(Collectors.toList());
			if (dlvrrestTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery result type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDeliveryResult(dlvrrestTypesF.get(0));
		}

		String name = StringUtils.isEmpty(updAdmission.getPatient().getName())
				? updAdmission.getPatient().getFirstName() + " " + updAdmission.getPatient().getSecondName()
				: updAdmission.getPatient().getName();
		logger.info("update admission for patient " + name);
		boolean isUpdated = admissionManager.updateAdmission(updAdmission);
		if (isUpdated) {
			return ResponseEntity.ok(updAdmission.getId());
		}
		throw new OHAPIException(new OHExceptionMessage(null, "Admission is not updated!", OHSeverityLevel.ERROR));
	}


}
