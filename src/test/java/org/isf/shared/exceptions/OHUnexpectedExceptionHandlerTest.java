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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import org.isf.OpenHospitalApiApplication;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.login.dto.LoginRequest;
import org.isf.security.CustomAuthenticationManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Runs with the whole application context, since what matters here is the order of the two advices and what the security filters do with the exceptions
 * left to them.
 */
@SpringBootTest(classes = OpenHospitalApiApplication.class)
@AutoConfigureMockMvc
class OHUnexpectedExceptionHandlerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AgeTypeBrowserManager ageTypeManager;

	@MockitoBean
	private CustomAuthenticationManager authenticationManager;

	@Test
	@WithMockUser(username = "admin", authorities = { "agetypes.read" })
	void handleUnexpectedException_shouldReturnGenericMessage_whenExceptionIsNotHandled() throws Exception {
		when(ageTypeManager.getAgeType()).thenThrow(new NullPointerException("super secret SQL detail near 'table users'"));

		mvc.perform(get("/agetypes"))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("An internal error occurred"))
			.andExpect(content().string(not(containsString("stackTrace"))))
			.andExpect(content().string(not(containsString("debugMessage"))))
			.andExpect(content().string(not(containsString("NullPointerException"))))
			.andExpect(content().string(not(containsString("super secret SQL detail"))));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "agetypes.read" })
	void handleUnexpectedException_shouldNotApply_whenCauseIsServiceException() throws Exception {
		// as when the exception translator of the core throws an OHServiceException from a method that does not declare it
		when(ageTypeManager.getAgeType()).thenThrow(new UndeclaredThrowableException(new OHServiceException(new OHExceptionMessage("A user-facing message"))));

		mvc.perform(get("/agetypes"))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("A user-facing message"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "agetypes.read" })
	void handleUnexpectedException_shouldNotAnswer_whenClientIsDisconnected() throws Exception {
		when(ageTypeManager.getAgeType()).thenAnswer(invocation -> {
			throw new IOException("Broken pipe");
		});

		mvc.perform(get("/agetypes"))
			.andDo(log())
			.andExpect(content().string(""));

		// an empty body is also what the security filters answer: make sure the request got to the controller
		verify(ageTypeManager).getAgeType();
	}

	@Test
	void handleSecurityException_shouldReturnUnauthorized_whenAuthenticationFails() throws Exception {
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Wrong password"));

		mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new LoginRequest("admin", "wrong"))))
			.andDo(log())
			.andExpect(status().isUnauthorized());
	}

	@Test
	void handleSecurityException_shouldNotApply_whenCauseIsServiceException() throws Exception {
		// UserDetailsServiceImpl wraps the service errors this way: a database failure during the login must not look like wrong credentials
		when(authenticationManager.authenticate(any()))
			.thenThrow(new UsernameNotFoundException("admin authentication failed.", new OHServiceException(new OHExceptionMessage("A user-facing message"))));

		mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new LoginRequest("admin", "secret"))))
			.andDo(log())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("A user-facing message"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "agetypes.read" })
	void handleSecurityException_shouldReturnForbidden_whenAccessIsDenied() throws Exception {
		when(ageTypeManager.getAgeType()).thenThrow(new AccessDeniedException("Access is denied"));

		mvc.perform(get("/agetypes"))
			.andDo(log())
			.andExpect(status().isForbidden());
	}
}
