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
package org.isf.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;

import org.isf.security.jwt.JWTFilter;
import org.isf.security.jwt.TokenProvider;
import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import io.jsonwebtoken.JwtException;

class CustomLogoutHandlerTest {

	@Mock
	private HttpSession httpSession;

	@Mock
	private SessionAuditManager sessionAuditManager;

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private CustomLogoutHandler customLogoutHandler;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testLogout_WithBearerToken_RevokesToken() {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer some.jwt.token");

		customLogoutHandler.logout(request, response, null);

		verify(tokenProvider).revokeToken("some.jwt.token");
	}

	@Test
	void testLogout_WithoutAuthorizationHeader_DoesNotRevoke() {
		customLogoutHandler.logout(request, response, null);

		verify(tokenProvider, never()).revokeToken(anyString());
	}

	@Test
	void testLogout_WithEmptyBearerToken_DoesNotRevoke() {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer ");

		customLogoutHandler.logout(request, response, null);

		verify(tokenProvider, never()).revokeToken(anyString());
	}

	@Test
	void testLogout_RevocationJwtException_StillInvalidatesSession() {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer garbage.jwt.token");
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		doThrow(new JwtException("Invalid token")).when(tokenProvider).revokeToken("garbage.jwt.token");

		customLogoutHandler.logout(request, response, null);

		assertThat(session.isInvalid()).isTrue();
	}

	@Test
	void testLogout_RevocationIllegalArgumentException_StillInvalidatesSession() {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer garbage.jwt.token");
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		doThrow(new IllegalArgumentException("Empty token")).when(tokenProvider).revokeToken("garbage.jwt.token");

		customLogoutHandler.logout(request, response, null);

		assertThat(session.isInvalid()).isTrue();
	}

	@Test
	void testLogout_SessionAuditFailure_StillRevokesToken() throws OHServiceException {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer some.jwt.token");
		when(httpSession.getAttribute("sessionAuditId")).thenReturn(1);
		when(sessionAuditManager.getSessionAudit(1)).thenThrow(new OHServiceException(new OHExceptionMessage("Failure")));

		customLogoutHandler.logout(request, response, null);

		InOrder inOrder = inOrder(tokenProvider, httpSession);
		inOrder.verify(tokenProvider).revokeToken("some.jwt.token");
		inOrder.verify(httpSession).getAttribute("sessionAuditId");
	}

	@Test
	void testLogout_InvalidatesSessionAndClearsContext() {
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer some.jwt.token");
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);

		customLogoutHandler.logout(request, response, null);

		verify(tokenProvider).revokeToken("some.jwt.token");
		assertThat(session.isInvalid()).isTrue();
	}
}
