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

/**
 * Immutable description of a single external plugin registered with the gateway.
 *
 * <p>Instances are populated by Spring Boot's {@code @ConfigurationProperties} binding
 * from {@code plugins.yaml} via the canonical record constructor. Every component maps
 * directly to a YAML key under the {@code plugins.definitions[*]} list.</p>
 *
 * <p>Example YAML fragment:</p>
 * <pre>{@code
 * plugins:
 *   definitions:
 *     - id: smart-doc
 *       url: http://localhost:4000/api
 *       health: /health
 *       configuration:
 *         permissions:
 *           - role: admin
 *             routes:
 *               - path: /documents
 *                 methods: [GET, POST, DELETE]
 * }</pre>
 *
 * @param id            unique identifier used as the URL path segment in
 *                      {@code /plugins/{id}/**}; must be URL-safe (lowercase, hyphens allowed)
 * @param url           base URL of the upstream plugin service (no trailing slash);
 *                      all proxied requests are forwarded to {@code url + subPath}
 * @param health        health check path relative to {@link #url()} (e.g. {@code "/health"});
 *                      probed at startup — a non-2xx or connection failure excludes the plugin
 * @param configuration access-control configuration for this plugin; a {@code null} or
 *                      empty configuration means no role has access (restrictive default)
 * @author Steve Tsala
 */
public record PluginDefinition(
	String id,
	String url,
	String health,
	PluginConfiguration configuration) {
}
