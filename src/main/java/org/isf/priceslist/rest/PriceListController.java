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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.priceslist.rest;

import java.util.List;
import java.util.stream.Collectors;

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
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PriceListController.class);

	@Autowired
	protected PriceListManager priceListManager;
	
	@Autowired
	protected PriceListMapper mapper;
	
	@Autowired
	protected PriceMapper priceMapper;

	public PriceListController(PriceListManager priceListManager, PriceListMapper priceListmapper) {
		this.priceListManager = priceListManager;
		this.mapper = priceListmapper;
	}

	/**
	 * Create a new {@link PriceList}.
	 * @param priceListDTO
	 * @return {@code true} if the price list has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PriceListDTO> newPriceList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		LOGGER.info("Create price list {}", priceListDTO.getCode());
		PriceList isCreatedPriceList = priceListManager.newList(mapper.map2Model(priceListDTO));
		if (isCreatedPriceList == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "price list is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedPriceList));
	}

	/**
	 * Updates the specified {@link PriceList}.
	 * @param priceListDTO
	 * @return {@code true} if the price list has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pricelists/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PriceListDTO> updatePriceListt(@PathVariable Integer id, @RequestBody PriceListDTO priceListDTO)
			throws OHServiceException {
		LOGGER.info("Update pricelists code: {}", priceListDTO.getCode());
		PriceList priceList = mapper.map2Model(priceListDTO);
		PriceList isUpdatedPriceList = priceListManager.updateList(priceList);
		if (isUpdatedPriceList == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "price list is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(mapper.map2DTO(isUpdatedPriceList));
	}

	/**
	 * Get all the available {@link PriceList}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceListDTO>> getPriceLists() throws OHServiceException {
		LOGGER.info("Get all price lists ");
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceListDTO> priceListDTOs = mapper.map2DTOList(priceLists);
		if (priceListDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(priceListDTOs);
		} else {
			return ResponseEntity.ok(priceListDTOs);
		}
	}
	
	/**
	 * Get all the available {@link Price}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/prices", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceDTO>> getPrices() throws OHServiceException {
		LOGGER.info("Get all price");
		List<Price> prices = priceListManager.getPrices();
		List<PriceDTO> priceListDTOs = priceMapper.map2DTOList(prices);
		if (priceListDTOs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(priceListDTOs);
		} else {
			return ResponseEntity.ok(priceListDTOs);
		}
	}

	/**
	 * Delete {@link PriceList} for specified code.
	 * @param id
	 * @return {@code true} if the {@link PriceList} has been deleted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricelists/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePriceList(@PathVariable int id) throws OHServiceException {
		LOGGER.info("Delete price list id: {}", id);
		boolean isDeleted;
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		if (!priceListFounds.isEmpty()) {
			isDeleted = priceListManager.deleteList(priceListFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * Duplicate specified {@link PriceList}.
	 * @return {@code true} if the list has been duplicated, {@code false} otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/duplicate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> copyList(@PathVariable Long id) throws OHServiceException {
		LOGGER.info("duplicate list for price list id : {}", id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		boolean isCopied;
		if (priceListFounds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		isCopied = priceListManager.copyList(priceListFounds.get(0));
		if (!isCopied) {
			throw new OHAPIException(new OHExceptionMessage(null, "price list has not been duplicated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isCopied);
	}
	
	/**
	 * Duplicate {@link PriceList} multiplying by {@code factor} and rounding by {@code step}.
	 * @return {@code true} if the list has been duplicated, {@code false} otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/duplicate/byfactor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> copyByFactorAndStep(@PathVariable Long id, @RequestParam double factor, @RequestParam double step) throws OHServiceException {
		LOGGER.info("duplicate list for price list id : {}", id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		boolean isCopied;
		if (priceListFounds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		isCopied = priceListManager.copyList(priceListFounds.get(0), factor, step);
		if (!isCopied) {
			throw new OHAPIException(new OHExceptionMessage(null, "price list has not been duplicated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isCopied);
	}

}
