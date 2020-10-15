package org.isf.supplier.rest;

import java.util.List;

import javax.validation.Valid;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.supplier.dto.SupplierDTO;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.mapper.SupplierMapper;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class SupplierController {
	private final Logger logger = LoggerFactory.getLogger(SupplierController.class);
	@Autowired
	private SupplierBrowserManager manager;
	@Autowired
	private SupplierMapper mapper;
	
	/**
	 * Saves the specified {@link SupplierDTO}
	 * @param suplierDTO
	 * @return <code>true</code> if the supplier was saved
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> saveSupplier(@RequestBody @Valid SupplierDTO suplierDTO) throws OHServiceException {
		logger.info("Saving a new supplier...");
		boolean isCreated = manager.saveOrUpdate(mapper.map2Model(suplierDTO));
		if (!isCreated) {
			logger.error("Supplier is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "Supplier is not created!", OHSeverityLevel.ERROR));
        }
		logger.info("Supplier saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(isCreated);
	}
	
	/**
	 * Updates the specified {@link SupplierDTO}
	 * @param suplierDTO
	 * @return <code>true</code> if the supplier was updated
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateSupplier(@RequestBody @Valid SupplierDTO suplierDTO) throws OHServiceException {
		if(suplierDTO.getSupId() == null || manager.getByID(suplierDTO.getSupId()) == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Supplier not found!", OHSeverityLevel.ERROR));
		}
		logger.info("Updating supplier...");
		boolean isUpdated = manager.saveOrUpdate(mapper.map2Model(suplierDTO));
		if (!isUpdated) {
			logger.error("Supplier is not updated!");
            throw new OHAPIException(new OHExceptionMessage(null, "Supplier is not updated!", OHSeverityLevel.ERROR));
        }
		logger.info("Supplier updated successfully");
        return ResponseEntity.ok(isUpdated);
	}
	
	/**
	 * Loads the stored suppliers
	 * @param excludeDeleted
	 * @return the list of suppliers found
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SupplierDTO>> getSuppliers(
			@RequestParam(name="exclude_deleted", defaultValue="true") boolean excludeDeleted) throws OHServiceException {
		logger.info("Loading suppliers...");
		List<Supplier> suppliers = excludeDeleted? manager.getList() : manager.getAll();
		List<SupplierDTO> mappedSuppliers = mapper.map2DTOList(suppliers);
		if(mappedSuppliers.isEmpty()) {
			logger.info("No supplier found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedSuppliers);
		} else {
			logger.info("Found " + mappedSuppliers.size() + " suppliers");
			return ResponseEntity.ok(mappedSuppliers);
		}
	}
	
	/**
	 * Load a supplier by its ID
	 * @param id
	 * @return the found supplier
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/suppliers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierDTO> getSuppliers(@PathVariable Integer id) throws OHServiceException {
		logger.info("Loading supplier with ID " + id);
		Supplier supplier = manager.getByID(id);
		if(supplier == null) {
			logger.info("Supplier not found");
			throw new OHAPIException(new OHExceptionMessage(null, "Supplier not found!", OHSeverityLevel.ERROR));
		} else {
			logger.info("Found supplier!");
			return ResponseEntity.ok(mapper.map2DTO(supplier));
		}
	}
}
