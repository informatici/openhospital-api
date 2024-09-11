/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is found.
 */
public class JWTFilter extends GenericFilterBean {

	public static final String AUTHORIZATION_HEADER = "Authorization";

	private final TokenProvider tokenProvider;

	public JWTFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
					throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		String jwt = resolveToken(httpServletRequest);

		if (StringUtils.hasText(jwt)) {
			TokenValidationResult validationResult = this.tokenProvider.validateToken(jwt);

			if (validationResult == null) {
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "Unknown token validation result.");
				return;
			}

			switch (validationResult) {
			case VALID:
				if (!this.tokenProvider.isTokenExpired(jwt)) {
					Authentication authentication = this.tokenProvider.getAuthentication(jwt);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired.");
					return;
				}
				break;

			case EXPIRED:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired.");
				return;

			case MALFORMED:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "JWT token is malformed.");
				return;

			case INVALID_SIGNATURE:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "JWT token has an invalid signature");
				return;

			case UNSUPPORTED:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "JWT token is unsupported.");
				return;

			case EMPTY_CLAIMS:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "JWT claims string is empty.");
				return;

			default:
				sendErrorResponse(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "Unknown token validation result.");
				return;
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
	}
}
