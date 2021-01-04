/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.permissions.rest;

import java.util.List;

import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PermissionController.class);

	@Autowired
	protected PermissionManager manager;

	@Autowired
	protected PermissionMapper mapper;

	@GetMapping(value = "/permissions/userGroupCode/{userGroupCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<PermissionDTO>> retrievePermissionsByUserGroupcode(@PathVariable("userGroupCode") String userGroupCode) throws OHServiceException {
		LOGGER.info("retrieving permissions: retrievePermissionsByUserGroupcode({})", userGroupCode);
		List<Permission> domains = this.manager.retrivePermisionsByGroupCode(userGroupCode);
		List<PermissionDTO> dtos = this.mapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

}
