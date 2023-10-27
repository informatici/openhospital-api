/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.examination.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.examination.dto.Ausculation;
import org.isf.examination.dto.Bowel;
import org.isf.examination.dto.Diurese;
import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.mapper.PatientExaminationMapper;
import org.isf.examination.model.PatientExamination;
import org.isf.generaldata.ExaminationParameters;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.pagination.Page;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/examinations")
@Tag(name = "Examinations")
@SecurityRequirement(name = "bearerAuth")
public class ExaminationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationController.class);

	@Autowired
    protected ExaminationBrowserManager examinationBrowserManager;

    @Autowired
    private PatientExaminationMapper patientExaminationMapper;

    @Autowired
    private PatientBrowserManager patientBrowserManager;

    public ExaminationController(ExaminationBrowserManager examinationBrowserManager, PatientExaminationMapper patientExaminationMapper, PatientBrowserManager patientBrowserManager) {
        this.examinationBrowserManager = examinationBrowserManager;
        this.patientExaminationMapper = patientExaminationMapper;
        this.patientBrowserManager = patientBrowserManager;
    }

    @PostMapping(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newPatientExamination(@RequestBody PatientExaminationDTO newPatientExamination) throws OHServiceException {
        Patient patient = patientBrowserManager.getPatientById(newPatientExamination.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage("Patient does not exist."));
        }
        validateExamination(newPatientExamination);
        PatientExamination patientExamination = patientExaminationMapper.map2Model(newPatientExamination);
        patientExamination.setPatient(patient);
        patientExamination.setPex_date(newPatientExamination.getPex_date());
        patientExamination.setPex_auscultation(newPatientExamination.getPex_auscultation().name());
        patientExamination.setPex_bowel_desc(newPatientExamination.getPex_bowel_desc().name());
        patientExamination.setPex_diuresis_desc(newPatientExamination.getPex_diuresis_desc().name());
        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PutMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> updateExamination(@PathVariable Integer id, @RequestBody PatientExaminationDTO dto) throws OHServiceException {
        if (dto.getPex_ID() != id) {
            throw new OHAPIException(new OHExceptionMessage("Patient examination id mismatch."));
        }
        if (examinationBrowserManager.getByID(id) == null) {
            throw new OHAPIException(new OHExceptionMessage("Patient examination not found."));
        }

        Patient patient = patientBrowserManager.getPatientById(dto.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage("Patient does not exist."));
        }
        validateExamination(dto);
        PatientExamination patientExamination = patientExaminationMapper.map2Model(dto);
        patientExamination.setPatient(patient);
        patientExamination.setPex_date(dto.getPex_date());
        patientExamination.setPex_auscultation(dto.getPex_auscultation().name());
        patientExamination.setPex_bowel_desc(dto.getPex_bowel_desc().name());
        patientExamination.setPex_diuresis_desc(dto.getPex_diuresis_desc().name());
        examinationBrowserManager.saveOrUpdate(patientExamination);

        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/examinations/defaultPatientExamination", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientExaminationDTO> getDefaultPatientExamination(@RequestParam Integer patId) throws OHServiceException {

        Patient patient = patientBrowserManager.getPatientById(patId);
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage("Patient does not exist."));
        }
        PatientExamination patientExamination = examinationBrowserManager.getDefaultPatientExamination(patient);
        PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(patientExamination);
		if (patientExaminationDTO == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(patientExaminationDTO);
		}
    }

    @GetMapping(value = "/examinations/fromLastPatientExamination/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientExaminationDTO> getFromLastPatientExamination(@PathVariable Integer id) throws OHServiceException {

		PatientExamination lastPatientExamination = examinationBrowserManager.getByID(id);
		PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(examinationBrowserManager.getFromLastPatientExamination(lastPatientExamination));
		if (patientExaminationDTO == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(patientExaminationDTO);
		}
	}

	@GetMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientExaminationDTO> getByID(@PathVariable Integer id) throws OHServiceException {

		PatientExamination patientExamination = examinationBrowserManager.getByID(id);

		if (patientExamination == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(patientExamination);
			return ResponseEntity.ok(patientExaminationDTO);

		}
	}

	@GetMapping(value = "/examinations/lastByPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PatientExaminationDTO> getLastByPatientId(@PathVariable Integer patId) throws OHServiceException {

		PatientExamination patientExamination = examinationBrowserManager.getLastByPatID(patId);

		if (patientExamination == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			PatientExaminationDTO patientExaminationDTO = patientExaminationMapper.map2DTO(patientExamination);
			return ResponseEntity.ok(patientExaminationDTO);
		}
	}

	@GetMapping(value = "/examinations/lastNByPatId", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<PatientExaminationDTO>> getLastNByPatID(@RequestParam Integer limit, @RequestParam Integer patId) throws OHServiceException {
		LOGGER.info("Get examinations limit: {}", limit);
		PagedResponse<PatientExamination> patientExaminationListPageable = examinationBrowserManager.getLastNByPatIDPageable(patId, limit);

		if (patientExaminationListPageable == null || patientExaminationListPageable.getData().isEmpty()) {
			LOGGER.info("The patient list is empty.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			Page<PatientExaminationDTO> patientExaminationPageableDTO = new Page<>();
			List<PatientExaminationDTO> patientExaminationDTO = patientExaminationMapper.map2DTOList(patientExaminationListPageable.getData());
			patientExaminationPageableDTO.setData(patientExaminationDTO);
			patientExaminationPageableDTO.setPageInfo(patientExaminationMapper.setParameterPageInfo(patientExaminationListPageable.getPageInfo()));
			return ResponseEntity.ok(patientExaminationPageableDTO);
		}
	}

	@GetMapping(value = "/examinations/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientExaminationDTO>> getByPatientId(@PathVariable Integer patId) throws OHServiceException {

		List<PatientExamination> patientExamination = examinationBrowserManager.getByPatID(patId);
		if (patientExamination == null || patientExamination.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			List<PatientExaminationDTO> listPatientExaminationDTO = patientExamination.stream().map(pat -> {
				return patientExaminationMapper.map2DTO(pat);
			}).collect(Collectors.toList());
			return ResponseEntity.ok(listPatientExaminationDTO);
		}
	}
	
    public void validateExamination(PatientExaminationDTO newPatientExamination) throws OHServiceException {
	ExaminationParameters.initialize();
		
	if (newPatientExamination.getPex_height() < ExaminationParameters.HEIGHT_MIN || newPatientExamination.getPex_height() > ExaminationParameters.HEIGHT_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("The size should be between "+ExaminationParameters.HEIGHT_MIN+" and "+ExaminationParameters.HEIGHT_MAX));
        }
        if (newPatientExamination.getPex_weight() < ExaminationParameters.WEIGHT_MIN || newPatientExamination.getPex_weight() > ExaminationParameters.WEIGHT_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("The weight should be between"+ExaminationParameters.WEIGHT_MIN+" and "+ExaminationParameters.WEIGHT_MAX));
        }
        if (newPatientExamination.getPex_ap_min() > newPatientExamination.getPex_ap_max() ) {
        	throw new OHAPIException(new OHExceptionMessage("The minimum blood pressure must be lower than the maximum blood pressure"));
        }
        if (newPatientExamination.getPex_ap_max() > ExaminationParameters.AP_MAX_INIT) {
        	throw new OHAPIException(new OHExceptionMessage("The maximum blood pressure must be lower than "+ExaminationParameters.AP_MAX_INIT));
        }
        if (newPatientExamination.getPex_hr() < ExaminationParameters.HR_MIN || newPatientExamination.getPex_hr() > ExaminationParameters.HR_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("Heart rate should be between "+ExaminationParameters.HR_MIN +" and "+ExaminationParameters.HR_MAX));
        }
        if (newPatientExamination.getPex_temp() < ExaminationParameters.TEMP_MIN || newPatientExamination.getPex_temp() > ExaminationParameters.TEMP_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("The temperature should be between "+ExaminationParameters.TEMP_MIN +" and "+ExaminationParameters.TEMP_MAX));
        }
        if (newPatientExamination.getPex_sat() < ExaminationParameters.SAT_MIN || newPatientExamination.getPex_temp() > ExaminationParameters.SAT_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("The saturation should be between "+ExaminationParameters.SAT_MIN+" and "+ExaminationParameters.SAT_MAX));
        }
        if (newPatientExamination.getPex_hgt() < ExaminationParameters.HGT_MIN || newPatientExamination.getPex_temp() > ExaminationParameters.HGT_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("HGT should be between "+ExaminationParameters.HGT_MIN+" and "+ExaminationParameters.HGT_MAX));
        }
        if (newPatientExamination.getPex_rr() < ExaminationParameters.RR_MIN || newPatientExamination.getPex_rr() > ExaminationParameters.RR_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("Respiratory rate should be between "+ExaminationParameters.RR_MIN+" and "+ExaminationParameters.RR_MAX));
        }
        if (newPatientExamination.getPex_diuresis() < ExaminationParameters.DIURESIS_MIN || newPatientExamination.getPex_diuresis() > ExaminationParameters.DIURESIS_MAX) {
        	throw new OHAPIException(new OHExceptionMessage("Diuresis should be between "+ExaminationParameters.DIURESIS_MIN+" and "+ExaminationParameters.DIURESIS_MAX));
        }
    }
}
