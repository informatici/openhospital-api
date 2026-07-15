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
package org.isf.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Restricts users that must change their password (OP-896) to the endpoints needed to change it. The must-change-password state is deliberately checked
 * against the database on every request carrying a bearer token: the claim embedded in the JWT token by {@link TokenProvider} only informs the client and is
 * never trusted by the server, so a token issued before an administrator forced the change (or before the password lease expired) cannot be used to keep
 * working until token invalidation (OP-1342) is available.
 */
public class MustChangePasswordFilter extends GenericFilterBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(MustChangePasswordFilter.class);

	private static final String[][] ALLOWED_ENDPOINTS = {
		{ "GET", "/users/me" },
		{ "PUT", "/users/me" },
		{ "POST", "/auth/logout" },
		{ "POST", "/auth/refresh-token" }
	};

	private final TokenProvider tokenProvider;

	private final UserBrowsingManager userManager;

	public MustChangePasswordFilter(TokenProvider tokenProvider, UserBrowsingManager userManager) {
		this.tokenProvider = tokenProvider;
		this.userManager = userManager;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
					throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		String jwt = resolveToken(httpServletRequest);

		// the whitelist is checked first so that the endpoints needed to change the password do not cost a database access
		if (StringUtils.hasText(jwt) && !isAllowed(httpServletRequest) && mustChangePassword(jwt)) {
			sendErrorResponse(httpServletResponse, HttpServletResponse.SC_FORBIDDEN,
				"The password must be changed before using the API.");
			return;
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	/**
	 * Checks the database to determine whether the user has to change the password. Fails closed: the restriction is kept in place when the check cannot be
	 * performed.
	 */
	private boolean mustChangePassword(String jwt) {
		try {
			User user = this.userManager.getUserByName(this.tokenProvider.getUsernameFromToken(jwt));
			return user != null && (user.isPasswdMustChange() || this.userManager.isPasswordExpired(user));
		} catch (OHServiceException e) {
			LOGGER.error("Unable to check the must-change-password flag, keeping the restriction in place");
			return true;
		}
	}

	private boolean isAllowed(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		for (String[] endpoint : ALLOWED_ENDPOINTS) {
			if (endpoint[0].equals(request.getMethod()) && endpoint[1].equals(path)) {
				return true;
			}
		}
		return false;
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(JWTFilter.AUTHORIZATION_HEADER);
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
