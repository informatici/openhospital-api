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
package org.isf.plugins.exception;

/**
 * Thrown when the authenticated user does not hold any of the privileges required to
 * access a plugin's routes, as declared in the plugin's {@code permissions} configuration.
 *
 * <p>Mapped to HTTP {@code 403 Forbidden} by
 * {@link org.isf.plugins.proxy.PluginProxyController}.</p>
 *
 * @author Steve Tsala
 */
public class PluginAccessDeniedException extends RuntimeException {

	public PluginAccessDeniedException(String pluginId, String username) {
		super("Access denied for user '" + username + "' to plugin '" + pluginId + "'");
	}
}
