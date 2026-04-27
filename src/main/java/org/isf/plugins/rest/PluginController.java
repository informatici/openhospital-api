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
package org.isf.plugins.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.isf.plugins.config.PluginDefinition;
import org.isf.plugins.exception.PluginAccessDeniedException;
import org.isf.plugins.exception.PluginNotFoundException;
import org.isf.plugins.proxy.IPluginRequestForwarder;
import org.isf.plugins.registry.IPluginRegistry;
import org.isf.plugins.security.AuthenticationContext;
import org.isf.plugins.security.IAuthenticationSupplier;
import org.isf.plugins.security.IPluginAuthorizationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.Collection;

/**
 * Controller for all {@code /plugins} routes.
 *
 * @author Steve Tsala
 */
@RestController
@RequestMapping("/plugins")
@Tag(name = "Plugins", description = "Dynamic gateway to registered external plugin services")
@SecurityRequirement(name = "bearerAuth")
public class PluginController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginController.class);

	private final IPluginRegistry pluginRegistry;
	private final IPluginAuthorizationChecker authorizationChecker;
	private final IPluginRequestForwarder requestForwarder;
	private final IAuthenticationSupplier authenticationSupplier;

	public PluginController(IPluginRegistry pluginRegistry, IPluginAuthorizationChecker authorizationChecker, IPluginRequestForwarder requestForwarder, IAuthenticationSupplier authenticationSupplier) {
		this.pluginRegistry = pluginRegistry;
		this.authorizationChecker = authorizationChecker;
		this.requestForwarder = requestForwarder;
		this.authenticationSupplier = authenticationSupplier;
	}

	/**
	 * Reads the raw request body, bypassing any servlet wrapper layers.
	 *
	 * <p>Spring's multipart support wraps the original request in a
	 * {@link jakarta.servlet.http.HttpServletRequestWrapper} (specifically
	 * {@code StandardMultipartHttpServletRequest}) that may replace the
	 * {@code InputStream} with a parsed view of the parts. Unwrapping to the
	 * underlying container request guarantees that the raw bytes — including the
	 * multipart boundary — are read unmodified, so they can be forwarded verbatim
	 * to the upstream plugin together with the original {@code Content-Type} header.
	 *
	 * @param request the incoming servlet request (possibly wrapped)
	 * @return the raw request body bytes; empty array if the body is absent
	 * @throws IOException if reading the stream fails
	 */
	private static byte[] readRawBody(HttpServletRequest request) throws IOException {
		HttpServletRequest target = request;
		while (target instanceof HttpServletRequestWrapper wrapper) {
			target = (HttpServletRequest) wrapper.getRequest();
		}
		return target.getInputStream().readAllBytes();
	}

	/**
	 * Returns the full list of plugins that are registered and healthy.
	 * Any authenticated user may call this endpoint.
	 *
	 * @return {@code 200 OK} with a JSON array of {@link PluginDefinition}
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List all registered plugins",
		description = "Returns all plugins that passed the startup health check and are currently available through the gateway.")
	@ApiResponse(responseCode = "200", description = "Plugin list returned successfully")
	public Collection<PluginDefinition> listPlugins() {
		return pluginRegistry.all();
	}

	/**
	 * Catch-all handler for every HTTP method under {@code /plugins/{id}/**}.
	 *
	 * @param request the original {@link HttpServletRequest}
	 * @return the upstream plugin's response, forwarded transparently
	 * @throws IOException if reading the request body fails
	 */
	@RequestMapping(value = "/{id}/{*path}")
	@Operation(summary = "Proxy a request to an external plugin",
		description = "Resolves the plugin by ID, checks authorization, then forwards the request " +
			"to the plugin's upstream URL. The sub-path after /plugins/{pluginId} is appended to the " +
			"plugin base URL. All HTTP methods are supported.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Upstream response forwarded successfully"),
		@ApiResponse(responseCode = "403", description = "Authenticated user lacks required privileges for this plugin"),
		@ApiResponse(responseCode = "404", description = "Plugin not found or unavailable"),
		@ApiResponse(responseCode = "502", description = "Upstream plugin returned an unexpected error")
	})
	public ResponseEntity<byte[]> proxy(
		@Parameter(hidden = true) HttpServletRequest request,
		@PathVariable(required = false) String id,
		@PathVariable(required = false) String path) throws IOException {

		PluginDefinition plugin = pluginRegistry.find(id).orElseThrow(() -> new PluginNotFoundException(id));

		// Extract sub-path (everything after /plugins/{id}).
		// Spring's {*path} catch-all may or may not include a leading slash depending on
		// the container version, so normalise defensively.
		String rawPath = path != null ? path : "";
		String subPath = rawPath.startsWith("/") ? rawPath : "/" + rawPath;

		authorizationChecker.assertAccess(plugin, subPath, request.getMethod());

		AuthenticationContext authContext = authenticationSupplier.get();

		return requestForwarder.forward(plugin, subPath, request, authContext.authentication().getName(), authContext.authentication().getAuthorities());
	}

	@ExceptionHandler(PluginNotFoundException.class)
	public ResponseEntity<PluginErrorResponse> handlePluginNotFound(PluginNotFoundException ex) {
		LOGGER.warn("Plugin not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(new PluginErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(PluginAccessDeniedException.class)
	public ResponseEntity<PluginErrorResponse> handleAccessDenied(PluginAccessDeniedException ex) {
		LOGGER.warn("Plugin access denied: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(new PluginErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
	}

	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<byte[]> handleUpstreamError(RestClientResponseException ex) {
		LOGGER.warn("Upstream plugin error: HTTP {} — {}", ex.getStatusCode(), ex.getMessage());
		return ResponseEntity.status(ex.getStatusCode()).headers(ex.getResponseHeaders()).body(ex.getResponseBodyAsByteArray());
	}

	// -------------------------------------------------------------------------
	// Nested error response DTO
	// -------------------------------------------------------------------------

	/**
	 * Minimal error payload returned by the plugin gateway for {@code 404} and {@code 403}
	 * responses. Kept as a nested class since it is exclusive to this controller.
	 */
	public record PluginErrorResponse(int status, String message) {
	}
}
