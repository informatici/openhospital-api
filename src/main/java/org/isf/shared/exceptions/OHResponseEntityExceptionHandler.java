package org.isf.shared.exceptions;

import org.isf.shared.controller.FailureDto;
import org.isf.shared.controller.ResponseCode;
import org.isf.shared.controller.ValidationErrorDto;
import org.isf.utils.exception.*;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OHResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {OHDataValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationErrorDto handleOHServiceValidationException(OHDataValidationException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return new ValidationErrorDto(ResponseCode.VALIDATION_ERROR.getValue(), errors);
    }

    @ExceptionHandler(value = {OHAPINotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected FailureDto handleOHAPIException(OHAPINotFoundException ex) {
        return new FailureDto(ResponseCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = {OHReportException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleReportException(OHReportException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.REPORT_ERROR.getValue());
        return new FailureDto(ResponseCode.REPORT_ERROR, message);
    }

    @ExceptionHandler(value = {OHInvalidSQLException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleInvalidSqlException(OHInvalidSQLException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.INVALID_SQL_ERROR.getValue());
        return new FailureDto(ResponseCode.INVALID_SQL_ERROR, message);
    }

    @ExceptionHandler(value = {OHOperationNotAllowedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ValidationErrorDto handleOHServiceOperationNotAllowedException(OHOperationNotAllowedException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return new ValidationErrorDto(ResponseCode.OPERATION_NOT_ALLOWED_ERROR.getValue(), errors);
    }

    @ExceptionHandler(value = {OHDicomException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleDicomException(OHDicomException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.DICOM_ERROR.getValue());
        return new FailureDto(ResponseCode.DICOM_ERROR, message);
    }

    @ExceptionHandler(value = {OHDBConnectionException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    protected FailureDto handleDbConnectionException(OHDBConnectionException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.DATABASE_CONNECTION_ERROR.getValue());
        return new FailureDto(ResponseCode.DATABASE_CONNECTION_ERROR, message);
    }

    @ExceptionHandler(value = {OHDataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleDataIntegrityViolationException(OHDataIntegrityViolationException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.DATA_INTEGRITY_ERROR.getValue());
        return new FailureDto(ResponseCode.DATA_INTEGRITY_ERROR, message);
    }

    @ExceptionHandler(value = {OHDataLockFailureException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleDbLockFailureException(OHDataLockFailureException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(ResponseCode.DATA_LOCK_ERROR.getValue());
        return new FailureDto(ResponseCode.DATA_LOCK_ERROR, message);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Map<String, Object> errors = new HashMap<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }
        final ValidationErrorDto validaErrorDto = new ValidationErrorDto(ResponseCode.INVALID_PARAMETER.getValue(),
                errors);
        return handleExceptionInternal(ex, validaErrorDto, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                new FailureDto(ResponseCode.INVALID_PARAMETER, ResponseCode.INVALID_PARAMETER.getValue()), headers,
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {OHServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleOHServiceException(OHServiceException ex) {
        return new FailureDto(ResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR.getValue());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected FailureDto handleGenericException(Exception ex) {
        return new FailureDto(ResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR.getValue());
    }

}
