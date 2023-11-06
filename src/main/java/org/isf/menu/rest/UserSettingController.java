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

import javax.validation.Valid;

import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.UserSetting;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/usersettings")
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserSettingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingController.class);

	@Autowired
	private UserSettingMapper userSettingMapper;

	@Autowired
	private UserSettingManager userSettingManager;
	
	@Autowired
	private UserBrowsingManager userManager;

	public UserSettingController(UserSettingMapper userSettingMapper, UserSettingManager userSettingManager, UserBrowsingManager userManager) {
		this.userSettingMapper = userSettingMapper;
		this.userSettingManager = userSettingManager;
		this.userManager = userManager;
	}

	/**
	 * Create or update {@link UserSettingDTO}.
	 * 
	 * @param userSettingDTO - the {@link UserSettingDTO} to insert
	 * @return {@link UserSettingDTO} if the userSetting has been inserted, null
	 *         otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> newUserSetting(@Valid @RequestBody UserSettingDTO userSettingDTO)
					throws OHServiceException {
		LOGGER.info("Attempting to create or update a UserSetting");
		String userName = userSettingDTO.getUser();
		final String ADMIN = "admin";
		if (!userName.equals(SecurityContextHolder.getContext().getAuthentication().getName()) && !userName.equals(ADMIN)) {
		    throw new OHAPIException(new OHExceptionMessage("Not allowed."));
		}
		LOGGER.info("userName {}", userName);
		UserSetting userSetting = userSettingManager.getUserSetting(userName, userSettingDTO.getConfigName());
		UserSetting isCreated;
		if (userSetting != null) {
			LOGGER.info("update a UserSetting");
			userSetting.setConfigValue(userSettingDTO.getConfigValue());
			isCreated = userSettingManager.updateUserSetting(userSetting);
		} else {
			LOGGER.info("create a UserSetting");
			userSetting = userSettingMapper.map2Model(userSettingDTO);
			userSetting.setUser(userName);
			isCreated = userSettingManager.newUserSetting(userSetting);
		}

		if (isCreated == null) {
			LOGGER.info("UserSetting is not created!");
			throw new OHAPIException(new OHExceptionMessage("UserSetting not created."));
		}
		LOGGER.info("UserSetting successfully created!");
		return ResponseEntity.status(HttpStatus.CREATED).body(userSettingMapper.map2DTO(userSetting));
	}

	/**
	 * Returns a {@link UserSettingDTO} of userSetting.
	 * 
	 * @return {@link UserSettingDTO} if the userSetting with configName dashboard
	 *         exist, null otherwise.
	 */
	@GetMapping(value = "/usersettings/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> getUserSettingDashboard() throws OHServiceException {
		LOGGER.info("Attempting to fetch the UserSetting of dashboard of the current user");
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		String dashboard = "dashboard";
		UserSetting userSetting = userSettingManager.getUserSetting(userName, dashboard);
		if (userSetting == null) {
			LOGGER.info("No dashboard settings for the current user {}", userName);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			UserSettingDTO userSettingDashboard = userSettingMapper.map2DTO(userSetting);
			LOGGER.info("Found {} user settings", userSettingDashboard);
			return ResponseEntity.ok(userSettingDashboard);
		}

	}
}
