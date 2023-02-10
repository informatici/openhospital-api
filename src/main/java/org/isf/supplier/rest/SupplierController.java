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

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SupplierController.class);

	@Autowired
	private SupplierBrowserManager manager;

	@Autowired
	private SupplierMapper mapper;
	
	/**
	 * Saves the specified {@link SupplierDTO}.
	 * @param supplierDTO
	 * @return {@code true} if the supplier was saved
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierDTO> saveSupplier(@RequestBody @Valid SupplierDTO supplierDTO) throws OHServiceException {
		LOGGER.info("Saving a new supplier...");
		Supplier isCreatedSupplier = manager.saveOrUpdate(mapper.map2Model(supplierDTO));
		if (isCreatedSupplier == null) {
			LOGGER.error("Supplier is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "Supplier is not created!", OHSeverityLevel.ERROR));
        }
		LOGGER.info("Supplier saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map2DTO(isCreatedSupplier));
	}
	
	/**
	 * Updates the specified {@link SupplierDTO}.
	 * @param supplierDTO
	 * @return {@code true} if the supplier was updated
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierDTO> updateSupplier(@RequestBody @Valid SupplierDTO supplierDTO) throws OHServiceException {
		if (supplierDTO.getSupId() == null || manager.getByID(supplierDTO.getSupId()) == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Supplier not found!", OHSeverityLevel.ERROR));
		}
		LOGGER.info("Updating supplier...");
		Supplier isUpdatedSupplier = manager.saveOrUpdate(mapper.map2Model(supplierDTO));
		if (isUpdatedSupplier == null) {
			LOGGER.error("Supplier is not updated!");
            throw new OHAPIException(new OHExceptionMessage(null, "Supplier is not updated!", OHSeverityLevel.ERROR));
        }
		LOGGER.info("Supplier updated successfully");
        return ResponseEntity.ok(mapper.map2DTO(isUpdatedSupplier));
	}
	
	/**
	 * Get the suppliers.
	 * @param excludeDeleted
	 * @return the list of suppliers found
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SupplierDTO>> getSuppliers(
			@RequestParam(name="exclude_deleted", defaultValue="true") boolean excludeDeleted) throws OHServiceException {
		LOGGER.info("Loading suppliers...");
		List<Supplier> suppliers = excludeDeleted? manager.getList() : manager.getAll();
		List<SupplierDTO> mappedSuppliers = mapper.map2DTOList(suppliers);
		if (mappedSuppliers.isEmpty()) {
			LOGGER.info("No supplier found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedSuppliers);
		} else {
			LOGGER.info("Found {} suppliers", mappedSuppliers.size());
			return ResponseEntity.ok(mappedSuppliers);
		}
	}
	
	/**
	 * Get a supplier by its ID.
	 * @param id
	 * @return the found supplier
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/suppliers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierDTO> getSuppliers(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Loading supplier with ID {}", id);
		Supplier supplier = manager.getByID(id);
		if (supplier == null) {
			LOGGER.info("Supplier not found");
			throw new OHAPIException(new OHExceptionMessage(null, "Supplier not found!", OHSeverityLevel.ERROR));
		} else {
			LOGGER.info("Found supplier!");
			return ResponseEntity.ok(mapper.map2DTO(supplier));
		}
	}
}
