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
package org.isf.users.rest;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.permissions.dto.PermissionDTO;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.mapper.PermissionMapper;
import org.isf.permissions.model.Permission;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.usergroups.mapper.UserGroupMapper;
import org.isf.users.dto.UserDTO;
import org.isf.users.dto.UserProfileDTO;
import org.isf.users.mapper.UserMapper;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final PermissionManager permissionManager;

    private final PermissionMapper permissionMapper;

    private final UserMapper userMapper;

    private final UserGroupMapper userGroupMapper;

    private final UserBrowsingManager userManager;

    public UserController(
        PermissionManager permissionManager,
        PermissionMapper permissionMapper,
        UserMapper userMapper,
        UserGroupMapper userGroupMapper,
        UserBrowsingManager userManager
    ) {
        this.permissionManager = permissionManager;
        this.permissionMapper = permissionMapper;
        this.userMapper = userMapper;
        this.userGroupMapper = userGroupMapper;
        this.userManager = userManager;
    }

    /**
     * Returns the list of {@link User}s.
     *
     * @return the list of {@link User}s.
     */
    @GetMapping("/users")
    public List<UserDTO> getUser(
        @RequestParam(name = "group_id", required = false) String groupID
    ) throws OHServiceException {
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
     * @param userName - username
     * @return {@link User}
     */
    @GetMapping("/users/{username}")
    public UserDTO getUserByName(@PathVariable("username") String userName) throws OHServiceException {
        User user = userManager.getUserByName(userName);
        if (user == null) {
            throw new OHAPIException(new OHExceptionMessage("User not found."), HttpStatus.NOT_FOUND);
        }
        user.setPasswd(null);
        return userMapper.map2DTO(user);
    }

    /**
     * Updates an existing {@link User}. When the password in the payload is not empty,
     * other fields are ignored and only the password is updated, otherwise
     * other fields are updated except the password(whether empty or not).
     *
     * @param userDTO - the {@link User} to update
     * @return the updated {@link UserDTO} if the user has been updated.
     * @throws OHServiceException throws if the update fails
     */
    @PutMapping("/users/{username}")
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
     * @return the new created user.
     * @throws OHServiceException When failed to create user
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDTO newUser(@Valid @RequestBody UserDTO userDTO) throws OHServiceException {
        LOGGER.info("Attempting to create user {}.", userDTO.getUserName());
        User user = userMapper.map2Model(userDTO);
        try {
            LOGGER.info("User successfully created.");
            return userMapper.map2DTO(userManager.newUser(user));
        } catch (OHServiceException serviceException) {
            LOGGER.info("User is not created.");
            throw new OHAPIException(new OHExceptionMessage("User not created."));
        }
    }

    /**
     * Deletes an existing {@link User}.
     *
     * @param username - the name of the {@link User} to delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{username}")
    public void deleteUser(@PathVariable String username) throws OHServiceException {
        User foundUser = userManager.getUserByName(username);
        if (foundUser == null) {
            throw new OHAPIException(new OHExceptionMessage("User not found."));
        }
        try {
            userManager.deleteUser(foundUser);
        } catch (OHServiceException serviceException) {
            throw new OHAPIException(new OHExceptionMessage("User not deleted."));
        }
    }

    /**
     * Retrieves profile of the current logged in user.
     * If user not found, an empty list of permissions is returned
     *
     * @return list of permissions {@link Permission}
     * @throws OHServiceException When failed to get logged in user
     */
    @GetMapping("/users/me")
    public UserProfileDTO retrieveProfileByCurrentLoggedInUser() throws OHServiceException {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Retrieving profile: retrieveProfileByCurrentLoggedInUser({}).", currentUser);
        return retrieveProfile(currentUser);
    }

    /**
     * Updates the current {@link User} profile. When the password
     * in the payload is not empty, other fields are ignored and only the password is updated,
     * otherwise other fields are updated except the password(whether empty or not).
     *
     * @param userDTO - the {@link User} to update
     * @return the current {@link UserProfileDTO} if the user has been updated.
     * @throws OHServiceException throws if the update fails
     */
    @PutMapping("/users/me")
    public UserProfileDTO updateProfile(@RequestBody UserDTO userDTO) throws OHServiceException {
        String requestUserName = userDTO.getUserName();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User entity = userManager.getUserByName(requestUserName);
        LOGGER.info("Updating the user {}.", currentUser);
        if (!requestUserName.equals(currentUser)) {
            throw new OHAPIException(new OHExceptionMessage(String.format(""
                + "You're not allowed to update %s's profile.", requestUserName)
            ),HttpStatus.FORBIDDEN);
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
     * Retrieves all permissions of the username passed as path variable.
     * If user not found, an empty list of permissions is returned.
     *
     * @return list of permissions {@link Permission}
     * @throws OHServiceException When failed to load user's permissions
     */
    @GetMapping("/users/{username}/permissions")
    public List<PermissionDTO> retrievePermissionsByUsername(
        @PathVariable("username") String username
    ) throws OHServiceException {
        LOGGER.info("Retrieving permissions: retrievePermissionsByUsername({}).", username);
        List<Permission> domains = this.permissionManager.retrievePermissionsByUsername(username);
        return this.permissionMapper.map2DTOList(domains);
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
