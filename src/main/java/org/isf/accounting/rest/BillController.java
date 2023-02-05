/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.rest;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.dto.FullBillDTO;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.mapper.BillItemsMapper;
import org.isf.accounting.mapper.BillMapper;
import org.isf.accounting.mapper.BillPaymentsMapper;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.PriceList;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import io.swagger.annotations.Authorization;

@RestController
@Api(value="/bills",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class BillController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BillController.class);

	@Autowired
	protected BillBrowserManager billManager;

	@Autowired
	protected PriceListManager priceListManager;

	@Autowired
	protected PatientBrowserManager patientManager;

	@Autowired
	protected BillMapper billMapper;

	@Autowired
	protected BillItemsMapper billItemsMapper;

	@Autowired
	protected BillPaymentsMapper billPaymentsMapper;

	public BillController(BillBrowserManager billManager, PriceListManager priceListManager,
			PatientBrowserManager patientManager, BillMapper billMapper, BillItemsMapper billItemsMapper,
			BillPaymentsMapper billPaymentsMapper) {
		this.billManager = billManager;
		this.priceListManager = priceListManager;
		this.patientManager = patientManager;
		this.billMapper = billMapper;
		this.billItemsMapper = billItemsMapper;
		this.billPaymentsMapper = billPaymentsMapper;
	}

	/**
     * Create new bill with the list of billItems and the list of billPayments
     * @param newBillDto
     * @return {@link FullBillDTO}
     * @throws OHServiceException
     */
	@PostMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FullBillDTO> newBill(@RequestBody FullBillDTO newBillDto) throws OHServiceException {

		if (newBillDto == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Bill is null!", OHSeverityLevel.ERROR));
		}
		LOGGER.info("Create Bill {}", newBillDto);

		Bill bill = billMapper.map2Model(newBillDto.getBill());

		Patient pat = patientManager.getPatientByName(bill.getPatName()); // FIXME: verify why we were searching by name

		List<PriceList> list = priceListManager.getLists();

		PriceList plist = list.stream().filter(pricel -> pricel.getName().equals(bill.getListName())).findAny().orElse(null);

		if (pat != null) {
			bill.setBillPatient(pat);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient Not found!", OHSeverityLevel.ERROR));
		}

		if (plist != null) {
			bill.setPriceList(plist);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Price list not found!", OHSeverityLevel.ERROR));
		}

		List<BillItems> billItems = billItemsMapper.map2ModelList(newBillDto.getBillItems());

		List<BillPayments> billPayments = billPaymentsMapper.map2ModelList(newBillDto.getBillPayments());

		boolean isCreated = billManager.newBill(bill, billItems, billPayments);

		if (!isCreated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Bill is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(newBillDto);
	}

	/**
	 * Update bill with the list of billItems and the list of billPayments
	 * 
	 * @param odBillDto
	 * @return {@link FullBillDTO}
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FullBillDTO> updateBill(@PathVariable Integer id, @RequestBody FullBillDTO odBillDto) throws OHServiceException {

		LOGGER.info("updated Bill {}", odBillDto);
		Bill bill = billMapper.map2Model(odBillDto.getBill());

		bill.setId(id);

		if (billManager.getBill(id) == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Bill to update not found!", OHSeverityLevel.ERROR));
		}

		Patient pat = patientManager.getPatientByName(bill.getPatName()); // FIXME: verify why we were searching by name

		List<PriceList> list = priceListManager.getLists();

		PriceList plist = list.stream().filter(pricel -> pricel.getName().equals(bill.getListName())).findAny().orElse(null);

		if (pat != null) {
			bill.setBillPatient(pat);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Patient Not found!", OHSeverityLevel.ERROR));
		}

		if (plist != null) {
			bill.setPriceList(plist);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, "Price list not found!", OHSeverityLevel.ERROR));
		}

		List<BillItems> billItems = billItemsMapper.map2ModelList(odBillDto.getBillItems());

		List<BillPayments> billPayments = billPaymentsMapper.map2ModelList(odBillDto.getBillPayments());

		boolean isUpdated = billManager.updateBill(bill, billItems, billPayments);

		if (!isUpdated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Bill is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(odBillDto);
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified parameters
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param code the patient code, which can be set or not.
	 * @return a list of retrieved {@link Bill}s or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBills(@RequestParam(value = "datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateFrom,
			@RequestParam(value = "dateto") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateTo,
			@RequestParam(value = "patient_code", required = false, defaultValue = "") Integer code) throws OHServiceException {

		List<Bill> bills;

		List<BillDTO> billDTOS;

		if (code == null) {
			LOGGER.info("Get payments datefrom: {}  dateTo: {}", dateFrom, dateTo);
			bills = billManager.getBills(dateFrom, dateTo);
		} else {
			Patient pat = patientManager.getPatientById(code);

			LOGGER.info("Get Bills datefrom: {}  dateTo: {} patient: {}", dateFrom, dateTo, pat);
			bills = billManager.getBills(dateFrom, dateTo, pat);
		}

		billDTOS = billMapper.map2DTOList(bills);

		if (billDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(billDTOS);
		}
		return ResponseEntity.ok(billDTOS);
	}

	/**
	 * Retrieves all the billPayments for a given parameters
	 * @param dateFrom
	 * @param dateTo
	 * @param code the patient code, which can be set or not.
	 * @return the list of payments
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/payments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPaymentsDTO>> searchBillsPayments(
			@RequestParam(value="datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateFrom,
			@RequestParam(value="dateto") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateTo, 
			@RequestParam(value="patient_code", required=false, defaultValue="") Integer code) throws OHServiceException {
		LOGGER.info("Get Payments datefrom: {}  dateTo: {} patient: {}", dateFrom, dateTo, code);

		List<BillPayments> payments;

		List<BillPaymentsDTO> paymentsDTOS;

		LOGGER.info("Get getPayments datefrom: {}  dateTo: {}", dateFrom, dateTo);

		if (code == null) {
			payments = billManager.getPayments(dateFrom, dateTo);
		} else {
			Patient pat = patientManager.getPatientById(code);
			payments = billManager.getPayments(dateFrom, dateTo, pat);
		}

		paymentsDTOS = billPaymentsMapper.map2DTOList(payments);

		if (paymentsDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(paymentsDTOS);
	}

	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 * @param id the bill id.
	 * @return a list of {@link BillPayments} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	
	@GetMapping(value = "/bills/payments/{bill_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPaymentsDTO>> getPaymentsByBillId(@PathVariable(value="bill_id") Integer id) throws OHServiceException {
		LOGGER.info("Get getPayments for bill with id: {}", id);

		List<BillPayments> billPayments = billManager.getPayments(id);

		List<BillPaymentsDTO> paymentsDTOS = billPaymentsMapper.map2DTOList(billPayments);

		if (paymentsDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(paymentsDTOS);
	}

	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * @param id the bill id.
	 * @return a list of {@link BillItems} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/items/{bill_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillItemsDTO>> getItems(@PathVariable(value="bill_id")Integer id) throws OHServiceException {
		LOGGER.info("Get Items for bill with id: {}", id);

		List<BillItems> items = billManager.getItems(id);

		List<BillItemsDTO> itemsDTOS = billItemsMapper.map2DTOList(items);

		if (itemsDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(itemsDTOS);
	}

	/**
	 * Get the {@link Bill} with specified billID
	 * @param id the bill Id
	 * @return the {@link Bill} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BillDTO> getBill(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Get bill with id: {}", id);

		Bill bill = billManager.getBill(id);

		BillDTO billDTO = billMapper.map2DTO(bill);

		if (billDTO == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(billDTO);
	}

	/**
	 * Retrieves all the {@link Bill}s associated to the specified {@link Patient}.
	 * @param code - the Patient's code
	 * @return the list of {@link Bill}s
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/pending/affiliate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> getPendingBillsAffiliate(@RequestParam(value="patient_code") Integer code) throws OHServiceException {
		LOGGER.info("Get bill with id: {}", code);

		List<Bill> bills = billManager.getPendingBillsAffiliate(code);

		List<BillDTO> billDTOS = billMapper.map2DTOList(bills);

		if (billDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(billDTOS);
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * @param code the patient code.
	 * @return the list of pending bills or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/pending", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> getPendingBills(@RequestParam(value="patient_code") Integer code) throws OHServiceException {
		LOGGER.info("Get bill with id: {}", code);

		List<Bill> bills = billManager.getPendingBills(code);

		List<BillDTO> billDTOS = billMapper.map2DTOList(bills);

		if (billDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(billDTOS);
		}
		return ResponseEntity.ok(billDTOS);
	}

	/**
	 * Search all the {@link Bill}s for the specified parameters
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param billItemDTO the bill item object.
	 * @return a list of retrieved {@link Bill}s or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/bills/search/by/item", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBills(@RequestParam(value="datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateFrom,
			@RequestParam(value="dateto")@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateTo,
			@RequestBody BillItemsDTO billItemDTO) throws OHServiceException {

		BillItems billItem = billItemsMapper.map2Model(billItemDTO);

		LOGGER.info("Get Bills dateFrom: {}  dateTo: {}  Bill ITEM ID: {}", dateFrom, dateTo, billItem.getId());

		List<Bill> bills = billManager.getBills(dateFrom, dateTo, billItem);

		List<BillDTO> billDTOS = billMapper.map2DTOList(bills);

		if (billDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(billDTOS);
	}

	/**
	 * Returns all the distinct stored {@link BillItems}.
	 *
	 * @return a list of  distinct {@link BillItems} or null if an error occurs.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillItemsDTO>> getDistinctItems() throws OHServiceException {

		LOGGER.info("get all the distinct stored BillItems");

		List<BillItems> items = billManager.getDistinctItems(); //TODO: verify if it's correct

		List<BillItemsDTO> itemsDTOS = billItemsMapper.map2DTOList(items);

		if (itemsDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(itemsDTOS);
	}

	@DeleteMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteBill(@PathVariable Integer id) throws OHServiceException {
		LOGGER.info("Delete bill id: {}", id);
		Bill bill = billManager.getBill(id);
		boolean isDeleted;
		if (bill != null) {
			isDeleted = billManager.deleteBill(bill);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Bill is not deleted!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isDeleted);
	}

	/**
	 * Search all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * @param paymentsDTO the {@link BillPaymentsDTO} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/bills/search/by/payments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBillsByPayments(@RequestBody List<BillPaymentsDTO> paymentsDTO) throws OHServiceException {

		List<BillPayments> billPayments = billPaymentsMapper.map2ModelList(paymentsDTO);

		List<Bill> bills = billManager.getBills(billPayments);

		List<BillDTO> billDTOS = billMapper.map2DTOList(bills);

		if (billDTOS.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok(billDTOS);
	}

}
