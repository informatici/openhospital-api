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
 * Groups the access-control configuration for a single plugin.
 *
 * <p>Bound from the {@code configuration} key under a plugin definition in
 * {@code plugins.yaml}. A {@code null} or empty {@code permissions} list means that
 * <strong>no role has access</strong> — the gateway enforces a restrictive default.</p>
 *
 * <p>Example YAML fragment:</p>
 * <pre>{@code
 * configuration:
 * 	 bundle:
 * 	 	label: "Document Manager"
 * 	 	manifest: "mf-manifest.json"
 * 	    type: "module"
 * 	    location: "main"
 * 	    styles: "assets/styles.css"
 *   permissions:
 *     - role: admin
 *       routes:
 *         - path: /documents
 *           methods: [GET, POST, DELETE]
 * }</pre>
 *
 * @param permissions list of role-to-routes mappings; defaults to an empty list if omitted
 * @author Steve Tsala
 */
public record PluginConfiguration(
	PluginBundle bundle,
	@DefaultValue List<PluginPermission> permissions) {
}
