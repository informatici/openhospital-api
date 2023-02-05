/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.sms.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.sms.dto.SmsDTO;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.mapper.SmsMapper;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
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
import io.swagger.annotations.Authorization;

@RestController
@Api(value="/sms",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class SmsController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SmsController.class);

	@Autowired
	private SmsManager smsManager;

	@Autowired
	private SmsMapper smsMapper;
	
	/**
	 * Fetch the list of {@link Sms}s.
	 * @param dateFrom
	 * @param dateTo
	 * @return the found list
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/sms", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SmsDTO>> getAll(
			@RequestParam(required = true) String dateFrom,
			@RequestParam(required = true) String dateTo) throws OHServiceException {
		LOGGER.info("Fetching the list of sms");
		LocalDateTime from = LocalDate.parse(dateFrom).atStartOfDay();
		LocalDateTime to = LocalDate.parse(dateTo).atStartOfDay();
		List<Sms> smsList = smsManager.getAll(from, to);
		List<SmsDTO> mappedSmsList = smsMapper.map2DTOList(smsList);
		if (mappedSmsList.isEmpty()) {
			LOGGER.info("No sms found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedSmsList);
		} else {
			LOGGER.info("Found {} sms", mappedSmsList.size());
			return ResponseEntity.ok(mappedSmsList);
		}
	}
	
	/**
	 * Save the specified {@link Sms}.
	 * @param smsDTO
	 * @return {@code true} if the sms is saved
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/sms", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> saveSms(
			@RequestBody @Valid SmsDTO smsDTO,
			@RequestParam(defaultValue="false") boolean split) throws OHServiceException {
		smsManager.saveOrUpdate(smsMapper.map2Model(smsDTO), split);
		return ResponseEntity.ok(true);
	}
	
	/**
	 * Deletes the specified {@link Sms}.
	 * @param smsDTOList
	 * @return {@code true} if the sms is deleted
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/sms/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteSms(@RequestBody @Valid List<SmsDTO> smsDTOList) throws OHServiceException {
		List<Sms> smsList = smsMapper.map2ModelList(smsDTOList);
		if (smsList.stream().anyMatch(sms -> sms.getSmsId() <= 0)) {
			throw new OHAPIException(new OHExceptionMessage(null, "Some Sms are not found!", OHSeverityLevel.ERROR));
		}
		smsManager.delete(smsList);
		return ResponseEntity.ok(true);
	}
}
