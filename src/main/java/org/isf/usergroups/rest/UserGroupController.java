/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.usergroups.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.usergroups.dto.UserGroupDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.usergroups.mapper.UserGroupMapper;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController(value = "/usergroups")
@Tag(name = "User Groups")
@SecurityRequirement(name = "bearerAuth")
public class UserGroupController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupController.class);

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private UserBrowsingManager userManager;

	@Autowired
	protected PermissionManager permissionManager;
	@Autowired
	protected GroupPermissionManager groupPermissionManager;

	/**
	 * Returns the list of {@link UserGroup}s.
	 *
	 * @return the list of {@link UserGroup}s
	 */
	@GetMapping(value = "/usergroups", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserGroupDTO> getUserGroups() throws OHServiceException {
		LOGGER.info("Attempting to fetch the list of user groups.");
		List<UserGroup> groups = userManager.getUserGroup();
		List<UserGroupDTO> mappedGroups = userGroupMapper.map2DTOList(groups);
		LOGGER.info("Found {} group(s).", mappedGroups.size());
		return mappedGroups;
	}

	/**
	 * Deletes a {@link UserGroup}.
	 *
	 * @param code - the code of the {@link UserGroup} to delete
	 * @return {@code true} if the group has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/usergroups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean deleteGroup(@PathVariable("group_code") String code) throws OHServiceException {
		try {
			UserGroup group = loadUserGroup(code);
			userManager.deleteGroup(group);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not deleted."));
		}
	}

	/**
	 * Creates a new {@link UserGroup} with a minimum set of rights.
	 *
	 * @param userGroupDTO - the {@link UserGroup} to insert
	 * @return {@code true} if the group has been inserted, {@code false} otherwise.
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/usergroups", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean newUserGroup(@Valid @RequestBody UserGroupDTO userGroupDTO) throws OHServiceException {
		UserGroup userGroup = userGroupMapper.map2Model(userGroupDTO);
		List<Permission> permissions = new ArrayList<>();

		if (userGroupDTO.getPermissions() != null && !userGroupDTO.getPermissions().isEmpty()) {
			permissions = userGroupDTO.getPermissions()
				.stream().map(permissionDTO -> permissionMapper.map2Model(permissionDTO))
				.toList();
		}

		try {
			userManager.newUserGroup(userGroup, permissions);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not created."));
		}
	}

	/**
	 * Updates an existing {@link UserGroup}.
	 *
	 * @param userGroupDTO - the {@link UserGroup} to update
	 * @return {@code true} if the group has been updated, {@code false} otherwise.
	 */
	@PutMapping(value = "/usergroups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean updateUserGroup(@PathVariable("group_code") String code, @Valid @RequestBody UserGroupDTO userGroupDTO) throws OHServiceException {
		userGroupDTO.setCode(code);
		UserGroup group = userGroupMapper.map2Model(userGroupDTO);

		if (!userManager.findUserGroupByCode(userGroupDTO.getCode()).getCode().equals(group.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}

		List<Permission> permissions = new ArrayList<>();
		if (userGroupDTO.getPermissions() != null && !userGroupDTO.getPermissions().isEmpty()) {
			permissions = userGroupDTO.getPermissions()
				.stream().map(permissionDTO -> permissionMapper.map2Model(permissionDTO))
				.toList();
		}

		boolean isUpdated = userManager.updateUserGroup(group, permissions);
		if (isUpdated) {
			return true;
		} else {
			throw new OHAPIException(new OHExceptionMessage("User group not updated."));
		}
	}

	/**
	 * Retrieve a {@link UserGroup} using its code
	 *
	 * @param code UserGroup code
	 * @return Returns the {@link UserGroup} found using the given code
	 * @throws OHServiceException When failed to retrieve the user group
	 */
	@GetMapping(value = "/usergroups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserGroupDTO getUserGroup(@PathVariable("group_code") String code) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(code);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}

		List<GroupPermission> groupPermissions = groupPermissionManager.findUserGroupPermissions(userGroup.getCode());
		List<PermissionDTO> permissions = groupPermissions.stream()
			.map(groupPermission -> permissionMapper.map2DTO(groupPermission.getPermission()))
			.toList();

		UserGroupDTO userGroupDTO = userGroupMapper.map2DTO(userGroup);
		userGroupDTO.setPermissions(permissions);

		return userGroupDTO;
	}

	/**
	 * Assign a {@link Permission} to a {@link UserGroup}
	 *
	 * @param userGroupCode - the {@link UserGroup}'s code
	 * @param permissionId - the {@link Permission}'s id
	 * @return {@code true} if the permission has been assigned to the group,
	 * {@code false} otherwise.
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/usergroups/{group_code}/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean assignPermission(
		@PathVariable("group_code") String userGroupCode,
		@PathVariable("id") int permissionId
	) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}

		Permission permission = permissionManager.retrievePermissionById(permissionId);

		if (permission == null || permission.getName() == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not found."));
		}

		try {
			groupPermissionManager.create(userGroup, permission);
			return true;
		} catch (OHDataValidationException e) {
			throw new OHAPIException(new OHExceptionMessage("Failed to assign permission"));
		}
	}

	/**
	 * Revoke a {@link Permission} from a {@link UserGroup}
	 *
	 * @param userGroupCode - the {@link UserGroup}'s code
	 * @param permissionId - the {@link Permission}'s id
	 * @return {@code true} if the permission has been revoked from the group,
	 * {@code false} otherwise.
	 */
	@DeleteMapping(value = "/usergroups/{group_code}/permissions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean revokePermission(
		@PathVariable("group_code") String userGroupCode,
		@PathVariable("id") int permissionId
	) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}

		Permission permission = permissionManager.retrievePermissionById(permissionId);

		if (permission == null || permission.getName() == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not found."));
		}

		try {
			groupPermissionManager.delete(userGroup, permission);
			return true;
		} catch (OHDataValidationException e) {
			throw new OHAPIException(new OHExceptionMessage("Failed to revoke permission"));
		}
	}

	private UserGroup loadUserGroup(String code) throws OHServiceException {
		List<UserGroup> group = userManager.getUserGroup().stream().filter(g -> g.getCode().equals(code)).collect(Collectors.toList());
		if (group.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}
		return group.get(0);
	}
}
