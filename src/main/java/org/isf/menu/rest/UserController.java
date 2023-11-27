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
package org.isf.menu.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.dto.UserProfileDTO;
import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserSetting;
import org.isf.permissions.dto.LitePermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.LitePermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/users")
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private UserBrowsingManager userManager;

	@Autowired
	protected PermissionManager permissionManager;

	@Autowired
	protected LitePermissionMapper litePermissionMapper;

	@Autowired
	private UserSettingManager userSettingManager;

	@Autowired
	private UserSettingMapper userSettingMapper;

	/**
	 * Returns the list of {@link User}s.
	 * @return the list of {@link User}s.
	 */
	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserDTO>> getUser(@RequestParam(name = "group_id", required = false) String groupID) throws OHServiceException {
		LOGGER.info("Fetching the list of users.");
		List<User> users;
		if (groupID != null) {
			users = userManager.getUser(groupID);
		} else {
			users = userManager.getUser();
		}
		List<UserDTO> mappedUsers = userMapper.map2DTOList(users);
		if (mappedUsers.isEmpty()) {
			LOGGER.info("No user found.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedUsers);
		} else {
			LOGGER.info("Found {} users.", mappedUsers.size());
			return ResponseEntity.ok(mappedUsers);
		}
	}

	/**
	 * Returns a {@link User}.
	 * @param userName - user name
	 * @return {@link User}
	 */
	@GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDTO> getUserByName(@PathVariable("username") String userName) throws OHServiceException {
		User user = userManager.getUserByName(userName);
		if (user == null) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
		return ResponseEntity.ok(userMapper.map2DTO(user));
	}

	/**
	 * Creates a new {@link User}.
	 * @param userDTO - the {@link User} to insert
	 * @return {@code true} if the user has been inserted, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newUser(@Valid @RequestBody UserDTO userDTO) throws OHServiceException {
		LOGGER.info("Attempting to create a user.");
		User user = userMapper.map2Model(userDTO);
		try {
			userManager.newUser(user);
			LOGGER.info("User successfully created.");
			return ResponseEntity.status(HttpStatus.CREATED).body(true);
		} catch (OHServiceException serviceException) {
			LOGGER.info("User is not created.");
			throw new OHAPIException(new OHExceptionMessage("User not created."));
		}
	}

	/**
	 * Updates an existing {@link User}.
	 * @param userDTO - the {@link User} to update
	 * @param updatePassword - indicates if it is the password that need to be updated
	 * @return {@code true} if the user has been updated, {@code false} otherwise.
	 */
	@PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUser(
					@Valid @RequestBody UserDTO userDTO,
					@RequestParam(name = "password", defaultValue = "false") boolean updatePassword) throws OHServiceException {
		String requestUserName = userDTO.getUserName();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		final String ADMIN = "admin";
		boolean isAdminOrSameUser = requestUserName.equals(currentUser) || currentUser.equals(ADMIN);
		boolean isSameUserUpdatingPassword = requestUserName.equals(currentUser) && updatePassword;
		if (!isAdminOrSameUser || !isSameUserUpdatingPassword) {
			throw new OHAPIException(new OHExceptionMessage("Not allowed."));
		}
		if (userManager.getUserByName(requestUserName) == null) {
			throw new OHAPIException(new OHExceptionMessage("The specified user does not exist."));
		}
		User user = userMapper.map2Model(userDTO);
		boolean isUpdated;
		if (updatePassword) {
			isUpdated = userManager.updatePassword(user);
		} else {
			isUpdated = userManager.updateUser(user);
		}
		if (isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage("User not updated."));
		}
	}

	/**
	 * Deletes an existing {@link User}.
	 * @param username - the name of the {@link User} to delete
	 * @return {@code true} if the user has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteUser(@PathVariable String username) throws OHServiceException {
		User foundUser = userManager.getUserByName(username);
		if (foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
		try {
			userManager.deleteUser(foundUser);
			return ResponseEntity.ok(true);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User not deleted."));
		}
	}

	/**
	 * Returns the list of {@link UserGroup}s.
	 * @return the list of {@link UserGroup}s
	 */
	@GetMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserGroupDTO>> getUserGroup() throws OHServiceException {
		LOGGER.info("Attempting to fetch the list of user groups.");
		List<UserGroup> groups = userManager.getUserGroup();
		List<UserGroupDTO> mappedGroups = userGroupMapper.map2DTOList(groups);
		if (mappedGroups.isEmpty()) {
			LOGGER.info("No group found.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedGroups);
		} else {
			LOGGER.info("Found {} groups.", mappedGroups.size());
			return ResponseEntity.ok(mappedGroups);
		}
	}

	/**
	 * Deletes a {@link UserGroup}.
	 * @param code - the code of the {@link UserGroup} to delete
	 * @return {@code true} if the group has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/users/groups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteGroup(@PathVariable("group_code") String code) throws OHServiceException {
		try {
			UserGroup group = loadUserGroup(code);
			userManager.deleteGroup(group);
			return ResponseEntity.ok(true);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not deleted."));
		}
	}

	/**
	 * Creates a new {@link UserGroup} with a minimum set of rights.
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return {@code true} if the group has been inserted, {@code false} otherwise.
	 */
	@PostMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
		UserGroup userGroup = userGroupMapper.map2Model(aGroup);
		try {
			userManager.newUserGroup(userGroup);
			return ResponseEntity.status(HttpStatus.CREATED).body(true);
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User group not created."));
		}
	}

	/**
	 * Updates an existing {@link UserGroup}.
	 * @param aGroup - the {@link UserGroup} to update
	 * @return {@code true} if the group has been updated, {@code false} otherwise.
	 */
	@PutMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
		UserGroup group = userGroupMapper.map2Model(aGroup);
		if (userManager.getUserGroup().stream().noneMatch(g -> g.getCode().equals(group.getCode()))) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}
		boolean isUpdated = userManager.updateUserGroup(group);
		if (isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage("User group not updated."));
		}
	}

	private UserGroup loadUserGroup(String code) throws OHServiceException {
		List<UserGroup> group = userManager.getUserGroup().stream().filter(g -> g.getCode().equals(code)).collect(Collectors.toList());
		if (group.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("User group not found."));
		}
		return group.get(0);
	}

	/**
	 * Retrieves all permissions of the current logged-in user. If user not found,
	 * an empty list of permissions is returned.
	 *
	 * @return list of permissions {@link Permission}
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LitePermissionDTO>> retrievePermissionsByCurrentLoggedInUser() throws OHServiceException {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		LOGGER.info("Retrieving permissions: retrievePermissionsByCurrentLoggedInUser({}).", currentUser);
		List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(currentUser);
		List<LitePermissionDTO> dtos = this.litePermissionMapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

	/**
	 * Retrieves profile of the current logged in user. If user not found,
	 * an empty list of permissions is returned
	 *
	 * @return list of permissions {@link Permission}
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserProfileDTO> retrieveProfileByCurrentLoggedInUser() throws OHServiceException {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		LOGGER.info("Retrieving profile: retrieveProfileByCurrentLoggedInUser({}).", currentUser);
		List<Permission> permissions = this.permissionManager.retrievePermissionsByUsername(currentUser);
		List<String> permissionsCode = permissions.stream().map(p -> p.getName()).collect(Collectors.toList());
		UserProfileDTO userProfileDTO = new UserProfileDTO();
		User user = userManager.getUserByName(currentUser);
		userProfileDTO.setUserGroup(userGroupMapper.map2DTO(user.getUserGroupName()));
		userProfileDTO.setUserName(currentUser);
		userProfileDTO.setPermissions(permissionsCode);
		return ResponseEntity.status(HttpStatus.OK).body(userProfileDTO);
	}

	/**
	 * Retrieves all permissions of the username passed as path variable. If user not
	 * found, an empty list of permissions is returned.
	 *
	 * @return list of permissions {@link Permission}
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/permissions/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LitePermissionDTO>> retrievePermissionsByUsername(@PathVariable("username") String username) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionsByUsername({}).", username);
		List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(username);
		List<LitePermissionDTO> dtos = this.litePermissionMapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

	/**
	 * Retrieves all userSetting of the current user.
	 * 
	 * @return list of userSetting {@link UserSettingDTO}.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/settings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserSettingDTO>> getUserSettings() throws OHServiceException {
		LOGGER.info("Retrieve all userSettings of the current user.");
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(currentUser);
		if (userSettings == null || userSettings.isEmpty()) {
			LOGGER.info("No settings for the current user {}.", currentUser);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		List<UserSettingDTO> userSettingsDTO = userSettingMapper.map2DTOList(userSettings);
		LOGGER.info("Found {} user settings.", userSettingsDTO);
		return ResponseEntity.ok(userSettingsDTO);
	}

	/**
	 * Returns a {@link UserSettingDTO} of userSetting created.
	 * 
	 * @param userSettingDTO -  the {@link UserSettingDTO} to insert.
	 * @return {@link UserSettingDTO} if the userSetting has been created, null otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/users/settings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> newUserSettings(@Valid @RequestBody UserSettingDTO userSettingDTO) throws OHServiceException {
		LOGGER.info("Create a UserSetting.");
		String requestUserName = userSettingDTO.getUser();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		final String ADMIN = "admin";
		if (!requestUserName.equals(currentUser)
						&& !currentUser.equals(ADMIN)) {
			throw new OHAPIException(new OHExceptionMessage("Not allowed."));
		}
		if (userSettingManager.getUserSettingByUserNameConfigName(requestUserName, userSettingDTO.getConfigName()) != null) {
			throw new OHAPIException(new OHExceptionMessage("A setting with that name already exists."));
		}
		if (userManager.getUserByName(requestUserName) == null) {
			throw new OHAPIException(new OHExceptionMessage("The specified user does not exist."));
		}
		userSettingDTO.setId(0);
		UserSetting userSetting = userSettingMapper.map2Model(userSettingDTO);
		UserSetting created = userSettingManager.newUserSetting(userSetting);
		if (created == null) {
			LOGGER.info("UserSetting is not created.");
			throw new OHAPIException(new OHExceptionMessage("UserSetting not created."));
		}
		LOGGER.info("UserSetting successfully created.");
		return ResponseEntity.status(HttpStatus.CREATED).body(userSettingMapper.map2DTO(created));
	}

	/**
	 * Updates an existing {@link UserSettingDTO}.
	 * 
	 * @param userSettingDTO - the {@link UserSettingDTO} to update.
	 * @param id - id of {@link UserSetting} .
	 * @return {@link UserSettingDTO} if the UserSetting has been updated , null otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/users/settings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> updateUserSettings(@PathVariable(name = "id") int id, @Valid @RequestBody UserSettingDTO userSettingDTO)
					throws OHServiceException {
		LOGGER.info("Update a UserSetting.");
		String requestUserName = userSettingDTO.getUser();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		final String ADMIN = "admin";
		if (userSettingDTO.getId() == 0 || userSettingDTO.getId() != 0 && userSettingDTO.getId() != id) {
			throw new OHAPIException(new OHExceptionMessage("Malformed request."));
		}
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		UserSetting updated;
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			throw new OHAPIException(new OHExceptionMessage("UserSetting doesn't exists."));
		}
		if (!userSetting.get().getUser().equals(requestUserName) && !currentUser.equals(ADMIN)) {
			throw new OHAPIException(new OHExceptionMessage("Not allowed."));
		}
		if (userSetting.get().getUser().equals(currentUser) || currentUser.equals(ADMIN)) {
			if (userSettingManager.getUserSettingByUserNameConfigName(requestUserName, userSettingDTO.getConfigName()) != null) {
				throw new OHAPIException(new OHExceptionMessage("A setting with that name already exists."));
			}
			if (userManager.getUserByName(requestUserName) == null) {
				throw new OHAPIException(new OHExceptionMessage("The specified user does not exist."));
			}
			UserSetting uSetting = userSettingMapper.map2Model(userSettingDTO);
			updated = userSettingManager.updateUserSetting(uSetting);
			if (updated == null) {
				LOGGER.info("UserSetting is not updated.");
				throw new OHAPIException(new OHExceptionMessage("UserSetting not updated."));
			}
			LOGGER.info("UserSetting successfully updated.");
			return ResponseEntity.ok(userSettingMapper.map2DTO(updated));
		}
		throw new OHAPIException(new OHExceptionMessage("Not allowed."));
	}

	/**
	 * Retrieves an existing {@link UserSettingDTO} by id.
	 * 
	 * @param id - id of userSetting {@link UserSetting} .
	 * @return {@link UserSettingDTO} if the UserSetting exists, null otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/settings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> getUserSettingById(@PathVariable(name = "id") int id) throws OHServiceException {
		LOGGER.info("Retrieve the userSetting By id {}.", id);
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(userSettingMapper.map2DTO(userSetting.get()));
	}

	/**
	 * Retrieves an existing {@link UserSettingDTO} by user.
	 * 
	 * @param userName - the name of user.
	 * @param configName - the name of the userSetting {@link UserSetting} . 
	 * @return {@link UserSettingDTO} if the UserSetting exists, null otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/{userName}/settings/{configName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> getUserSettingByUser(@PathVariable(name = "userName") String userName,
					@PathVariable(name = "configName") String configName) throws OHServiceException {
		LOGGER.info("Retrieve the userSetting By user {} and configName {}.", userName, configName);
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(userName);
		if (userSettings == null || userSettings.isEmpty()) {
			LOGGER.info("UserSetting not found.");
			throw new OHAPIException(new OHExceptionMessage("UserSetting not found."));
		}
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(userName, configName);
		if (userSetting == null) {
			LOGGER.info("No user settings for the user {}.", userName);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(userSettingMapper.map2DTO(userSetting));
	}

	/**
	 * Deletes a {@link UserSetting}.
	 * 
	 * @param id - the id of the userSetting {@link UserSetting} to delete.
	 * @return {@code true} if the userSetting has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/users/settings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteUserSetting(@PathVariable(name = "id") int id) throws OHServiceException {
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		final String ADMIN = "admin";
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		if (userSetting.get().getUser().equals(currentUser) || currentUser.equals(ADMIN)) {
			try {
				userSettingManager.deleteUserSetting(userSetting.get());
			} catch (OHServiceException serviceException) {
				throw new OHAPIException(new OHExceptionMessage("UserSetting not deleted."));
			}
			return ResponseEntity.ok(true);
		}
		throw new OHAPIException(new OHExceptionMessage("Not allowed."));
	}
}
