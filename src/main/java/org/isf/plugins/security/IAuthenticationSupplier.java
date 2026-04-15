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

/**
 * Functional interface that resolves the current authentication context — both the
 * raw {@link org.springframework.security.core.Authentication} and the user's resolved
 * role (user-group code) — for the incoming request.
 *
 * <p>The returned {@link AuthenticationContext} bundles:</p>
 * <ul>
 *   <li>the Spring Security {@link org.springframework.security.core.Authentication} object
 *       (used for username, authorities, and authenticated flag), and</li>
 *   <li>the user-group {@code role} string resolved by looking up the full {@code User}
 *       entity via {@code UserBrowsingManager.getUserByName(username)} and reading
 *       {@code user.getUserGroupName().getCode()}.</li>
 * </ul>
 *
 * <p>If role resolution fails (e.g. database error) the {@code role} field is {@code null}.
 * The authorization checker treats a {@code null} role as an unknown group and denies
 * access.</p>
 *
 * <p>Abstracting this behind an interface allows controller and service classes to avoid
 * static calls to {@code SecurityContextHolder}, making them fully testable without
 * requiring a real security context or database.</p>
 *
 * <p>The default Spring bean is registered in {@link PluginSecurityConfig}.</p>
 *
 * @author Steve Tsala
 */
@FunctionalInterface
public interface IAuthenticationSupplier {

	/**
	 * Returns the current {@link AuthenticationContext} for the active request.
	 *
	 * @return the authentication context (authentication + role); never {@code null},
	 *         but {@link AuthenticationContext#authentication()} may be {@code null}
	 *         if no principal is present, and {@link AuthenticationContext#role()} may
	 *         be {@code null} if role resolution failed
	 */
	AuthenticationContext get();
}
