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

import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Permissions")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);

	private final PermissionManager permissionManager;

	private final PermissionMapper permissionMapper;

	public PermissionController(PermissionManager permissionManager, PermissionMapper permissionMapper) {
		this.permissionManager = permissionManager;
		this.permissionMapper = permissionMapper;
	}

	@GetMapping(value = "/permissions/userGroupCode/{userGroupCode}")
	public List<PermissionDTO> retrievePermissionsByUserGroupCode(
		@PathVariable("userGroupCode") String userGroupCode
	) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionsByUserGroupCode({}).", userGroupCode);

		return permissionMapper.map2DTOList(permissionManager.retrievePermissionsByGroupCode(userGroupCode));
	}

	@GetMapping(value = "/permissions")
	public List<PermissionDTO> retrieveAllPermissions() throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrieveAllPermissions().");

		return permissionMapper.map2DTOList(permissionManager.retrieveAllPermissions());
	}

	@GetMapping(value = "/permissions/{id}")
	public PermissionDTO retrievePermissionById(@PathVariable("id") Integer id) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionById({}).", id);
		Permission permission = permissionManager.retrievePermissionById(id);

		if (permission == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not created."), HttpStatus.NOT_FOUND);
		}

		return permissionMapper.map2DTO(permission);
	}

	@GetMapping(value = "/permissions/name/{name:.+}")
	public PermissionDTO retrievePermissionByName(@PathVariable("name") String name) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionByName({}).", name);
		Permission permission = permissionManager.retrievePermissionByName(name);

		if (permission == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not created."), HttpStatus.NOT_FOUND);
		}

		return permissionMapper.map2DTO(permission);
	}
}
