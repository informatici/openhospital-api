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
package org.isf.therapy.rest;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medical.mapper.MedicalMapper;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.therapy.dto.TherapyDTO;
import org.isf.therapy.dto.TherapyRowDTO;
import org.isf.therapy.manager.TherapyManager;
import org.isf.therapy.mapper.TherapyMapper;
import org.isf.therapy.mapper.TherapyRowMapper;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Therapies")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TherapyController {

	private final TherapyManager manager;

	private final TherapyMapper therapyMapper;

	private final TherapyRowMapper therapyRowMapper;

	private final MedicalMapper medicalMapper;

	public TherapyController(
		TherapyManager manager,
		TherapyMapper therapyMapper,
		TherapyRowMapper therapyRowMapper,
		MedicalMapper medicalMapper
	) {
		this.manager = manager;
		this.therapyMapper = therapyMapper;
		this.therapyRowMapper = therapyRowMapper;
		this.medicalMapper = medicalMapper;
	}

	/**
	 * Creates a new therapy for related Patient.
	 * @param thRowDTO - the therapy
	 * @return the created therapy
	 * @throws OHServiceException When failed to create therapy
	 */
	@PostMapping("/therapies")
	@ResponseStatus(HttpStatus.CREATED)
	public TherapyRowDTO newTherapy(@RequestBody TherapyRowDTO thRowDTO) throws OHServiceException {
		if (thRowDTO.getPatID() == null) {
			throw new OHAPIException(new OHExceptionMessage("Patient not found."), HttpStatus.NOT_FOUND);
		}

		return therapyRowMapper.map2DTO(manager.newTherapy(therapyRowMapper.map2Model(thRowDTO)));
	}

	/**
	 * Replaces all therapies for related Patient.
	 * @param thRowDTOs - the list of therapies
	 * @return {@code true} if the rows has been inserted, {@code false} otherwise
	 * @throws OHServiceException When failed to replace patient therapies
	 */
	@PostMapping("/therapies/replace")
	@ResponseStatus(HttpStatus.CREATED)
	public TherapyRowDTO replaceTherapies(
		@RequestBody @Valid List<TherapyRowDTO> thRowDTOs
	) throws OHServiceException {
		ArrayList<TherapyRow> therapies = (ArrayList<TherapyRow>)therapyRowMapper.map2ModelList(thRowDTOs);

		return therapyRowMapper.map2DTO(manager.newTherapy(therapies.get(0)));
	}

	/**
	 * Deletes all therapies for specified Patient Code.
	 * @param code - the Patient Code
	 * @return {@code true} if the therapies have been deleted, throws an exception otherwise
	 * @throws OHServiceException When failed to delete patient therapies
	 */
	@DeleteMapping("/therapies/{code_patient}")
	public boolean deleteAllTherapies(@PathVariable("code_patient") Integer code) throws OHServiceException {
		try {
			manager.deleteAllTherapies(code);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Therapies not deleted."));
		}
	}

	/**
	 * Gets the medicals that are not available for the specified list of therapies.
	 * @param therapyDTOs - the list of therapies
	 * @return the list of medicals out of stock
	 * @throws OHServiceException When failed to get not available medicals
	 */
	@PostMapping("/therapies/meds-out-of-stock")
	public List<MedicalDTO> getMedicalsOutOfStock(
		@RequestBody List<TherapyDTO> therapyDTOs
	) throws OHServiceException {
		return medicalMapper.map2DTOList(manager.getMedicalsOutOfStock(therapyMapper.map2ModelList(therapyDTOs)));
	}

	/**
	 * Gets the list of therapies for specified Patient ID.
	 * @param patientID - the Patient ID
	 * @return the list of therapies of the patient or all the therapies if {@code 0} is passed
	 * @throws OHServiceException When failed to get patient therapies
	 */
	@GetMapping("/therapies/{code_patient}")
	public List<TherapyRowDTO> getTherapyRows
	(@PathVariable("code_patient") Integer patientID
	) throws OHServiceException {
		return therapyRowMapper.map2DTOList(manager.getTherapyRows(patientID));
	}

	/**
	 * Gets a list of therapies from a list of therapyRows (DB records).
	 * @param thRowDTOs - the list of therapyRows
	 * @return the list of therapies
	 * @throws OHServiceException When failed to get therapies
	 */
	@PostMapping("/therapies/from-rows")
	public List<TherapyDTO> getTherapies(
		@RequestBody @Valid List<TherapyRowDTO> thRowDTOs
	) throws OHServiceException {
		return therapyMapper.map2DTOList(manager.getTherapies(therapyRowMapper.map2ModelList(thRowDTOs)));
	}

	/**
	 * Gets therapy from a therapyRow (DB record).
	 * @param thRowDTO - the therapyRow
	 * @return the therapy
	 * @throws OHServiceException When failed to get therapy
	 */
	@PostMapping("/therapies/from-row")
	public TherapyDTO getTherapy(@RequestBody @Valid TherapyRowDTO thRowDTO) throws OHServiceException {
		return therapyMapper.map2DTO(manager.createTherapy(therapyRowMapper.map2Model(thRowDTO)));
	}
}
