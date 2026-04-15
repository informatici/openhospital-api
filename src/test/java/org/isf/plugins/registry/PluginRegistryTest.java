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

import org.isf.OpenHospitalApiApplication;
import org.isf.plugins.config.PluginDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OpenHospitalApiApplication.class)
class PluginRegistryTest {

	private static final PluginDefinition SMART_DOC = new PluginDefinition(
		"smart-doc", "http://localhost:4000/api", "/health", null);
	private static final PluginDefinition REPORTS = new PluginDefinition(
		"reports", "http://localhost:5000", "/ping", null);

	@Autowired
	private IPluginRegistry registry;

	@Test
	@DisplayName("Should register a single plugin and find it by ID")
	void shouldFindPluginAfterRegisteringIt() {
		registry.register(Map.of("smart-doc", SMART_DOC));

		assertThat(registry.size()).isEqualTo(1);
		Optional<PluginDefinition> found = registry.find("smart-doc");
		assertThat(found).isPresent();
		assertThat(found.get().id()).isEqualTo("smart-doc");
		assertThat(found.get().url()).isEqualTo("http://localhost:4000/api");
	}

	@Test
	@DisplayName("Should register multiple plugins and find them by ID")
	void shouldFindAllPluginsAfterRegisteringMultiple() {
		registry.register(Map.of("smart-doc", SMART_DOC, "reports", REPORTS));

		assertThat(registry.size()).isEqualTo(2);
		assertThat(registry.find("smart-doc")).isPresent();
		assertThat(registry.find("reports")).isPresent();
	}

	@Test
	@DisplayName("Finding an unknown plugin ID should return empty")
	void shouldReturnEmptyWhenPluginNotFound() {
		registry.register(Map.of("smart-doc", SMART_DOC));

		assertThat(registry.find("no-such-plugin")).isEmpty();
	}

	@Test
	@DisplayName("Should return all registered plugin definitions")
	void shouldReturnAllRegisteredDefinitions() {
		registry.register(Map.of("smart-doc", SMART_DOC, "reports", REPORTS));

		assertThat(registry.all())
			.hasSize(2)
			.containsExactlyInAnyOrder(SMART_DOC, REPORTS);
	}

	@Test
	@DisplayName("Should register a new set of plugins and replace the old contents")
	void shouldReplaceContentsOnSecondRegisterCall() {
		registry.register(Map.of("smart-doc", SMART_DOC));
		assertThat(registry.size()).isEqualTo(1);

		registry.register(Map.of("reports", REPORTS));
		assertThat(registry.size()).isEqualTo(1);
		assertThat(registry.find("smart-doc")).isEmpty();
		assertThat(registry.find("reports")).isPresent();
	}

	@Test
	@DisplayName("Should register an empty map and clear the registry")
	void shouldClearRegistryWhenRegisteredWithEmptyMap() {
		registry.register(Map.of("smart-doc", SMART_DOC));
		registry.register(Map.of());

		assertThat(registry.size()).isZero();
		assertThat(registry.all()).isEmpty();
	}
}
