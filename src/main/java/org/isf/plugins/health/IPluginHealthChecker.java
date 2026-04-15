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
package org.isf.plugins.health;

import org.isf.plugins.config.PluginDefinition;
import org.isf.plugins.registry.IPluginRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * Probes each configured plugin's health endpoint at application startup and populates
 * the {@link IPluginRegistry} with only the plugins that respond successfully.
 *
 * <h3>Startup sequence</h3>
 * <ol>
 *   <li>Listens for {@link ApplicationReadyEvent} — fired after the full Spring context
 *       and embedded server are up.</li>
 *   <li>For each {@link PluginDefinition}, issues a {@code GET} to
 *       {@code plugin.url + plugin.health}.</li>
 *   <li>Plugins that return any {@code 2xx} response are registered as healthy.</li>
 *   <li>Plugins that fail are logged at {@code WARN} level and excluded from the registry.
 *       The application continues to start normally.</li>
 * </ol>
 *
 * @author Steve Tsala
 */
public interface IPluginHealthChecker {

	/**
	 * Triggered once the application context is fully started.
	 * Iterates over all configured plugins, probes their health endpoints, and registers
	 * healthy ones into the {@link IPluginRegistry}.
	 *
	 * @param event the {@link ApplicationReadyEvent} (triggers the method)
	 */
	void onApplicationReady(ApplicationReadyEvent event);

	/**
	 * Probes a single plugin's health endpoint.
	 *
	 * @param plugin the plugin definition to check
	 * @return {@code true} if the health endpoint returned a {@code 2xx} status within the timeout
	 */
	boolean isHealthy(PluginDefinition plugin);
}
