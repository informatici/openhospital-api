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

import org.springframework.security.core.Authentication;

/**
 * Value object that bundles the current {@link Authentication} with the resolved
 * user-group role (i.e. {@code UserGroup.getCode()}) of the authenticated user.
 *
 * <p>The role is resolved by looking up the full {@code User} entity via
 * {@code UserBrowsingManager.getUserByName(username)} and reading
 * {@code user.getUserGroupName().getCode()}. If resolution fails (e.g. due to a
 * database error), {@code role} is set to {@code null} and access is denied by
 * the authorization checker.</p>
 *
 * @param authentication the current Spring Security {@link Authentication} object
 * @param role           the user-group code for the authenticated user, or {@code null}
 *                       if the role could not be resolved
 * @author Steve Tsala
 */
public record AuthenticationContext(Authentication authentication, String role) {
}
