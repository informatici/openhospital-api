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

import org.isf.plugins.config.*;
import org.isf.plugins.exception.PluginAccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Unit tests for {@link PluginAuthorizationChecker}.
 *
 * <p>The real {@link org.isf.menu.manager.UserBrowsingManager} bean is intentionally
 * <em>not</em> used here (it cannot be mocked with Byte Buddy on Java 25+).
 * Instead, the {@link IAuthenticationSupplier} is built as a simple lambda that reads
 * the Spring Security context and resolves the role from an in-memory
 * {@code Map<String, String>} (username → group code), bypassing the database entirely.</p>
 */
class PluginAuthorizationCheckerTest {

	/**
	 * In-memory username → role map used by the test supplier.
	 */
	private final Map<String, String> userRoles = new HashMap<>();

	private static final PluginBundle PLUGIN_BUNDLE = new PluginBundle("Smart Doc", "mf-manifest.json", "module", PluginLocation.MAIN, "assets/styles.css");

	private IPluginAuthorizationChecker checker;

	/**
	 * Build a PluginDefinition with a single-permission, single-route config.
	 */
	private static PluginDefinition pluginWithRoute(String role, String path, String... methods) {
		PluginRoute route = new PluginRoute(path, List.of(methods));
		PluginPermission perm = new PluginPermission(role, List.of(route));
		return new PluginDefinition("smart-doc", "http://localhost:4000", "/health", new PluginConfiguration(PLUGIN_BUNDLE, List.of(perm)));
	}

	/**
	 * Build a PluginDefinition with no configuration.
	 */
	private static PluginDefinition pluginWithNoConfiguration() {
		return new PluginDefinition("smart-doc", "http://localhost:4000", "/health", null);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	/**
	 * Build a PluginDefinition with an empty permissions list.
	 */
	private static PluginDefinition pluginWithEmptyPermissions() {
		return new PluginDefinition("smart-doc", "http://localhost:4000", "/health", new PluginConfiguration(PLUGIN_BUNDLE, List.of()));
	}

	@BeforeEach
	void setUp() {
		// Build a supplier that reads the Security context and looks up the role
		// in the local map — no UserBrowsingManager required.
		IAuthenticationSupplier supplier = () -> {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				return new AuthenticationContext(authentication, null);
			}
			String role = userRoles.get(authentication.getName());
			return new AuthenticationContext(authentication, role);
		};
		checker = new PluginAuthorizationChecker(supplier);
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	/**
	 * Populates the security context and registers the username → role mapping.
	 */
	private void authenticateAs(String username, String groupCode) {
		userRoles.put(username, groupCode);
		var auth = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// -------------------------------------------------------------------------
	// Restrictive default: no configuration / empty permissions → deny all
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should deny access when plugin has no configuration at all")
	void shouldDenyAccessWhenNoConfiguration() {
		authenticateAs("alice", "admin");

		assertThatThrownBy(() -> checker.assertAccess(pluginWithNoConfiguration(), "/documents", "GET")).isInstanceOf(PluginAccessDeniedException.class).hasMessageContaining("alice").hasMessageContaining("smart-doc");
	}

	@Test
	@DisplayName("Should deny access when plugin has an empty permissions list")
	void shouldDenyAccessWhenEmptyPermissions() {
		authenticateAs("alice", "admin");

		assertThatThrownBy(() -> checker.assertAccess(pluginWithEmptyPermissions(), "/documents", "GET")).isInstanceOf(PluginAccessDeniedException.class);
	}

	// -------------------------------------------------------------------------
	// Role matching
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should grant access when role, path prefix, and method all match")
	void shouldGrantAccessWhenRolePathAndMethodMatch() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET", "POST");

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET"));
	}

	@Test
	@DisplayName("Should deny access when user role does not match any permission entry")
	void shouldDenyAccessWhenRoleDoesNotMatch() {
		authenticateAs("bob", "nurse");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET")).isInstanceOf(PluginAccessDeniedException.class).hasMessageContaining("bob").hasMessageContaining("smart-doc");
	}

	@Test
	@DisplayName("Should grant access when user matches one of multiple permission groups")
	void shouldGrantAccessWhenUserMatchesOneOfMultiplePermissionGroups() {
		authenticateAs("bob", "doctor");

		PluginRoute adminRoute = new PluginRoute("/documents", List.of("GET", "POST", "DELETE"));
		PluginRoute doctorRoute = new PluginRoute("/documents", List.of("GET"));
		PluginPermission adminPerm = new PluginPermission("admin", List.of(adminRoute));
		PluginPermission doctorPerm = new PluginPermission("doctor", List.of(doctorRoute));
		PluginDefinition plugin = new PluginDefinition("smart-doc", "http://localhost:4000", "/health", new PluginConfiguration(PLUGIN_BUNDLE, List.of(adminPerm, doctorPerm)));

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents/42", "GET"));
	}

	// -------------------------------------------------------------------------
	// Path prefix matching
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should grant access when request path exactly matches configured path")
	void shouldGrantAccessOnExactPathMatch() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET"));
	}

