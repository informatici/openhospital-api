package org.isf.priceothers.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.priceothers.dto.PricesOthersDTO;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	private final Logger logger = LoggerFactory.getLogger(PricesOthersController.class);

	/**
	 * return the list of {@link PricesOthersDTO}s in the DB
	 * 
	 * @return the list of {@link PricesOthersDTO}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PricesOthersDTO>> getOthers() throws OHServiceException {
		logger.info(String.format("getOthers"));
        return ResponseEntity.ok().body(toDTOList(manager.getOthers()));
	}

	/**
	 * insert a new {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to insert
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		logger.info(String.format("newOther [%s]", pricesOthersDTO.getCode()));
		// TODO: to better follow REST conventions we need an URI to use as Location header value on created. Check: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
        return manager.newOther(toModel(pricesOthersDTO))
				? ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	/**
	 * delete a {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/priceothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		logger.info(String.format("deleteOther [%s]", pricesOthersDTO.getCode()));
        return manager.deleteOther(toModel(pricesOthersDTO))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
	}

	/**
	 * update a {@link PricesOthersDTO} in the DB
	 * 
	 * @param pricesOthersDTO - the {@link PricesOthersDTO} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public ResponseEntity<Boolean> updateOther(PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		logger.info(String.format("updateOther [%s]", pricesOthersDTO.getCode()));
        return manager.updateOther(toModel(pricesOthersDTO))
				? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
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