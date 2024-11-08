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
package org.isf.exatype.rest;

import java.util.List;

import org.isf.exatype.dto.ExamTypeDTO;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.mapper.ExamTypeMapper;
import org.isf.exatype.model.ExamType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Exam Types")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ExamTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExamTypeController.class);

	private final ExamTypeBrowserManager examTypeBrowserManager;

	private final ExamTypeMapper examTypeMapper;

	public ExamTypeController(ExamTypeBrowserManager examTypeBrowserManager, ExamTypeMapper examTypeMapper) {
		this.examTypeBrowserManager = examTypeBrowserManager;
		this.examTypeMapper = examTypeMapper;
	}

	@PostMapping("/examtypes")
	@ResponseStatus(HttpStatus.CREATED)
	public ExamTypeDTO newExamType(@RequestBody ExamTypeDTO newExamType) throws OHServiceException {
		return examTypeMapper.map2DTO(examTypeBrowserManager.newExamType(examTypeMapper.map2Model(newExamType)));
	}

	@PutMapping("/examtypes/{code:.+}")
	public ExamTypeDTO updateExamType(
		@PathVariable String code, @RequestBody ExamTypeDTO updateExamType
	) throws OHServiceException {
		if (!updateExamType.getCode().equals(code)) {
			throw new OHAPIException(new OHExceptionMessage("Exam Type code mismatch."));
		}
		if (!examTypeBrowserManager.isCodePresent(code)) {
			throw new OHAPIException(new OHExceptionMessage("Exam Type not found."), HttpStatus.NOT_FOUND);
		}

		return examTypeMapper.map2DTO(examTypeBrowserManager.updateExamType(examTypeMapper.map2Model(updateExamType)));
	}

	@GetMapping("/examtypes")
	public List<ExamTypeDTO> getExamTypes() throws OHServiceException {
		return examTypeMapper.map2DTOList(examTypeBrowserManager.getExamType());
	}

	@DeleteMapping("/examtypes/{code:.+}")
	public boolean deleteExamType(@PathVariable String code) throws OHServiceException {
		LOGGER.info("Delete Exam Type Type code: {}", code);

		List<ExamType> examTypes = examTypeBrowserManager.getExamType();
		List<ExamType> examFounds = examTypes.stream().filter(ad -> ad.getCode().equals(code)).toList();
		if (examFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Exam Type not found."), HttpStatus.NOT_FOUND);
		}

		try {
			examTypeBrowserManager.deleteExamType(examFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.error("Delete Exam Type: {} failed.", code);
			throw new OHAPIException(new OHExceptionMessage("Exam Type not deleted."));
		}
	}
}
