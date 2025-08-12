/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.menu.rest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RootController {

	@Autowired
	private VersionService versionService;

	@GetMapping(value = "/")
	public Map<String, String> root() {

		String version;
		try {
			version = versionService.getVersion();
		} catch (IOException e) {
			version = "Unknown";
		}

		Map<String, String> response = new LinkedHashMap<>();
		response.put("service", "Open Hospital API");
		response.put("version", version);
		response.put("documentation", "/swagger-ui/index.html");
		response.put("healthcheck", "/healthcheck");
		return response;
	}

	@GetMapping(value = "/healthcheck")
	public Map<String, String> healthcheck() {
		Map<String, String> response = new LinkedHashMap<>();
		response.put("status", "UP");
		return response;
	}
}
