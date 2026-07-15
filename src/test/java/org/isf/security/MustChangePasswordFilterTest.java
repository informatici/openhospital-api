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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.security.jwt.JWTFilter;
import org.isf.security.jwt.MustChangePasswordFilter;
import org.isf.security.jwt.TokenProvider;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class MustChangePasswordFilterTest {

	private static final String TOKEN = "must.change.token";
	private static final String USERNAME = "testuser";

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private UserBrowsingManager userManager;

	@Mock
	private FilterChain filterChain;

	private MustChangePasswordFilter filter;

	private MockHttpServletResponse response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		filter = new MustChangePasswordFilter(tokenProvider, userManager);
		response = new MockHttpServletResponse();
	}

	@Test
	void testDoFilter_NoToken() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/patients");

		filter.doFilter(request, response, filterChain);

		// Ensure the filter chain continues without any database access
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(userManager);
	}

	@Test
	void testDoFilter_StaleTokenWithoutClaim() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("GET", "/patients");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		// Ensure a token issued before the flag was raised cannot bypass the restriction: the claim is never trusted, only the database is
		assertThat(response.getStatus()).isEqualTo(403);
		verify(tokenProvider, never()).getMustChangePasswordFromToken(TOKEN);
		verifyNoInteractions(filterChain);
	}

	@Test
	void testDoFilter_MustChangePassword_OtherEndpoint() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("GET", "/patients");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		// Ensure the request is rejected with 403 and the JSON error body
		assertThat(response.getStatus()).isEqualTo(403);
		assertThat(response.getContentAsString()).contains("The password must be changed before using the API.");
	}

	@Test
	void testDoFilter_MustChangePassword_PasswordLeaseExpired() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("POST", "/admissions");
		mockUserInDatabase(false, true);

		filter.doFilter(request, response, filterChain);

		// Ensure the request is rejected also when only the password lease has expired
		assertThat(response.getStatus()).isEqualTo(403);
	}

	@Test
	void testDoFilter_MustChangePassword_UpdateProfileAllowed() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("PUT", "/users/me");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		// Ensure the password change endpoint stays reachable
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void testDoFilter_MustChangePassword_RetrieveProfileAllowed() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("GET", "/users/me");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void testDoFilter_MustChangePassword_LogoutAllowed() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("POST", "/auth/logout");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void testDoFilter_MustChangePassword_RefreshTokenAllowed() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("POST", "/auth/refresh-token");
		mockUserInDatabase(true, false);

		filter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void testDoFilter_MustChangePassword_ClearedInDatabase() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("GET", "/patients");
		mockUserInDatabase(false, false);

		filter.doFilter(request, response, filterChain);

		// Ensure the same token is released right after the password change
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void testDoFilter_MustChangePassword_DatabaseError() throws ServletException, IOException, OHServiceException {
		MockHttpServletRequest request = requestWithToken("GET", "/patients");
		when(tokenProvider.getUsernameFromToken(TOKEN)).thenReturn(USERNAME);
		when(userManager.getUserByName(USERNAME)).thenThrow(new OHServiceException(new OHExceptionMessage("Database error")));

		filter.doFilter(request, response, filterChain);

		// Ensure the filter fails closed when the flag cannot be checked
		assertThat(response.getStatus()).isEqualTo(403);
		verifyNoInteractions(filterChain);
	}

	@Test
	void testDoFilter_MustChangePassword_UserNotFound() throws ServletException, IOException {
		MockHttpServletRequest request = requestWithToken("GET", "/patients");
		when(tokenProvider.getUsernameFromToken(TOKEN)).thenReturn(USERNAME);

		filter.doFilter(request, response, filterChain);

		// Ensure the filter chain continues when the user no longer exists
		verify(filterChain).doFilter(request, response);
	}

	// Helper method to build a request carrying the bearer token
	private MockHttpServletRequest requestWithToken(String method, String requestURI) {
		MockHttpServletRequest request = new MockHttpServletRequest(method, requestURI);
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + TOKEN);
		return request;
	}

	// Helper method to mock the persisted state of the user resolved from the token, flagged or not in the database
	private void mockUserInDatabase(boolean passwdMustChange, boolean passwordExpired) throws OHServiceException {
		User user = new User();
		user.setUserName(USERNAME);
		user.setPasswdMustChange(passwdMustChange);

		when(tokenProvider.getUsernameFromToken(TOKEN)).thenReturn(USERNAME);
		when(userManager.getUserByName(USERNAME)).thenReturn(user);
		when(userManager.isPasswordExpired(user)).thenReturn(passwordExpired);
	}
}
