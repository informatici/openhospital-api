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
		String expiredToken = "expired.token";
		when(tokenProvider.validateToken(expiredToken)).thenReturn(TokenValidationResult.EXPIRED);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + expiredToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus()); // Ensure response status is 401 for expired token
	}

	@Test
	public void testDoFilter_MalformedToken() throws ServletException, IOException {
		String malformedToken = "malformed.token";
		when(tokenProvider.validateToken(malformedToken)).thenReturn(TokenValidationResult.MALFORMED);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + malformedToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for malformed token
	}

	@Test
	public void testDoFilter_NoToken() throws ServletException, IOException {
		jwtFilter.doFilter(request, response, filterChain);
		verify(filterChain).doFilter(request, response); // Ensure the filter chain continues even without a token
	}

	@Test
	public void testDoFilter_InvalidSignatureToken() throws ServletException, IOException {
		String invalidSignatureToken = "eyJhbGciOiJIUzI1NiJ9.MISSING_PART.HMAC_SIGNATURE";
		when(tokenProvider.validateToken(invalidSignatureToken)).thenReturn(TokenValidationResult.INVALID_SIGNATURE);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + invalidSignatureToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for invalid signature token
	}

	@Test
	public void testDoFilter_UnsupportedToken() throws ServletException, IOException {
		String unsupportedToken = "unsupported.token";
		when(tokenProvider.validateToken(unsupportedToken)).thenReturn(TokenValidationResult.UNSUPPORTED);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + unsupportedToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for unsupported token
	}

	@Test
	public void testDoFilter_EmptyClaimsToken() throws ServletException, IOException {
		String emptyClaimsToken = "empty.claims.token";
		when(tokenProvider.validateToken(emptyClaimsToken)).thenReturn(TokenValidationResult.EMPTY_CLAIMS);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + emptyClaimsToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for empty claims token
	}

	@Test
	public void testDoFilter_UnexpectedResult() throws ServletException, IOException {
		String unexpectedToken = "unexpected.token";
		when(tokenProvider.validateToken(unexpectedToken)).thenAnswer(invocation -> {
			// Return an unexpected result
			return TokenValidationResult.valueOf("UNKNOWN"); // Use a valid value for testing
		});

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + unexpectedToken);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for unexpected result
	}

	@Test
	public void testDoFilter_NullValidationResult() throws ServletException, IOException {
		String tokenWithNullValidationResult = "null.validation.token";
		when(tokenProvider.validateToken(tokenWithNullValidationResult)).thenReturn(null);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenWithNullValidationResult);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(400, response.getStatus()); // Ensure response status is 400 for null validation result
	}

	@Test
	public void testDoFilter_ValidButExpiredToken() throws ServletException, IOException {
		String validTokenButExpired = "valid.token.but.expired";
		Authentication mockAuthentication = mock(Authentication.class);

		when(tokenProvider.validateToken(validTokenButExpired)).thenReturn(TokenValidationResult.VALID);
		when(tokenProvider.isTokenExpired(validTokenButExpired)).thenReturn(true); // Token is expired
		when(tokenProvider.getAuthentication(validTokenButExpired)).thenReturn(mockAuthentication);

		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + validTokenButExpired);

		jwtFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus()); // Ensure response status is 401 for expired token
	}
}