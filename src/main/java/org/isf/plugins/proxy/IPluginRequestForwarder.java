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

import jakarta.servlet.http.HttpServletRequest;
import org.isf.plugins.config.PluginDefinition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Collection;
import java.util.Map;

/**
 * Constructs and dispatches HTTP requests to upstream plugin services, then returns
 * the raw response back to the caller unchanged.
 *
 * <h3>Request forwarding rules</h3>
 * <ul>
 *   <li>Target URL: {@code plugin.url + subPath [+ ?queryString]}</li>
 *   <li>Method, body, and all original headers are forwarded as-is, except {@code Host}
 *       (removed to avoid conflicts with the upstream server).</li>
 *   <li>The original {@code Authorization: Bearer <token>} header is forwarded so that
 *       the plugin can optionally re-validate it.</li>
 *   <li>Two identity headers are added:
 *     <ul>
 *       <li>{@code X-User} — the authenticated username</li>
 *       <li>{@code X-Permissions} — comma-separated granted authorities from the JWT</li>
 *     </ul>
 *   </li>
 *   <li>The full upstream response (status, headers, body) is returned unmodified.</li>
 * </ul>
 *
 * <h3>Error handling</h3>
 * If the upstream service returns an error HTTP status, that status and body are passed
 * through to the client without modification — the gateway does not swallow upstream errors.
 * {@link RestClientResponseException} is re-thrown as-is and handled by
 * {@link PluginProxyController}.
 *
 * @author Steve Tsala
 */
public interface IPluginRequestForwarder {

	/**
	 * Forwards an incoming request to the appropriate upstream plugin endpoint.
	 *
	 * @param plugin      the target plugin definition
	 * @param subPath     the path segment after {@code /plugins/{id}} (e.g. {@code "/documents/123"})
	 * @param request     the original HTTP servlet request (provides method, headers, body)
	 * @param username    the authenticated username (added as {@code X-User})
	 * @param authorities the user's granted authorities (added as {@code X-Permissions})
	 * @return the upstream response with its original status, headers, and body
	 */
	ResponseEntity<byte[]> forward(
		PluginDefinition plugin,
		String subPath,
		HttpServletRequest request,
		String username,
		Collection<? extends GrantedAuthority> authorities);

	/**
	 * Extracts only the form parameters from a multipart request, excluding any query parameters.
	 *
	 * @param multipartRequest the incoming multipart HTTP request
	 * @return a map of form parameter names to their values, excluding query parameters
	 */
	Map<String, String[]> getFormParametersOnly(MultipartHttpServletRequest multipartRequest);

	/**
	 * Copies all headers from the {@link HttpServletRequest} into an {@link HttpHeaders} map.
	 *
	 * @param request the incoming servlet request
	 * @return assembled {@link HttpHeaders}
	 */
	HttpHeaders buildRequestHeaders(HttpServletRequest request);
}
