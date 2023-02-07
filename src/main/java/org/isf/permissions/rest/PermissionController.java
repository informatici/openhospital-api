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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.permissions.rest;

import java.util.List;

import org.isf.menu.manager.UserGroupManager;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PermissionController.class);

	@Autowired
	protected PermissionManager permissionManager;

	@Autowired
	protected PermissionMapper permissionMapper;

	@Autowired
	private GroupPermissionManager groupPermissionManager;

	@Autowired
	private UserGroupManager userGroupManager;

	@GetMapping(value = "/permissions/userGroupCode/{userGroupCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PermissionDTO>> retrievePermissionsByUserGroupcode(@PathVariable("userGroupCode") String userGroupCode) throws OHServiceException {
		LOGGER.info("retrieving permissions: retrievePermissionsByUserGroupcode({})", userGroupCode);
		List<Permission> domains = this.permissionManager.retrivePermisionsByGroupCode(userGroupCode);
		List<PermissionDTO> dtos = this.permissionMapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

	@GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PermissionDTO>> retrieveAllPermissions() throws OHServiceException {
		LOGGER.info("retrieving permissions: retrieveAllPermissions({})");
		List<Permission> permissions = this.permissionManager.retrieveAllPermissions();
		if (permissions == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		List<PermissionDTO> dtos = this.permissionMapper.map2DTOList(permissions);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}

	@GetMapping(value = "/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> retrievePermissionById(@PathVariable("id") Integer id) throws OHServiceException {
		LOGGER.info("retrieving permissions: retrievePermissionById({})", id);
		Permission permission = this.permissionManager.retrievePermissionById(id);
		if (permission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		PermissionDTO dtos = this.permissionMapper.map2DTO(permission);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}

	@GetMapping(value = "/permissions/name/{name:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> retrievePermissionByName(@PathVariable("name") String name) throws OHServiceException {
		LOGGER.info("retrieving permissions: retrievePermissionByName({})", name);
		Permission permission = this.permissionManager.retrievePermissionByName(name);
		if (permission == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		PermissionDTO dtos = this.permissionMapper.map2DTO(permission);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
	}

	@PostMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> insertPermission(@RequestBody PermissionDTO permissionDTO) throws OHServiceException {
		LOGGER.info("insertPermission({})", permissionDTO);
		Permission model = this.permissionMapper.map2Model(permissionDTO);
		List<UserGroup> userGroups = this.userGroupManager.findByIdIn(permissionDTO.getUserGroupIds());
		model.setGroupPermission(this.groupPermissionManager.generateGroupPermissionList(model, userGroups));
		Permission permission = this.permissionManager.insertPermission(model);

		PermissionDTO resultPermissionDTO = this.permissionMapper.map2DTO(permission);
		return ResponseEntity.status(HttpStatus.CREATED).body(resultPermissionDTO);
	}

	@PutMapping(value = "/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> updatePermission(@PathVariable int id, @RequestBody PermissionDTO permissionDTO) throws OHServiceException {
		LOGGER.info("Update permission id:"  +  id);
		permissionDTO.setId(id);

		if (!this.permissionManager.exists(permissionDTO.getId())) {
			throw new OHAPIException(new OHExceptionMessage(null, "permission not found", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Permission model = this.permissionMapper.map2Model(permissionDTO);

		List<GroupPermission> groupPermissions = this.groupPermissionManager.findByPermissionIdAndUserGroupCodes(permissionDTO.getId(), permissionDTO.getUserGroupIds());
		model.setGroupPermission(groupPermissions);
		Permission permission = this.permissionManager.updatePermission(model);
		if (permission != null) {
			PermissionDTO dtos = this.permissionMapper.map2DTO(permission);
			return ResponseEntity.status(HttpStatus.OK).body(dtos);
		}
		throw new OHAPIException(new OHExceptionMessage(null, "permission is not updated!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/*
	@PutMapping(value = { "/permissions/{id}", "/permissions" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PermissionDTO> updatePermission(@PathVariable(value = "id") Optional<Integer> optionalPermissionId, @RequestBody PermissionDTO permissionDTO) throws OHServiceException {
		LOGGER.info("updatePermission(id: {}, id: {})", optionalPermissionId.isPresent() ? optionalPermissionId.get() : "EMPTY", permissionDTO);

		if (optionalPermissionId == null && (permissionDTO == null || permissionDTO.getId() == null)) {
			throw new OHAPIException(new OHExceptionMessage(null, "wrong input: no permission id", OHSeverityLevel.ERROR), HttpStatus.BAD_REQUEST);
		}

		if (optionalPermissionId.isPresent() && optionalPermissionId.get().compareTo(permissionDTO.getId()) != 0) {
			throw new OHAPIException(new OHExceptionMessage(null, "wrong input: permissio ids does not match", OHSeverityLevel.ERROR), HttpStatus.BAD_REQUEST);
		}

		final Integer permissionId = optionalPermissionId.isPresent() ? optionalPermissionId.get() : permissionDTO.getId();

		if (!this.permissionManager.exists(permissionId.intValue())) {
			throw new OHAPIException(new OHExceptionMessage(null, "permission not found", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Permission model = this.permissionMapper.map2Model(permissionDTO);

		List<GroupPermission> groupPermissions = this.groupPermissionManager.findByPermissionIdAndUserGroupCodes(permissionDTO.getId(), permissionDTO.getUserGroupIds());
		model.setGroupPermission(groupPermissions);
		Permission permission = this.permissionManager.updatePermission(model);
		if (permission != null) {
			PermissionDTO dtos = this.permissionMapper.map2DTO(permission);
			return ResponseEntity.status(HttpStatus.OK).body(dtos);
		}
		throw new OHAPIException(new OHExceptionMessage(null, "permission is not updated!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	*/

	@DeleteMapping(value = "/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePermission(@PathVariable("id") Integer id) throws OHServiceException {
		LOGGER.info("deletePermission({})", id);
		Boolean result = this.permissionManager.deletePermission(id);
		return ResponseEntity.status(result.booleanValue() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	}

}
