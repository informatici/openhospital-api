package org.isf.menu.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.menu.dto.UserDTO;
import org.isf.menu.dto.UserGroupDTO;
import org.isf.menu.dto.UserMenuItemDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.mapper.UserGroupMapper;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserMenuItemMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserGroupMapper userGroupMapper;
	
	@Autowired
	private UserMenuItemMapper userMenuItemMapper;
	
	@Autowired
	private UserBrowsingManager userManager;
	
	/**
	 * returns the list of {@link User}s
	 * @return the list of {@link User}s
	 */
	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserDTO>> getUser(@RequestParam(name="group_id", required=false) String groupID) throws OHServiceException {
		logger.info("Fetching the list of users");
		List<User> users;
		if(groupID != null) {
			users = userManager.getUser(groupID);
		} else {
			users = userManager.getUser();
		}
		List<UserDTO> mappedUsers = userMapper.map2DTOList(users);
		if(mappedUsers.isEmpty()) {
			logger.info("No user found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedUsers);
		} else {
			logger.info("Found " + mappedUsers.size() + " users");
			return ResponseEntity.ok(mappedUsers);
		}
	}
	
	/**
	 * returns the {@link User}
	 * @param userName - user name
	 * @return {@link User}
	 */
	@GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDTO> getUserByName(@PathVariable("username") String userName) throws OHServiceException {
		User user = userManager.getUserByName(userName);
		if(user == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "User not found!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(userMapper.map2DTO(user));
	}
	
	/**
	 * inserts a new {@link User} in the DB
	 * @param userDTO - the {@link User} to insert
	 * @return <code>true</code> if the user has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newUser(@Valid @RequestBody UserDTO userDTO) throws OHServiceException {
		logger.info("Attempting to create a user");
		User user = userMapper.map2Model(userDTO);
		boolean isCreated = userManager.newUser(user);
		if (!isCreated) {
			logger.info("User is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "User is not created!", OHSeverityLevel.ERROR));
        }
		logger.info("User successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(isCreated);
	}
	
	/**
	 * updates an existing {@link User} in the DB
	 * @param userDTO - the {@link User} to update
	 * @param updatePassword - indicates if it is the password that need to be updated
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 */
	@PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUser(
			@Valid @RequestBody UserDTO userDTO, 
			@RequestParam(name="password", defaultValue="false") boolean updatePassword) throws OHServiceException {
		User user = userMapper.map2Model(userDTO);
		User foundUser = userManager.getUserByName(user.getUserName());
		if(foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "User not found!", OHSeverityLevel.ERROR));
		}
		boolean isUpdated;
		if(updatePassword) {
			isUpdated = userManager.updatePassword(user);
		} else {
			isUpdated = userManager.updateUser(user);
		}
		if(isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "User is not updated!", OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * deletes an existing {@link User}
	 * @param user - the {@link User} to delete
	 * @return <code>true</code> if the user has been deleted, <code>false</code> otherwise.
	 */
	@DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteUser(@PathVariable String username) throws OHServiceException {
		User foundUser = userManager.getUserByName(username);
		if(foundUser == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "User not found!", OHSeverityLevel.ERROR));
		}
		boolean isDelete = userManager.deleteUser(foundUser);
		if(isDelete) {
			return ResponseEntity.ok(isDelete);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "User is not deleted!", OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * returns the list of {@link UserGroup}s
	 * @return the list of {@link UserGroup}s
	 */
	@GetMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserGroupDTO>> getUserGroup() throws OHServiceException {
		logger.info("Attempting to fetch the list of user groups");
        List<UserGroup> groups = userManager.getUserGroup();
        List<UserGroupDTO> mappedGroups = userGroupMapper.map2DTOList(groups);
        if(mappedGroups.isEmpty()) {
			logger.info("No group found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedGroups);
		} else {
			logger.info("Found " + mappedGroups.size() + " groups");
			return ResponseEntity.ok(mappedGroups);
		}
	}
	
	/**
	 * returns the list of {@link UserMenuItem}s that compose the menu for specified {@link User}
	 * @param userDTO - the {@link User}
	 * @return the list of {@link UserMenuItem}s 
	 */
	@GetMapping(value = "/users/menus/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserMenuItemDTO>> getMenu(@PathVariable String username) throws OHServiceException {
		User user = userManager.getUserByName(username);
		if(user == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "User not found!", OHSeverityLevel.ERROR));
		}
        List<UserMenuItem> menuItems = userManager.getMenu(user);
        List<UserMenuItemDTO> mappedMenuItems = userMenuItemMapper.map2DTOList(menuItems);
        if(mappedMenuItems.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMenuItems);
		} else {
			return ResponseEntity.ok(mappedMenuItems);
		}
	}
	
	/**
	 * returns the list of {@link UserMenuItem}s that compose the menu for specified {@link UserGroup}
	 * @param aGroup - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s 
	 */
	@GetMapping(value = "/users/group-menus/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserMenuItemDTO>> getGroupMenu(@PathVariable("group_code") String code) throws OHServiceException {
        UserGroup group = loadUserGroup(code);
        List<UserMenuItemDTO> menus = userMenuItemMapper.map2DTOList(userManager.getGroupMenu(group));
        if(menus.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menus);
		} else {
			return ResponseEntity.ok(menus);
		}
	}
	
	/**
	 * replaces the {@link UserGroup} rights
	 * @param code - the {@link UserGroup}'s code
	 * @param menusDTO - the list of {@link UserMenuItem}s
	 * @return <code>true</code> if the menu has been replaced, <code>false</code> otherwise.
	 */
	@PostMapping(value = "/users/groups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> setGroupMenu(
			@PathVariable("group_code") String code, 
			@Valid @RequestBody List<UserMenuItemDTO> menusDTO) throws OHServiceException {
		UserGroup group = loadUserGroup(code);
        ArrayList<UserMenuItem> menus = new ArrayList<UserMenuItem>();
        userMenuItemMapper.map2ModelList(menusDTO).forEach(m -> menus.add(m));
        boolean done = userManager.setGroupMenu(group, menus);
        if(done) {
        	return ResponseEntity.ok(done);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, "Group rights hasn't been updated!", OHSeverityLevel.ERROR));
        }
	}
	
	/**
	 * deletes a {@link UserGroup}
	 * @param code - the code of the {@link UserGroup} to delete
	 * @return <code>true</code> if the group has been deleted, <code>false</code> otherwise.
	 */
	@DeleteMapping(value = "/users/groups/{group_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteGroup(@PathVariable("group_code") String code) throws OHServiceException {
		UserGroup group = loadUserGroup(code);
		boolean isDeleted = userManager.deleteGroup(group);
		if(isDeleted) {
			return ResponseEntity.ok(isDeleted);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "User group is not deleted!", OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * insert a new {@link UserGroup} with a minimum set of rights
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return <code>true</code> if the group has been inserted, <code>false</code> otherwise.
	 */
	@PostMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
		UserGroup userGroup = userGroupMapper.map2Model(aGroup);
		boolean isCreated = userManager.newUserGroup(userGroup);
		if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "User group is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(isCreated);
	}
	
	/**
	 * updates an existing {@link UserGroup} in the DB
	 * @param aGroup - the {@link UserGroup} to update
	 * @return <code>true</code> if the group has been updated, <code>false</code> otherwise.
	 */
	@PutMapping(value = "/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUserGroup(@Valid @RequestBody UserGroupDTO aGroup) throws OHServiceException {
        UserGroup group = userGroupMapper.map2Model(aGroup);
        if(!userManager.getUserGroup().stream().anyMatch(g -> g.getCode().equals(group.getCode()))) {
        	throw new OHAPIException(new OHExceptionMessage(null, "User group not found!", OHSeverityLevel.ERROR));
        }
        boolean isUpdated = userManager.updateUserGroup(group);
        if(isUpdated) {
			return ResponseEntity.ok(isUpdated);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "User group is not updated!", OHSeverityLevel.ERROR));
		}
	}
	
	private UserGroup loadUserGroup(String code) throws OHServiceException {
		List<UserGroup> group = userManager.getUserGroup().stream().filter(g -> g.getCode().equals(code)).collect(Collectors.toList());
        if(group.isEmpty()) {
        	throw new OHAPIException(new OHExceptionMessage(null, "User group not found!", OHSeverityLevel.ERROR));
        }
        return group.get(0);
	}
}
