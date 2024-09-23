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

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserProfileDTO;
import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserSetting;
import org.isf.permissions.dto.LitePermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.LitePermissionMapper;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	private PermissionMapper permissionMapper;

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
	 *
	 * @return the list of {@link User}s.
	 */
	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserDTO> getUser(@RequestParam(name = "group_id", required = false) String groupID) throws OHServiceException {
		LOGGER.info("Fetching the list of users.");
		List<User> users;
		if (groupID != null) {
			users = userManager.getUser(groupID);
		} else {
			users = userManager.getUser();
		}
		List<UserDTO> mappedUsers = userMapper.map2DTOList(
			users.stream().peek(user -> user.setPasswd(null)).toList()
		);
		LOGGER.info("Found {} user(s).", mappedUsers.size());
		return mappedUsers;
	}

	/**
	 * Returns a {@link User}.
	 *
	 * @param userName - user name
	 * @return {@link User}
	 */
	@GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDTO getUserByName(@PathVariable("username") String userName) throws OHServiceException {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!Objects.equals(currentUser, userName)) {
			throw new OHAPIException(new OHExceptionMessage("You're not authorized to access this resource."), HttpStatus.FORBIDDEN);
		}

		User user = userManager.getUserByName(userName);
		if (user == null) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
		user.setPasswd(null);
		return userMapper.map2DTO(user);
	}

	/**
	 * Updates an existing {@link User}.
	 * When the password in the payload is not empty, other field are ignored and only the password is updated,
	 * otherwise other field are updated except the password(whether empty or not).
	 *
	 * @param userDTO - the {@link User} to update
	 * @return the updated {@link UserDTO} if the user has been updated.
	 * @throws OHServiceException throws if the update fails
	 */
	@PutMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDTO updateUser(
		@PathVariable("username") String userName,
		@Valid @RequestBody UserDTO userDTO) throws OHServiceException {
		LOGGER.info("Updating the user {}.", userName);
		String requestUserName = userDTO.getUserName();
		if (requestUserName != null && !userName.equals(requestUserName)) {
			throw new OHAPIException(new OHExceptionMessage("Invalid request payload"));
		}
		if (userManager.getUserByName(userName) == null) {
			LOGGER.info("User with name '{}' has not been found.", userName);
			throw new OHAPIException(new OHExceptionMessage("The specified user does not exist."), HttpStatus.NOT_FOUND);
		}
		User user = userMapper.map2Model(userDTO);
		boolean isUpdated;
		if (!user.getPasswd().isEmpty()) {
			isUpdated = userManager.updatePassword(user);
		} else {
			isUpdated = userManager.updateUser(user);
		}
		if (isUpdated) {
			LOGGER.info("User {} has been updated successfully.", userName);
			user = userManager.getUserByName(userName);
			user.setPasswd(null);
			return userMapper.map2DTO(user);
		} else {
			throw new OHAPIException(new OHExceptionMessage("User not updated."));
		}
	}

	/**
	 * Creates a new {@link User}.
	 *
	 * @param userDTO - the {@link User} to insert
	 * @return {@code true} if the user has been inserted, {@code false} otherwise.
	 * @throws OHServiceException When failed to create user
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean newUser(@Valid @RequestBody UserDTO userDTO) throws OHServiceException {
		LOGGER.info("Attempting to create user {}.", userDTO.getUserName());
		User user = userMapper.map2Model(userDTO);
		try {
			userManager.newUser(user);
			LOGGER.info("User successfully created.");
			return true;
		} catch (OHServiceException serviceException) {
			LOGGER.info("User is not created.");
			throw new OHAPIException(new OHExceptionMessage("User not created."));
		}
	}

	/**
	 * Deletes an existing {@link User}.
	 *
	 * @param username - the name of the {@link User} to delete
	 * @return {@code true} if the user has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean deleteUser(@PathVariable String username) throws OHServiceException {
		User foundUser = userManager.getUserByName(username);
		if (foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
		try {
			userManager.deleteUser(foundUser);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("User not deleted."));
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
	public UserProfileDTO retrieveProfileByCurrentLoggedInUser() throws OHServiceException {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		LOGGER.info("Retrieving profile: retrieveProfileByCurrentLoggedInUser({}).", currentUser);
		return retrieveProfile(currentUser);
	}

	/**
	 * Updates the current {@link User} profile.
	 * When the password in the payload is not empty, other field are ignored and only the password is updated,
	 * otherwise other field are updated except the password(whether empty or not).
	 *
	 * @param userDTO - the {@link User} to update
	 * @return the current {@link UserProfileDTO} if the user has been updated.
	 * @throws OHServiceException throws if the update fails
	 */
	@PutMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserProfileDTO updateProfile(
		@RequestBody UserDTO userDTO) throws OHServiceException {
		String requestUserName = userDTO.getUserName();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User entity = userManager.getUserByName(requestUserName);
		LOGGER.info("Updating the user {}.", currentUser);
		if (!requestUserName.equals(currentUser)) {
			throw new OHAPIException(new OHExceptionMessage(String.format("You're not allowed to update %s's profile.", requestUserName)),
				HttpStatus.FORBIDDEN);
		}
		if (entity == null) {
			throw new OHAPIException(new OHExceptionMessage("The specified user does not exist."));
		}
		User user = userMapper.map2Model(userDTO);
		user.setUserGroupName(entity.getUserGroupName());
		boolean isUpdated;
		if (!user.getPasswd().isEmpty()) {
			isUpdated = userManager.updatePassword(user);
		} else {
			isUpdated = userManager.updateUser(user);
		}
		if (isUpdated) {
			LOGGER.info("User {} has been successfully updated.", currentUser);
			return retrieveProfile(currentUser);
		} else {
			throw new OHAPIException(new OHExceptionMessage("User not updated."));
		}
	}

	/**
	 * Retrieves all permissions of the username passed as path variable. If user not
	 * found, an empty list of permissions is returned.
	 *
	 * @return list of permissions {@link Permission}
	 * @throws OHServiceException When failed to load user's permissions
	 */
	@GetMapping(value = "/users/{username}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<LitePermissionDTO> retrievePermissionsByUsername(@PathVariable("username") String username) throws OHServiceException {
		LOGGER.info("Retrieving permissions: retrievePermissionsByUsername({}).", username);
		List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(username);
		return this.litePermissionMapper.map2DTOList(domains);
	}

	/**
	 * Retrieves all userSetting of the current user.
	 *
	 * @return list of userSetting {@link UserSettingDTO}.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/settings", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserSettingDTO> getUserSettings() throws OHServiceException {
		LOGGER.info("Retrieve all userSettings of the current user.");
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(currentUser);
		if (userSettings == null || userSettings.isEmpty()) {
			LOGGER.info("No settings for the current user {}.", currentUser);
			return Collections.emptyList();
		}
		List<UserSettingDTO> userSettingsDTO = userSettingMapper.map2DTOList(userSettings);
		LOGGER.info("Found {} user settings.", userSettingsDTO);
		return userSettingsDTO;
	}

	/**
	 * Returns a {@link UserSettingDTO} of userSetting created.
	 *
	 * @param userSettingDTO -  the {@link UserSettingDTO} to insert.
	 * @return {@link UserSettingDTO} if the userSetting has been created, null otherwise.
	 * @throws OHServiceException
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/users/settings", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserSettingDTO newUserSettings(@Valid @RequestBody UserSettingDTO userSettingDTO) throws OHServiceException {
		LOGGER.info("Create a UserSetting.");
		String requestUserName = userSettingDTO.getUser();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!requestUserName.equals(currentUser)) {
			throw new OHAPIException(new OHExceptionMessage("Not allowed."), HttpStatus.FORBIDDEN);
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
		return userSettingMapper.map2DTO(created);
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
	public UserSettingDTO updateUserSettings(@PathVariable(name = "id") int id, @Valid @RequestBody UserSettingDTO userSettingDTO)
		throws OHServiceException {
		LOGGER.info("Update a UserSetting.");
		String requestUserName = userSettingDTO.getUser();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userSettingDTO.getId() == 0 || userSettingDTO.getId() != 0 && userSettingDTO.getId() != id) {
			throw new OHAPIException(new OHExceptionMessage("Malformed request."));
		}
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		UserSetting updated;
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			throw new OHAPIException(new OHExceptionMessage("UserSetting doesn't exists."));
		}
		if (!userSetting.get().getUser().equals(requestUserName) || !userSetting.get().getUser().equals(currentUser)) {
			throw new OHAPIException(new OHExceptionMessage("Not allowed."), HttpStatus.FORBIDDEN);
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
		return userSettingMapper.map2DTO(updated);
	}

	/**
	 * Retrieves an existing {@link UserSettingDTO} by id.
	 *
	 * @param id - id of userSetting {@link UserSetting} .
	 * @return {@link UserSettingDTO} if the UserSetting exists, null otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/settings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserSettingDTO getUserSettingById(@PathVariable(name = "id") int id) throws OHServiceException {
		LOGGER.info("Retrieve the userSetting By id {}.", id);
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			throw new OHAPIException(new OHExceptionMessage("No setting found"), HttpStatus.NOT_FOUND);
		}
		var setting = userSetting.get();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!Objects.equals(currentUser, setting.getUser())) {
			throw new OHAPIException(new OHExceptionMessage("You're not authorized to access this resource."), HttpStatus.FORBIDDEN);
		}
		return userSettingMapper.map2DTO(userSetting.get());
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
	public UserSettingDTO getUserSettingByUser(@PathVariable(name = "userName") String userName,
		@PathVariable(name = "configName") String configName) throws OHServiceException {
		LOGGER.info("Retrieve the userSetting By user {} and configName {}.", userName, configName);
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(userName);
		if (userSettings == null || userSettings.isEmpty()) {
			LOGGER.info("No user settings for the user {}.", userName);
			throw new OHAPIException(new OHExceptionMessage("No setting found"), HttpStatus.NOT_FOUND);
		}
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(userName, configName);
		if (userSetting == null) {
			LOGGER.info("No user settings '{}' for the user {}.", configName, userName);
			throw new OHAPIException(new OHExceptionMessage("No setting found"), HttpStatus.NOT_FOUND);
		}
		return userSettingMapper.map2DTO(userSetting);
	}

	/**
	 * Deletes a {@link UserSetting}.
	 *
	 * @param id - the id of the userSetting {@link UserSetting} to delete.
	 * @return {@code true} if the userSetting has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/users/settings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Boolean deleteUserSetting(@PathVariable(name = "id") int id) throws OHServiceException {
		Optional<UserSetting> userSetting = userSettingManager.getUserSettingById(id);
		final String ADMIN = "admin";
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userSetting.isEmpty()) {
			LOGGER.info("No user settings with id {}.", id);
			throw new OHAPIException(new OHExceptionMessage("No setting found"), HttpStatus.NOT_FOUND);
		}
		if (userSetting.get().getUser().equals(currentUser) || currentUser.equals(ADMIN)) {
			try {
				userSettingManager.deleteUserSetting(userSetting.get());
			} catch (OHServiceException serviceException) {
				throw new OHAPIException(new OHExceptionMessage("UserSetting not deleted."));
			}
			return true;
		}
		throw new OHAPIException(new OHExceptionMessage("Not allowed."));
	}

	private UserProfileDTO retrieveProfile(String currentUser) throws OHServiceException {
		List<Permission> permissions = this.permissionManager.retrievePermissionsByUsername(currentUser);
		List<String> permissionsCode = permissions.stream().map(Permission::getName).collect(Collectors.toList());
		UserProfileDTO userProfileDTO = new UserProfileDTO();
		User user = userManager.getUserByName(currentUser);
		userProfileDTO.setUserGroup(userGroupMapper.map2DTO(user.getUserGroupName()));
		userProfileDTO.setUserName(currentUser);
		userProfileDTO.setPermissions(permissionsCode);
		return userProfileDTO;
	}
}
