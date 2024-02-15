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
package org.isf.pricesothers.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.pricesothers.dto.PricesOthersDTO;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.mapper.PricesOthersMapper;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/pricesothers")
@Tag(name = "Others Price")
@SecurityRequirement(name = "bearerAuth")
public class PricesOthersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PricesOthersController.class);

	@Autowired
	protected PricesOthersManager pricesOthersManager;

	@Autowired
	protected PricesOthersMapper mapper;

	public PricesOthersController(PricesOthersManager pricesOthersManager, PricesOthersMapper pricesOthersmapper) {
		this.pricesOthersManager = pricesOthersManager;
		this.mapper = pricesOthersmapper;
	}

	/**
	 * Create a new {@link PricesOthers}.
	 * @param pricesOthersDTO
	 * @return {@code true} if the prices others has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricesothers", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> newPricesOthers(@RequestBody PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		LOGGER.info("Create prices others {}", pricesOthersDTO.getCode());
		PricesOthers newPricesOthers;
		try {
			newPricesOthers = pricesOthersManager.newOther(mapper.map2Model(pricesOthersDTO));
			return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(newPricesOthers));
		} catch (OHServiceException serviceException) {
			return ResponseEntity.internalServerError().body(new OHExceptionMessage("Prices Others not created."));
		}
	}

	/**
	 * Updates the specified {@link PricesOthers}.
	 * @param pricesOthersDTO
	 * @return {@code true} if the prices others has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pricesothers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> updatePricesOthers(@PathVariable Integer id, @RequestBody PricesOthersDTO pricesOthersDTO)
					throws OHServiceException {
		LOGGER.info("Update pricesothers code: {}", pricesOthersDTO.getCode());
		PricesOthers pricesOthers = mapper.map2Model(pricesOthersDTO);
		List<PricesOthers> pricesOthersFounds = pricesOthersManager.getOthers().stream().filter(po -> po.getId() == pricesOthersDTO.getId())
						.collect(Collectors.toList());
		if (pricesOthersFounds.isEmpty()) {
			return ResponseEntity.badRequest().body("Price others not found.");
		}
		PricesOthers isUpdatedPricesOthers;
		try {
			isUpdatedPricesOthers = pricesOthersManager.updateOther(pricesOthers);
			return ResponseEntity.ok(mapper.map2DTO(isUpdatedPricesOthers));
		} catch (OHServiceException serviceException) {
			return ResponseEntity.internalServerError().body(new OHExceptionMessage("Prices Others not updated."));
		}
	}

	/**
	 * Get all the available {@link PricesOthers}s.
	 * @return a {@link List} of {@link PricesOthers} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricesothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PricesOthersDTO>> getPricesOthers() throws OHServiceException {
		LOGGER.info("Get all prices others ");
		List<PricesOthers> pricesOthers = pricesOthersManager.getOthers();
		List<PricesOthersDTO> pricesOthersDTOs = mapper.map2DTOList(pricesOthers);
		if (pricesOthersDTOs.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(pricesOthersDTOs);
		}
	}

	/**
	 * Delete {@link PricesOthers} for specified code.
	 * @param id
	 * @return {@code true} if the {@link PricesOthers} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricesothers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deletePricesOthers(@PathVariable int id) throws OHServiceException {
		LOGGER.info("Delete prices others id: {}", id);
		List<PricesOthers> pricesOthers = pricesOthersManager.getOthers();
		List<PricesOthers> pricesOthersFounds = pricesOthers.stream().filter(po -> po.getId() == id).collect(Collectors.toList());
		if (pricesOthersFounds.isEmpty()) {
			return ResponseEntity.badRequest().body("No price others found with the specified id.");
		}
		try {
			pricesOthersManager.deleteOther(pricesOthersFounds.get(0));
			return ResponseEntity.ok(true);
		} catch (OHServiceException serviceException) {
			return ResponseEntity.internalServerError().body(new OHExceptionMessage("Prices Others not deleted."));
		}
	}

}
