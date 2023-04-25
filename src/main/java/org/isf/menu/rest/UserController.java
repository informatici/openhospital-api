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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.dto.UserMenuItemDTO;
import org.isf.menu.dto.UserProfileDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserMenuItemMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.permissions.dto.LitePermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.LitePermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.FormatErrorMessage;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserGroupMapper userGroupMapper;
	
	@Autowired
	private UserMenuItemMapper userMenuItemMapper;
	
	@Autowired
	private UserBrowsingManager userManager;

	@Autowired
	protected PermissionManager permissionManager;

	@Autowired
	protected LitePermissionMapper litePermissionMapper;

	/**
	 * Returns the list of {@link User}s.
	 * @return the list of {@link User}s.
	 */
	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserDTO>> getUser(@RequestParam(name="group_id", required=false) String groupID) throws OHServiceException {
		LOGGER.info("Fetching the list of users");
		List<User> users;
		if (groupID != null) {
			try {
				users = userManager.getUser(groupID);
			} catch (OHServiceException e) {
				throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
			}
		} else {
			try {
				users = userManager.getUser();
			} catch (OHServiceException e) {
				throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
			}
		}
		List<UserDTO> mappedUsers = userMapper.map2DTOList(users);
		if (mappedUsers.isEmpty()) {
			LOGGER.info("No user found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedUsers);
		} else {
			LOGGER.info("Found {} users", mappedUsers.size());
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
		User user = null;
		try {
			user = userManager.getUserByName(userName);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (user == null) {
			throw new OHAPIException(new OHExceptionMessage("user.notfound"));
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
		LOGGER.info("Attempting to create a user");
		User user = userMapper.map2Model(userDTO);
		boolean isCreated;
		try {
			isCreated = userManager.newUser(user);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (!isCreated) {
			LOGGER.info("User is not created!");
            throw new OHAPIException(new OHExceptionMessage("user.notcreated"));
        }
		LOGGER.info("User successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(isCreated);
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
			@RequestParam(name="password", defaultValue="false") boolean updatePassword) throws OHServiceException {
		User user = userMapper.map2Model(userDTO);
		User foundUser;
		try {
			foundUser = userManager.getUserByName(user.getUserName());
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage("user.notfound"));
		}
		boolean isUpdated;
		if (updatePassword) {
			try {
				isUpdated = userManager.updatePassword(user);
			} catch (OHServiceException e) {
				throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
			}
		} else {
			try {
				isUpdated = userManager.updateUser(user);
			} catch (OHServiceException e) {
				throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
			}
		}
		if (isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage("user.notupdated"));
		}
	}
	
	/**
	 * Deletes an existing {@link User}.
	 * @param username - the name of the {@link User} to delete
	 * @return {@code true} if the user has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteUser(@PathVariable String username) throws OHServiceException {
		User foundUser;
		try {
			foundUser = userManager.getUserByName(username);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage("user.notfound"));
		}
		boolean isDelete;
		try {
			isDelete = userManager.deleteUser(foundUser);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (isDelete) {
			return ResponseEntity.ok(isDelete);
		} else {
			throw new OHAPIException(new OHExceptionMessage("user.notdeleted"));
		}
	}
	
	/**
	 * Returns the list of {@link UserGroup}s.
	 * @return the list of {@link UserGroup}s
	 */
	@GetMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserGroupDTO>> getUserGroup() throws OHServiceException {
		LOGGER.info("Attempting to fetch the list of user groups");
        List<UserGroup> groups = new ArrayList<>();
        try {
        	 groups = userManager.getUserGroup();
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        List<UserGroupDTO> mappedGroups = userGroupMapper.map2DTOList(groups);
        if (mappedGroups.isEmpty()) {
			LOGGER.info("No group found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedGroups);
		} else {
	        LOGGER.info("Found {} groups", mappedGroups.size());
			return ResponseEntity.ok(mappedGroups);
		}
	}
	
	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for specified {@link User}.
	 * @param username - the name of the {@link User}
	 * @return the list of {@link UserMenuItem}s 
	 */
	@GetMapping(value = "/users/menus/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserMenuItemDTO>> getMenu(@PathVariable String username) throws OHServiceException {
		User user = userManager.getUserByName(username);
		if (user == null) {
			throw new OHAPIException(new OHExceptionMessage("user.notfound"));
		}
        List<UserMenuItem> menuItems = new ArrayList<>();
        try {
        	menuItems = userManager.getMenu(user);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        List<UserMenuItemDTO> mappedMenuItems = userMenuItemMapper.map2DTOList(menuItems);
        if (mappedMenuItems.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMenuItems);
		} else {
			return ResponseEntity.ok(mappedMenuItems);
		}
	}
	
	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for specified {@link UserGroup}.
	 * @param code - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s 
	 */
	@GetMapping(value = "/users/group-menus/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserMenuItemDTO>> getGroupMenu(@PathVariable("group_code") String code) throws OHServiceException {
        UserGroup group;
        try {
        	group = loadUserGroup(code);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        List<UserMenuItemDTO> menus = userMenuItemMapper.map2DTOList(userManager.getGroupMenu(group));
        if (menus.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menus);
		} else {
			return ResponseEntity.ok(menus);
		}
	}
	
	/**
	 * Replaces the {@link UserGroup} rights.
	 * @param code - the {@link UserGroup}'s code
	 * @param menusDTO - the list of {@link UserMenuItem}s
	 * @return {@code true} if the menu has been replaced, {@code false} otherwise.
	 */
	@PostMapping(value = "/users/groups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> setGroupMenu(
			@PathVariable("group_code") String code, 
			@Valid @RequestBody List<UserMenuItemDTO> menusDTO) throws OHServiceException {
		UserGroup group;
		try {
        	group = loadUserGroup(code);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        List<UserMenuItem> menus = new ArrayList<>();
		menus.addAll(userMenuItemMapper.map2ModelList(menusDTO));
        boolean done;
        try {
        	done = userManager.setGroupMenu(group, menus);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        if (done) {
        	return ResponseEntity.ok(done);
        } else {
        	throw new OHAPIException(new OHExceptionMessage("user.grouprightsnotupdate"));
        }
	}
	
	/**
	 * Deletes a {@link UserGroup}.
	 * @param code - the code of the {@link UserGroup} to delete
	 * @return {@code true} if the group has been deleted, {@code false} otherwise.
	 */
	@DeleteMapping(value = "/users/groups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteGroup(@PathVariable("group_code") String code) throws OHServiceException {
		UserGroup group;
		try {
			group = loadUserGroup(code);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		boolean isDeleted;
		try {
			isDeleted = userManager.deleteGroup(group);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (isDeleted) {
			return ResponseEntity.ok(isDeleted);
		} else {
			throw new OHAPIException(new OHExceptionMessage("user.usergroupnotdeleted"));
		}
	}
	
	/**
	 * Create a new {@link UserGroup} with a minimum set of rights.
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return {@code true} if the group has been inserted, {@code false} otherwise.
	 */
	@PostMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
		UserGroup userGroup = userGroupMapper.map2Model(aGroup);
		boolean isCreated;
		try {
			isCreated = userManager.newUserGroup(userGroup);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
		if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage("user.usergroupenotcreated"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(isCreated);
	}
	
	/**
	 * Updates an existing {@link UserGroup}.
	 * @param aGroup - the {@link UserGroup} to update
	 * @return {@code true} if the group has been updated, {@code false} otherwise.
	 */
	@PutMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
        UserGroup group = userGroupMapper.map2Model(aGroup);
        boolean isPresent;
        try {
			isPresent= userManager.getUserGroup().stream().noneMatch(g -> g.getCode().equals(group.getCode()));
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        if (isPresent) {
        	throw new OHAPIException(new OHExceptionMessage("user.usergroupnotfound"));
        }
        boolean isUpdated;
        try {
        	isUpdated = userManager.updateUserGroup(group);
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        if (isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage("user.usergroupenotupdated"));
		}
	}
	
	private UserGroup loadUserGroup(String code) throws OHServiceException {
		List<UserGroup> group = new ArrayList<>();
		try {
			group = userManager.getUserGroup().stream().filter(g -> g.getCode().equals(code)).collect(Collectors.toList());
		} catch (OHServiceException e) {
			throw new OHAPIException(new OHExceptionMessage(FormatErrorMessage.format(e.getMessages().get(0).getMessage())));
		}
        if (group.isEmpty()) {
        	throw new OHAPIException(new OHExceptionMessage("user.usergroupnotfound"));
        }
        return group.get(0);
	}

	/**
	 * Retrieves all permissions of the current logged in user. If user not found,
	 * an empty list of permissions is returned
	 *
	 * @return list of permissions {@link Permission}
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/users/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LitePermissionDTO>> retrievePermissionsByCurrentLoggedInUser() throws OHServiceException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			LOGGER.info("retrieving permissions: retrievePermissionsByCurrentLoggedInUser({})", currentUserName);
			List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(currentUserName);
			List<LitePermissionDTO> dtos = this.litePermissionMapper.map2DTOList(domains);
			if (dtos.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
			} else {
				return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
			}
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			LOGGER.info("retrieving profile: retrieveProfileByCurrentLoggedInUser({})", currentUserName);
			List<Permission> permissions = this.permissionManager.retrievePermissionsByUsername(currentUserName);
			List<String> permissionsCode = permissions.stream().map(p -> p.getName()).collect(Collectors.toList());
			UserProfileDTO userProfileDTO = new UserProfileDTO();
			userProfileDTO.setUserName(currentUserName);
			userProfileDTO.setPermissions(permissionsCode);
			return ResponseEntity.status(HttpStatus.OK).body(userProfileDTO);
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new UserProfileDTO());
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
		LOGGER.info("retrieving permissions: retrievePermissionsByUsername({})", username);
		List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(username);
		List<LitePermissionDTO> dtos = this.litePermissionMapper.map2DTOList(domains);
		if (dtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
		}
	}

}
