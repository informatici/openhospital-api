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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Exam Rows")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ExamRowController {

    private final ExamBrowsingManager examManager;

    private final ExamRowBrowsingManager examRowBrowsingManager;

    private final ExamRowMapper examRowMapper;

    public ExamRowController(
        ExamBrowsingManager examManager,
        ExamRowBrowsingManager examRowBrowsingManager,
        ExamRowMapper examRowMapper
    ) {
        this.examManager = examManager;
        this.examRowBrowsingManager = examRowBrowsingManager;
        this.examRowMapper = examRowMapper;
    }

    @PostMapping("/examrows")
    @ResponseStatus(HttpStatus.CREATED)
    public ExamRowDTO newExamRow(@RequestBody ExamRowDTO examRowDTO) throws OHServiceException {
        Exam exam = examManager.getExams()
            .stream()
            .filter(e -> examRowDTO.getExam().getCode().equals(e.getCode()))
            .findFirst().orElse(null);

        if (exam == null) {
            throw new OHAPIException(new OHExceptionMessage("Exam not found."), HttpStatus.NOT_FOUND);
        }

        ExamRow examRow = examRowMapper.map2Model(examRowDTO);
        examRow.setExamCode(exam);

        ExamRow isCreatedExamRow = examRowBrowsingManager.newExamRow(examRow);
        if (isCreatedExamRow == null) {
            throw new OHAPIException(new OHExceptionMessage("ExamRow not created."));
        }

        return examRowMapper.map2DTO(isCreatedExamRow);
    }

    @GetMapping("/examrows")
    public List<ExamRowDTO> getExamRows() throws OHServiceException {
        return examRowMapper.map2DTOList(examRowBrowsingManager.getExamRow());
    }

    @GetMapping(value = "/examrows/{code:.+}")
    public List<ExamRowDTO> getExamRowsByCode(@PathVariable Integer code) throws OHServiceException {
        return examRowMapper.map2DTOList(examRowBrowsingManager.getExamRow(code));
    }

    @GetMapping(value = "/examrows/search")
    public List<ExamRowDTO> getExamRowsByCodeAndDescription(
        @RequestParam Integer code, @RequestParam String description
    ) throws OHServiceException {
        return examRowMapper.map2DTOList(examRowBrowsingManager.getExamRow(code, description));
    }

    @DeleteMapping(value = "/examrows/{code:.+}")
    public boolean deleteExam(@PathVariable Integer code) throws OHServiceException {
        List<ExamRow> examRows = examRowBrowsingManager.getExamRow(code);
        if (examRows == null || examRows.isEmpty()) {
            throw new OHAPIException(new OHExceptionMessage("ExamRows not found."), HttpStatus.NOT_FOUND);
        }

        if (examRows.size() > 1) {
            throw new OHAPIException(new OHExceptionMessage("Found multiple ExamRows."));
        }

        try {
            examRowBrowsingManager.deleteExamRow(examRows.get(0));
            return true;
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("ExamRow not deleted."),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/examrows/byExamCode/{examCode:.+}")
    public List<ExamRowDTO> getExamRowsByExamCode(@PathVariable String examCode) throws OHServiceException {
        return examRowMapper.map2DTOList(examRowBrowsingManager.getExamRowByExamCode(examCode));
    }
}
