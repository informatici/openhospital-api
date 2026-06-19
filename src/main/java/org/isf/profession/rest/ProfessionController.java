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
package org.isf.profession.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.profession.dto.ProfessionDTO;
import org.isf.profession.manager.ProfessionBrowserManager;
import org.isf.profession.mapper.ProfessionMapper;
import org.isf.profession.model.Profession;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@Tag(name = "Professions")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfessionController {

	private final ProfessionBrowserManager professionManager;

	private final ProfessionMapper mapper;

	public ProfessionController(ProfessionBrowserManager professionManager, ProfessionMapper professionMapper) {
		this.professionManager = professionManager;
		this.mapper = professionMapper;
	}

	/**
	 * Returns all the stored {@link Profession}s.
	 * @return a list of professions.
	 * @throws OHServiceException When failed to get professions
	 */
	@GetMapping(value = "/professions")
	public List<ProfessionDTO> getProfessions() throws OHServiceException {
		return mapper.map2DTOList(professionManager.getProfessions());
	}

	/**
	 * Create a new {@link Profession}.
	 * @param professionDTO Profession payload
	 * @return the profession created
	 * @throws OHServiceException - in case of duplicated code or in case of error
	 */
	@PostMapping(value = "/professions")
	@ResponseStatus(HttpStatus.CREATED)
	public ProfessionDTO newProfession(
		@Valid @RequestBody ProfessionDTO professionDTO
	) throws OHServiceException {
		Profession profession = mapper.map2Model(professionDTO);
		if (professionManager.isCodePresent(profession.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Specified Profession code is already used."));
		}

		try {
			return mapper.map2DTO(professionManager.newProfession(profession));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Failed to create profession."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the specified {@link Profession}.
	 * @param professionDTO - the profession to update.
	 * @return the updated profession
	 * @throws OHServiceException When failed to update profession
	 */
	@PutMapping(value = "/professions")
	public ProfessionDTO updateProfession(@Valid @RequestBody ProfessionDTO professionDTO) throws OHServiceException {
		Profession profession = mapper.map2Model(professionDTO);
		if (!professionManager.isCodePresent(profession.getCode())) {
			throw new OHAPIException(new OHExceptionMessage("Profession not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return mapper.map2DTO(professionManager.updateProfession(profession));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Profession not updated."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes the specified {@link Profession}.
	 * @param code - the code of the profession to remove.
	 * @return {@code true} if the profession has been removed, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete profession
	 */
	@DeleteMapping(value = "/professions/{code}")
	public boolean deleteProfession(@PathVariable String code) throws OHServiceException {
		Profession profession = professionManager.getProfession(code);
		if (profession == null) {
			throw new OHAPIException(new OHExceptionMessage("No Profession found with the given code."), HttpStatus.NOT_FOUND);
		}

		try {
			professionManager.deleteProfession(profession);
			return true;
		} catch (OHServiceException e) {
			return false;
		}
	}
}
