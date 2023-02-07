/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.shared.exceptions;

import java.util.Locale;

import org.isf.utils.exception.OHDBConnectionException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataLockFailureException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHInvalidSQLException;
import org.isf.utils.exception.OHOperationNotAllowedException;
import org.isf.utils.exception.OHReportException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OHResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {OHDataValidationException.class})
    protected ResponseEntity<Object> handleOHServiceValidationException(OHServiceException ex) {
        return buildResponseEntity(new OHAPIError(HttpStatus.BAD_REQUEST, ex));
    }

    @ExceptionHandler(value = {OHServiceException.class})
    protected ResponseEntity<Object> handleOHServiceException(OHServiceException ex) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHReportException.class})
    protected ResponseEntity<Object> handleReportException(OHReportException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHInvalidSQLException.class})
    protected ResponseEntity<Object> handleInvalidSqlException(OHInvalidSQLException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHOperationNotAllowedException.class})
    protected ResponseEntity<Object> handleOHServiceOperationNotAllowedException(OHOperationNotAllowedException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHDicomException.class})
    protected ResponseEntity<Object> handleDicomException(OHDicomException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHDBConnectionException.class})
    protected ResponseEntity<Object> handleDbConnectionException(OHDBConnectionException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.SERVICE_UNAVAILABLE, ex));
    }

    @ExceptionHandler(value = {OHDataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleDataIntegrityViolationException(OHDataIntegrityViolationException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHDataLockFailureException.class})
    protected ResponseEntity<Object> handleDbLockFailureException(OHDataLockFailureException ex, Locale locale) {
        return buildResponseEntity(new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(value = {OHAPIException.class})
    protected ResponseEntity<Object> handleOHAPIException(OHAPIException ex) {
        return buildResponseEntity(new OHAPIError(ex.getStatus(), ex));
    }

    private ResponseEntity<Object> buildResponseEntity(OHAPIError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
