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
package org.isf.sms.rest;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.sms.dto.SmsDTO;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.mapper.SmsMapper;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "SMS")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsController.class);

	private final SmsManager smsManager;

	private final SmsMapper smsMapper;

	public SmsController(SmsManager smsManager, SmsMapper smsMapper) {
		this.smsManager = smsManager;
		this.smsMapper = smsMapper;
	}

	/**
	 * Fetch the list of {@link Sms}s.
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @return the found list
	 * @throws OHServiceException When failed to get sms
	 */
	@GetMapping(value = "/sms")
	public List<SmsDTO> getAll(
		@RequestParam() String dateFrom,
		@RequestParam() String dateTo
	) throws OHServiceException {
		LOGGER.info("Fetching the list of sms");

		return smsMapper.map2DTOList(smsManager.getAll(
			LocalDate.parse(dateFrom).atStartOfDay(),
			LocalDate.parse(dateTo).atStartOfDay()
		));
	}

	/**
	 * Save the specified {@link Sms}.
	 * @param smsDTO SMS payload
	 * @return {@code true} if the sms is saved
	 * @throws OHServiceException When failed to save the SMS
	 */
	@PostMapping(value = "/sms")
	public boolean saveSms(
		@RequestBody @Valid SmsDTO smsDTO,
		@RequestParam(defaultValue="false") boolean split
	) throws OHServiceException {
		smsManager.saveOrUpdate(smsMapper.map2Model(smsDTO), split);
		return true;
	}

	/**
	 * Deletes the specified {@link Sms}.
	 * @param smsDTOList SMS
	 * @return {@code true} if the sms is deleted
	 * @throws OHServiceException When failed to delete SMS
	 */
	@PostMapping(value = "/sms/delete")
	public Boolean deleteSms(@RequestBody @Valid List<SmsDTO> smsDTOList) throws OHServiceException {
		List<Sms> smsList = smsMapper.map2ModelList(smsDTOList);
		if (smsList.stream().anyMatch(sms -> sms.getSmsId() <= 0)) {
			throw new OHAPIException(new OHExceptionMessage("Some Sms are not found."), HttpStatus.NOT_FOUND);
		}
		smsManager.delete(smsList);

		return true;
	}
}
