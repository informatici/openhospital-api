package org.isf.medicalstockward.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.dto.MedicalWardDTO;
import org.isf.medicalstockward.dto.MovementWardDTO;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.mapper.MedicalWardMapper;
import org.isf.medicalstockward.mapper.MovementWardMapper;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
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

@RestController
@Api(value = "/medicalstockward", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicalStockWardController {
	private final Logger logger = LoggerFactory.getLogger(MedicalStockWardController.class);
	
	@Autowired
	private MedicalWardMapper medicalWardMapper;
	
	@Autowired 
	private MovementWardMapper movementWardMapper;
	
	@Autowired
	private MovWardBrowserManager movWardBrowserManager;
	
	@Autowired
	private MedicalBrowsingManager medicalManager;
	
	@Autowired
	private WardBrowserManager wardManager;
	
	/**
	 * Gets all the {@link MedicalWard}s associated to the specified ward.
	 * @param wardId the ward id.
	 * @return the retrieved {@link MedicalWard}s.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicalstockward/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MedicalWardDTO>> getMedicalsWard(@PathVariable("ward_code") char wardId) throws OHServiceException {
		List<MedicalWard> medWards = movWardBrowserManager.getMedicalsWard(wardId, true); //FIXME: provide provision for boolean ,false?
		List<MedicalWardDTO> mappedMedWards = medicalWardMapper.map2DTOList(medWards);
		if(mappedMedWards.isEmpty()) {
			logger.info("No medical found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMedWards);
		} else {
			logger.info("Found " + mappedMedWards.size() + " medicals");
			return ResponseEntity.ok(mappedMedWards);
		}
	}
	
	/**
	 * Gets the current quantity for the specified {@link Medical} and specified {@link Ward}.
	 * @param ward - if {@code null} the quantity is counted for the whole hospital
	 * @param medical - the {@link Medical} to check.
	 * @return the total quantity.
	 * @throws OHServiceException if an error occurs retrieving the quantity.
	 */
	@GetMapping(value = "/medicalstockward/current/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getCurrentQuantityInWard(
			@PathVariable("ward_code") String wardId, 
			@RequestParam("med_id") int medicalId) throws OHServiceException {
		Medical medical = medicalManager.getMedical(medicalId);
		if(medical == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Medical not found!", OHSeverityLevel.ERROR));
		}
		List<Ward> wards = wardManager.getWards().stream().filter(w -> w.getCode().equals(wardId)).collect(Collectors.toList());
		if(wards == null || wards.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "Ward not found!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(movWardBrowserManager.getCurrentQuantityInWard(wards.get(0), medical));
	}
	
	/**
	 * Gets all the {@link MovementWard}s.
	 * @return all the retrieved movements ward.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getMovementWard() throws OHServiceException {
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movWardBrowserManager.getMovementWard());
		if(mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		} else {
			return ResponseEntity.ok(mappedMovs);
		}
	}
	
	/**
	 * Gets all the movement ward with the specified criteria.
	 * @param wardId the ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return all the retrieved movements.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicalstockward/movements/{ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getMovementWard(
			@PathVariable("ward_code") String wardId, 
			@RequestParam("from") Date dateFrom, 
			@RequestParam("to") Date dateTo) throws OHServiceException {
		GregorianCalendar dateFrom_ = null;
		if(dateFrom != null) {
			dateFrom_ = new GregorianCalendar();
			dateFrom_.setTime(dateFrom);
		}
		
		GregorianCalendar dateTo_ = null;
		if(dateTo != null) {
			dateTo_ = new GregorianCalendar();
			dateTo_.setTime(dateTo);
		}
		
		List<MovementWard> movs = movWardBrowserManager.getMovementWard(wardId, dateFrom_, dateTo_);
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movs);
		if(mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		} else {
			return ResponseEntity.ok(mappedMovs);
		}
	}
	
	/**
	 * Gets all the movement ward with the specified criteria.
	 * @param idwardTo the target ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return all the retrieved movements.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/medicalstockward/movements/to/{target_ward_code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovementWardDTO>> getWardMovementsToWard(
			@PathVariable("target_ward_code") String idwardTo, 
			@RequestParam("from") Date dateFrom, 
			@RequestParam("to") Date dateTo) throws OHServiceException {
		GregorianCalendar dateFrom_ = null;
		if(dateFrom != null) {
			dateFrom_ = new GregorianCalendar();
			dateFrom_.setTime(dateFrom);
		}
		
		GregorianCalendar dateTo_ = null;
		if(dateTo != null) {
			dateTo_ = new GregorianCalendar();
			dateTo_.setTime(dateTo);
		}
		
		List<MovementWard> movs = movWardBrowserManager.getWardMovementsToWard(idwardTo, dateFrom_, dateTo_);
		List<MovementWardDTO> mappedMovs = movementWardMapper.map2DTOList(movs);
		if(mappedMovs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMovs);
		} else {
			return ResponseEntity.ok(mappedMovs);
		}
	}
	
	/**
	 * Persists the specified movement.
	 * @param newMovementDTO the movement to persist.
	 * @return <code>true</code> if the movement has been persisted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMovementWard(@Valid @RequestBody MovementWardDTO newMovementDTO) throws OHServiceException {
		MovementWard newMovement = movementWardMapper.map2Model(newMovementDTO);
		boolean isPersisted = movWardBrowserManager.newMovementWard(newMovement);
		if (!isPersisted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Movement ward is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Persists the specified movements.
	 * @param newMovementDTOs the movements to persist.
	 * @return <code>true</code> if the movements have been persisted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/medicalstockward/movements/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> newMovementWard(@Valid @RequestBody List<MovementWardDTO> newMovementDTOs) throws OHServiceException {
		ArrayList<MovementWard> newMovements = new ArrayList<MovementWard>();
		movementWardMapper.map2ModelList(newMovementDTOs).forEach(mov -> {
			newMovements.add(mov);
		});
		boolean arePersisted = movWardBrowserManager.newMovementWard(newMovements);
		if (!arePersisted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Movements ward are not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Updates the specified {@link MovementWard}.
	 * @param movementWardDTO the movement ward to update.
	 * @return <code>true</code> if the movement has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@PutMapping(value = "/medicalstockward/movements", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateMovementWard(@Valid @RequestBody MovementWardDTO movementWardDTO) throws OHServiceException {
		MovementWard movemenWard = movementWardMapper.map2Model(movementWardDTO);
		boolean isPresent = movWardBrowserManager.getMovementWard().stream().anyMatch(mov -> mov.getCode() == movemenWard.getCode());
		if(!isPresent) {
			throw new OHAPIException(new OHExceptionMessage(null, "Movement ward not found!", OHSeverityLevel.ERROR));
		} 
		
		boolean isUpdated = movWardBrowserManager.updateMovementWard(movemenWard);
		if(!isUpdated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Movement ward is not updated!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.ok(isUpdated);
	}
	
}
