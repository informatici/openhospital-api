package org.isf.medicalhistory.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.medicalhistory.dto.MedicalHistoryDTO;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.mapper.MedicalHistoryMapper;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "MedicalHistory")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalHistoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalHistoryController.class);

	private final MedicalHistoryBrowsingManager manager;
	private final MedicalHistoryMapper mapper;

	public MedicalHistoryController(MedicalHistoryBrowsingManager manager, MedicalHistoryMapper mapper) {
		this.manager = manager;
		this.mapper = mapper;
	}

	@GetMapping("/medicalhistories")
	public List<MedicalHistoryDTO> getAll() throws OHServiceException {
		LOGGER.info("Get all medical histories");
		return mapper.map2DTOList(manager.getAll());
	}

	@GetMapping("/medicalhistories/{id}")
	public MedicalHistoryDTO getOne(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Get medical history {}", id);
		MedicalHistory mh = manager.getMedicalHistoryById(id);
		if (mh == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical history not found with id: " + id), HttpStatus.NOT_FOUND);
		}
		return mapper.map2DTO(mh);
	}

	@GetMapping("/medicalhistories/patient/{patientCode}")
	public List<MedicalHistoryDTO> getByPatientCode(@PathVariable Integer patientCode) throws OHServiceException {
		LOGGER.info("Get medical histories for patient code {}", patientCode);
		List<MedicalHistory> histories = manager.getMedicalHistoriesByPatientCode(patientCode);
		if (histories == null || histories.isEmpty()) {
			throw new OHAPIException(
				new OHExceptionMessage("No medical histories found for patient code: " + patientCode),
				HttpStatus.NOT_FOUND
			);
		}
		return mapper.map2DTOList(histories);
	}

	@PostMapping("/medicalhistories")
	public ResponseEntity<MedicalHistoryDTO> create(@Valid @RequestBody MedicalHistoryDTO dto) throws OHServiceException {
		LOGGER.info("Create medical history for patient {}", dto.getPatientId());
		MedicalHistory mh = mapper.map2Model(dto);
		MedicalHistoryDTO saved = mapper.map2DTO(manager.add(mh));
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PutMapping("/medicalhistories/{id}")
	public MedicalHistoryDTO update(@PathVariable Integer id, @Valid @RequestBody MedicalHistoryDTO dto) throws OHServiceException {
		LOGGER.info("Update medical history {}", id);
		MedicalHistory mh = manager.getMedicalHistoryById(id);
		if (mh == null) {
			throw new OHAPIException(new OHExceptionMessage("Medical history not found with id: " + id), HttpStatus.NOT_FOUND);
		}
		mh = mapper.map2Model(dto);
		mh.setId(id);
		return mapper.map2DTO(manager.update(mh));
	}
}
