package org.isf.pricelist.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.pricelist.dto.PriceDTO;
import org.isf.pricelist.dto.PriceListDTO;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PriceListController extends OHApiAbstractController<PriceList, PriceListDTO> {

	@Autowired
	private PriceListManager manager;

	private final Logger logger = LoggerFactory.getLogger(PriceListController.class);

    @Autowired
    protected ModelMapper modelMapper;

    public PriceListController(PriceListManager manager, ModelMapper modelMapper) {
        super(modelMapper);
        this.manager = manager;
    }

	/**
	 * return the list of {@link List}s in the DB
	 * @return the list of {@link List}s
	 * @throws OHServiceException
	 */
	@GetMapping (value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceListDTO>> getLists() throws OHServiceException {
		logger.info(String.format("getLists"));
        return ResponseEntity.ok().body(toDTOList(manager.getLists()));
	}
	
	/**
	 * return the list of {@link Price}s in the DB
	 * @return the list of {@link Price}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricelist/price", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PriceDTO>> getPrices() throws OHServiceException {
		logger.info(String.format("getPrices"));
        return ResponseEntity.ok().body(manager.getPrices().stream().map(it -> modelMapper.map(it, PriceDTO.class)).collect(Collectors.toList()));
	}

	/**
	 * updates all {@link Price}s in the specified {@link List}
	 * @param //priceListDTO - the {@link List}
	 * @param //priceDTOList - the list of {@link Price}s
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PatchMapping(value = "/pricelist/{priceListCode}/price", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updatePrices(@PathVariable String priceListCode, @RequestBody List<PriceDTO> priceDTOList) throws OHServiceException {
		logger.info(String.format("updatePrices priceListCode [%s] priceDTOList size [%d]"), priceListCode, priceDTOList.size());
		List<Price> prices = priceDTOList.stream().map(it -> modelMapper.map(it, Price.class)).collect(Collectors.toList());
        return manager.updatePrices(getListById(priceListCode), new ArrayList<Price>(prices))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
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
		logger.info(String.format("getListById priceListCode [%s]"), priceListCode);
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
	public ResponseEntity<Boolean> newList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		logger.info(String.format("newList code [%s]"), priceListDTO.getCode());
        return manager.newList(toModel(priceListDTO))
				? ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	/**
	 * update a {@link List} in the DB
	 * 
	 * @param priceListDTO - the {@link List} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PatchMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		logger.info(String.format("updateList code [%s]"), priceListDTO.getCode());
        return manager.updateList(toModel(priceListDTO))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	/**
	 * delete a {@link List} in the DB
	 * 
	 * @param  priceListDTO - the {@link List} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteList(@RequestBody PriceListDTO priceListDTO) throws OHServiceException {
		logger.info(String.format("deleteList code [%s]"), priceListDTO.getCode());
        return manager.deleteList(toModel(priceListDTO))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
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
	public ResponseEntity<Boolean> copyList(@RequestBody PriceListDTO priceListDTO, @RequestParam(value = "factor", defaultValue = "1.") double factor, @RequestParam(value = "factor", defaultValue = "0.") double step) throws OHServiceException {
		logger.info(String.format("copyList code [%s] factor[%.,2f] step[%.,2f]"), priceListDTO.getCode(), factor, step);
        return manager.copyList(toModel(priceListDTO), factor, step)
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	//	TODO: is it useful in a web context?
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