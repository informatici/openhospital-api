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
package org.isf.usersettings.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.model.UserSetting;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.usersettings.dto.UserSettingDTO;
import org.isf.usersettings.mapper.UserSettingMapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/usersettings")
@Tag(name = "User Settings")
@SecurityRequirement(name = "bearerAuth")
public class UserSettingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingController.class);

	@Autowired
	private UserBrowsingManager userManager;
	@Autowired
	private UserSettingManager userSettingManager;

	@Autowired
	private UserSettingMapper userSettingMapper;

	/**
	 * Retrieves all userSettings of the current user.
	 * @return list of userSetting {@link UserSettingDTO}.
	 * @throws OHServiceException When fail to retrieve userSettings
	 */
	@GetMapping(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * @param userSettingDTO -  the {@link UserSettingDTO} to insert.
	 * @return {@link UserSettingDTO} if the userSetting has been created, null otherwise.
	 * @throws OHServiceException
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * @param userSettingDTO - the {@link UserSettingDTO} to update.
	 * @param id - id of {@link UserSetting} .
	 * @return {@link UserSettingDTO} if the UserSetting has been updated , null otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/usersettings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * Retrieves an existing {@link UserSettingDTO} by user.
	 * @param configName - the name of the userSetting {@link UserSetting} .
	 * @return {@link UserSettingDTO} if the UserSetting exists, null otherwise.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/usersettings/{configName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserSettingDTO getUserSettingByUser(@PathVariable(name = "configName") String configName) throws OHServiceException {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		LOGGER.info("Retrieve the userSetting By user {} and configName {}.", currentUser, configName);
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(currentUser, configName);
		if (userSetting == null) {
			LOGGER.info("No user settings '{}' for the user {}.", configName, currentUser);
			throw new OHAPIException(new OHExceptionMessage("No setting found"), HttpStatus.NOT_FOUND);
		}
		return userSettingMapper.map2DTO(userSetting);
	}

	/**
	 * Deletes a {@link UserSetting}.
	 * @param id - the id of the userSetting {@link UserSetting} to delete.
	 * @return {@code true} if the userSetting has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/usersettings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
}
