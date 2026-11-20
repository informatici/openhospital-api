/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.shared.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.DisconnectedClientHelper;

/**
 * Last resort for the exceptions that {@link OHResponseEntityExceptionHandler} does not handle. It is a separate advice with the lowest precedence because
 * Spring asks each advice for the exception and then for its causes: in the same class a handler for {@code Exception} would come before the handlers of a
 * wrapped {@code OHServiceException} (e.g. one thrown by the core through a proxy).
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class OHUnexpectedExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OHUnexpectedExceptionHandler.class);

	private static final String GENERIC_ERROR_MESSAGE = "An internal error occurred";

	/**
	 * Authentication and authorization failures raised while handling a request (e.g. wrong credentials on login) are rethrown, so that Spring Security keeps
	 * answering 401 or 403 instead of the generic error below.
	 */
	@ExceptionHandler(value = {AuthenticationException.class, AccessDeniedException.class})
	protected void handleSecurityException(RuntimeException ex) {
		throw ex;
	}

	/**
	 * The full exception is logged for diagnostics, while the response only carries a generic message and the status, so that no stack trace, exception class
	 * name or internal/SQL detail is disclosed to the client.
	 */
	@ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<Object> handleUnexpectedException(Exception ex) throws Exception {
		if (DisconnectedClientHelper.isClientDisconnectedException(ex)) {
			// nobody is left to answer: rethrown, Spring handles it without an error in the log
			throw ex;
		}
		LOGGER.error("Unhandled exception while processing request", ex);
		OHAPIError apiError = new OHAPIError(HttpStatus.INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
