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
package org.isf.pricesothers.rest;

import java.util.List;

import org.isf.pricesothers.dto.PricesOthersDTO;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.mapper.PricesOthersMapper;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.shared.exceptions.OHAPIException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Others Price")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PricesOthersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PricesOthersController.class);

	private final PricesOthersManager pricesOthersManager;

	private final PricesOthersMapper mapper;

	public PricesOthersController(PricesOthersManager pricesOthersManager, PricesOthersMapper pricesOthersmapper) {
		this.pricesOthersManager = pricesOthersManager;
		this.mapper = pricesOthersmapper;
	}

	/**
	 * Create a new {@link PricesOthers}.
	 * @param pricesOthersDTO PriceOther payload
	 * @return {@code true} if the prices others has been stored, {@code false} otherwise.
	 * @throws OHServiceException When failed to create price other
	 */
	@PostMapping("/pricesothers")
	@ResponseStatus(HttpStatus.CREATED)
	public PricesOthersDTO newPricesOthers(@RequestBody PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		LOGGER.info("Create prices others {}", pricesOthersDTO.getCode());
		try {
			return mapper.map2DTO(pricesOthersManager.newOther(mapper.map2Model(pricesOthersDTO)));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Prices Others not created."));
		}
	}

	/**
	 * Updates the specified {@link PricesOthers}.
	 * @param pricesOthersDTO PriceOther payload
	 * @return {@code true} if the prices others has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update price other
	 */
	@PutMapping("/pricesothers/{id}")
	public PricesOthersDTO updatePricesOthers(
		@PathVariable Integer id, @RequestBody PricesOthersDTO pricesOthersDTO
	) throws OHServiceException {
		LOGGER.info("Update prices other code: {}", pricesOthersDTO.getCode());

		PricesOthers pricesOthers = mapper.map2Model(pricesOthersDTO);
		List<PricesOthers> pricesOthersFounds = pricesOthersManager.getOthers()
			.stream()
			.filter(po -> po.getId() == pricesOthersDTO.getId())
			.toList();

		if (pricesOthersFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Other price not found."), HttpStatus.NOT_FOUND);
		}

		try {
			return mapper.map2DTO(pricesOthersManager.updateOther(pricesOthers));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Prices Others not updated."));
		}
	}

	/**
	 * Get all the available {@link PricesOthers}s.
	 * @return a {@link List} of {@link PricesOthers} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get other prices
	 */
	@GetMapping("/pricesothers")
	public List<PricesOthersDTO> getPricesOthers() throws OHServiceException {
		LOGGER.info("Get all prices others ");

		return mapper.map2DTOList(pricesOthersManager.getOthers());
	}

	/**
	 * Delete {@link PricesOthers} for specified code.
	 * @param id Other price ID
	 * @return {@code true} if the {@link PricesOthers} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException When failed to delete the other price
	 */
	@DeleteMapping("/pricesothers/{id}")
	public boolean deletePricesOthers(@PathVariable int id) throws OHServiceException {
		LOGGER.info("Delete prices others id: {}", id);

		List<PricesOthers> pricesOthers = pricesOthersManager.getOthers();
		List<PricesOthers> pricesOthersFounds = pricesOthers.stream().filter(po -> po.getId() == id).toList();

		if (pricesOthersFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Other price not deleted."), HttpStatus.NOT_FOUND);
		}

		try {
			pricesOthersManager.deleteOther(pricesOthersFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Prices Others not deleted."));
		}
	}
}
