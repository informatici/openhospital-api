/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.settings.rest;

import java.util.List;

import org.isf.settings.dto.SettingDTO;
import org.isf.settings.dto.UpdateSettingDTO;
import org.isf.settings.manager.SettingManager;
import org.isf.settings.mapper.SettingMapper;
import org.isf.settings.model.Setting;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Setting DTO
 * @author Silevester D.
 */
@RestController
@Tag(name = "Settings")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingController {

	private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

	private final SettingMapper mapper;

	private final SettingManager manager;

	public SettingController(SettingMapper mapper, SettingManager manager) {
		this.mapper = mapper;
		this.manager = manager;
	}

	/**
	 * Get a setting by its ID
	 * @param id Setting ID
	 * @return {@link SettingDTO}
	 * @throws OHServiceException When failed to get the setting
	 */
	@GetMapping("/settings/{id}")
	public SettingDTO getSettingById(@PathVariable("id") int id) throws OHServiceException {
		Setting setting = manager.getById(id);

		if (setting == null) {
			throw new OHAPIException(new OHExceptionMessage("setting not found with ID: " + id), HttpStatus.NOT_FOUND);
		}

		logger.info("Retrieving setting {} using id, current value: {}", setting.getCode(), setting.getValue());

		return mapper.map2DTO(setting);
	}

	/**
	 * Get a setting by its code
	 * @param code Setting code
	 * @return {@link SettingDTO}
	 * @throws OHServiceException When failed to get the setting
	 */
	@GetMapping("/settings/code/{code}")
	public SettingDTO getSettingByCode(@PathVariable("code") String code) throws OHServiceException {
		Setting setting = manager.getByCode(code);

		if (setting == null) {
			throw new OHAPIException(new OHExceptionMessage("setting not found with code: " + code), HttpStatus.NOT_FOUND);
		}

		logger.info("Retrieving setting {} using code, current value: {}", setting.getCode(), setting.getValue());

		return mapper.map2DTO(setting);
	}

	/**
	 * Get all settings
	 * @return {@link List} of {@link SettingDTO}
	 * @throws OHServiceException When failed to get settings
	 */
	@GetMapping("/settings")
	public List<SettingDTO> getAllSettings() throws OHServiceException {
		logger.info("Retrieved all settings");
		return mapper.map2DTOList(manager.findAll());
	}

	/**
	 * Update setting
	 * @param code Setting code
	 * @param dto Setting Update payload
	 * @return {@link SettingDTO} the updated setting
	 * @throws OHServiceException When failed to update setting
	 */
	@PutMapping("/settings/{code}")
	public SettingDTO updateSetting(
		@PathVariable("code") String code, @RequestBody UpdateSettingDTO dto
	) throws OHServiceException {
		Setting setting = manager.getByCode(code);
		if (setting == null) {
			throw new OHAPIException(new OHExceptionMessage("setting not found with code: " + code), HttpStatus.NOT_FOUND);
		}

		logger.info("Updating setting {}, old value: {}, new value: {}", setting.getCode(), setting.getValue(), dto.getValue());

		setting.setValue(dto.getValue());

		return mapper.map2DTO(manager.update(setting));
	}

	/**
	 * Reset all settings to default
	 * @return <code>true</code> when settings have been reset, <code>false</code> otherwise
	 * @throws OHServiceException When failed to reset settings
	 */
	@PostMapping("/settings/reset")
	@ResponseStatus(HttpStatus.OK)
	public boolean resetAllSettings() throws OHServiceException {
		logger.info("Reset all settings");
		return manager.resetAll();
	}
}
