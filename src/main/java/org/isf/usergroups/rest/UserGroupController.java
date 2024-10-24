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
import java.util.Objects;

import jakarta.validation.Valid;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.usergroups.dto.GroupPermissionsDTO;
import org.isf.usergroups.dto.UserGroupDTO;
import org.isf.usergroups.mapper.UserGroupMapper;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User Groups")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserGroupController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupController.class);

	private final PermissionManager permissionManager;

	private final GroupPermissionManager groupPermissionManager;

	private final UserGroupMapper userGroupMapper;

	private final PermissionMapper permissionMapper;

	private final UserBrowsingManager userManager;

	public UserGroupController(
		PermissionManager permissionManager,
		GroupPermissionManager groupPermissionManager,
		UserGroupMapper userGroupMapper,
		PermissionMapper permissionMapper,
		UserBrowsingManager userManager
	) {
		this.permissionManager = permissionManager;
		this.groupPermissionManager = groupPermissionManager;
		this.userGroupMapper = userGroupMapper;
		this.permissionMapper = permissionMapper;
		this.userManager = userManager;
	}

	/**
	 * Returns the list of {@link UserGroup}s.
	 * @return the list of {@link UserGroup}s
	 */
	@GetMapping(value = "/usergroups")
	public List<UserGroupDTO> getUserGroups() throws OHServiceException {
		LOGGER.info("Attempting to fetch the list of user groups.");
		List<UserGroup> groups = userManager.getUserGroup();
		List<UserGroupDTO> mappedGroups = userGroupMapper.map2DTOList(groups);
		LOGGER.info("Found {} group(s).", mappedGroups.size());
		return mappedGroups;
	}

	/**
	 * Deletes a {@link UserGroup}.
	 * @param code - the code of the {@link UserGroup} to delete
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/usergroups/{group_code}")
	public void deleteGroup(@PathVariable("group_code") String code) throws OHServiceException {
		try {
			UserGroup group = loadUserGroup(code);
			userManager.deleteGroup(group);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not deleted."));
		}
	}

	/**
	 * Creates a new {@link UserGroup} with a minimum set of rights.
	 * @param userGroupDTO - the {@link UserGroup} to insert
	 * @return the {@link UserGroupDTO} of new user group.
	 * @throws OHServiceException When failed to create the user group
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/usergroups")
	public UserGroupDTO newUserGroup(@Valid @RequestBody UserGroupDTO userGroupDTO) throws OHServiceException {
		UserGroup userGroup = userGroupMapper.map2Model(userGroupDTO);
		List<Permission> permissions = new ArrayList<>();

		if (userGroupDTO.getPermissions() != null && !userGroupDTO.getPermissions().isEmpty()) {
			permissions = userGroupDTO.getPermissions()
				.stream().map(permissionMapper::map2Model)
				.toList();
		}

		try {
			var group = userManager.newUserGroup(userGroup, permissions);
			return getUserGroup(group.getCode());
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not created."));
		}
	}

	/**
	 * Updates an existing {@link UserGroup}.
	 * @param userGroupDTO - the {@link UserGroup} to update
	 * @return {@link  UserGroupDTO} for the updated group.
	 * @throws OHServiceException When failed to update the user group
	 */
	@PutMapping(value = "/usergroups/{group_code}")
	public UserGroupDTO updateUserGroup(
		@PathVariable("group_code") String code, @Valid @RequestBody UserGroupDTO userGroupDTO
	) throws OHServiceException {
		if (!Objects.equals(userGroupDTO.getCode(), code)) {
			throw new OHAPIException(new OHExceptionMessage("Invalid request payload"));
		}
		UserGroup group = userGroupMapper.map2Model(userGroupDTO);

		if (!userManager.findUserGroupByCode(userGroupDTO.getCode()).getCode().equals(group.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}

		List<Permission> permissions = new ArrayList<>();
		if (userGroupDTO.getPermissions() != null && !userGroupDTO.getPermissions().isEmpty()) {
			permissions = userGroupDTO.getPermissions()
				.stream().map(permissionMapper::map2Model)
				.toList();
		}

		boolean isUpdated = userManager.updateUserGroup(group, permissions);
		if (isUpdated) {
			return getUserGroup(group.getCode());
		} else {
			throw new OHAPIException(new OHExceptionMessage("User group not updated."));
		}
	}

	/**
	 * Retrieve a {@link UserGroup} using its code
	 * @param code UserGroup code
	 * @return Returns the {@link UserGroup} found using the given code
	 * @throws OHServiceException When failed to retrieve the user group
	 */
	@GetMapping(value = "/usergroups/{group_code}")
	public UserGroupDTO getUserGroup(@PathVariable("group_code") String code) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(code);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
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
	 * @param userGroupCode - the {@link UserGroup}'s code
	 * @param permissionId - the {@link Permission}'s id
	 * @return the id of the new group permission.
	 * @throws OHServiceException When failed to assign the permission to the user group
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/usergroups/{group_code}/permissions/{id}")
	public int assignPermission(
		@PathVariable("group_code") String userGroupCode,
		@PathVariable("id") int permissionId
	) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}

		Permission permission = permissionManager.retrievePermissionById(permissionId);

		if (permission == null || permission.getName() == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not found."));
		}

		try {
			return groupPermissionManager.create(userGroup, permission).getId();
		} catch (OHDataValidationException e) {
			throw new OHAPIException(new OHExceptionMessage("Failed to assign permission"));
		}
	}

	/**
	 * Assign permissions to the target user group, ignore those already assigned.
	 * @param userGroupCode Code of the group to update
	 * @param payload New group permissions
	 * @return List of {@link PermissionDTO} corresponding to the list of permissions assigned to the group
	 * @throws OHServiceException If the update operation fails
	 */
	@PutMapping(value = "/usergroups/{group_code}/permissions")
	public List<PermissionDTO> updateGroupPermissions(
		@PathVariable("group_code") String userGroupCode,
		@RequestBody GroupPermissionsDTO payload
	) throws OHServiceException {
		LOGGER.info("Attempting to update user group({}) permissions, with permissions ids, {}", userGroupCode, payload.permissionIds());
		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			LOGGER.info("Could not find user corresponding to the group code {}", userGroupCode);
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return permissionMapper.map2DTOList(
				groupPermissionManager.update(userGroup, payload.permissionIds(), false)
			);
		} catch (OHDataValidationException e) {
			LOGGER.info("Fail to update user groups permissions, reason: {}", e.getMessage());
			throw new OHAPIException(new OHExceptionMessage("Failed to update permissions"));
		}
	}

	/**
	 * Replace permissions for the target user group with the one provided in the payload.
	 * <ul>
	 *     <li>Ids corresponding to permissions already assigned to the group will be skipped.</li>
	 *     <li>User group permissions that don't have their id in the permission ids payload will be removed</li>
	 *     <li>ids that corresponding permission are not yet assigned to the groups will be assigned.</li>
	 *     <li>Permissions ids that don't exist are ignored</li>
	 * </ul>
	 * @param userGroupCode Code of the group to update
	 * @param payload New group permissions
	 * @return List of {@link PermissionDTO} corresponding to the list of permissions assigned to the group
	 * @throws OHServiceException If the update operation fails
	 */
	@PatchMapping(value = "/usergroups/{group_code}/permissions")
	public List<PermissionDTO> replaceGroupPermissions(
		@PathVariable("group_code") String userGroupCode,
		@RequestBody GroupPermissionsDTO payload
	) throws OHServiceException {
		LOGGER.info(
			"Attempting to replace user group({}) permissions, with permissions ids, {}",
			userGroupCode, payload.permissionIds()
		);

		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			LOGGER.info("Could not find user group corresponding to the group code {}", userGroupCode);
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return permissionMapper.map2DTOList(
				groupPermissionManager.update(userGroup, payload.permissionIds(), true)
			);
		} catch (OHDataValidationException e) {
			LOGGER.info("Fail to replace user groups permissions, reason: {}", e.getMessage());
			throw new OHAPIException(new OHExceptionMessage("Failed to update permissions"));
		}
	}

	/**
	 * Revoke a {@link Permission} from a {@link UserGroup}
	 * @param userGroupCode - the {@link UserGroup}'s code
	 * @param permissionId - the {@link Permission}'s id
	 * @throws OHServiceException When failed to revoke the permission to the user group
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/usergroups/{group_code}/permissions/{id}")
	public void revokePermission(
		@PathVariable("group_code") String userGroupCode,
		@PathVariable("id") int permissionId
	) throws OHServiceException {
		UserGroup userGroup = userManager.findUserGroupByCode(userGroupCode);
		if (userGroup == null) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}

		Permission permission = permissionManager.retrievePermissionById(permissionId);

		if (permission == null || permission.getName() == null) {
			throw new OHAPIException(new OHExceptionMessage("Permission not found."));
		}

		try {
			groupPermissionManager.delete(userGroup, permission);
		} catch (OHDataValidationException e) {
			throw new OHAPIException(new OHExceptionMessage("Failed to revoke permission"));
		}
	}

	private UserGroup loadUserGroup(String code) throws OHServiceException {
		List<UserGroup> group = userManager.getUserGroup().stream().filter(g -> g.getCode().equals(code)).toList();
		if (group.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."), HttpStatus.NOT_FOUND);
		}
		return group.get(0);
	}
}
