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
@Api(value="/bills",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="basicAuth")})
public class BillController {
	
	private static final String DEFAULT_PAGE_SIZE = "80";

	@Autowired
	protected BillBrowserManager billManager;
	
	@Autowired
	protected PriceListManager priceListManager;
	
	@Autowired
	protected PatientBrowserManager patientManager;

	private final Logger logger = LoggerFactory.getLogger(BillController.class);

	public BillController(BillBrowserManager billManager) {
		this.billManager = billManager;
	}

    /**
     * Create new bill with the list of billItems and the list of billPayments
     * @param newPatient
     * @return {@link FullBillDTO}
     * @throws OHServiceException
     */
	@PostMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FullBillDTO> newPatient(@RequestBody FullBillDTO newBillDto) throws OHServiceException {
        
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
    ResponseEntity<FullBillDTO> updatePatient(@PathVariable Integer id, @RequestBody FullBillDTO odBillDto) throws OHServiceException {
        
		logger.info("updated Bill "  + odBillDto.toString()); 
      
        Bill bill = getObjectMapper().map(odBillDto.getBill(), Bill.class);
        
        bill.setId(id);
        
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
    
        if(!isUpdated || odBillDto == null){
            throw new OHAPIException(new OHExceptionMessage(null, "Bill is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(odBillDto);
	}
	
	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillDTO>> getBills(
			@RequestParam(value="datefrom") Date dateFrom,
			@RequestParam(value="dateto") Date dateTo, @RequestParam(value="patient_code") Integer code) throws OHServiceException {
        logger.info("Get Bills datefrom:"  +  dateFrom + " dateTo:" + dateTo);
        
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);
        
        GregorianCalendar dateto = new GregorianCalendar();
        datefrom.setTime(dateTo); 
        
        Patient pat = patientManager.getPatient(code);
        
	    ArrayList<Bill> bills = billManager.getBills(datefrom, dateto, pat);
	    
        List<BillDTO> billDTOS = bills.stream().map(bil-> getObjectMapper().map(bil, BillDTO.class)).collect(Collectors.toList());
        
        if(billDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(billDTOS);
        }else{
            return ResponseEntity.ok(billDTOS);
        }
	}
	
	/**
	 * Retrieves all the billPayments for a given patient between dateFrom and dateTo
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the list of payments
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/payments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPaymentsDTO>> getPayments(
			@RequestParam(value="datefrom") Date dateFrom,
			@RequestParam(value="dateto") Date dateTo, @RequestParam(value="patient_code") Integer code) throws OHServiceException {
        logger.info("Get getPayments datefrom:"  +  dateFrom + " dateTo:" + dateTo);
        
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);
        
        GregorianCalendar dateto = new GregorianCalendar();
        datefrom.setTime(dateTo); 
        
        Patient pat = patientManager.getPatient(code);
        
	    ArrayList<BillPayments> bills = billManager.getPayments(datefrom, dateto, pat);
	    
        List<BillPaymentsDTO> paymentsDTOS = bills.stream().map(pay-> getObjectMapper().map(pay, BillPaymentsDTO.class)).collect(Collectors.toList());
        
        if(paymentsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(paymentsDTOS);
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
	@GetMapping(value = "/bills/payments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillPaymentsDTO>> getPayments(@PathVariable Integer id) throws OHServiceException {
        logger.info("Get getPayments for bill with id:"  + id);
           
	    ArrayList<BillPayments> bills = billManager.getPayments(id);
	    
        List<BillPaymentsDTO> paymentsDTOS = bills.stream().map(pay-> getObjectMapper().map(pay, BillPaymentsDTO.class)).collect(Collectors.toList());
        
        if(paymentsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(paymentsDTOS);
        }else{
            return ResponseEntity.ok(paymentsDTOS);
        }
	}
	
	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/bills/items/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BillItemsDTO>> getItems(@PathVariable Integer id) throws OHServiceException {
        logger.info("Get getPayments for bill with id:"  + id);
           
	    ArrayList<BillItems> items = billManager.getItems(id);
	    
        List<BillItemsDTO> itemsDTOS = items.stream().map(it-> getObjectMapper().map(it, BillItemsDTO.class)).collect(Collectors.toList());
        
        if(itemsDTOS.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(itemsDTOS);
        }else{
            return ResponseEntity.ok(itemsDTOS);
        }
	}

}
