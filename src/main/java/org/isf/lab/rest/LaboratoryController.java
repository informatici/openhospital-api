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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Laboratories")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class LaboratoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryController.class);

	// TODO: to centralize
	protected static final String DEFAULT_PAGE_SIZE = "80";

	private static final String DRAFT = LaboratoryStatus.draft.toString();

	private static final String OPEN = LaboratoryStatus.open.toString();

	private static final String DELETED = LaboratoryStatus.deleted.toString();

	private static final String INVALID = LaboratoryStatus.invalid.toString();

	private static final String DONE = LaboratoryStatus.done.toString();

	private final LabManager laboratoryManager;

	private final ExamBrowsingManager examManager;

	private final PatientBrowserManager patientBrowserManager;

	private final LaboratoryMapper laboratoryMapper;

	private final LaboratoryRowMapper laboratoryRowMapper;

	public LaboratoryController(
		LabManager laboratoryManager,
		PatientBrowserManager patientBrowserManager,
		ExamBrowsingManager examManager,
		LaboratoryMapper laboratoryMapper,
		LaboratoryRowMapper laboratoryRowMapper
	) {
		this.laboratoryManager = laboratoryManager;
		this.patientBrowserManager = patientBrowserManager;
		this.examManager = examManager;
		this.laboratoryMapper = laboratoryMapper;
		this.laboratoryRowMapper = laboratoryRowMapper;
	}

	/**
	 * Create a new {@link LaboratoryRowDTO}.
	 *
	 * @param labWithRowsDTO Lab with rows payload
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException When failed to create lab with rows
	 */
	@PostMapping("/laboratories")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newLaboratory(@RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {
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

		return true;
	}

	/**
	 * Create a new {@link LaboratoryDTO}.
	 *
	 * @param laboratoryDTO Lab exam payload
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException When failed to create lab exam
	 * @author Arnaud
	 */
	@PostMapping("/laboratories/examRequest")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newExamRequest(@RequestBody LaboratoryDTO laboratoryDTO) throws OHServiceException {
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
		labToInsert.setStatus(DRAFT);
		labToInsert.setResult("");
		labToInsert.setInOutPatient(laboratoryDTO.getInOutPatient().toString());
		List<Laboratory> labList = laboratoryManager.getLaboratory(patient)
			.stream()
			.filter(e -> e.getStatus().equals(DRAFT)).toList();

		if (!labList.isEmpty()) {
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

		return true;
	}

	/**
	 * Create a {@link List} of {@link LaboratoryRowDTO}.
	 *
	 * @param labsWithRows List of lab exam with rows
	 * @return {@code true} if the record has been created,  {@code false} otherwise.
	 * @throws OHServiceException When failed to create lab exams
	 */
	@PostMapping("/laboratories/insertList")
	@ResponseStatus(HttpStatus.CREATED)
	public boolean newLaboratory2(@RequestBody List<LabWithRowsDTO> labsWithRows) throws OHServiceException {
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

		return true;
	}

	/**
	 * Updates the specified {@link LaboratoryRowDTO} object.
	 *
	 * @param code Lab exam code
	 * @param labWithRowsDTO Lab exam payload
	 * @return {@code true} if has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update lab exam
	 */
	@PutMapping("/laboratories/{code}")
	public boolean updateLaboratory(
		@PathVariable Integer code,
		@RequestBody LabWithRowsDTO labWithRowsDTO
	) throws OHServiceException {
		LOGGER.info("Update labWithRows code: {}", code);
		LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
		List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

		if (!code.equals(laboratoryDTO.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory code mismatch."));
		}

		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		if (labo.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not found."), HttpStatus.NOT_FOUND);
		}

		Laboratory lab = labo.get();
		if (lab.getStatus().equalsIgnoreCase(DELETED) || lab.getStatus().equalsIgnoreCase(INVALID)) {
			throw new OHAPIException(new OHExceptionMessage("This exam can not be update because its status is " + lab.getStatus()));
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
			labToInsert.setStatus(DONE);
		}

		try {
			laboratoryManager.updateLaboratory(labToInsert, labRows);
		} catch (OHServiceException e) {
			// TODO: workaround waiting OH2-182
			if (e.getMessages().get(0).getMessage().equals("angal.labnew.someexamswithoutresultpleasecheck.msg")) {
				throw new OHAPIException(new OHExceptionMessage("errors.lab.someexamswithoutresultpleasecheck"));
			}
			throw new OHAPIException(e.getMessages().get(0));
		}

		return true;
	}

	/**
	 * Updates the specified {@link LaboratoryDTO} object.
	 *
	 * @param code Lab exam code
	 * @param status Lab exam status
	 * @return {@code true} if has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to change lab exam status
	 * @author Arnaud
	 */
	@PutMapping("/laboratories/examRequest/{code}")
	public boolean updateExamRequest(
		@PathVariable Integer code, @RequestParam String status
	) throws OHServiceException {
		LOGGER.info("Update exam request code: {}", code);

		try {
			laboratoryManager.updateExamRequest(code, status);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Laboratory not updated."));
		}

		return true;
	}

	/**
	 * Set an {@link LaboratoryDTO} record to be deleted.
	 *
	 * @param code Lab exam code
	 * @return {@code true} if the record has been set to delete, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete lab exam
	 */
	@DeleteMapping("/laboratories/{code}")
	public boolean deleteExam(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Delete Exam code: {}", code);
		Optional<Laboratory> lab = laboratoryManager.getLaboratory(code);
		if (lab.isPresent()) {
			Laboratory labToDelete = lab.get();
			if (labToDelete.getStatus().equalsIgnoreCase(DELETED)) {
				throw new OHAPIException(new OHExceptionMessage("This exam can not be deleted because its status is " + labToDelete.getStatus()));
			}
		} else {
			throw new OHAPIException(new OHExceptionMessage("Lab exam not found."), HttpStatus.NOT_FOUND);
		}

		try {
			laboratoryManager.updateExamRequest(code, DELETED);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam is not deleted."));
		}
	}

	/**
	 * Get the list of exams {@link LaboratoryRowDTO}s divided by pages.
	 *
	 * @param oneWeek Get for the previous week?
	 * @param page The page number
	 * @param size The page size
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exams
	 */
	@GetMapping("/laboratories")
	public Page<LabWithRowsDTO> getLaboratory(
		@RequestParam boolean oneWeek, @RequestParam int page, @RequestParam int size
	) throws OHServiceException {
		LOGGER.info("Get all LabWithRows");
		PagedResponse<Laboratory> labListPageable = laboratoryManager.getLaboratoryPageable(oneWeek, page, size);

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
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			labDTO.setLaboratoryDTO(laboratoryDTO);
			labDTO.setLaboratoryRowList(labDescription);
			return labDTO;
		}).collect(Collectors.toList());

		Page<LabWithRowsDTO> labWithRowsDtoPageable = new Page<>();
		labWithRowsDtoPageable.setPageInfo(laboratoryMapper.setParameterPageInfo(labListPageable.getPageInfo()));
		labWithRowsDtoPageable.setData(labWithRowsDto);

		return labWithRowsDtoPageable;
	}

	/**
	 * Get all {@link LaboratoryRowDTO}s for the specified id.
	 *
	 * @param patId Patient ID
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get patient's lab exams
	 */
	@GetMapping("/laboratories/byPatientId/{patId}")
	public List<LabWithRowsDTO> getLaboratory(@PathVariable Integer patId) throws OHServiceException {
		LOGGER.info("Get LabWithRows for patient Id: {}", patId);

		Patient patient = patientBrowserManager.getPatientById(patId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		List<Laboratory> labList = laboratoryManager.getLaboratory(patient).stream()
			.filter(e -> !e.getStatus().equalsIgnoreCase(DRAFT) && !e.getStatus().equalsIgnoreCase(OPEN)).toList();

		return labList.stream().map(lab -> {
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
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			labDTO.setLaboratoryDTO(laboratoryDTO);
			labDTO.setLaboratoryRowList(labDescription);
			return labDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * Get all {@link LaboratoryDTO}s for the specified id.
	 *
	 * @param patId Patient ID
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get patient exam request
	 * @author Arnaud
	 */
	@GetMapping("/laboratories/examRequest/patient/{patId}")
	public List<LaboratoryDTO> getLaboratoryExamRequest(
		@PathVariable Integer patId
	) throws OHServiceException {
		LOGGER.info("Get Exam requested by patient Id: {}", patId);

		Patient patient = patientBrowserManager.getPatientById(patId);
		if (patient == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."));
		}

		List<Laboratory> labList = laboratoryManager.getLaboratory(patient).stream()
			.filter(e -> e.getStatus().equalsIgnoreCase(DRAFT) || e.getStatus().equalsIgnoreCase(OPEN)).toList();

		return labList.stream().map(lab -> {
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			return laboratoryDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * Get all {@link LaboratoryDTO}s.
	 *
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get exam requests
	 * @author Arnaud
	 */
	@GetMapping("/laboratories/examRequest")
	public List<LaboratoryDTO> getLaboratoryExamRequest() throws OHServiceException {
		LOGGER.info("Get all Exam Requested");
		List<Laboratory> labList = laboratoryManager.getLaboratory().stream()
			.filter(e -> e.getStatus().equalsIgnoreCase(DRAFT) || e.getStatus().equalsIgnoreCase(OPEN)).toList();

		return labList.stream().map(lab -> {
			LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
			laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
			laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			return laboratoryDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * Get all {@link String}s.
	 *
	 * @return the {@link List} of all material or NO_CONTENT otherwise.
	 */
	@GetMapping("/laboratories/materials")
	public List<String> getMaterials() {
		LOGGER.info("Get all Material");
		return laboratoryManager.getMaterialList();
	}

	/**
	 * Get all the {@link LaboratoryRowDTO}s based on the applied filters.
	 *
	 * @param examName Exam name
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @param patientCode Patient Code
	 * @return the {@link List} of found {@link LabWithRowsDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exams
	 */
	@GetMapping("/laboratories/exams")
	public Page<LabWithRowsDTO> getLaboratoryForPrint(
		@RequestParam(required = false, defaultValue = "") String examName,
		@RequestParam(value = "dateFrom") String dateFrom, @RequestParam(value = "dateTo") String dateTo,
		@RequestParam(value = "patientCode", required = false, defaultValue = "0") int patientCode,
		@RequestParam(value = "status", required = false, defaultValue = "") String status,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
		@RequestParam(value = "paged", required = false, defaultValue = "false") boolean paged
	) throws OHServiceException {
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
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(exam.getDescription(), dateF, dateT, patient, page, size);
				} else {
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(null, dateF, dateT, patient, page, size);
				}
				labList = laboratoryPageable.getData()
					.stream().filter(lab -> lab.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
			} else {
				if (!examName.isEmpty()) {
					Exam exam = examManager.getExams(examName).get(0);
					laboratoryPageable = laboratoryManager.getLaboratoryPageable(exam.getDescription(), dateF, dateT, patient, page, size);
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
			laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));
			labDTO.setLaboratoryDTO(laboratoryDTO);
			labDTO.setLaboratoryRowList(labDescription);
			return labDTO;
		}).collect(Collectors.toList());
		result.setData(labWithRowList);

		return result;
	}

	/**
	 * Get all the {@link LaboratoryDTO}s for the specified id.
	 *
	 * @param code Lab exam code
	 * @return the {@link List} of found {@link LaboratoryDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exam
	 */
	@GetMapping("/laboratories/{code}")
	public LaboratoryDTO getExamById(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get Laboratory associated to specified CODE: {}", code);
		Laboratory lab = laboratoryManager.getLaboratory(code).orElse(null);
		if (lab == null) {
			throw new OHAPIException(new OHExceptionMessage("Lab exam not found"), HttpStatus.NOT_FOUND);
		}

		LaboratoryDTO laboratoryDTO = laboratoryMapper.map2DTO(lab);
		laboratoryDTO.setRegistrationDate(lab.getCreatedDate());
		laboratoryDTO.setInOutPatient(PatientSTATUS.valueOf(lab.getInOutPatient()));
		laboratoryDTO.setStatus(LaboratoryStatus.valueOf(lab.getStatus()));

		return laboratoryDTO;
	}

	/**
	 * Get all the {@link LaboratoryRowDTO}s for the specified id.
	 *
	 * @param code Lab exam code
	 * @return the {@link List} of found {@link LaboratoryRowDTO} or NO_CONTENT otherwise.
	 * @throws OHServiceException When failed to get lab exam with row
	 */
	@GetMapping("/laboratories/exams/{code}")
	public LabWithRowsDTO getExamWithRowsById(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get labWithRows associated to specified CODE: {}", code);
		LabWithRowsDTO lab = new LabWithRowsDTO();

		Laboratory laboratory = laboratoryManager.getLaboratory(code).orElse(null);
		if (laboratory == null) {
			throw new OHAPIException(new OHExceptionMessage("Lab exam not found"), HttpStatus.NOT_FOUND);
		}

		LaboratoryDTO labDTO = laboratoryMapper.map2DTO(laboratory);
		labDTO.setRegistrationDate(laboratory.getCreatedDate());
		labDTO.setInOutPatient(PatientSTATUS.valueOf(laboratory.getInOutPatient()));
		labDTO.setStatus(LaboratoryStatus.valueOf(laboratory.getStatus()));
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

		return lab;
	}

	/**
	 * Set an {@link Laboratory} record to be deleted.
	 *
	 * @param code Lab exam code
	 * @return {@code true} if the record has been set to invalid, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete lab exam
	 * @author Arnaud
	 */
	@DeleteMapping("/laboratories/examRequest/{code}")
	public boolean deleteExamRequest(@PathVariable Integer code) throws OHServiceException {
		LOGGER.info("Get Laboratory associated to specified CODE: {}", code);
		Optional<Laboratory> labo = laboratoryManager.getLaboratory(code);
		if (labo.isPresent()) {
			Laboratory lab = labo.get();
			if (!lab.getStatus().equalsIgnoreCase(DRAFT) && !lab.getStatus().equalsIgnoreCase(OPEN)) {
				throw new OHAPIException(new OHExceptionMessage("This exam can not be deleted because its status is " + lab.getStatus()));
			}
		} else {
			throw new OHAPIException(new OHExceptionMessage("Lab exam not found"), HttpStatus.NOT_FOUND);
		}

		try {
			laboratoryManager.updateExamRequest(code, INVALID);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam request is not deleted."));
		}
	}
}
