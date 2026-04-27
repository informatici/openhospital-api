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
 * Thrown when a request targets a plugin whose ID is not present in the
 * {@link org.isf.plugins.registry.PluginRegistry} — either because the plugin was never
 * defined, or because its health check failed at startup and it was skipped.
 *
 * <p>Mapped to HTTP {@code 404 Not Found} by
 * {@link org.isf.plugins.proxy.PluginProxyController}.</p>
 *
 * @author Steve Tsala
 */
public class PluginNotFoundException extends RuntimeException {

	public PluginNotFoundException(String pluginId) {
		super("Plugin not found or unavailable: '" + pluginId + "'");
	}
}
