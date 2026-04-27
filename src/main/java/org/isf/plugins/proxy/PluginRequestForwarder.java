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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Steve Tsala
 */
@Component
public class PluginRequestForwarder implements IPluginRequestForwarder {

	/**
	 * Identity header carrying the authenticated username.
	 */
	public static final String HEADER_X_USER = "X-User";
	/**
	 * Identity header carrying the comma-separated permission list from the JWT.
	 */
	public static final String HEADER_X_PERMISSIONS = "X-Permissions";
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginRequestForwarder.class);
	private final RestClient restClient;

	public PluginRequestForwarder() {
		this(RestClient.builder().build());
	}

	public PluginRequestForwarder(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	public ResponseEntity<byte[]> forward(PluginDefinition plugin, String subPath, HttpServletRequest request, String username, Collection<? extends GrantedAuthority> authorities) {

		LOGGER.debug("Routing [{}] /plugins/{}{} → {}{}", request.getMethod(), plugin.id(), subPath, plugin.url(), subPath);

		URI targetUri = PluginUriBuilder.build(plugin, subPath, request.getQueryString());

		LOGGER.info("Forwarding request to plugin '{}' at {}", plugin.id(), targetUri);

		HttpHeaders forwardHeaders = PluginHeadersBuilder.build(buildRequestHeaders(request), username, authorities);

		HttpMethod method = HttpMethod.valueOf(request.getMethod());

		LOGGER.debug("Proxying {} {} → {}", method, subPath, targetUri);

		try (var inputStream = request.getInputStream()) {
			RestClient.RequestBodySpec requestSpec = restClient.method(method).uri(targetUri).headers(h -> h.addAll(forwardHeaders));

			String contentType = request.getContentType();

			if (contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

				var paramtersMap = getFormParametersOnly(multipartRequest);
				paramtersMap.forEach((key, values) -> {
					for (String value : values) {
						body.add(key, value);
					}
				});

				for (Map.Entry<String, MultipartFile> entry : multipartRequest.getFileMap().entrySet()) {
					MultipartFile file = entry.getValue();
					body.add(entry.getKey(), file.getResource());
				}

				requestSpec.body(body);
			} else {

				requestSpec.body(inputStream::transferTo);
			}

			return requestSpec.retrieve().onStatus(status -> true, (req, res) -> {
				// Pass all statuses through — do not throw on 4xx/5xx from upstream.
			}).toEntity(byte[].class);

		} catch (RestClientResponseException ex) {
			LOGGER.warn("Upstream plugin '{}' returned error {}: {}", plugin.id(), ex.getStatusCode(), ex.getMessage());
			throw ex;
		} catch (IOException ex) {
			LOGGER.error("I/O error forwarding request to plugin '{}': {}", plugin.id(), ex.getMessage());
			throw new RuntimeException("I/O error forwarding request to plugin", ex);
		}
	}

	@Override
	public Map<String, String[]> getFormParametersOnly(MultipartHttpServletRequest multipartRequest) {
		Map<String, String[]> allParams = multipartRequest.getParameterMap();
		if (allParams.isEmpty()) {
			return Collections.emptyMap();
		}

		Set<String> queryKeys = new HashSet<>();
		String query = multipartRequest.getQueryString();
		if (query != null && !query.isEmpty()) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int eqIndex = pair.indexOf('=');
				String key = (eqIndex > 0) ? pair.substring(0, eqIndex) : pair;
				key = URLDecoder.decode(key, StandardCharsets.UTF_8);
				queryKeys.add(key);
			}
		}

		Map<String, String[]> formOnly = new LinkedHashMap<>();
		for (Map.Entry<String, String[]> entry : allParams.entrySet()) {
			String key = entry.getKey();
			if (!queryKeys.contains(key)) {
				formOnly.put(key, entry.getValue());
			}
		}

		return formOnly;
	}

	/**
	 * Copies all headers from the {@link HttpServletRequest} into an {@link HttpHeaders} map.
	 *
	 * @param request the incoming servlet request
	 * @return assembled {@link HttpHeaders}
	 */
	@Override
	public HttpHeaders buildRequestHeaders(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		java.util.Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				java.util.Enumeration<String> values = request.getHeaders(name);
				while (values.hasMoreElements()) {
					headers.add(name, values.nextElement());
				}
			}
		}
		headers.set("Content-Length", null);
		return headers;
	}
}
