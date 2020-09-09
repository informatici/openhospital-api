package org.isf.exam.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class ExamRowController {

    private final Logger logger = LoggerFactory.getLogger(ExamRowController.class);

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
    public ResponseEntity newExamRow(@RequestBody ExamRowDTO examRowDTO) throws OHServiceException {
        Exam exam = examManager.getExams().stream().filter(e -> examRowDTO.getExam().getCode().equals(e.getCode())).findFirst().orElse(null);

        if (exam == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
        }

        ExamRow examRow = examRowMapper.map2Model(examRowDTO);
        examRow.setExamCode(exam);

        boolean isCreated = examRowBrowsingManager.newExamRow(examRow);
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamRow is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
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
    public ResponseEntity deleteExam(@PathVariable Integer code) throws OHServiceException {
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
