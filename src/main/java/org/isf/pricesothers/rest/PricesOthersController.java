package org.isf.pricesothers.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.pricesothers.dto.PricesOthersDTO;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.mapper.PricesOthersMapper;
import org.isf.pricesothers.model.PricesOthers;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/pricesothers", produces = MediaType.APPLICATION_JSON_VALUE)
public class PricesOthersController {

	@Autowired
	protected PricesOthersManager pricesOthersManager;
	
	@Autowired
	protected PricesOthersMapper mapper;

	private final Logger logger = LoggerFactory.getLogger(PricesOthersController.class);

	public PricesOthersController(PricesOthersManager pricesOthersManager, PricesOthersMapper pricesOthersmapper) {
		this.pricesOthersManager = pricesOthersManager;
		this.mapper = pricesOthersmapper;
	}

	/**
	 * create a new {@link PricesOthers}
	 * @param pricesOthersDTO
	 * @return <code>true</code> if the prices others has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/pricesothers", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newPricesOthers(@RequestBody PricesOthersDTO pricesOthersDTO) throws OHServiceException {
		logger.info("Create prices others " + pricesOthersDTO.getCode());
		boolean isCreated = pricesOthersManager.newOther(mapper.map2Model(pricesOthersDTO));
		if (!isCreated) {
			throw new OHAPIException(new OHExceptionMessage(null, "prices others is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(pricesOthersDTO.getCode());
	}

	/**
	 * Updates the specified {@link PricesOthers}.
	 * @param pricesOthersDTO
	 * @return <code>true</code> if the prices others has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/pricesothers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updatePricesOtherst(@PathVariable Integer id, @RequestBody PricesOthersDTO pricesOthersDTO)
			throws OHServiceException {
		logger.info("Update pricesothers code:" + pricesOthersDTO.getCode());
		PricesOthers pricesOthers = mapper.map2Model(pricesOthersDTO);
		List<PricesOthers> pricesOthersFounds = pricesOthersManager.getOthers().stream().filter(po -> po.getId() == pricesOthersDTO.getId()).collect(Collectors.toList());
		if (pricesOthersFounds.size() == 0)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		boolean isUpdated = pricesOthersManager.updateOther(pricesOthers);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "prices others is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(pricesOthers.getCode());
	}

	/**
	 * get all the available {@link PricesOthers}s.
	 * @return a {@link List} of {@link PricesOthers} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/pricesothers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PricesOthersDTO>> getPricesOtherss() throws OHServiceException {
		logger.info("Get all prices others ");
		List<PricesOthers> pricesOthers = pricesOthersManager.getOthers();
		List<PricesOthersDTO> pricesOthersDTOs = mapper.map2DTOList(pricesOthers);
		if (pricesOthersDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(pricesOthersDTOs);
		} else {
			return ResponseEntity.ok(pricesOthersDTOs);
		}
	}
	
	
	/**
	 * Delete {@link PricesOthers} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link PricesOthers} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/pricesothers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePricesOthers(@PathVariable int id) throws OHServiceException {
		logger.info("Delete prices others id:" + id);
		boolean isDeleted = false;
		List<PricesOthers> pricesOtherss = pricesOthersManager.getOthers();
		List<PricesOthers> pricesOthersFounds = pricesOtherss.stream().filter(po -> po.getId() == id).collect(Collectors.toList());
		if (pricesOthersFounds.size() > 0)
			isDeleted = pricesOthersManager.deleteOther(pricesOthersFounds.get(0));
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}

}
