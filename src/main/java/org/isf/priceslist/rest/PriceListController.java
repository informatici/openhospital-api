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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

	@Autowired
	protected PriceListManager priceListManager;
	
	@Autowired
	protected PriceListMapper mapper;
	
	@Autowired
	protected PriceMapper priceMapper;

	private final Logger logger = LoggerFactory.getLogger(PriceListController.class);

	public PriceListController(PriceListManager priceListManager, PriceListMapper priceListmapper) {
		this.priceListManager = priceListManager;
		this.mapper = priceListmapper;
	}

	/**
	 * create a new {@link PriceList}
	 * @param priceListDTO
	 * @return <code>true</code> if the price list has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newPriceList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		logger.info("Create price list " + priceListDTO.getCode());
		boolean isCreated = priceListManager.newList(mapper.map2Model(priceListDTO));
		if (!isCreated) {
			throw new OHAPIException(new OHExceptionMessage(null, "price list is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(priceListDTO.getCode());
	}

	/**
	 * Updates the specified {@link PriceList}.
	 * @param priceListDTO
	 * @return <code>true</code> if the price list has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pricelists/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updatePriceListt(@PathVariable Integer id, @RequestBody PriceListDTO priceListDTO)
			throws OHServiceException {
		logger.info("Update pricelists code:" + priceListDTO.getCode());
		PriceList priceList = mapper.map2Model(priceListDTO);
		boolean isUpdated = priceListManager.updateList(priceList);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "price list is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(priceList.getCode());
	}

	/**
	 * get all the available {@link PriceList}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceListDTO>> getPriceLists() throws OHServiceException {
		logger.info("Get all price lists ");
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceListDTO> priceListDTOs = mapper.map2DTOList(priceLists);
		if (priceListDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(priceListDTOs);
		} else {
			return ResponseEntity.ok(priceListDTOs);
		}
	}
	
	/**
	 * get all the available {@link Price}s.
	 * @return a {@link List} of {@link PriceList} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/prices", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceDTO>> getPrices() throws OHServiceException {
		logger.info("Get all price");
		List<Price> prices = priceListManager.getPrices();
		List<PriceDTO> priceListDTOs = priceMapper.map2DTOList(prices);
		if (priceListDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(priceListDTOs);
		} else {
			return ResponseEntity.ok(priceListDTOs);
		}
	}

	/**
	 * Delete {@link PriceList} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link PriceList} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricelists/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePriceList(@PathVariable int id) throws OHServiceException {
		logger.info("Delete price list id:" + id);
		boolean isDeleted = false;
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		if (priceListFounds.size() > 0)
			isDeleted = priceListManager.deleteList(priceListFounds.get(0));
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * duplicate specified {@link PriceList}.
	 * @return <code>true</code> if the list has been duplicated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/duplicate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> copyList(@PathVariable Long id) throws OHServiceException {
		logger.info("duplicate list for price liste id : " + id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		boolean isCopied = false;
		if (priceListFounds.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
		    isCopied = priceListManager.copyList(priceListFounds.get(0));
		}
		if (!isCopied)
			throw new OHAPIException(new OHExceptionMessage(null, "price list has not been diplicated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(isCopied);
	}
	
	/**
	 * duplicate {@link list} multiplying by <code>factor</code> and rounding by <code>step</code>
	 * @return <code>true</code> if the list has been duplicated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelists/duplicate/byfactor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> copyByFactorAndStep(@PathVariable Long id, @RequestParam double factor, @RequestParam double step) throws OHServiceException {
		logger.info("duplicate list for price liste id : " + id);
		List<PriceList> priceLists = priceListManager.getLists();
		List<PriceList> priceListFounds = priceLists.stream().filter(pl -> pl.getId() == id).collect(Collectors.toList());
		boolean isCopied = false;
		if (priceListFounds.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
		    isCopied = priceListManager.copyList(priceListFounds.get(0), factor, step);
		}
		if (!isCopied)
			throw new OHAPIException(new OHExceptionMessage(null, "price list has not been diplicated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(isCopied);
	}

}
