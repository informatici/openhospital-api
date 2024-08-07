/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.permissions.rest;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/permissions")
@Tag(name = "Permissions")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);

	@Autowired
	protected PermissionManager permissionManager;

	@Autowired
	protected PermissionMapper permissionMapper;

	@GetMapping(value = "/permissions/userGroupCode/{userGroupCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PermissionDTO>> retrievePermissionsByUserGroupCode(@PathVariable("userGroupCode") String userGroupCode)
					throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionsByUserGroupCode({}).", userGroupCode);
		List<Permission> domains = permissionManager.retrievePermissionsByGroupCode(userGroupCode);
		List<PermissionDTO> dtos = permissionMapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

	@GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PermissionDTO>> retrieveAllPermissions() throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrieveAllPermissions().");
		List<Permission> permissions = permissionManager.retrieveAllPermissions();
		if (permissions == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		List<PermissionDTO> dtos = permissionMapper.map2DTOList(permissions);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}

	@GetMapping(value = "/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> retrievePermissionById(@PathVariable("id") Integer id) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionById({}).", id);
		Permission permission = permissionManager.retrievePermissionById(id);
		if (permission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		PermissionDTO dtos = permissionMapper.map2DTO(permission);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}

	@GetMapping(value = "/permissions/name/{name:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> retrievePermissionByName(@PathVariable("name") String name) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionByName({}).", name);
		Permission permission = permissionManager.retrievePermissionByName(name);
		if (permission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		PermissionDTO dtos = permissionMapper.map2DTO(permission);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}
}
