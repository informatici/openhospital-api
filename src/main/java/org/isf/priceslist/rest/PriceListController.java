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
package org.isf.priceslist.rest;

import java.util.List;

import org.isf.priceslist.dto.PriceDTO;
import org.isf.priceslist.dto.PriceListDTO;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.mapper.PriceListMapper;
import org.isf.priceslist.mapper.PriceMapper;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Price Lists")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PriceListController.class);

	private final PriceListManager priceListManager;

	private final PriceListMapper mapper;

	private final PriceMapper priceMapper;

	public PriceListController(PriceListManager priceListManager, PriceListMapper priceListmapper, PriceMapper priceMapper) {
		this.priceListManager = priceListManager;
		this.mapper = priceListmapper;
		this.priceMapper = priceMapper;
	}

	/**
	 * Create a new {@link PriceList}.
	 * @param priceListDTO PriceList payload
	 * @return the new {@link PriceList}.
	 * @throws OHServiceException When failed to create price list
	 */
	@PostMapping("/pricelists")
	@ResponseStatus(HttpStatus.CREATED)
	public PriceListDTO newPriceList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		LOGGER.info("Create price list {}.", priceListDTO.getCode());
		try {
			return mapper.map2DTO(priceListManager.newList(mapper.map2Model(priceListDTO)));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Price list not created."));
		}
	}

	/**
	 * Updates the specified {@link PriceList}.
	 * @param priceListDTO Price List payload
	 * @return the updated {@link PriceList}.
	 * @throws OHServiceException When failed to update the price list
	 */
	@PutMapping("/pricelists/{id}")
	public PriceListDTO updatePriceLists(
		@PathVariable Integer id, @RequestBody PriceListDTO priceListDTO
	) throws OHServiceException {
		LOGGER.info("Update price list code: {}.", priceListDTO.getCode());
		PriceList priceList = mapper.map2Model(priceListDTO);
		try {
			return mapper.map2DTO(priceListManager.updateList(priceList));
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Price list not updated."));
		}
	}

	/**
	 * Get all the available {@link PriceList}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException WHen failed to price lists
	 */
	@GetMapping("/pricelists")
	public List<PriceListDTO> getPriceLists() throws OHServiceException {
		LOGGER.info("Get all price lists.");

		return mapper.map2DTOList(priceListManager.getLists());
	}

	/**
	 * Get all the available {@link Price}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException When failed to get prices
	 */
	@GetMapping("/pricelists/prices")
	public List<PriceDTO> getPrices() throws OHServiceException {
		LOGGER.info("Get all prices.");

		return priceMapper.map2DTOList(priceListManager.getPrices());
	}

	/**
	 * Delete {@link PriceList} for specified code.
	 * @param id Price list ID
	 * @return {@code true} if the {@link PriceList} has been deleted.
	 * @throws OHServiceException When failed to delete price list
	 */
	@DeleteMapping("/pricelists/{id}")
	public boolean deletePriceList(@PathVariable int id) throws OHServiceException {
		LOGGER.info("Delete price list id: {}.", id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).toList();
		if (priceListFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Price list not found."), HttpStatus.NOT_FOUND);
		}
		try {
			priceListManager.deleteList(priceListFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Price list not deleted."));
		}
	}

	/**
	 * Duplicate specified {@link PriceList}.
	 * @return {@code true} if the list has been duplicated.
	 * @throws OHServiceException When failed to duplicate the price list
	 */
	@GetMapping("/pricelists/duplicate/{id}")
	public boolean copyList(@PathVariable Long id) throws OHServiceException {
		LOGGER.info("Duplicate list for price list id: {}.", id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).toList();
		if (priceListFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Price list not found."), HttpStatus.NOT_FOUND);
		}

		try {
			priceListManager.copyList(priceListFounds.get(0));
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Price list not duplicated."));
		}
	}

	/**
	 * Duplicate {@link PriceList} multiplying by {@code factor} and rounding by {@code step}.
	 * @return {@code true} if the list has been duplicated.
	 * @throws OHServiceException When failed to duplicate the price list
	 */
	@GetMapping("/pricelists/duplicate/byfactor/{id}")
	public boolean copyByFactorAndStep(
		@PathVariable Long id, @RequestParam double factor, @RequestParam double step
	) throws OHServiceException {
		LOGGER.info("Duplicate list for price list id: {}.", id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).toList();
		if (priceListFounds.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("Price list not found."), HttpStatus.NOT_FOUND);
		}

		try {
			priceListManager.copyList(priceListFounds.get(0), factor, step);
			return true;
		} catch (OHServiceException serviceException) {
			throw new OHAPIException(new OHExceptionMessage("Price list not duplicated."));
		}
	}
}
