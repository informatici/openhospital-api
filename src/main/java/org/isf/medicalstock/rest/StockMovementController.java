package org.isf.medicalstock.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.dto.LotDTO;
import org.isf.medicalstock.dto.MovementDTO;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.mapper.LotMapper;
import org.isf.medicalstock.mapper.MovementMapper;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/stockmovements", produces = MediaType.APPLICATION_JSON_VALUE)
public class StockMovementController {
	@Autowired
	private MovementMapper movMapper;
	
	@Autowired
	private LotMapper lotMapper;
	
	@Autowired
	private MovBrowserManager movManager;
	
	@Autowired
	private MovStockInsertingManager movInsertingManager;
	
	@Autowired
	private MedicalBrowsingManager medicalManager;
	
	/**
	 * Insert a list of charging {@link Movement}s and related {@link Lot}s
	 * 
	 * @param movementDTOs - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * 		   if {@link null}, each movements must have a different referenceNumber 
	 * @return 
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/stockmovements/charge", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMultipleChargingMovements(@RequestBody List<MovementDTO> movementDTOs, 
			@RequestParam(name="ref", required=true) String referenceNumber) throws OHServiceException {
		ArrayList<Movement> movements = new ArrayList<Movement>();
		movMapper.map2ModelList(movementDTOs).forEach(mov -> {
			movements.add(mov);
		});
		boolean done = movInsertingManager.newMultipleChargingMovements(movements, referenceNumber);
		return ResponseEntity.status(HttpStatus.CREATED).body(done);
	}
	
	/**
	 * Insert a list of discharging {@link Movement}s
	 * 
	 * @param movementDTOs - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * 		   if {@link null}, each movements must have a different referenceNumber 
	 * @return 
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/stockmovements/discharge", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMultipleDischargingMovements(@RequestBody List<MovementDTO> movementDTOs, 
			@RequestParam(name="ref", required=true) String referenceNumber) throws OHServiceException {
		ArrayList<Movement> movements = new ArrayList<Movement>();
		movMapper.map2ModelList(movementDTOs).forEach(mov -> {
			movements.add(mov);
		});
		boolean done = movInsertingManager.newMultipleDischargingMovements(movements, referenceNumber);
		return ResponseEntity.status(HttpStatus.CREATED).body(done);
	}
	
	/**
	 * Retrieves all the {@link Movement}s.
	 * @return the retrieved movements.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/stockmovements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementDTO>> getMovements() throws OHServiceException { 
		List<Movement> movements = movManager.getMovements();
		return collectResults(movements);
	}
	
	/**
	 * Retrieves all the movement associated to the specified {@link Ward}.
	 * @param wardId
	 * @param dateFrom
	 * @param dateTo
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/stockmovements/filter/v1", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementDTO>> getMovements(
			@RequestParam("ward_id") String wardId,
			@RequestParam("from") Date dateFrom,
			@RequestParam("to") Date dateTo) throws OHServiceException {
		GregorianCalendar dateFrom_ = new GregorianCalendar();
		dateFrom_.setTime(dateFrom);
		GregorianCalendar dateTo_ = new GregorianCalendar();
		dateTo_.setTime(dateTo);
		List<Movement> movements = movManager.getMovements(wardId, dateFrom_, dateTo_);
		return collectResults(movements);
	}
	
	/**
	 * Retrieves all the movement associated to the specified reference number.
	 * @param refNo
	 * @return the retrieved movements
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/stockmovements/{ref}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementDTO>> getMovements(@PathVariable("ref") String refNo) throws OHServiceException {
		List<Movement> movements = movManager.getMovementsByReference(refNo);
		return collectResults(movements);
	}
	
	/**
	 * Retrieves all the {@link Movement}s with the specified criteria.
	 * @param medicalCode
	 * @param medicalType
	 * @param wardId
	 * @param movType
	 * @param movFrom
	 * @param movTo
	 * @param lotPrepFrom
	 * @param lotPrepTo
	 * @param lotDueFrom
	 * @param lotDueTo
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/stockmovements/filter/v2", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementDTO>> getMovements(
			@RequestParam(name="med_code", required=false) Integer medicalCode,
			@RequestParam(name="med_type", required=false) String medicalType,
			@RequestParam(name="ward_id", required=false) String wardId,
			@RequestParam(name="mov_type", required=false) String movType,
			@RequestParam(name="mov_from", required=false) Date movFrom,
			@RequestParam(name="mov_to", required=false) Date movTo,
			@RequestParam(name="lot_prep_from", required=false) Date lotPrepFrom,
			@RequestParam(name="lot_prep_to", required=false) Date lotPrepTo,
			@RequestParam(name="lot_due_from", required=false) Date lotDueFrom,
			@RequestParam(name="lot_due_to", required=false) Date lotDueTo) throws OHServiceException {
		GregorianCalendar movFrom_ = null;
		if(movFrom != null) {
			movFrom_ = new GregorianCalendar();
			movFrom_.setTime(movFrom);
		}
		GregorianCalendar movTo_ = null;
		if(movTo != null) {
			movTo_ = new GregorianCalendar();
			movTo_.setTime(movTo);
		}
		GregorianCalendar lotPrepFrom_ = null;
		if(lotPrepFrom != null) {
			lotPrepFrom_ = new GregorianCalendar();
			lotPrepFrom_.setTime(lotPrepFrom);
		}
		GregorianCalendar lotPrepTo_ = null;
		if(lotPrepTo != null) {
			lotPrepTo_ = new GregorianCalendar();
			lotPrepTo_.setTime(lotPrepTo);
		}
		GregorianCalendar lotDueFrom_ = null;
		if(lotDueFrom != null) {
			lotDueFrom_ = new GregorianCalendar();
			lotDueFrom_.setTime(lotDueFrom);
		}
		GregorianCalendar lotDueTo_ = null;
		if(lotDueTo != null) {
			lotDueTo_ = new GregorianCalendar();
			lotDueTo_.setTime(lotDueTo);
		}
		
		List<Movement> movements = movManager.getMovements(medicalCode, medicalType, wardId, movType, movFrom_, movTo_, lotPrepFrom_, lotPrepTo_, lotDueFrom_, lotDueTo_);
		return collectResults(movements);
	}
	
	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}
	 * @param medCode
	 * @return the retrieved lots.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/stockmovements/lot/{med_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LotDTO>> getLotByMedical(@PathVariable("med_code") int medCode) throws OHServiceException {
		Medical med = medicalManager.getMedical(medCode);
		if(med == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		List<Lot> lots = movInsertingManager.getLotByMedical(med);
		List<LotDTO> mappedLots = lotMapper.map2DTOList(lots);
		if(mappedLots.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedLots);
		} else {
			return ResponseEntity.ok(mappedLots);
		}
	}
	
	/**
	 * Checks if the provided quantity is under the medical limits. 
	 * @param medCode
	 * @param specifiedQuantity
	 * @return <code>true</code> if is under the limit, false otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/stockmovements/critical/check", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> alertCriticalQuantity(
			@RequestParam("med_code") int medCode,
			@RequestParam("qty") int specifiedQuantity) throws OHServiceException {
		Medical med = medicalManager.getMedical(medCode);
		if(med == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(movInsertingManager.alertCriticalQuantity(med, specifiedQuantity));
	}
	
	private ResponseEntity<List<MovementDTO>> collectResults(List<Movement> movements) {
		List<MovementDTO> mappedMovements = movMapper.map2DTOList(movements);
		if(mappedMovements.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovements);
		} else {
			return ResponseEntity.ok(mappedMovements);
		}
	}
}
