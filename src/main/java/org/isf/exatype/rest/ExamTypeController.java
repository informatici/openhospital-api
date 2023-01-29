/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.exatype.rest;

import java.util.List;
import java.util.Optional;

import org.isf.exatype.dto.ExamTypeDTO;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.mapper.ExamTypeMapper;
import org.isf.exatype.model.ExamType;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/examtypes", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class ExamTypeController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExamTypeController.class);

    @Autowired
    protected ExamTypeBrowserManager examTypeBrowserManager;

    @Autowired
    private ExamTypeMapper examTypeMapper;

    public ExamTypeController(ExamTypeBrowserManager examTypeBrowserManager, ExamTypeMapper examTypeMapper) {
        this.examTypeBrowserManager = examTypeBrowserManager;
        this.examTypeMapper = examTypeMapper;
    }

	@PostMapping(value = "/examtypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ExamTypeDTO> newExamType(@RequestBody ExamTypeDTO newExamType) throws OHServiceException {

		ExamType examType = examTypeMapper.map2Model(newExamType);
		ExamType createdExamType = examTypeBrowserManager.newExamType(examType);
		if (createdExamType == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "ExamType type not created!", OHSeverityLevel.ERROR));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(examTypeMapper.map2DTO(createdExamType));
	}

    @PutMapping(value = "/examtypes/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamTypeDTO> updateExamType(@PathVariable String code, @RequestBody ExamTypeDTO updateExamType) throws OHServiceException {

        if (!updateExamType.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType code mismatch", OHSeverityLevel.ERROR));
        }
        if (!examTypeBrowserManager.isCodePresent(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType not Found!", OHSeverityLevel.WARNING));
        }

        ExamType examType = examTypeMapper.map2Model(updateExamType);
        ExamType exType = examTypeBrowserManager.updateExamType(examType);
        if (exType == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType is not updated!", OHSeverityLevel.ERROR));
        }

        return ResponseEntity.ok(examTypeMapper.map2DTO(exType));
    }


    @GetMapping(value = "/examtypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamTypeDTO>> getExamTypes() throws OHServiceException {
        List<ExamTypeDTO> examTypeDTOS = examTypeMapper.map2DTOList(examTypeBrowserManager.getExamType());

        if (examTypeDTOS == null || examTypeDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(examTypeDTOS);
        }
    }

    @DeleteMapping(value = "/examtypes/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteExamType(@PathVariable String code) throws OHServiceException {
	    LOGGER.info("Delete exams code: {}", code);
        Optional<ExamType> examType = examTypeBrowserManager.getExamType().stream().filter(e -> e.getCode().equals(code)).findFirst();
        if (!examType.isPresent()) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam type not Found!", OHSeverityLevel.WARNING));
        }
        if (!examTypeBrowserManager.deleteExamType(examType.get())) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam type is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }

}
