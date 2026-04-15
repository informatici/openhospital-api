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
 * Declares a single upstream path and the HTTP methods that are permitted on it.
 *
 * <p>Path matching uses a <em>prefix</em> strategy: a configured {@code path} of
 * {@code /documents} will match any incoming sub-path such as
 * {@code /documents/123} or {@code /documents/123/attachments}.</p>
 *
 * <p>Example YAML fragment:</p>
 * <pre>{@code
 * routes:
 *   - path: /documents
 *     methods:
 *       - GET
 *       - POST
 * }</pre>
 *
 * @param path    path prefix on the upstream service (e.g. {@code "/documents"});
 *                matched as a prefix against the incoming sub-path
 * @param methods HTTP methods permitted on this path (e.g. {@code "GET"}, {@code "POST"});
 *                defaults to an empty list if omitted in YAML
 * @author Steve Tsala
 */
public record PluginRoute(
	String path,
	@DefaultValue List<String> methods) {
}
