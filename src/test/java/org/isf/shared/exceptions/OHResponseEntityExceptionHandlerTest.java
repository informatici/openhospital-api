/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * This program is free and open source software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 */
package org.isf.shared.exceptions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class OHResponseEntityExceptionHandlerTest {

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		this.mvc = MockMvcBuilders
			.standaloneSetup(new ExceptionController())
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
	}

	@Test
	void clientErrorDoesNotExposeDebugInformation() throws Exception {
		mvc.perform(get("/client-error").accept("application/json"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Bill not found"))
			.andExpect(jsonPath("$.debugMessage").doesNotExist())
			.andExpect(jsonPath("$.stackTrace").doesNotExist());
	}

	@Test
	void serverErrorUsesGenericMessage() throws Exception {
		mvc.perform(get("/server-error").accept("application/json"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("The request could not be completed."))
			.andExpect(jsonPath("$.description").doesNotExist())
			.andExpect(jsonPath("$.debugMessage").doesNotExist())
			.andExpect(jsonPath("$.stackTrace").doesNotExist())
			.andExpect(content().string(not(containsString("database.internal"))));
	}

	@RestController
	private static class ExceptionController {

		@GetMapping("/client-error")
		void clientError() throws OHAPIException {
			throw new OHAPIException(new OHExceptionMessage("Bill not found"), HttpStatus.NOT_FOUND);
		}

		@GetMapping("/server-error")
		void serverError() throws OHServiceException {
			throw new OHServiceException(new OHExceptionMessage("database.internal"));
		}
	}
}
