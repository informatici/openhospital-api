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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Steve Tsala
 */
@Component
public class PluginRegistry implements IPluginRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginRegistry.class);

	/**
	 * Insertion-ordered map of pluginId → definition for all healthy plugins.
	 */
	private Map<String, PluginDefinition> registry = Collections.emptyMap();

	@Override
	public void register(Map<String, PluginDefinition> definitions) {
		this.registry = Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
		LOGGER.info("Plugin registry initialized with {} active plugin(s): {}", registry.size(), registry.keySet());
	}

	@Override
	public Optional<PluginDefinition> find(String pluginId) {
		return Optional.ofNullable(registry.get(pluginId));
	}

	@Override
	public Collection<PluginDefinition> all() {
		return registry.values();
	}

	@Override
	public int size() {
		return registry.size();
	}
}
