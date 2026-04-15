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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * Type-safe binding of the {@code plugins} namespace from {@code plugins.yaml}.
 *
 * <p>Activated by {@link PluginsConfig} via {@code @EnableConfigurationProperties}.
 *
 * <p>Example {@code plugins.yaml}:</p>
 * <pre>{@code
 * plugins:
 *   definitions:
 *     - id: smart-doc
 *       url: http://localhost:4000/api
 *       health: /health
 *       permissions:
 *         - role: admin
 *           privileges:
 *             - smart-doc.read
 *             - smart-doc.write
 * }</pre>
 *
 * @param definitions the list of plugin definitions loaded from {@code plugins.yaml};
 *                    defaults to an empty list when the file is absent or the key is omitted
 * @author Steve Tsala
 */
@ConfigurationProperties(prefix = "plugins")
public record PluginProperties(
	@DefaultValue List<PluginDefinition> definitions) {
}
