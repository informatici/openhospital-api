/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.time.LocalDateTime;

import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.ErrorDescription;
import org.springframework.http.HttpStatus;

/**
 * Exception DTO returned to API clients.
 *
 * <p>
 * Only the HTTP {@code status} and a clean, user-facing {@code message} are exposed. Stack traces, exception class names and raw internal/SQL details are
 * never serialized: they would disclose information about the server internals. Full exception details must be written to the application log only.
 * </p>
 *
 * @author antonio
 */
public class OHAPIError {

	private HttpStatus status;
	private String message;
	private LocalDateTime timestamp;
	private ErrorDescription description;

	public OHAPIError(HttpStatus status, OHServiceException ex) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = ex.getMessages().get(0).getMessage();
		this.description = ex.getMessages().get(0).getDescription();
	}

	public OHAPIError(HttpStatus status, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public ErrorDescription getDescription() {
		return description;
	}

	public void setDescription(ErrorDescription description) {
		this.description = description;
	}

}
