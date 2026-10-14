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

import io.jsonwebtoken.JwtException;

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
		// login re-authenticates and re-issues the token with a freshly recomputed flag, so a stale token attached by the client must never block it
		{ "POST", "/auth/login" },
		// normally consumed by Spring's LogoutFilter before this filter runs, kept as belt-and-braces
		{ "POST", "/auth/logout" },
		{ "POST", "/auth/refresh-token" },
		// the healthcheck must never require any authentication state
		{ "GET", "/healthcheck" }
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

		String jwt = JWTFilter.resolveToken(httpServletRequest);

		// the whitelist is checked first so that the endpoints needed to change the password do not cost a database access
		if (StringUtils.hasText(jwt) && !isAllowed(httpServletRequest) && mustChangePassword(jwt)) {
			JWTFilter.sendErrorResponse(httpServletResponse, HttpServletResponse.SC_FORBIDDEN,
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
		} catch (OHServiceException | JwtException e) {
			// JwtException covers a token expiring between the JWTFilter check and this re-parse
			LOGGER.error("Unable to check the must-change-password flag, keeping the restriction in place", e);
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
}
