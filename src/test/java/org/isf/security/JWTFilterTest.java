/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.isf.security.jwt.JWTFilter;
import org.isf.security.jwt.TokenProvider;
import org.isf.security.jwt.TokenValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

public class JWTFilterTest {

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JWTFilter jwtFilter;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void testDoFilter_ValidToken() throws ServletException, IOException {
		// Mock valid token
		String validToken = "valid.token";
		Authentication mockAuthentication = mock(Authentication.class);

		when(tokenProvider.validateToken(validToken)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.isTokenExpired(validToken)).thenReturn(false);
		when(tokenProvider.getAuthentication(validToken)).thenReturn(mockAuthentication);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + validToken);

		jwtFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response); // Ensure the filter chain continues
	}

	@Test
	public void testDoFilter_ExpiredToken() throws ServletException, IOException {
		// Mock expired token
		String expiredToken = "expired.token";
		when(tokenProvider.validateToken(expiredToken)).thenReturn(TokenValidationResult.EXPIRED);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + expiredToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus()); // Ensure response status is 401 for expired token
	}

	@Test
	public void testDoFilter_MalformedToken() throws ServletException, IOException {
		// Mock malformed token
		String malformedToken = "malformed.token";
		when(tokenProvider.validateToken(malformedToken)).thenReturn(TokenValidationResult.MALFORMED);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + malformedToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for malformed token
	}

	@Test
	public void testDoFilter_NoToken() throws ServletException, IOException {
		// No Authorization header
		jwtFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response); // Ensure the filter chain continues even without a token
	}
}
