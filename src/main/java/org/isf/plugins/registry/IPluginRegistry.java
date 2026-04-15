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
package org.isf.plugins.registry;

import org.isf.plugins.config.PluginDefinition;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Runtime store of healthy plugin definitions.
 *
 * <p>Populated once at startup by {@link org.isf.plugins.health.IPluginHealthChecker}.
 * After startup the registry is effectively immutable and safe for concurrent reads
 * with no additional synchronization required during normal request processing.
 *
 * @author Steve Tsala
 */
public interface IPluginRegistry {

	/**
	 * Replaces the current registry contents with the supplied map.
	 *
	 * <p>Intended to be called exactly once at application startup after all plugin
	 * health checks have completed. Subsequent calls replace the entire registry.
	 *
	 * @param definitions map of plugin ID → {@link PluginDefinition} for all healthy plugins;
	 *                    must not be {@code null}
	 */
	void register(Map<String, PluginDefinition> definitions);

	/**
	 * Returns the plugin definition for the given ID, if it is registered and healthy.
	 *
	 * @param pluginId the unique plugin identifier (e.g. {@code "smart-doc"})
	 * @return an {@link Optional} containing the definition, or empty if the plugin is
	 * unknown or was excluded due to a failed health check
	 */
	Optional<PluginDefinition> find(String pluginId);

	/**
	 * Returns all currently registered plugin definitions.
	 *
	 * @return unmodifiable collection of all healthy plugin definitions; never {@code null}
	 */
	Collection<PluginDefinition> all();

	/**
	 * Returns the number of currently registered plugins.
	 *
	 * @return count of healthy registered plugins
	 */
	int size();
}
