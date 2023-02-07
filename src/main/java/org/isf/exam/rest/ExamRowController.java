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

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.manager.ExamRowBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exam.dto.ExamRowDTO;
import org.isf.exam.mapper.ExamRowMapper;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class ExamRowController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExamRowController.class);

    @Autowired
    protected ExamBrowsingManager examManager;

    @Autowired
    protected ExamRowBrowsingManager examRowBrowsingManager;

    @Autowired
    private ExamRowMapper examRowMapper;

    public ExamRowController(ExamBrowsingManager examManager, ExamRowBrowsingManager examRowBrowsingManager, ExamRowMapper examRowMapper) {
        this.examManager = examManager;
        this.examRowBrowsingManager = examRowBrowsingManager;
        this.examRowMapper = examRowMapper;
    }

    @PostMapping(value = "/examrows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamRowDTO> newExamRow(@RequestBody ExamRowDTO examRowDTO) throws OHServiceException {
        Exam exam = examManager.getExams().stream().filter(e -> examRowDTO.getExam().getCode().equals(e.getCode())).findFirst().orElse(null);

        if (exam == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
        }

        ExamRow examRow = examRowMapper.map2Model(examRowDTO);
        examRow.setExamCode(exam);

        ExamRow isCreatedExamRow = examRowBrowsingManager.newExamRow(examRow);
        if (isCreatedExamRow == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamRow is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(examRowMapper.map2DTO(isCreatedExamRow));
    }

    @GetMapping(value = "/examrows", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ExamRowDTO>> getExamRows() throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRow();

        if (examRows == null || examRows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(examRowMapper.map2DTOList(examRows));
        }
    }

    @GetMapping(value = "/examrows/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ExamRowDTO>> getExamRowsByCode(@PathVariable Integer code) throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRow(code);

        if (examRows == null || examRows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(examRowMapper.map2DTOList(examRows));
        }
    }

    @GetMapping(value = "/examrows/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ExamRowDTO>> getExamRowsByCodeAndDescription(@RequestParam Integer code, @RequestParam String description) throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRow(code, description);

        if (examRows == null || examRows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(examRowMapper.map2DTOList(examRows));
        }
    }

    @DeleteMapping(value = "/examrows/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> deleteExam(@PathVariable Integer code) throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRow(code);
        if (examRows == null || examRows.isEmpty()) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamRows not Found!", OHSeverityLevel.WARNING));
        }
        if (examRows.size() > 1) {
            throw new OHAPIException(new OHExceptionMessage(null, "Found multiple ExamRow!", OHSeverityLevel.WARNING));
        }
        if (!examRowBrowsingManager.deleteExamRow(examRows.get(0))) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamRow is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/examrows/byExamCode/{examCode:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ExamRowDTO>> getExamRowsByExamCode(@PathVariable String examCode) throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRowByExamCode(examCode);

        if (examRows == null || examRows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(examRowMapper.map2DTOList(examRows));
        }
    }
}
