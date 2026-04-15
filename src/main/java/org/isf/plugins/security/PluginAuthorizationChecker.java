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
package org.isf.plugins.security;

import org.isf.plugins.config.PluginConfiguration;
import org.isf.plugins.config.PluginDefinition;
import org.isf.plugins.config.PluginPermission;
import org.isf.plugins.config.PluginRoute;
import org.isf.plugins.exception.PluginAccessDeniedException;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Steve Tsala
 */
@Component
public class PluginAuthorizationChecker implements IPluginAuthorizationChecker {

	private final IAuthenticationSupplier authenticationSupplier;

	public PluginAuthorizationChecker(IAuthenticationSupplier authenticationSupplier) {
		this.authenticationSupplier = authenticationSupplier;
	}

	@Override
	public void assertAccess(PluginDefinition plugin, String requestPath, String httpMethod) {
		AuthenticationContext ctx = authenticationSupplier.get();
		Authentication authentication = ctx.authentication();

		List<PluginPermission> permissions = getPluginPermissions(plugin, authentication);

		String userRole = ctx.role();

		boolean hasAccess = permissions.stream()
			.filter(p -> p.role() != null && p.role().equals(userRole))
			.flatMap(p -> p.routes() == null ? java.util.stream.Stream.empty() : p.routes().stream())
			.anyMatch(route -> pathMatches(route, requestPath) && methodMatches(route, httpMethod));

		if (!hasAccess) {
			throw new PluginAccessDeniedException(plugin.id(), authentication.getName());
		}
	}

	private static @NonNull List<PluginPermission> getPluginPermissions(PluginDefinition plugin, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("No authenticated principal found in SecurityContext");
		}

		PluginConfiguration configuration = plugin.configuration();

		// Restrictive default: no configuration or empty permissions → deny everyone.
		if (configuration == null) {
			throw new PluginAccessDeniedException(plugin.id(), authentication.getName());
		}

		List<PluginPermission> permissions = configuration.permissions();
		if (permissions == null || permissions.isEmpty()) {
			throw new PluginAccessDeniedException(plugin.id(), authentication.getName());
		}
		return permissions;
	}

	/**
	 * Returns {@code true} if {@code requestPath} starts with {@code route.path()},
	 * implementing prefix-based path matching.
	 *
	 * <p>Examples:</p>
	 * <ul>
	 *   <li>{@code route.path() = "/documents"}, {@code requestPath = "/documents"} → {@code true}</li>
	 *   <li>{@code route.path() = "/documents"}, {@code requestPath = "/documents/123"} → {@code true}</li>
	 *   <li>{@code route.path() = "/documents"}, {@code requestPath = "/other"} → {@code false}</li>
	 * </ul>
	 */
	private static boolean pathMatches(PluginRoute route, String requestPath) {
		if (route.path() == null || requestPath == null) {
			return false;
		}
		return requestPath.equals(route.path()) || requestPath.startsWith(route.path() + "/");
	}

	/**
	 * Returns {@code true} if the route's {@code methods} list contains
	 * {@code httpMethod} (case-insensitive).
	 */
	private static boolean methodMatches(PluginRoute route, String httpMethod) {
		if (route.methods() == null || httpMethod == null) {
			return false;
		}
		return route.methods().stream().anyMatch(m -> m.equalsIgnoreCase(httpMethod));
	}
}
