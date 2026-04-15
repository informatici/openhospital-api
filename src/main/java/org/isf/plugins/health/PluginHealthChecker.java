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
package org.isf.plugins.health;

import org.isf.plugins.config.PluginDefinition;
import org.isf.plugins.config.PluginProperties;
import org.isf.plugins.registry.IPluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steve Tsala
 */
@Component
public class PluginHealthChecker implements IPluginHealthChecker {

	/**
	 * Maximum seconds to wait for a plugin's health endpoint to respond.
	 */
	static final int HEALTH_CHECK_TIMEOUT_SECONDS = 3;
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginHealthChecker.class);
	private final PluginProperties pluginProperties;
	private final IPluginRegistry pluginRegistry;
	private final RestClient restClient;

	public PluginHealthChecker(PluginProperties pluginProperties, IPluginRegistry pluginRegistry) {
		this.pluginProperties = pluginProperties;
		this.pluginRegistry = pluginRegistry;
		this.restClient = RestClient.builder()
			.requestInitializer(request -> {
				request.getHeaders().set("Connection", "close");
			})
			.build();
	}

	@Override
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady(ApplicationReadyEvent event) {
		List<PluginDefinition> definitions = pluginProperties.definitions();

		if (definitions.isEmpty()) {
			LOGGER.info("No plugins configured. Plugin gateway is inactive.");
			return;
		}

		LOGGER.info("Starting health checks for {} configured plugin(s)...", definitions.size());

		Map<String, PluginDefinition> healthy = new LinkedHashMap<>();

		for (PluginDefinition plugin : definitions) {
			if (isHealthy(plugin)) {
				healthy.put(plugin.id(), plugin);
			}
		}

		pluginRegistry.register(healthy);

		int skipped = definitions.size() - healthy.size();
		if (skipped > 0) {
			LOGGER.warn("{} plugin(s) failed health checks and will not be available.", skipped);
		}
	}

	@Override
	public boolean isHealthy(PluginDefinition plugin) {
		String healthUrl = buildHealthUrl(plugin);
		try {
			LOGGER.debug("Checking health of plugin '{}' at '{}'...", plugin.id(), healthUrl);

			ResponseEntity<Void> response = restClient.get()
				.uri(healthUrl)
				.retrieve()
				.onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
					// Don't throw — we handle non-2xx below via the response object
				})
				.toBodilessEntity();

			if (response.getStatusCode().is2xxSuccessful()) {
				LOGGER.info("Plugin '{}' is healthy (HTTP {}).", plugin.id(), response.getStatusCode().value());
				return true;
			} else {
				LOGGER.warn("Plugin '{}' health check at '{}' returned HTTP {}. Plugin will be skipped.",
					plugin.id(), healthUrl, response.getStatusCode().value());
				return false;
			}
		} catch (RestClientException ex) {
			LOGGER.warn("Plugin '{}' health check at '{}' failed: {}. Plugin will be skipped.",
				plugin.id(), healthUrl, ex.getMessage());
			return false;
		}
	}

	private String buildHealthUrl(PluginDefinition plugin) {
		String base = plugin.url().endsWith("/")
			? plugin.url().substring(0, plugin.url().length() - 1)
			: plugin.url();
		String path = plugin.health().startsWith("/") ? plugin.health() : "/" + plugin.health();
		return base + path;
	}
}
