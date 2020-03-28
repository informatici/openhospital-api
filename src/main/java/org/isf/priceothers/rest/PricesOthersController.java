package org.isf.priceothers.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.priceothers.dto.PricesOthersDTO;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PricesOthersController extends OHApiAbstractController<PricesOthers, PricesOthersDTO> {

	@Autowired
	private PricesOthersManager manager;

	/**
	 * return the list of {@link PricesOthersDTO}s in the DB
	 * 
	 * @return the list of {@link PricesOthersDTO}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PricesOthersDTO> getOthers() throws OHServiceException {
        return toDTOList(manager.getOthers());
	}

	/**
	 * insert a new {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to insert
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean newOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
        return manager.newOther(toModel(pricesOthersDTO));
	}

	/**
	 * delete a {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean deleteOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
        return manager.deleteOther(toModel(pricesOthersDTO));
	}

	/**
	 * update a {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean updateOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
        return manager.updateOther(toModel(pricesOthersDTO));
	}

	@Override
	protected Class<PricesOthersDTO> getDTOClass() {
		return PricesOthersDTO.class;
	}

	@Override
	protected Class<PricesOthers> getModelClass() {
		return PricesOthers.class;
	}
}