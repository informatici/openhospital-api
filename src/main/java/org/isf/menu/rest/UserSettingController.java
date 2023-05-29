package org.isf.menu.rest;

import java.util.List;

import javax.validation.Valid;

import org.isf.menu.dto.UserSettingDTO;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.mapper.UserMapper;
import org.isf.menu.mapper.UserSettingMapper;
import org.isf.menu.model.User;
import org.isf.menu.model.UserSetting;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserSettingController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserSettingController.class);

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserSettingMapper userSettingMapper;
	
	@Autowired
	private UserBrowsingManager userManager;
	
	@Autowired
	private UserSettingManager userSettingManager;
	
	/**
	 * Creates a new {@link UserSetting}.
	 * @param userSettingDTO - the {@link UserSetting} to insert
	 * @return {@link UserSetting} if the userSetting has been inserted, null otherwise.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSettingDTO> newUserSetting(@RequestParam(name="userId", required=true) String userId, @Valid @RequestBody UserSettingDTO userSettingDTO) throws OHServiceException {
		LOGGER.info("Attempting to create a userSetting");
		User user = userManager.getUserByName(userId);
		if (user == null || !userSettingDTO.getUser().equals(userId)) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
		UserSetting userSetting = userSettingMapper.map2Model(userSettingDTO);
		UserSetting isCreated = userSettingManager.newUserSetting(userSetting);
		if (isCreated == null) {
			LOGGER.info("UserSetting is not created!");
            throw new OHAPIException(new OHExceptionMessage("UserSetting not created."));
        }
		LOGGER.info("UserSetting successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(userSettingMapper.map2DTO(isCreated));
	}
	
	@GetMapping(value = "/usersettings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserSettingDTO>> getUserSetting(@RequestParam(name="userId", required=true) String userId) throws OHServiceException {
		LOGGER.info("Attempting to fetch the list of user settings");
		User user = userManager.getUserByName(userId);
		if (user == null) {
			throw new OHAPIException(new OHExceptionMessage("User not found."));
		}
        List<UserSettingDTO> listUserSetting = userSettingMapper.map2DTOList(userSettingManager.getUserSetting(userId));
        if (listUserSetting.isEmpty()) {
			LOGGER.info("No user settings found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listUserSetting);
		} else {
	        LOGGER.info("Found {} user settings", listUserSetting.size());
			return ResponseEntity.ok(listUserSetting);
		}
	}
}
