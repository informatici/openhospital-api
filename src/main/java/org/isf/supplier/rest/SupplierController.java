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
package org.isf.supplier.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.supplier.dto.SupplierDTO;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.mapper.SupplierMapper;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController(value = "/suppliers")
@Tag(name = "Suppliers")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierController.class);

	@Autowired
	private SupplierBrowserManager manager;

	@Autowired
	private SupplierMapper mapper;

	/**
	 * Saves the specified {@link SupplierDTO}.
	 * @param supplierDTO The payload
	 * @return {@code true} if the supplier was saved
	 * @throws OHServiceException When failed to save the supplier
	 */
	@PostMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public SupplierDTO saveSupplier(@RequestBody @Valid SupplierDTO supplierDTO) throws OHServiceException {
		LOGGER.info("Saving a new supplier...");
		try {
			Supplier newSupplier = manager.saveOrUpdate(mapper.map2Model(supplierDTO));
			LOGGER.info("Supplier saved successfully.");
			return mapper.map2DTO(newSupplier);
		} catch (OHServiceException serviceException) {
			LOGGER.error("Supplier is not created.");
			throw new OHAPIException(new OHExceptionMessage("Supplier not created."));
		}
	}

	/**
	 * Updates the specified {@link SupplierDTO}.
	 * @param supplierDTO The payload
	 * @return {@code true} if the supplier was updated
	 * @throws OHServiceException When failed to update the supplier
	 */
	@PutMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public SupplierDTO updateSupplier(@RequestBody @Valid SupplierDTO supplierDTO) throws OHServiceException {
		if (supplierDTO.getSupId() == null || manager.getByID(supplierDTO.getSupId()) == null) {
			throw new OHAPIException(new OHExceptionMessage("Supplier not found."), HttpStatus.NOT_FOUND);
		}
		LOGGER.info("Updating supplier...");
		try {
			Supplier updatedSupplier = manager.saveOrUpdate(mapper.map2Model(supplierDTO));
			LOGGER.info("Supplier updated successfully.");
			return mapper.map2DTO(updatedSupplier);
		} catch (OHServiceException serviceException) {
			LOGGER.error("Supplier is not updated.");
			throw new OHAPIException(new OHExceptionMessage("Supplier not updated."));
		}
	}

	/**
	 * Get the suppliers.
	 * @param excludeDeleted Whether to exclude deleted suppliers or not
	 * @return the list of suppliers found
	 * @throws OHServiceException When failed to retrieve suppliers
	 */
	@GetMapping(value = "/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SupplierDTO>> getSuppliers(
					@RequestParam(name="exclude_deleted", defaultValue="true") boolean excludeDeleted
	) throws OHServiceException {
		LOGGER.info("Loading suppliers...");
		List<Supplier> suppliers = excludeDeleted? manager.getList() : manager.getAll();
		List<SupplierDTO> mappedSuppliers = mapper.map2DTOList(suppliers);

		if (mappedSuppliers.isEmpty()) {
			LOGGER.info("No supplier found.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedSuppliers);
		}

		LOGGER.info("Found {} suppliers.", mappedSuppliers.size());
		return ResponseEntity.ok(mappedSuppliers);
	}

	/**
	 * Get a supplier by its ID.
	 * @param id The ID of the supplier to retrieve
	 * @return the found supplier
	 * @throws OHServiceException When failed to retrieve the supplier
	 */
	@GetMapping(value = "/suppliers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public SupplierDTO getSuppliers(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Loading supplier with ID {}", id);
		Supplier supplier = manager.getByID(id);
		if (supplier == null) {
			LOGGER.info("Supplier not found.");
			throw new OHAPIException(new OHExceptionMessage("Supplier not found."), HttpStatus.NOT_FOUND);
		}

		LOGGER.info("Found supplier.");
		return mapper.map2DTO(supplier);
	}

	/**
	 * Delete a supplier.
	 * <p>This is a soft deletion.</p>
	 * @param id Supplier ID
	 * @throws OHServiceException When failed to delete the supplier
	 */
	@DeleteMapping(value = "/suppliers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSupplier(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Loading supplier with ID {}", id);
		Supplier supplier = manager.getByID(id);
		if (supplier == null) {
			LOGGER.info("Supplier not found.");
			throw new OHAPIException(new OHExceptionMessage("Supplier not found."), HttpStatus.NOT_FOUND);
		}

		LOGGER.info("Deleting supplier with ID {}", id);

		manager.delete(supplier);

		LOGGER.info("Supplier with ID {} deleted", id);
	}
}
