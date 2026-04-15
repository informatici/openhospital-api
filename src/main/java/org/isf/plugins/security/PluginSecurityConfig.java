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
package org.isf.plugins.security;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Registers security-related beans used by the plugin subsystem.
 *
 * @author Steve Tsala
 */
@Configuration
public class PluginSecurityConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginSecurityConfig.class);

	/**
	 * Provides the {@link IAuthenticationSupplier} bean that resolves the current
	 * {@link AuthenticationContext} for each incoming request.
	 *
	 * <p>The role is resolved by calling
	 * {@link UserBrowsingManager#getUserByName(String)} with the authenticated
	 * username and reading {@code user.getUserGroupName().getCode()}. If resolution
	 * fails (e.g. database error or unknown user) a warning is logged and
	 * {@code role} is set to {@code null}, which will cause the authorization
	 * checker to deny access.</p>
	 *
	 * @param userBrowsingManager the manager used to look up the full {@link User} entity
	 * @return the {@link IAuthenticationSupplier} bean
	 */
	@Bean
	public IAuthenticationSupplier authenticationSupplier(UserBrowsingManager userBrowsingManager) {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				return new AuthenticationContext(authentication, null);
			}
			String role = resolveRole(authentication.getName(), userBrowsingManager);
			return new AuthenticationContext(authentication, role);
		};
	}

	private static String resolveRole(String username, UserBrowsingManager userBrowsingManager) {
		try {
			User user = userBrowsingManager.getUserByName(username);
			if (user == null || user.getUserGroupName() == null) {
				LOGGER.warn("Plugin auth: no user or group found for username '{}'", username);
				return null;
			}
			return user.getUserGroupName().getCode();
		} catch (OHServiceException e) {
			LOGGER.warn("Plugin auth: failed to resolve role for username '{}': {}", username, e.getMessage());
			return null;
		}
	}
}
