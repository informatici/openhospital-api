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
package org.isf.plugins.proxy;

import org.isf.plugins.config.PluginDefinition;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Package-private helper responsible for assembling the target {@link URI} when
 * forwarding a request to an upstream plugin.
 *
 * <p>Extracted from {@link PluginRequestForwarder} to satisfy the Single Responsibility
 * Principle — URI construction is a distinct concern from HTTP dispatch.</p>
 *
 * @author Steve Tsala
 */
abstract class PluginUriBuilder {
	/**
	 * Builds the full target URI by joining the plugin base URL, sub-path, and optional
	 * query string. Ensures no double slashes at the join point.
	 *
	 * @param plugin      plugin definition (provides base URL)
	 * @param subPath     path after the plugin prefix; must start with {@code /} or be empty
	 * @param queryString raw query string, may be {@code null} or empty
	 * @return the resolved {@link URI}
	 */
	static URI build(PluginDefinition plugin, String subPath, String queryString) {
		String base = plugin.url().endsWith("/")
			? plugin.url().substring(0, plugin.url().length() - 1)
			: plugin.url();

		String path = StringUtils.hasText(subPath)
			? (subPath.startsWith("/") ? subPath : "/" + subPath)
			: "";

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base + path);

		if (StringUtils.hasText(queryString)) {
			builder.query(queryString);
		}

		return builder.build(true).toUri();
	}
}
