package org.isf.accounting.rest;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.accounting.dto.BillDTO;
import org.isf.accounting.dto.BillItemsDTO;
import org.isf.accounting.dto.BillPaymentsDTO;
import org.isf.accounting.dto.FullBillDTO;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.test.TestBill;
import org.isf.accounting.test.TestBillItems;
import org.isf.accounting.test.TestBillPayments;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.test.TestPriceList;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.shared.mapper.OHModelMapper;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Emerson Castaneda
 *
 */

public class BillControllerTest {
	private final Logger logger = LoggerFactory.getLogger(BillControllerTest.class);
	
	@Mock
	protected BillBrowserManager billManagerMock;
	
	@Mock
	protected PriceListManager priceListManagerMock;
	
	@Mock
	protected PatientBrowserManager patientManagerMock;
	
    private MockMvc mockMvc;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new BillController(billManagerMock, priceListManagerMock, patientManagerMock))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    }

	@Test
	public void when_post_bills_is_call_without_contentType_header_then_HttpMediaTypeNotSupportedException() throws Exception {
		String request = "/bills";
		
		MvcResult result = this.mockMvc
			.perform(post(request).content(new byte[]{'a', 'b', 'c'}))
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(content().string(anyOf(nullValue(), equalTo(""))))
			.andReturn();
		
		Optional<HttpMediaTypeNotSupportedException> exception = Optional.ofNullable((HttpMediaTypeNotSupportedException) result.getResolvedException());
		logger.debug("exception: {}", exception);
		exception.ifPresent( (se) -> assertThat(se, notNullValue()));
		exception.ifPresent( (se) -> assertThat(se, instanceOf(HttpMediaTypeNotSupportedException.class)));
	
	}
	
	@Test
	public void when_post_bills_is_call_with_empty_body_then_BadRequest_HttpMessageNotReadableException() throws Exception {
		String request = "/bills";
		String empty_body = "";
				
		MvcResult result = this.mockMvc
			.perform(
				post(request)
				.content(empty_body.getBytes())
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(log())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest())
			.andExpect(content().string(anyOf(nullValue(), equalTo(""))))
			.andReturn();
		
		Optional<HttpMessageNotReadableException> exception = Optional.ofNullable((HttpMessageNotReadableException) result.getResolvedException());
		logger.debug("exception: {}", exception);
		exception.ifPresent( (se) -> assertThat(se, notNullValue()));
		exception.ifPresent( (se) -> assertThat(se, instanceOf(HttpMessageNotReadableException.class)));
	}
	
	
	@Test
	public void when_post_patients_PatientBrowserManager_getPatient_returns_null_then_OHAPIException_BadRequest() throws Exception {
		String request = "/bills";
		FullBillDTO newFullBillDTO =  FullBillDTOHelper.setup();
		Integer id = 0;
		newFullBillDTO.getBill().setId(id);
		Integer code = 0;
		newFullBillDTO.getBill().getPatientDTO().setCode(code);
		
		newFullBillDTO.getBill().setPatient(true);
		
		String jsonNewFullBillDTO = FullBillDTOHelper.asJsonString(newFullBillDTO);
		System.out.println("JSON ---> " + jsonNewFullBillDTO);
		
		logger.info("JSON --> " +jsonNewFullBillDTO);
		
		when(patientManagerMock.getPatient(any(String.class))).thenReturn(null);
		
		MvcResult result = this.mockMvc
			.perform(
				post(request)
				.contentType(MediaType.APPLICATION_JSON)
				.content(FullBillDTOHelper.asJsonString(newFullBillDTO))
			)
			.andDo(log())
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) //TODO Create OHCreateAPIException
			.andExpect(content().string(containsString("Patient Not found!")))
			.andReturn();
		
		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	
	

	@Test
	public void testUpdateBill() {
		fail("Not yet implemented");
	}

	@Test
	public void when_put_bills_PatientBrowserManager_getPatient_returns_null_then_OHAPIException_BadRequest() throws Exception {
		Integer id = 123;
		String request = "/bills/{id}";
		
		FullBillDTO newFullBillDTO =  FullBillDTOHelper.setup();
		newFullBillDTO.getBill().setId(id);
		Integer code = 111;
		newFullBillDTO.getBill().getPatientDTO().setCode(code);
		newFullBillDTO.getBill().setPatient(true);
		String jsonNewFullBillDTO = FullBillDTOHelper.asJsonString(newFullBillDTO);
		System.out.println("JSON ---> " + jsonNewFullBillDTO);
		Bill bill = BillHelper.setupBill();
		when(patientManagerMock.getPatient(eq(bill.getPatName()))).thenReturn(null);
		when(billManagerMock.getBill(eq(id))).thenReturn(bill);
		when(billManagerMock.deleteBill(eq(bill))).thenReturn(true);
		
		MvcResult result = this.mockMvc
			.perform(
				put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(FullBillDTOHelper.asJsonString(newFullBillDTO))
			)
			.andDo(log())
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) //TODO Create OHCreateAPIException
			.andExpect(content().string(containsString("Patient Not found!")))
			.andReturn();
		
		//TODO Create OHCreateAPIException
		Optional<OHAPIException> oHAPIException = Optional.ofNullable((OHAPIException) result.getResolvedException());
		logger.debug("oHAPIException: {}", oHAPIException);
		oHAPIException.ifPresent( (se) -> assertThat(se, notNullValue()));
		oHAPIException.ifPresent( (se) -> assertThat(se, instanceOf(OHAPIException.class)));
	}
	
	@Test
	public void when_put_bills_PatientBrowserManager_getPatient_returns_null_then_OK() throws Exception {
		Integer id = 123;
		String request = "/bills/{id}";
		FullBillDTO newFullBillDTO =  FullBillDTOHelper.setup();
		newFullBillDTO.getBill().setId(id);
		Integer code = 111;
		newFullBillDTO.getBill().getPatientDTO().setCode(code);
		newFullBillDTO.getBill().setPatient(true);
		String jsonNewFullBillDTO = FullBillDTOHelper.asJsonString(newFullBillDTO);
		System.out.println("JSON ---> " + jsonNewFullBillDTO);
		Bill bill = BillHelper.setupBill();
		Patient patient = bill.getPatient();
		System.out.println("patient ---> " + patient);
		when(patientManagerMock.getPatient(any(String.class))).thenReturn(patient);
		when(billManagerMock.getBill(eq(id))).thenReturn(bill);
		ArrayList<PriceList> list = new ArrayList<PriceList>();
		System.out.println("bill.getList() ---> " + bill.getList());
		list.add(bill.getList());
		when(priceListManagerMock.getLists()).thenReturn(list);
		
		this.mockMvc
			.perform(
				put(request, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(FullBillDTOHelper.asJsonString(newFullBillDTO))
			)
			.andDo(log())
			.andDo(print())
			.andExpect(status().isOk()) 
			.andExpect(content().string(containsString("Patient Not found!")))
			.andReturn();
	}
	
	
	@Test
	public void testSearchBillsDateDateInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchBillsPayments() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPaymentsByBillId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetItems() {
		fail("Not yet implemented");
	}

	@Test
	public void when_get_bill_with_existent_id_then_response_BillDTO_and_OK() throws Exception {
		Integer id = 123;
		String request = "/bills/{id}";
		
		Bill bill = BillHelper.setupBill();
		bill.setId(id);
		BillDTO expectedBillDTO = BillDTOHelper.setup();
				
		when(billManagerMock.getBill(eq(id))).thenReturn(bill);
		
		this.mockMvc
			.perform(
					get(request, id)
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(BillDTOHelper.asJsonString(expectedBillDTO))))
			.andReturn();
	}


	@Test
	public void testGetPendingBillsAffiliate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPendingBills() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchBillsDateDateBillItemsDTO() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDistinctItems() {
		fail("Not yet implemented");
	}

	@Test
	public void when_delete_bill_with_existent_id_then_response_true_and_OK() throws Exception {
		Integer id = 123;
		String request = "/bills/{id}";
		
		Bill bill = BillHelper.setupBill();
		
		when(billManagerMock.getBill(eq(id))).thenReturn(bill);
		
		when(billManagerMock.deleteBill(eq(bill))).thenReturn(true);
		
		this.mockMvc
			.perform(
					delete(request, id)
					.contentType(MediaType.APPLICATION_JSON)
					)		
			.andDo(log())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("true")));
	}
	
	

	static class BillHelper{
		
		public static Bill setupBill() throws OHException{
			TestPatient testPatient =  new TestPatient();
			Patient patient = testPatient.setup(false); 
			TestPriceList testPriceList =  new TestPriceList();
			PriceList priceList = testPriceList.setup(false);
			TestBill testBill = new TestBill();
			Bill bill = testBill.setup(priceList, patient, false);
			//bill.setId(id);
			return bill;
		}
		
		public static ArrayList<Bill> setupBillList(int size) {
			return (ArrayList<Bill>) IntStream.range(1, size+1)
					.mapToObj(i -> {	Bill b = null;
										try {
											b = BillHelper.setupBill();
										} catch (OHException e) {
											e.printStackTrace();
										}
										return b;
									}
					).collect(Collectors.toList());
		}
	}
	
	static class FullBillDTOHelper{
		public static FullBillDTO setup() throws OHException{
			Bill bill = BillHelper.setupBill();
			FullBillDTO fullBillDTO = new  FullBillDTO();
			
			Patient patient = new TestPatient().setup(true);
			PatientDTO patientDTO = OHModelMapper.getObjectMapper().map(patient, PatientDTO.class);
			
			BillDTO billDTO = new BillDTO();
			billDTO.setPatientDTO(patientDTO);
			billDTO.setListName(bill.getListName());
			billDTO.setPatName(patient.getFirstName());

			//OP-205
			//BillDTO billDTO = OHModelMapper.getObjectMapper().map(bill, BillDTO.class);
			
			fullBillDTO.setBill(billDTO);

			
			TestBillItems tbi = new TestBillItems();
			BillItems billItems = tbi.setup(bill, false);
			BillItemsDTO billItemsDTO = OHModelMapper.getObjectMapper().map(billItems, BillItemsDTO.class);
			fullBillDTO.setBillItems(Arrays.asList(billItemsDTO));

			TestBillPayments tbp = new TestBillPayments();
			BillPayments billPayments = tbp.setup(bill, false);
			BillPaymentsDTO billPaymentsDTO = OHModelMapper.getObjectMapper().map(billPayments, BillPaymentsDTO.class);
			fullBillDTO.setBillPayments(Arrays.asList(billPaymentsDTO));

			
			return fullBillDTO;
		}
		
		public static String asJsonString(FullBillDTO fullBillDTO){
			try {
				return new ObjectMapper().writeValueAsString(fullBillDTO);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static String asJsonString(List<FullBillDTO> fullBillDTOList){
			try {
				return new ObjectMapper().writeValueAsString(fullBillDTOList);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	static class BillDTOHelper{
		public static BillDTO setup() throws OHException{
			Bill bill = BillHelper.setupBill();
			FullBillDTO fullBillDTO = new  FullBillDTO();
			
			Patient patient = new TestPatient().setup(true);
			PatientDTO patientDTO = OHModelMapper.getObjectMapper().map(patient, PatientDTO.class);
			
			BillDTO billDTO = new BillDTO();
			billDTO.setPatientDTO(patientDTO);
			fullBillDTO.setBill(billDTO);
			
		
			
			return billDTO;
		}
		
		public static String asJsonString(BillDTO billDTO){
			try {
				return new ObjectMapper().writeValueAsString(billDTO);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static String asJsonString(List<BillDTO> billDTOList){
			try {
				return new ObjectMapper().writeValueAsString(billDTOList);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
}
