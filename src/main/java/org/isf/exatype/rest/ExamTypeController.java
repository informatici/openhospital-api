package org.isf.exatype.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.exatype.dto.ExamTypeDTO;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.mapper.ExamTypeMapper;
import org.isf.exatype.model.ExamType;
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
import java.util.Optional;

@RestController
@Api(value = "/examtypes", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class ExamTypeController {

    private final Logger logger = LoggerFactory.getLogger(ExamTypeController.class);

    @Autowired
    protected ExamTypeBrowserManager examTypeBrowserManager;
    @Autowired
    private ExamTypeMapper examTypeMapper;

    public ExamTypeController(ExamTypeBrowserManager examTypeBrowserManager, ExamTypeMapper examTypeMapper) {
        this.examTypeBrowserManager = examTypeBrowserManager;
        this.examTypeMapper = examTypeMapper;
    }

    @PostMapping(value = "/examtypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newExamType(@RequestBody ExamTypeDTO newExamType) throws OHServiceException {

        ExamType examType = examTypeMapper.map2Model(newExamType);
        boolean created = examTypeBrowserManager.newExamType(examType);

        if (!created) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType type not created!", OHSeverityLevel.ERROR));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PutMapping(value = "/examtypes/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExamType(@PathVariable String code, @RequestBody ExamTypeDTO updateExamType) throws OHServiceException {

        if (!updateExamType.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType code mismatch", OHSeverityLevel.ERROR));
        }
        if (!examTypeBrowserManager.codeControl(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType not Found!", OHSeverityLevel.WARNING));
        }

        ExamType examType = examTypeMapper.map2Model(updateExamType);
        if (!examTypeBrowserManager.updateExamType(examType)) {
            throw new OHAPIException(new OHExceptionMessage(null, "ExamType is not updated!", OHSeverityLevel.ERROR));
        }

        return ResponseEntity.ok(true);
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
    public ResponseEntity deleteExamType(@PathVariable String code) throws OHServiceException {
        logger.info("Delete exams code:" + code);
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
