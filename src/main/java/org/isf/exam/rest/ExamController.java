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
package org.isf.exam.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.dto.ExamWithRowsDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.model.ExamType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Exams")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ExamController {

	private final ExamBrowsingManager examManager;

	private final ExamTypeBrowserManager examTypeBrowserManager;

	private final ExamMapper examMapper;

	public ExamController(
		ExamBrowsingManager examManager,
		ExamTypeBrowserManager examTypeBrowserManager,
		ExamMapper examMapper
	) {
		this.examManager = examManager;
		this.examTypeBrowserManager = examTypeBrowserManager;
		this.examMapper = examMapper;
	}

	@PostMapping(value = "/exams")
	@ResponseStatus(HttpStatus.CREATED)
	public ExamDTO newExam(@Valid @RequestBody ExamWithRowsDTO examWithRowsDTO) throws OHServiceException {
		ExamDTO examDTO = examWithRowsDTO.exam();
		List<String> examRows = examWithRowsDTO.rows();

		ExamType examType = examTypeBrowserManager.findByCode(examDTO.getExamtype().getCode());

		if (examType == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam type not found."));
		}

		if (examDTO.getProcedure() == 1 && examDTO.getDefaultResult() != null) {
			if ((examRows == null ? Collections.emptyList() : examRows).stream()
				.noneMatch(row -> examDTO.getDefaultResult().equals(row))) {
				throw new OHAPIException(new OHExceptionMessage("Exam default result doesn't match any exam rows."));
			}
		}

		Exam exam = examMapper.map2Model(examDTO);
		exam.setExamtype(examType);
		try {
			exam = examManager.create(exam, examRows);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam not created."));
		}
		return examMapper.map2DTO(exam);
	}

	@PutMapping(value = "/exams/{code:.+}")
	public ExamDTO updateExam(
		@PathVariable String code, @Valid @RequestBody ExamWithRowsDTO examWithRowsDTO
	) throws OHServiceException {
		ExamDTO examDTO = examWithRowsDTO.exam();
		List<String> examRows = examWithRowsDTO.rows();

		if (!examDTO.getCode().equals(code)) {
			throw new OHAPIException(new OHExceptionMessage("Exam code mismatch."));
		}
		if (examManager.findByCode(code) == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam not found."), HttpStatus.NOT_FOUND);
		}

		ExamType examType = examTypeBrowserManager.findByCode(examDTO.getExamtype().getCode());
		if (examType == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam type not found."));
		}

		if (examDTO.getProcedure() == 1 && examDTO.getDefaultResult() != null) {
			if ((examRows == null ? Collections.emptyList() : examRows).stream()
				.noneMatch(row -> examDTO.getDefaultResult().equals(row))) {
				throw new OHAPIException(new OHExceptionMessage("Exam default result doesn't match any exam rows."));
			}
		}

		Exam exam = examMapper.map2Model(examDTO);
		exam.setExamtype(examType);
		Exam examUpdated = examManager.update(exam, examRows);
		if (examUpdated == null) {
			throw new OHAPIException(new OHExceptionMessage("Exam not updated."));
		}

		return examMapper.map2DTO(examUpdated);
	}

	@GetMapping(value = "/exams/description/{description:.+}")
	public List<ExamDTO> getExams(@PathVariable String description) throws OHServiceException {
		return examMapper.map2DTOList(examManager.getExams(description));
	}

	@GetMapping(value = "/exams")
	public List<ExamDTO> getExams() throws OHServiceException {
		return examMapper.map2DTOList(examManager.getExams());
	}

	@DeleteMapping(value = "/exams/{code:.+}")
	public boolean deleteExam(@PathVariable String code) throws OHServiceException {
		Optional<Exam> exam = examManager.getExams().stream().filter(e -> e.getCode().equals(code)).findFirst();
		if (exam.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Exam not found."));
		}
		try {
			examManager.deleteExam(exam.get());
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Exam not deleted."));
		}
	}
}