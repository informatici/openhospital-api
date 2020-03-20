package org.isf.admission.rest;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.admission.dto.AdmissionCUDTO;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.dto.AdmissionSimpleDTO;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
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
	private AdmissionTypeBrowserManager admtManager;

	@Autowired
	private DiseaseBrowserManager diseaseManager;

	@Autowired
	private OperationBrowserManager operationManager;

	@Autowired
	private DischargeTypeBrowserManager disTypeManager;

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
	
	@PostMapping(value = "/admissions", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> newAdmissions(@Valid @RequestBody AdmissionCUDTO newAdmissionCUDTO)
			throws OHServiceException {

		Admission newAdmission = getObjectMapper().map(newAdmissionCUDTO.getAdmissionSimpleDTO(), Admission.class);

		Patient patient = patientManager.getPatient(newAdmissionCUDTO.getPatientId());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
		}
		newAdmission.setPatient(patient);

		List<Ward> wards = wardManager.getWards().stream()
				.filter(w -> w.getCode().equals(newAdmissionCUDTO.getWardCode())).collect(Collectors.toList());
		if (wards.size() == 0) {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
		}
		newAdmission.setWard(wards.get(0));

		List<AdmissionType> types = admtManager.getAdmissionType().stream()
				.filter(admt -> admt.getCode().equals(newAdmissionCUDTO.getAdmissionTypeCode()))
				.collect(Collectors.toList());
		if (types.size() == 0) {
			throw new OHAPIException(new OHExceptionMessage(null, "Admission type not found!", OHSeverityLevel.ERROR));
		}
		newAdmission.setAdmType(types.get(0));

		List<Disease> diseases = null;
		if (newAdmissionCUDTO.getDiseaseInCode() != null) {
			diseases = diseaseManager.getDisease();
			List<Disease> dIns = diseases.stream()
					.filter(d -> d.getCode().equals(newAdmissionCUDTO.getDiseaseInCode().toString()))
					.collect(Collectors.toList());
			if (dIns.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease in not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseIn(dIns.get(0));
		}

		if (newAdmissionCUDTO.getDiseaseOut1Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut1s = diseases.stream()
					.filter(d -> d.getCode().equals(newAdmissionCUDTO.getDiseaseOut1Code().toString()))
					.collect(Collectors.toList());
			if (dOut1s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 1 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut1(dOut1s.get(0));
		}

		if (newAdmissionCUDTO.getDiseaseOut2Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut2s = diseases.stream()
					.filter(d -> d.getCode().equals(newAdmissionCUDTO.getDiseaseOut2Code().toString()))
					.collect(Collectors.toList());
			if (dOut2s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 2 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut2(dOut2s.get(0));
		}

		if (newAdmissionCUDTO.getDiseaseOut3Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut3s = diseases.stream()
					.filter(d -> d.getCode().equals(newAdmissionCUDTO.getDiseaseOut3Code().toString()))
					.collect(Collectors.toList());
			if (dOut3s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 3 not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDiseaseOut3(dOut3s.get(0));
		}

		if (newAdmissionCUDTO.getOperationCode() != null) {
			List<Operation> operations = operationManager.getOperation();
			List<Operation> opFounds = operations.stream()
					.filter(op -> op.getCode().equals(newAdmissionCUDTO.getOperationCode()))
					.collect(Collectors.toList());
			if (opFounds.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Operation not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setOperation(opFounds.get(0));
		}

		if (newAdmissionCUDTO.getDisTypeCode() != null) {
			List<DischargeType> disTypes = disTypeManager.getDischargeType();
			List<DischargeType> disTypesF = disTypes.stream()
					.filter(dtp -> dtp.getCode().equals(newAdmissionCUDTO.getDisTypeCode()))
					.collect(Collectors.toList());
			if (disTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Discharge type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDisType(disTypesF.get(0));
		}

		if (newAdmissionCUDTO.getPregTreatmentTypeCode() != null) {
			List<PregnantTreatmentType> pregTTypes = pregTraitTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTTypesF = pregTTypes.stream()
					.filter(pregtt -> pregtt.getCode().equals(newAdmissionCUDTO.getPregTreatmentTypeCode()))
					.collect(Collectors.toList());
			if (pregTTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Pregnant treatment type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setPregTreatmentType(pregTTypesF.get(0));
		}

		if (newAdmissionCUDTO.getDeliveryTypeCode() != null) {
			List<DeliveryType> dlvrTypes = dlvrTypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypesF = dlvrTypes.stream()
					.filter(dlvrType -> dlvrType.getCode().equals(newAdmissionCUDTO.getDeliveryTypeCode()))
					.collect(Collectors.toList());
			if (dlvrTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
			}
			newAdmission.setDeliveryType(dlvrTypesF.get(0));
		}

		if (newAdmissionCUDTO.getDeliveryResultCode() != null) {
			List<DeliveryResultType> dlvrrestTypes = dlvrrestTypeManager.getDeliveryResultType();
			List<DeliveryResultType> dlvrrestTypesF = dlvrrestTypes.stream()
					.filter(dlvrrestType -> dlvrrestType.getCode().equals(newAdmissionCUDTO.getDeliveryResultCode()))
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
	 * Nested objects are only replaced if a new value has been sent.
	 * 
	 * @param updAdmissionCUDTO
	 * @param id
	 * @return
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/admissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> updateAdmissions(@RequestBody AdmissionCUDTO updAdmissionCUDTO, @PathVariable int id)
			throws OHServiceException {
		Admission old = admissionManager.getAdmission(id);
		if (old == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Admission not found!", OHSeverityLevel.ERROR));
		}
		Admission updAdmission = manualMap(old, updAdmissionCUDTO.getAdmissionSimpleDTO());

		if (updAdmissionCUDTO.getPatientId() != null) {
			Patient patient = patientManager.getPatient(updAdmissionCUDTO.getPatientId());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setPatient(patient);
		}

		if (updAdmissionCUDTO.getWardCode() != null) {
			List<Ward> wards = wardManager.getWards().stream()
					.filter(w -> w.getCode().equals(updAdmissionCUDTO.getWardCode())).collect(Collectors.toList());
			if (wards.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setWard(wards.get(0));
		}

		if (updAdmissionCUDTO.getAdmissionTypeCode() != null) {
			List<AdmissionType> types = admtManager.getAdmissionType().stream()
					.filter(admt -> admt.getCode().equals(updAdmissionCUDTO.getAdmissionTypeCode()))
					.collect(Collectors.toList());
			if (types.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Admission type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setAdmType(types.get(0));
		}

		List<Disease> diseases = null;
		if (updAdmissionCUDTO.getDiseaseInCode() != null) {
			diseases = diseaseManager.getDisease();
			List<Disease> dIns = diseases.stream()
					.filter(d -> d.getCode().equals(updAdmissionCUDTO.getDiseaseInCode().toString()))
					.collect(Collectors.toList());
			if (dIns.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Disease in not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseIn(dIns.get(0));
		}

		if (updAdmissionCUDTO.getDiseaseOut1Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut1s = diseases.stream()
					.filter(d -> d.getCode().equals(updAdmissionCUDTO.getDiseaseOut1Code().toString()))
					.collect(Collectors.toList());
			if (dOut1s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 1 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut1(dOut1s.get(0));
		}

		if (updAdmissionCUDTO.getDiseaseOut2Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut2s = diseases.stream()
					.filter(d -> d.getCode().equals(updAdmissionCUDTO.getDiseaseOut2Code().toString()))
					.collect(Collectors.toList());
			if (dOut2s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 2 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut2(dOut2s.get(0));
		}

		if (updAdmissionCUDTO.getDiseaseOut3Code() != null) {
			if (diseases == null)
				diseases = diseaseManager.getDisease();
			List<Disease> dOut3s = diseases.stream()
					.filter(d -> d.getCode().equals(updAdmissionCUDTO.getDiseaseOut3Code().toString()))
					.collect(Collectors.toList());
			if (dOut3s.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Disease out 3 not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDiseaseOut3(dOut3s.get(0));
		}

		if (updAdmissionCUDTO.getOperationCode() != null) {
			List<Operation> operations = operationManager.getOperation();
			List<Operation> opFounds = operations.stream()
					.filter(op -> op.getCode().equals(updAdmissionCUDTO.getOperationCode()))
					.collect(Collectors.toList());
			if (opFounds.size() == 0) {
				throw new OHAPIException(new OHExceptionMessage(null, "Operation not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setOperation(opFounds.get(0));
		}

		if (updAdmissionCUDTO.getDisTypeCode() != null) {
			List<DischargeType> disTypes = disTypeManager.getDischargeType();
			List<DischargeType> disTypesF = disTypes.stream()
					.filter(dtp -> dtp.getCode().equals(updAdmissionCUDTO.getDisTypeCode()))
					.collect(Collectors.toList());
			if (disTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Discharge type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDisType(disTypesF.get(0));
		}

		if (updAdmissionCUDTO.getPregTreatmentTypeCode() != null) {
			List<PregnantTreatmentType> pregTTypes = pregTraitTypeManager.getPregnantTreatmentType();
			List<PregnantTreatmentType> pregTTypesF = pregTTypes.stream()
					.filter(pregtt -> pregtt.getCode().equals(updAdmissionCUDTO.getPregTreatmentTypeCode()))
					.collect(Collectors.toList());
			if (pregTTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Pregnant treatment type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setPregTreatmentType(pregTTypesF.get(0));
		}

		if (updAdmissionCUDTO.getDeliveryTypeCode() != null) {
			List<DeliveryType> dlvrTypes = dlvrTypeManager.getDeliveryType();
			List<DeliveryType> dlvrTypesF = dlvrTypes.stream()
					.filter(dlvrType -> dlvrType.getCode().equals(updAdmissionCUDTO.getDeliveryTypeCode()))
					.collect(Collectors.toList());
			if (dlvrTypesF.size() == 0) {
				throw new OHAPIException(
						new OHExceptionMessage(null, "Delivery type not found!", OHSeverityLevel.ERROR));
			}
			updAdmission.setDeliveryType(dlvrTypesF.get(0));
		}

		if (updAdmissionCUDTO.getDeliveryResultCode() != null) {
			List<DeliveryResultType> dlvrrestTypes = dlvrrestTypeManager.getDeliveryResultType();
			List<DeliveryResultType> dlvrrestTypesF = dlvrrestTypes.stream()
					.filter(dlvrrestType -> dlvrrestType.getCode().equals(updAdmissionCUDTO.getDeliveryResultCode()))
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

	private Admission manualMap(Admission admission, AdmissionSimpleDTO dto) {
		if (dto.getAbortDate() != null) {
			GregorianCalendar abDate = new GregorianCalendar();
			abDate.setTime(dto.getAbortDate());
			admission.setAbortDate(abDate);
		}
		if (dto.getAdmDate() != null) {
			GregorianCalendar abmDate = new GregorianCalendar();
			abmDate.setTime(dto.getAdmDate());
			admission.setAdmDate(abmDate);
		}
		admission.setAdmitted(dto.getAdmitted());
		if (dto.getCtrlDate1() != null) {
			GregorianCalendar ctrlDate1 = new GregorianCalendar();
			ctrlDate1.setTime(dto.getCtrlDate1());
			admission.setCtrlDate1(ctrlDate1);
		}
		if (dto.getCtrlDate2() != null) {
			GregorianCalendar ctrlDate2 = new GregorianCalendar();
			ctrlDate2.setTime(dto.getCtrlDate2());
			admission.setCtrlDate2(ctrlDate2);
		}
		if (dto.getDeliveryDate() != null) {
			GregorianCalendar dlvDate = new GregorianCalendar();
			dlvDate.setTime(dto.getDeliveryDate());
			admission.setDeliveryDate(dlvDate);
		}
		if (dto.getDisDate() != null) {
			GregorianCalendar disDate = new GregorianCalendar();
			disDate.setTime(dto.getDisDate());
			admission.setDisDate(disDate);
		}
		admission.setFHU(dto.getFHU());
		admission.setLock(dto.getLock());
		admission.setNote(dto.getNote());
		if (dto.getOpDate() != null) {
			GregorianCalendar opDate = new GregorianCalendar();
			opDate.setTime(dto.getOpDate());
			admission.setOpDate(opDate);
		}
		admission.setOpResult(dto.getOpResult());
		admission.setTransUnit(dto.getTransUnit());
		admission.setType(dto.getType());
		admission.setUserID(dto.getUserID());
		if (dto.getVisitDate() != null) {
			GregorianCalendar visitDate = new GregorianCalendar();
			visitDate.setTime(dto.getVisitDate());
			admission.setVisitDate(visitDate);
		}
		admission.setWeight(dto.getWeight());
		admission.setYProg(dto.getyProg());
		return admission;
	}
}
