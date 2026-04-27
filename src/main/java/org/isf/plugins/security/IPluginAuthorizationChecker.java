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

import org.isf.plugins.config.PluginDefinition;
import org.isf.plugins.config.PluginPermission;
import org.isf.plugins.config.PluginRoute;

/**
 * Enforces plugin-level access control using a <em>restrictive</em> policy:
 * any route not explicitly declared in the plugin's configuration is blocked.
 *
 * <h3>Authorization model</h3>
 * Each plugin declares a {@code configuration.permissions} list. Every entry binds
 * a user-group role (matched against {@code UserGroup.getCode()}) to a set of
 * {@link org.isf.plugins.config.PluginRoute} objects, each specifying a path prefix
 * and a list of allowed HTTP methods.
 *
 * <p>Access is granted when <strong>all</strong> of the following hold:</p>
 * <ol>
 *   <li>The authenticated user's group code equals {@code permission.role()} for at
 *       least one entry in the permissions list.</li>
 *   <li>Within that matching entry, at least one route whose {@code path} is a
 *       <em>prefix</em> of the incoming sub-path also includes the request's HTTP
 *       method in its {@code methods} list.</li>
 * </ol>
 *
 * <p>If the plugin has no {@code configuration} or an empty {@code permissions}
 * list, access is denied for <em>all</em> users.</p>
 *
 * <h3>Path matching</h3>
 * Matching is a simple prefix check: a configured path of {@code /documents} matches
 * {@code /documents}, {@code /documents/123}, {@code /documents/123/attachments}, etc.
 *
 * <h3>Integration</h3>
 * The check runs entirely within the controller layer, after the {@code JWTFilter} has
 * already validated the token and populated the security context. No changes to
 * {@link org.isf.config.SecurityConfig} are required.
 *
 * @author Steve Tsala
 */
public interface IPluginAuthorizationChecker {

	/**
	 * {@inheritDoc}
	 *
	 * <p>Enforcement algorithm (restrictive — deny by default):</p>
	 * <ol>
	 *   <li>Verify a valid, authenticated principal exists.</li>
	 *   <li>If the plugin has no {@code configuration} or empty {@code permissions}
	 *       → deny.</li>
	 *   <li>Find all {@link PluginPermission} entries whose {@code role} matches the
	 *       user's group code (case-sensitive).</li>
	 *   <li>Within those entries, look for any {@link PluginRoute} where:
	 *       <ul>
	 *         <li>{@code requestPath} starts with {@code route.path()} (prefix match), and</li>
	 *         <li>{@code httpMethod} is contained in {@code route.methods()}
	 *             (case-insensitive).</li>
	 *       </ul></li>
	 *   <li>If no matching route is found → deny.</li>
	 * </ol>
	 *
	 * @throws IllegalStateException if there is no authenticated principal in the
	 *                               security context
	 */
	void assertAccess(PluginDefinition plugin, String requestPath, String httpMethod);
}
