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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verifies that the controller advice masks internal error details (stack traces, exception class names, raw/SQL messages) and only returns a clean,
 * user-facing message to the client.
 */
class OHResponseEntityExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders
			.standaloneSetup(new FailingController())
			.setControllerAdvice(new OHResponseEntityExceptionHandler())
			.build();
	}

	@Test
	void unhandledRuntimeExceptionReturnsGenericMessageWithoutDetails() throws Exception {
		this.mockMvc
			.perform(get("/test/runtime-exception").accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("An internal error occurred"))
			.andExpect(content().string(not(containsString("stackTrace"))))
			.andExpect(content().string(not(containsString("debugMessage"))))
			.andExpect(content().string(not(containsString("NullPointerException"))))
			.andExpect(content().string(not(containsString("super secret SQL detail"))));
	}

	@Test
	void ohServiceExceptionReturnsUserFacingMessageWithoutDetails() throws Exception {
		this.mockMvc
			.perform(get("/test/service-exception").accept(MediaType.APPLICATION_JSON))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("A user-facing message"))
			.andExpect(content().string(not(containsString("stackTrace"))))
			.andExpect(content().string(not(containsString("debugMessage"))))
			.andExpect(content().string(not(containsString("OHServiceException"))));
	}

	@RestController
	private static class FailingController {

		@GetMapping("/test/runtime-exception")
		public ResponseEntity<String> throwRuntimeException() {
			throw new NullPointerException("super secret SQL detail near 'table users'");
		}

		@GetMapping("/test/service-exception")
		public ResponseEntity<String> throwServiceException() throws OHServiceException {
			throw new OHServiceException(new OHExceptionMessage("A user-facing message"));
		}
	}
}
