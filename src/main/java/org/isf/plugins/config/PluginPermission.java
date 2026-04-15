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
package org.isf.plugins.config;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * Represents the access-control entry for a single user-group role within a plugin definition.
 *
 * <p>Each {@code PluginPermission} binds a user-group role (matched against
 * {@code UserGroup.code}, e.g. {@code "admin"}) to the set of upstream routes
 * ({@link PluginRoute}) that members of that group may access. Authorization is enforced
 * by comparing the authenticated user's group code against {@link #role()} and then
 * verifying that the request path and HTTP method match at least one declared
 * {@link PluginRoute}.</p>
 *
 * <p>Example YAML fragment:</p>
 * <pre>{@code
 * permissions:
 *   - role: admin
 *     routes:
 *       - path: /documents
 *         methods:
 *           - GET
 *           - POST
 * }</pre>
 *
 * @param role   user-group code (e.g. {@code "admin"}, {@code "doctor"}) matched against
 *               {@code UserGroup.getCode()} for the authenticated user
 * @param routes upstream path + method combinations accessible to this role;
 *               defaults to an empty list if omitted in YAML
 * @author Steve Tsala
 */
public record PluginPermission(
	String role,
	@DefaultValue List<PluginRoute> routes) {
}
