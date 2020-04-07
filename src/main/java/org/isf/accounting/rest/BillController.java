package org.isf.accounting.rest;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.dto.FullBillDTO;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.PriceList;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Api(value="/bills",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="basicAuth")})
public class BillController {
	
	@Autowired
	protected BillBrowserManager billManager;
	
	@Autowired
	protected PriceListManager priceListManager;
	
	@Autowired
	protected PatientBrowserManager patientManager;

	private final Logger logger = LoggerFactory.getLogger(BillController.class);

    /**
     * Create new bill with the list of billItems and the list of billPayments
     * @param newPatient
     * @return {@link FullBillDTO}
     * @throws OHServiceException
     */
	@PostMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FullBillDTO> newBill(@RequestBody FullBillDTO newBillDto) throws OHServiceException {
        
		logger.info("Create Bill "  + newBillDto.toString()); 
      
        Bill bill = getObjectMapper().map(newBillDto.getBill(), Bill.class);
        
        Patient pat = patientManager.getPatient(bill.getPatName());
        
        ArrayList<PriceList> list = priceListManager.getLists();
        
        PriceList plist = list.stream()
        		  .filter(pricel -> pricel.getName().equals(bill.getListName()))
        		  .findAny()
        		  .orElse(null);
        
        if(pat != null) {
        	bill.setPatient(pat);
        } else {
        	 throw new OHAPIException(new OHExceptionMessage(null, "Patient Not found!", OHSeverityLevel.ERROR));
        }
        
        if(plist != null) {
        	bill.setList(plist);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, "Price list not found!", OHSeverityLevel.ERROR));
        }
        
        ArrayList<BillItems> billItems = new ArrayList<BillItems>(newBillDto.getBillItems().stream().map(item -> getObjectMapper().map(item, BillItems.class)).collect(Collectors.toList()));
        
        ArrayList<BillPayments> billPayments =  new ArrayList<BillPayments>( newBillDto.getBillPayments().stream().map(item -> getObjectMapper().map(item, BillPayments.class)).collect(Collectors.toList()));
        
        boolean isCreated = billManager.newBill(bill, billItems, billPayments);
        
        if(!isCreated || newBillDto == null){
            throw new OHAPIException(new OHExceptionMessage(null, "Bill is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newBillDto);
	}

	 /**
     * update bill with the list of billItems and the list of billPayments
     * @param odBillDto
     * @return {@link FullBillDTO}
     * @throws OHServiceException
     */
	@PutMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FullBillDTO> updateBill(@PathVariable Integer id, @RequestBody FullBillDTO odBillDto) throws OHServiceException {
        
		logger.info("updated Bill "  + odBillDto.toString()); 
        Bill bill = getObjectMapper().map(odBillDto.getBill(), Bill.class);
        
        bill.setId(id);
        
        if(billManager.getBill(id) == null) {
        	throw new OHAPIException(new OHExceptionMessage(null, "Bill to update not found!", OHSeverityLevel.ERROR));
        }
        
        Patient pat = patientManager.getPatient(bill.getPatName());
        
        ArrayList<PriceList> list = priceListManager.getLists();
        
        PriceList plist = list.stream()
        		  .filter(pricel -> pricel.getName().equals(bill.getListName()))
        		  .findAny()
        		  .orElse(null);
          
        if(pat != null) {
        	bill.setPatient(pat);
        } else {
        	 throw new OHAPIException(new OHExceptionMessage(null, "Patient Not found!", OHSeverityLevel.ERROR));
        }
        
        if(plist != null) {
        	bill.setList(plist);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, "Price list not found!", OHSeverityLevel.ERROR));
        }
        
        ArrayList<BillItems> billItems = new ArrayList<BillItems>(odBillDto.getBillItems().stream().map(item -> getObjectMapper().map(item, BillItems.class)).collect(Collectors.toList()));
        
        ArrayList<BillPayments> billPayments =  new ArrayList<BillPayments>( odBillDto.getBillPayments().stream().map(item -> getObjectMapper().map(item, BillPayments.class)).collect(Collectors.toList()));
        
        boolean isUpdated = billManager.updateBill(bill, billItems, billPayments);
    
        if(!isUpdated){
            throw new OHAPIException(new OHExceptionMessage(null, "Bill is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(odBillDto);
	}
	
	/**
	 * Retrieves all the {@link Bill}s for the specified parameters
	 * @param dateFrom the low date range endpoint, inclusive. 
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param code the patient code, which can be set or not.
	 * @return a list of retrieved {@link Bill}s or <code>null</code> if an error occurred.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBills(@RequestParam(value="datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateFrom,
			@RequestParam(value="dateto")@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateTo,
			@RequestParam(value="patient_code", required=false, defaultValue="") Integer code) throws OHServiceException {
        
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);
        
        GregorianCalendar dateto = new GregorianCalendar();
        dateto.setTime(dateTo);
        
        ArrayList<Bill> bills = new ArrayList<Bill>();
        
        List<BillDTO> billDTOS = new ArrayList<BillDTO>();
        
        if(code == null) {
        	logger.info("Get payments datefrom:"  +  datefrom + " dateTo:" + dateto);
        	bills = billManager.getBills(datefrom, dateto);
        } else {
        	Patient pat = patientManager.getPatient(code);
             
            logger.info("Get Bills datefrom:"  +  datefrom + " dateTo:" + dateto +"patient: "+pat);
             
     	    bills = billManager.getBills(datefrom, dateto, pat);
        }
        
        billDTOS = bills.stream().map(bil-> getObjectMapper().map(bil, BillDTO.class)).collect(Collectors.toList());
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(billDTOS);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
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
			@RequestParam(value="datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateFrom,
			@RequestParam(value="dateto")@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateTo, @RequestParam(value="patient_code", required=false, defaultValue="") Integer code) throws OHServiceException {
        logger.info("Get Payments datefrom:"  +  dateFrom + " dateTo:" + dateTo +"patient: "+code);
        
        ArrayList<BillPayments> payments = new ArrayList<BillPayments>();
        
        List<BillPaymentsDTO> paymentsDTOS = new ArrayList<BillPaymentsDTO>();
        
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);
        
        GregorianCalendar dateto = new GregorianCalendar();
        dateto.setTime(dateTo); 
        
        logger.info("Get getPayments datefrom:"  +  datefrom + " dateTo:" + dateto);
        
        if(code == null) {
        	payments = billManager.getPayments(datefrom, dateto);
        } else {
        	 Patient pat = patientManager.getPatient(code);             
             payments = billManager.getPayments(datefrom, dateto, pat);
        }
        
        paymentsDTOS = payments.stream().map(pay-> getObjectMapper().map(pay, BillPaymentsDTO.class)).collect(Collectors.toList());
        
        if(paymentsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(paymentsDTOS);
        }
	}
	
	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 * @param billID the bill id.
	 * @return a list of {@link BillPayments} or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/payments/{bill_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPaymentsDTO>> getPaymentsByBillId(@PathVariable(value="bill_id") Integer id) throws OHServiceException {
        logger.info("Get getPayments for bill with id:"  + id);
           
	    ArrayList<BillPayments> bills = billManager.getPayments(id);
	    
        List<BillPaymentsDTO> paymentsDTOS = bills.stream().map(pay-> getObjectMapper().map(pay, BillPaymentsDTO.class)).collect(Collectors.toList());
        
        if(paymentsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(paymentsDTOS);
        }
	}
	
	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * @param id the bill id.
	 * @return a list of {@link BillItems} or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/items/{bill_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillItemsDTO>> getItems(@PathVariable(value="bill_id")Integer id) throws OHServiceException {
        logger.info("Get Items for bill with id:"  + id);
           
	    ArrayList<BillItems> items = billManager.getItems(id);
	    
        List<BillItemsDTO> itemsDTOS = items.stream().map(it-> getObjectMapper().map(it, BillItemsDTO.class)).collect(Collectors.toList());
        
        if(itemsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(itemsDTOS);
        }
	}
	
	/**
	 * Get the {@link Bill} with specified billID
	 * @param id the bill Id
	 * @return the {@link Bill} or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BillDTO> getBill(@PathVariable Integer id) throws OHServiceException {
        logger.info("Get bill with id:"  + id);
           
	    Bill bill = billManager.getBill(id);
	    
	    BillDTO billDTO = getObjectMapper().map(bill, BillDTO.class);
        
        if(billDTO == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(billDTO);
        }
	}
	
	/**
	 * Retrieves all the {@link Bill}s associated to the specified {@link Patient}.
	 * @param code - the Patient's code
	 * @return the list of {@link Bill}s
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/pending/affiliate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> getPendingBillsAffiliate(@RequestParam(value="patient_code") Integer code) throws OHServiceException {
        logger.info("Get bill with id:"  + code);
           
	    List<Bill> bills = billManager.getPendingBillsAffiliate(code);
	    
	    List<BillDTO> billDTOS = bills.stream().map(bill-> getObjectMapper().map(bill, BillDTO.class)).collect(Collectors.toList());
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * @param code the patient code.
	 * @return the list of pending bills or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/pending", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> getPendingBills(@RequestParam(value="patient_code") Integer code) throws OHServiceException {
        logger.info("Get bill with id:"  + code);
           
	    List<Bill> bills = billManager.getPendingBills(code);
	    
	    List<BillDTO> billDTOS = bills.stream().map(bill-> getObjectMapper().map(bill, BillDTO.class)).collect(Collectors.toList());
        
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(billDTOS);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
	}
	
	/**
	 * Search all the {@link Bill}s for the specified parameters
	 * @param dateFrom the low date range endpoint, inclusive. 
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param bill item the bill item object.
	 * @return a list of retrieved {@link Bill}s or <code>null</code> if an error occurred.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/bills/search/by/item", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBills(@RequestParam(value="datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateFrom,
			@RequestParam(value="dateto")@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateTo,
			@RequestBody BillItemsDTO billItemDTO) throws OHServiceException {
        
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);
        
        GregorianCalendar dateto = new GregorianCalendar();
        dateto.setTime(dateTo);
               
        BillItems billItem = getObjectMapper().map(billItemDTO, BillItems.class);
        	
        logger.info("Get Bills datefrom:"  +  datefrom + " dateTo:" + dateto + " Bill ITEM ID: "+billItem.getId());
             
        ArrayList<Bill> bills = billManager.getBills(datefrom, dateto, billItem);
        
        List<BillDTO> billDTOS = bills.stream().map(bil-> getObjectMapper().map(bil, BillDTO.class)).collect(Collectors.toList());
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
	}
	
	/**
	 * Returns all the distinct stored {@link BillItems}.
	 * 
	 * @return a list of  distinct {@link BillItems} or null if an error occurs.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillItemsDTO>> getDistinctItems() throws OHServiceException {
		
		logger.info("get all the distinct stored BillItems");
           
	    ArrayList<BillItems> items = billManager.getDistinctItems();
	    
        List<BillItemsDTO> itemsDTOS = items.stream().map(it-> getObjectMapper().map(it, BillItemsDTO.class)).collect(Collectors.toList());
        
        if(itemsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(itemsDTOS);
        }
	}
	
	@DeleteMapping(value = "/bills/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteBill(@PathVariable Integer id) throws OHServiceException {
        logger.info("Delete bill id:"  +  id);
        Bill bill = billManager.getBill(id);
        boolean isDeleted = false;
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
	 * search all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * @param payments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments} or <code>null</code> if an error occurred.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/bills/search/by/payments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> searchBillsByPayments(@RequestBody List<BillPaymentsDTO> paymentsDTO) throws OHServiceException {
    
        ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>(paymentsDTO.stream().map(pay-> getObjectMapper().map(pay, BillPayments.class)).collect(Collectors.toList()));
        
        List<Bill> bills = billManager.getBills(billPayments);
        
        List<BillDTO>billDTOS = bills.stream().map(bil-> getObjectMapper().map(bil, BillDTO.class)).collect(Collectors.toList());
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
	}

}