	@Test
	@DisplayName("Should grant access when request path is a sub-path of the configured prefix")
	void shouldGrantAccessOnSubPathMatch() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents/12912", "GET"));
	}

	@Test
	@DisplayName("Should grant access on deeply nested sub-path")
	void shouldGrantAccessOnDeeplyNestedSubPath() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "DELETE");

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents/123/attachments", "DELETE"));
	}

	@Test
	@DisplayName("Should deny access when path does not start with configured prefix")
	void shouldDenyAccessWhenPathDoesNotMatchPrefix() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/reports", "GET")).isInstanceOf(PluginAccessDeniedException.class);
	}

	@Test
	@DisplayName("Should deny access to /documents-other when configured prefix is /documents")
	void shouldDenyAccessWhenPathSharesPrefixButIsNotSubPath() {
		authenticateAs("alice", "admin");
		// /documents-other must NOT match /documents
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/documents-other", "GET")).isInstanceOf(PluginAccessDeniedException.class);
	}

	// -------------------------------------------------------------------------
	// HTTP method matching
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should deny access when HTTP method is not in the allowed list")
	void shouldDenyAccessWhenMethodNotAllowed() {
		authenticateAs("alice", "doctor");
		PluginDefinition plugin = pluginWithRoute("doctor", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/documents", "DELETE")).isInstanceOf(PluginAccessDeniedException.class);
	}

	@Test
	@DisplayName("HTTP method matching should be case-insensitive")
	void shouldMatchMethodCaseInsensitively() {
		authenticateAs("alice", "admin");
		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "get");

		assertThatNoException().isThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET"));
	}

	// -------------------------------------------------------------------------
	// Unauthenticated / no principal
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should throw IllegalStateException when there is no authentication in the security context")
	void shouldThrowIllegalStateWhenNoAuthentication() {
		SecurityContextHolder.clearContext();

		assertThatThrownBy(() -> checker.assertAccess(pluginWithRoute("admin", "/documents", "GET"), "/documents", "GET")).isInstanceOf(IllegalStateException.class).hasMessageContaining("No authenticated principal");
	}

	@Test
	@DisplayName("Should throw IllegalStateException when authentication is present but not authenticated")
	void shouldThrowIllegalStateWhenPrincipalIsNotAuthenticated() {
		SecurityContextHolder.clearContext();

		assertThatThrownBy(() -> checker.assertAccess(pluginWithEmptyPermissions(), "/documents", "GET")).isInstanceOf(IllegalStateException.class);
	}

	// -------------------------------------------------------------------------
	// Role resolution failure (unknown user → null role) → deny
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Should deny access when the user has no role mapping (simulates OHServiceException / user not found)")
	void shouldDenyWhenRoleResolutionFails() {
		// "eve" is authenticated but has no entry in userRoles → supplier returns role=null
		var auth = new UsernamePasswordAuthenticationToken("eve", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(auth);
		// intentionally do NOT call userRoles.put("eve", ...) so role resolves to null

		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET")).isInstanceOf(PluginAccessDeniedException.class);
	}

	@Test
	@DisplayName("Should deny access when user is not found in the database (null returned)")
	void shouldDenyWhenUserNotFound() {
		// "ghost" is authenticated but has no role mapping → role=null → access denied
		var auth = new UsernamePasswordAuthenticationToken("ghost", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(auth);
		// intentionally do NOT add "ghost" to userRoles

		PluginDefinition plugin = pluginWithRoute("admin", "/documents", "GET");

		assertThatThrownBy(() -> checker.assertAccess(plugin, "/documents", "GET")).isInstanceOf(PluginAccessDeniedException.class);
	}
}
