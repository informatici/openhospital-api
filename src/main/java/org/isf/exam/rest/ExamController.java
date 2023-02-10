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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.exam.rest;

import java.util.List;
import java.util.Optional;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.exatype.manager.ExamTypeBrowserManager;
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
@Api(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class ExamController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExamController.class);

    @Autowired
    protected ExamBrowsingManager examManager;

    @Autowired
    protected ExamTypeBrowserManager examTypeBrowserManager;

    @Autowired
    private ExamMapper examMapper;

    public ExamController(ExamBrowsingManager examManager, ExamMapper examMapper) {
        this.examManager = examManager;
        this.examMapper = examMapper;
    }

    @PostMapping(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamDTO> newExam(@RequestBody ExamDTO newExam) throws OHServiceException {
        ExamType examType = examTypeBrowserManager.getExamType().stream().filter(et -> newExam.getExamtype().getCode().equals(et.getCode())).findFirst().orElse(null);

        if (examType == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam type not found!", OHSeverityLevel.ERROR));
        }

        Exam exam = examMapper.map2Model(newExam);
        exam.setExamtype(examType);

        boolean isCreated = examManager.newExam(exam);
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(examMapper.map2DTO(exam));
    }

    @PutMapping(value = "/exams/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamDTO> updateExams(@PathVariable String code, @RequestBody ExamDTO updateExam) throws OHServiceException {

        if (!updateExam.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam code mismatch", OHSeverityLevel.ERROR));
        }
        if (examManager.getExams().stream().noneMatch(e -> e.getCode().equals(code))) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not Found!", OHSeverityLevel.WARNING));
        }

        ExamType examType = examTypeBrowserManager.getExamType().stream().filter(et -> updateExam.getExamtype().getCode().equals(et.getCode())).findFirst().orElse(null);
        if (examType == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam type not found!", OHSeverityLevel.ERROR));
        }

        Exam exam = examMapper.map2Model(updateExam);
        exam.setExamtype(examType);
        exam.setLock(updateExam.getLock());
        Exam examUpdated = examManager.updateExam(exam);
        if (examUpdated == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam is not updated!", OHSeverityLevel.ERROR));
        }

        return ResponseEntity.ok(examMapper.map2DTO(examUpdated));
    }


    @GetMapping(value = "/exams/description/{description:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamDTO>> getExams(@PathVariable String description) throws OHServiceException {
        List<ExamDTO> exams = examMapper.map2DTOList(examManager.getExams(description));

        if (exams == null || exams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(exams);
        }
    }

    @GetMapping(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamDTO>> getExams() throws OHServiceException {
        List<ExamDTO> exams = examMapper.map2DTOList(examManager.getExams());

        if (exams == null || exams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(exams);
        }
    }

    @DeleteMapping(value = "/exams/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteExam(@PathVariable String code) throws OHServiceException {
        Optional<Exam> exam = examManager.getExams().stream().filter(e -> e.getCode().equals(code)).findFirst();
        if (!exam.isPresent()) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not Found!", OHSeverityLevel.WARNING));
        }
        if (!examManager.deleteExam(exam.get())) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }
}
