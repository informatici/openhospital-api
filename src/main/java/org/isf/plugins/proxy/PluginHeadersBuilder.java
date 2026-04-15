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

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/**
 * Package-private helper responsible for assembling the {@link HttpHeaders} that are
 * forwarded to an upstream plugin.
 *
 * <p>Extracted from {@link PluginRequestForwarder} to satisfy the Single Responsibility
 * Principle — header construction is a distinct concern from HTTP dispatch.</p>
 *
 * <p>Forwarding rules:</p>
 * <ul>
 *   <li>All incoming headers are copied except {@code Host}.</li>
 *   <li>{@value PluginRequestForwarder#HEADER_X_USER} is set to the authenticated username.</li>
 *   <li>{@value PluginRequestForwarder#HEADER_X_PERMISSIONS} is set to a comma-separated
 *       list of the user's granted authorities.</li>
 * </ul>
 *
 * @author Steve Tsala
 */
abstract class PluginHeadersBuilder {

	/**
	 * Headers that must not be forwarded to the upstream plugin.
	 */
	private static final Set<String> EXCLUDED_REQUEST_HEADERS = Set.of(
		HttpHeaders.HOST.toLowerCase()
	);

	/**
	 * Builds the headers to send to the upstream plugin.
	 * Copies all incoming headers except those in {@link #EXCLUDED_REQUEST_HEADERS},
	 * then appends the identity headers.
	 *
	 * @param incomingHeaders original request headers
	 * @param username        authenticated username
	 * @param authorities     the user's granted authorities
	 * @return the assembled {@link HttpHeaders} for the upstream request
	 */
	static HttpHeaders build(
		HttpHeaders incomingHeaders,
		String username,
		Collection<? extends GrantedAuthority> authorities) {

		HttpHeaders headers = new HttpHeaders();

		incomingHeaders.forEach((name, values) -> {
			if (!EXCLUDED_REQUEST_HEADERS.contains(name.toLowerCase())) {
				headers.addAll(name, values);
			}
		});

		// Identity headers — always set, overwriting any client-supplied values.
		headers.set(PluginRequestForwarder.HEADER_X_USER, username);
		headers.set(PluginRequestForwarder.HEADER_X_PERMISSIONS, buildPermissionsHeader(authorities));

		return headers;
	}

	/**
	 * Converts a collection of GrantedAuthority into a comma-separated string for the
	 *
	 * @param authorities the user's granted authorities
	 * @return a comma-separated string of authority names, or an empty string if there are none
	 */
	private static String buildPermissionsHeader(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.reduce((a, b) -> a + "," + b)
			.orElse("");
	}
}
