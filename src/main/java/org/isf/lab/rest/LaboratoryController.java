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
package org.isf.lab.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.lab.dto.LabWithRowsDTO;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.dto.LaboratoryRowDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryForPrintMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.mapper.LaboratoryRowMapper;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.patient.dto.PatientSTATUS;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/laboratories")
@Tag(name = "Laboratories")
@SecurityRequirement(name = "bearerAuth")
public class LaboratoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryController.class);
	
	// TODO: to centralize
	protected static final String DEFAULT_PAGE_SIZE = "80";
	
	private static String draft = LaboratoryStatus.DRAFT.toString();
	
	private static String open = LaboratoryStatus.OPEN.toString();

	private static String deleted = LaboratoryStatus.DELETED.toString();
	
	private static String invalid = LaboratoryStatus.INVALID.toString();

	@Autowired
	protected LabManager laboratoryManager;

	@Autowired
	protected ExamBrowsingManager examManager;

	@Autowired
	private PatientBrowserManager patientBrowserManager;

	@Autowired
	private LaboratoryMapper laboratoryMapper;

	@Autowired
	private LaboratoryRowMapper laboratoryRowMapper;

	@Autowired
	private LaboratoryForPrintMapper laboratoryForPrintMapper;

	public LaboratoryController(LabManager laboratoryManager, PatientBrowserManager patientBrowserManager,
			ExamBrowsingManager examManager, LaboratoryMapper laboratoryMapper, LaboratoryRowMapper laboratoryRowMapper,
			LaboratoryForPrintMapper laboratoryForPrintMapper) {
		this.laboratoryManager = laboratoryManager;
		this.patientBrowserManager = patientBrowserManager;
		this.examManager = examManager;
		this.laboratoryMapper = laboratoryMapper;
		this.laboratoryRowMapper = laboratoryRowMapper;
		this.laboratoryForPrintMapper = laboratoryForPrintMapper;
	}

	/**
	 * Create a new {@link LaboratoryRowDTO}.
	 * 
	 * @param labWithRowsDTO
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newLaboratory(@RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {
		LOGGER.info("store exam with result");
		LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
		List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

		Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode()))
				.findFirst().orElse(null);
		if (exam == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam not found."));
		}

		Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
		labToInsert.setExam(exam);
		labToInsert.setPatient(patient);
		labToInsert.setLock(0);
		labToInsert.setInOutPatient(laboratoryDTO.getInOutPatient().toString());
		List<String> labRows = new ArrayList<>();
		if (labRow != null) {
			labRows = new ArrayList<>(labRow);
		}
		try {
			laboratoryManager.newLaboratory(labToInsert, labRows);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}
	
	/**
	 * Create a new {@link LaboratoryDTO}.
	 * 
	 * @param laboratoryDTO
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException
	 * @author Arnaud
	 */
	@PostMapping(value = "/laboratories/examRequest", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newExamRequest(@RequestBody LaboratoryDTO laboratoryDTO) throws OHServiceException {
		LOGGER.info("store exam request");

		Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode()))
				.findFirst().orElse(null);
		if (exam == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam not found."));
		}

		Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
		labToInsert.setExam(exam);
		labToInsert.setPatient(patient);
		labToInsert.setLock(0);
		labToInsert.setStatus(draft);
		labToInsert.setResult("");
		labToInsert.setInOutPatient(laboratoryDTO.getInOutPatient().toString());
		List<Laboratory> labList = laboratoryManager.getLaboratory(patient).stream()
				.filter(e -> e.getStatus().equals(LaboratoryStatus.DRAFT.toString())).collect(Collectors.toList());

		if (!(labList == null || labList.isEmpty())) {
			for (Laboratory lab : labList) {
				if (lab.getExam().equals(exam)) {
					throw new OHAPIException(new OHExceptionMessage("Exam Request already exists."));
				}
			}
		}

		try {
			laboratoryManager.newExamRequest(labToInsert);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}

	/**
	 * Create a {@link List} of {@link LaboratoryRowDTO}.
	 * 
	 * @param labsWithRows
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/laboratories/insertList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newLaboratory2(@RequestBody List<LabWithRowsDTO> labsWithRows)
			throws OHServiceException {
		LOGGER.info("store List of Exam with result");
		List<Laboratory> labsToInsert = new ArrayList<>();
		List<List<LaboratoryRow>> labsRowsToInsert = new ArrayList<>();

		for (LabWithRowsDTO labWithRowsDTO : labsWithRows) {
			LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
			Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
			if (patient == null) {
				throw new OHAPIException(new OHExceptionMessage("Patient not found."));
			}

			Exam exam = examManager.getExams().stream()
					.filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
			if (exam == null) {
				throw new OHAPIException(new OHExceptionMessage("Exam not found."));
			}

			Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
			labToInsert.setExam(exam);
			labToInsert.setPatient(patient);
			labToInsert.setInOutPatient(laboratoryDTO.getInOutPatient().toString());
			labsToInsert.add(labToInsert);

			if (labWithRowsDTO.getLaboratoryRowList() != null) {
				List<LaboratoryRow> labRowToInsert = new ArrayList<>();
				for (String rowDescription : labWithRowsDTO.getLaboratoryRowList()) {
					labRowToInsert
							.add(laboratoryRowMapper.map2Model(new LaboratoryRowDTO(rowDescription, laboratoryDTO)));
				}
				if (!labRowToInsert.isEmpty()) {
					labsRowsToInsert.add(labRowToInsert);
				}
			}
		}

		try {
			laboratoryManager.newLaboratory2(labsToInsert, labsRowsToInsert);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not created."));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}
	
	/**
	 * Updates the specified {@link LaboratoryRowDTO} object.
	 * 
	 * @param code
	 * @param labWithRowsDTO
	 * @return {@code true} if has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateLaboratory(@PathVariable Integer code,
			@RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {
		LOGGER.info("Update labWithRows code: {}", code);
		LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
		List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

		if (!code.equals(laboratoryDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory code mismatch."));
		}

		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		if (!labo.isPresent()) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not found."));
		}
		Laboratory lab = labo.get();
		if (lab.getStatus().equalsIgnoreCase(deleted) || lab.getStatus().equalsIgnoreCase(invalid)) {
			throw new OHAPIException(new OHExceptionMessage("This exam can not be update because its status is " + lab.getStatus().toUpperCase()));
		}
		Patient patient = patientBrowserManager.getPatientById(laboratoryDTO.getPatientCode());
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode()))
				.findFirst().orElse(null);
		if (exam == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam not found."));
		}

		Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
		labToInsert.setExam(exam);
		labToInsert.setPatient(patient);
		labToInsert.setInOutPatient(laboratoryDTO.getInOutPatient().toString());
		List<String> labRows = new ArrayList<>();
		if (labRow != null) {
			labRows = new ArrayList<>(labRow);
		}
		if (!laboratoryDTO.getResult().isEmpty()) {
			labToInsert.setStatus(LaboratoryStatus.DONE.toString());
		}

		try {
			laboratoryManager.updateLaboratory(labToInsert, labRows);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not updated."));
		}
		return ResponseEntity.ok(true);
	}
	
	/**
	 * Updates the specified {@link LaboratoryDTO} object.
	 * 
	 * @param code
	 * @param status
	 * @return {@code true} if has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 * @author Arnaud
	 */
	@PutMapping(value = "/laboratories/examRequest/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateExamRequest(@PathVariable Integer code, @RequestParam String status)
			throws OHServiceException {
		LOGGER.info("Update exam request code: {}", code);
		LaboratoryStatus stat = LaboratoryStatus.valueOf(status);
		if (stat != null) {
			try {
				laboratoryManager.updateExamRequest(code, status);
			} catch (OHServiceException serviceException) {
				throw new OHAPIException(new OHExceptionMessage("Laboratory not updated."));
			}
			return ResponseEntity.ok(true);
		} else {
			throw new OHAPIException(new OHExceptionMessage("This status doesn't exist."));
		}
		
	}
	
	/**
	 * Set an {@link LaboratoryDTO} record to be deleted.
	 * 
	 * @param code
	 * @return {@code true} if the record has been set to delete, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteExam(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Delete Exam code: {}", code);
		Optional<Laboratory> lab = laboratoryManager.getLaboratory(code);
		if (lab.isPresent()) {
			Laboratory labToDelete = lab.get();
			if (labToDelete.getStatus().equalsIgnoreCase(deleted)) {
				throw new OHAPIException(new OHExceptionMessage("This exam can not be deleted because its status is " + labToDelete.getStatus().toUpperCase()));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		try {
			laboratoryManager.updateExamRequest(code, deleted);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam is not deleted."));
		}
		return ResponseEntity.ok(true);
	}

	/**
	 * Get the list of exams {@link LaboratoryRowDTO}s divided by pages.
	 * 
	 * @param oneWeek
	 * @param page
	 * @param size
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<LabWithRowsDTO>> getLaboratory(@RequestParam boolean oneWeek, @RequestParam int page, @RequestParam int size) throws OHServiceException {
		LOGGER.info("Get all LabWithRows");
		PagedResponse<Laboratory> labListPageable = laboratoryManager.getLaboratoryPageable(oneWeek, page, size);
		if (labListPageable == null || labListPageable.getData().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		List<LabWithRowsDTO> labWithRowsDto = labListPageable.getData().stream().map(lab -> {
			LabWithRowsDTO labDTO = new LabWithRowsDTO();
			List<String> labDescription = new ArrayList<>();
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			if (lab.getExam().getProcedure() == 2) {
				List<LaboratoryRow> labDes = new ArrayList<>();
				try {
					labDes = laboratoryManager.getLaboratoryRowList(lab.getCode());
				} catch (OHServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!labDes.isEmpty()) {
					for (LaboratoryRow laboratoryRow : labDes) {
						labDescription.add(laboratoryRow.getDescription());
					}
				}

			}
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
			labDTO.setLaboratoryDTO(laboratoryDTO);
			labDTO.setLaboratoryRowList(labDescription);
			return labDTO;
		}).collect(Collectors.toList());
		Page<LabWithRowsDTO> labWithRowsDtoPageable = new Page<>();
		labWithRowsDtoPageable.setPageInfo(laboratoryMapper.setParameterPageInfo(labListPageable.getPageInfo()));
		labWithRowsDtoPageable.setData(labWithRowsDto);
		return ResponseEntity.ok(labWithRowsDtoPageable);
	}
	
	/**
	 * Get all {@link LaboratoryRowDTO}s for the specified id.
	 * 
	 * @param patId
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/laboratories/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LabWithRowsDTO>> getLaboratory(@PathVariable Integer patId) throws OHServiceException {
		LOGGER.info("Get LabWithRows for patient Id: {}", patId);
		Patient patient = patientBrowserManager.getPatientById(patId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		List<Laboratory> labList = laboratoryManager.getLaboratory(patient).stream()
				.filter(e -> !e.getStatus().equalsIgnoreCase(draft) && !e.getStatus().equalsIgnoreCase(open)).collect(Collectors.toList());
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(labList.stream().map(lab -> {
			LabWithRowsDTO labDTO = new LabWithRowsDTO();
			List<String> labDescription = new ArrayList<>();
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			if (lab.getExam().getProcedure() == 2) {
				List<LaboratoryRow> labDes = new ArrayList<>();
				try {
					labDes = laboratoryManager.getLaboratoryRowList(lab.getCode());
				} catch (OHServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!labDes.isEmpty()) {
					for (LaboratoryRow laboratoryRow : labDes) {
						labDescription.add(laboratoryRow.getDescription());
					}
				}

			}
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
			labDTO.setLaboratoryDTO(laboratoryDTO);
			labDTO.setLaboratoryRowList(labDescription);
			return labDTO;
		}).collect(Collectors.toList()));
	}
	
	/**
	 * Get all {@link LaboratoryDTO}s for the specified id.
	 * 
	 * @param patId
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 * @author Arnaud
	 */
	@GetMapping(value = "/laboratories/examRequest/patient/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryDTO>> getLaboratoryExamRequest(@PathVariable Integer patId)
			throws OHServiceException {
		LOGGER.info("Get Exam requested by patient Id: {}", patId);
		Patient patient = patientBrowserManager.getPatientById(patId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		List<Laboratory> labList = laboratoryManager.getLaboratory(patient).stream()
				.filter(e -> e.getStatus().equalsIgnoreCase(draft) || e.getStatus().equalsIgnoreCase(open)).collect(Collectors.toList());
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}

		return ResponseEntity.ok(labList.stream().map(lab -> {
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
			return laboratoryDTO;
		}).collect(Collectors.toList()));
	}
	
	/**
	 * Get all {@link LaboratoryDTO}s.
	 * 
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 * @author Arnaud
	 */
	@GetMapping(value = "/laboratories/examRequest", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LaboratoryDTO>> getLaboratoryExamRequest() throws OHServiceException {
		LOGGER.info("Get all Exam Requested");
		List<Laboratory> labList = laboratoryManager.getLaboratory().stream()
				.filter(e -> e.getStatus().equalsIgnoreCase(draft) || e.getStatus().equalsIgnoreCase(open)).collect(Collectors.toList());
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}

		return ResponseEntity.ok(labList.stream().map(lab -> {
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
			return laboratoryDTO;
		}).collect(Collectors.toList()));
	}
	
	/**
	 * Get all {@link String}s.
	 * 
	 * @return the {@link List} of all material or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/laboratories/materials", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getMaterials() throws OHServiceException {
		LOGGER.info("Get all Material");
		List<String> materialList = laboratoryManager.getMaterialList();
		if (materialList == null || materialList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.ok(materialList);
		}
	}

	/**
	 * Get all the {@link LaboratoryRowDTO}s based on the applied filters.
	 * 
	 * @param examName
	 * @param dateFrom
	 * @param dateTo
	 * @param patientCode
	 * @return the {@link List} of found {@link LabWithRowsDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	
	@GetMapping(value = "/laboratories/exams", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<LabWithRowsDTO>> getLaboratoryForPrint(
			@RequestParam(required = false, defaultValue = "") String examName,
			@RequestParam(value = "dateFrom") String dateFrom, @RequestParam(value = "dateTo") String dateTo,
			@RequestParam(value = "patientCode", required = false, defaultValue = "0") int patientCode,
			@RequestParam(value = "status", required = false, defaultValue = "") String status,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "paged", required = false, defaultValue = "false") boolean paged)
			throws OHServiceException {
		LOGGER.info("Get labWithRow within specified date");
		LOGGER.debug("examName: {}", examName);
		LOGGER.debug("dateFrom: {}", dateFrom);
		LOGGER.debug("dateTo: {}", dateTo);
		LOGGER.debug("patientCode: {}", patientCode);
		LOGGER.debug("status: {}", status);
		LOGGER.debug("page: {}", page);
		LOGGER.debug("size: {}", size);
		LOGGER.debug("paged: {}", paged);
		Patient patient = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime dateT = LocalDateTime.parse(dateTo, formatter);
		LocalDateTime dateF = LocalDateTime.parse(dateFrom, formatter);
		Page<LabWithRowsDTO> result = new Page<>();
		
		if (patientCode != 0) {
			patient = patientBrowserManager.getPatientById(patientCode);
			if (patient == null || laboratoryManager.getLaboratory(patient) == null) {
				throw new OHAPIException(new OHExceptionMessage("Patient not found."),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		PagedResponse<Laboratory> laboratoryPageable;
		List<Laboratory> labList;
		if (paged) {
			if (!status.isEmpty()) {
				if (!examName.isEmpty()) {
					Exam exam = examManager.getExams(examName).get(0); 
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(exam, dateF, dateT, patient, page, size);
				} else {
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(null, dateF, dateT, patient, page, size);
				}
				labList = laboratoryPageable.getData()
	                    .stream().filter(lab -> lab.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
			} else {
				if (!examName.isEmpty()) {
					Exam exam = examManager.getExams(examName).get(0); 
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(exam, dateF, dateT, patient, page, size);
				} else {
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(null, dateF, dateT, patient, page, size);
				}
				labList = laboratoryPageable.getData();
			}
			result.setPageInfo(laboratoryMapper.setParameterPageInfo(laboratoryPageable.getPageInfo()));
			
		} else {
			if (!status.isEmpty()) {
				labList = laboratoryManager.getLaboratory(examName, dateF, dateT, patient)
		                    .stream().filter(lab -> lab.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
				
			} else {
				labList = laboratoryManager.getLaboratory(examName, dateF, dateT, patient);

			}
		}
		
		if (labList == null || labList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			List<LabWithRowsDTO> labWithRowList = labList.stream().map(lab -> {
				LabWithRowsDTO labDTO = new LabWithRowsDTO();
				List<String> labDescription = new ArrayList<>();
				LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
				if (lab.getExam().getProcedure() == 2) {
					List<LaboratoryRow> labDes = new ArrayList<>();
					try {
						labDes = laboratoryManager.getLaboratoryRowList(lab.getCode());
					} catch (OHServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!labDes.isEmpty()) {
						for (LaboratoryRow laboratoryRow : labDes) {
							labDescription.add(laboratoryRow.getDescription());
						}
					}
				}
				laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
				laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
				laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
				labDTO.setLaboratoryDTO(laboratoryDTO);
				labDTO.setLaboratoryRowList(labDescription);
				return labDTO;
			}).collect(Collectors.toList());
			result.setData(labWithRowList);
			return ResponseEntity.ok(result);
		}
	}

   /**
	 * Get all the {@link LaboratoryDTO}s for the specified id.
	 * 
	 * @param code
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LaboratoryDTO> getExamById(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get Laboratory associated to specified CODE: {}", code);
		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		Laboratory lab;
		if (labo.isPresent()) {
			lab = labo.get();
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
		laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
		laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
		laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus().toUpperCase()));
		return ResponseEntity.ok(laboratoryDTO);
	}

	/**
	 * Get all the {@link LaboratoryRowDTO}s for the specified id.
	 * 
	 * @param code
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/laboratories/exams/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LabWithRowsDTO> getExamWithRowsById(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get labWithRows associated to specified CODE: {}", code);
		LabWithRowsDTO lab = new LabWithRowsDTO();
		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		Laboratory laboratory;
		if (labo.isPresent()) {
			laboratory = labo.get();
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		LaboratoryDTO labDTO = laboratoryMapper.map2DTO(laboratory);
		labDTO.setRegistrationDate(laboratory.getCreatedDate());
		labDTO.setInOutPatient(PatientSTATUS.valueOf(laboratory.getInOutPatient()));
		labDTO.setStatus(LaboratoryStatus.valueOf(laboratory.getStatus().toUpperCase()));
		lab.setLaboratoryDTO(labDTO);

		List<String> labDescription = new ArrayList<>();
		if (laboratory.getExam().getProcedure() == 2) {
			List<LaboratoryRow> labDes = laboratoryManager.getLaboratoryRowList(laboratory.getCode());
			if (!labDes.isEmpty()) {
				for (LaboratoryRow laboratoryRow : labDes) {
					labDescription.add(laboratoryRow.getDescription());
				}
			}

		}
		lab.setLaboratoryRowList(labDescription);
		return ResponseEntity.ok(lab);
	}
	
	/**
	 * Set an {@link Laboratory} record to be deleted.
	 * 
	 * @param code
	 * @return {@code true} if the record has been set to invalid, {@code false} otherwise.
	 * @throws OHServiceException
	 * @author Arnaud
	 */
	@DeleteMapping(value = "/laboratories/examRequest/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteExamRequest(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get Laboratory associated to specified CODE: {}", code);
		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		if (labo.isPresent()) {
			Laboratory lab = labo.get();
			if (!lab.getStatus().equalsIgnoreCase(draft) && !lab.getStatus().equalsIgnoreCase(open)) {
				throw new OHAPIException(new OHExceptionMessage("This exam can not be deleted because its status is " + lab.getStatus().toUpperCase()));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		try {
			laboratoryManager.updateExamRequest(code, invalid);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam request is not deleted."));
		}
		return ResponseEntity.ok(true);
	}
}
