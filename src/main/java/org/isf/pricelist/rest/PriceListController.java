package org.isf.pricelist.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.pricelist.dto.PriceDTO;
import org.isf.pricelist.dto.PriceListDTO;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PriceListController extends OHApiAbstractController<PriceList, PriceListDTO> {

	@Autowired
	private PriceListManager manager;
	
	/**
	 * return the list of {@link List}s in the DB
	 * @return the list of {@link List}s
	 * @throws OHServiceException
	 */
	@GetMapping (value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PriceListDTO> getLists() throws OHServiceException {
        return toDTOList(manager.getLists());
	}
	
	/**
	 * return the list of {@link Price}s in the DB
	 * @return the list of {@link Price}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelist/price", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PriceDTO> getPrices() throws OHServiceException {
        return manager.getPrices().stream().map(it -> modelMapper.map(it, PriceDTO.class)).collect(Collectors.toList());
	}

	/**
	 * updates all {@link Price}s in the specified {@link List}
	 * @param //priceListDTO - the {@link List}
	 * @param //priceDTOList - the list of {@link Price}s
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PatchMapping(value = "/pricelist/{priceListCode}/price", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean updatePrices(@PathVariable String priceListCode, @RequestBody List<PriceDTO> priceDTOList) throws OHServiceException {
		List<Price> prices = priceDTOList.stream().map(it -> modelMapper.map(it, Price.class)).collect(Collectors.toList());
        return manager.updatePrices(getListById(priceListCode), new ArrayList<Price>(prices));
	}


	/**
	 * Get a {@link PriceList} for the code provided
	 * TODO: move this logic to the manager in the core library
	 *
	 * @param priceListCode - the code to identify the {@link PriceList}
	 * @return the {@link PriceList}
	 * @throws OHServiceException
	 */
	private PriceList getListById(String priceListCode) throws OHServiceException {
		ArrayList<PriceList> priceLists = manager.getLists();
		for (Iterator<PriceList> priceListIterator = priceLists.iterator(); priceListIterator.hasNext(); ) {
			PriceList priceList = priceListIterator.next();
			if (priceList.getCode().equals(priceListCode)) {
				return priceList;
			}
		}
		throw new OHServiceException(new OHExceptionMessage(")\"Price list not found\"", ")\"Price list not found for the code provided\"", OHSeverityLevel.ERROR));
	}

	/**
	 * insert a new {@link List} in the DB
	 * 
	 * @param priceListDTO - the {@link List}
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean newList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
        return manager.newList(toModel(priceListDTO));
	}

	/**
	 * update a {@link List} in the DB
	 * 
	 * @param priceListDTO - the {@link List} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PatchMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean updateList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
        return manager.updateList(toModel(priceListDTO));
	}

	/**
	 * delete a {@link List} in the DB
	 * 
	 * @param  priceListDTO - the {@link List} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean deleteList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
        return manager.deleteList(toModel(priceListDTO));
	}
	
	/**
	 * duplicate {@link //list} multiplying by <code>factor</code> and rounding by <code>step</code>
	 * 
	 * @param priceListDTO - the {@link //list} to be duplicated
	 * @param factor - the multiplying factor
	 * @param step - the rounding step
	 * @return <code>true</code> if the list has been duplicated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricelist/copy/{factor}/{step}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean copyList(@RequestBody PriceListDTO priceListDTO, @RequestParam(value = "factor", defaultValue = "1.") double factor, @RequestParam(value = "factor", defaultValue = "0.") double step) throws OHServiceException {
        return manager.copyList(toModel(priceListDTO), factor, step);
	}

//	TODO: is it useful in a web context
//	@PostMapping(value = "/pricelist/convert")
//	public List<PriceForPrint> convertPrice(@RequestBody Map<PriceListDTO, List<PriceDTO>> pricesJson) throws OHServiceException {
//		return null;
//	}

	@Override
	protected Class<PriceListDTO> getDTOClass() {
		return PriceListDTO.class;
	}

	@Override
	protected Class<PriceList> getModelClass() {
		return PriceList.class;
	}
}