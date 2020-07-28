package org.isf.opd.rest;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.mapper.OpdMapper;
import org.isf.opd.model.Opd;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpdController {

	@Autowired
	protected OpdBrowserManager opdManager;
	
	@Autowired
	protected OpdMapper mapper;
	
	@Autowired
	protected DiseaseBrowserManager diseaseManager;

	@Autowired
	protected DiseaseMapper dmapper;
	
	private final Logger logger = LoggerFactory.getLogger(OpdController.class);

	public OpdController(OpdBrowserManager opdManager, OpdMapper opdmapper) {
		this.opdManager = opdManager;
		this.mapper = opdmapper;
	}

	/**
	 * create a new {@link Opd}
	 * @param opdDTO
	 * @return the code of {@link Opd} stored
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> newOpd(@RequestBody OpdDTO opdDTO) throws OHServiceException {
		int code = opdDTO.getCode();
		logger.info("store Out patient " + code);
		Opd opd = mapper.map2Model(opdDTO);
		opd.setDisease(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease().getCode())));
		if(opdDTO.getDisease2() != null) opd.setDisease2(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease2().getCode())));
		if(opdDTO.getDisease3() != null) opd.setDisease3(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease3().getCode())));
		boolean isCreated = opdManager.newOpd(opd);
		if (!isCreated) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(code);
	}

	/**
	 * Updates the specified {@link Opd}.
	 * @param opdDTO
	 * @return the code of updated {@link Opd}
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Integer> updateOpd(@RequestBody OpdDTO opdDTO)
			throws OHServiceException {
		logger.info("Update opds code:" + opdDTO.getCode());
		Opd opd = mapper.map2Model(opdDTO);
		opd.setDisease(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease().getCode())));
		if(opdDTO.getDisease2() != null) opd.setDisease2(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease2().getCode())));
		if(opdDTO.getDisease3() != null) opd.setDisease3(diseaseManager.getDiseaseByCode(Integer.parseInt(opdDTO.getDisease3().getCode())));
		Opd updatedOpd = opdManager.updateOpd(opd);
		return ResponseEntity.ok(updatedOpd.getCode());
	}

	/**
	 * get all {@link Opd}s of today or since one week
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdToDayOrWeek(@RequestParam(required=false) Boolean oneWeek) throws OHServiceException {
		logger.info("Get all today or since one week opd");
		if(oneWeek == null) {
			oneWeek = false;
		}		
		List<Opd> opds = opdManager.getOpd(oneWeek);
		List<OpdDTO> opdDTOs = mapper.map2DTOList(opds);
		if (opdDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);
		} else {
			return ResponseEntity.ok(opdDTOs);
		}
	}
	
	/**
	 * get all Opds within specified dates
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdByDates(@RequestParam String diseaseTypeCode, @RequestParam String diseaseCode,
			@RequestParam Date dateFrom, @RequestParam Date dateTo, @RequestParam int ageFrom, @RequestParam int ageTo, @RequestParam char sex,
			@RequestParam char newPatient) throws OHServiceException {
		logger.info("Get opd within specified dates");
		
		GregorianCalendar datefrom = new GregorianCalendar();
		GregorianCalendar dateto = new GregorianCalendar();
        dateto.setTime(dateTo);
        datefrom.setTime(dateFrom);
        
		List<Opd> opds = opdManager.getOpd(diseaseTypeCode, diseaseCode, datefrom, dateto, ageFrom,  ageTo, sex, newPatient);
		List<OpdDTO> opdDTOs = mapper.map2DTOList(opds);
		if (opdDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);
		} else {
			return ResponseEntity.ok(opdDTOs);
		}
	}
	
	/**
	 * get all {@link Opd}s associated to specified patient CODE
	 * @return a {@link List} of {@link Opd} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/patient/{pcode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OpdDTO>> getOpdByPatient(@PathVariable("pcode") int patientcode) throws OHServiceException {
		logger.info("Get opd associated to specified patient CODE:" + patientcode);
		List<Opd> opds = opdManager.getOpdList(patientcode);
		List<OpdDTO> opdDTOs = mapper.map2DTOList(opds);
		if (opdDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOs);
		} else {
			return ResponseEntity.ok(opdDTOs);
		}
	}

	/**
	 * Delete {@link Opd} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link Opd} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/opds/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOpd(@PathVariable int code) throws OHServiceException {
		logger.info("Delete Opd code:" + code);
		Opd toDelete = new Opd();
		toDelete.setCode(code);
		boolean isDeleted = opdManager.deleteOpd(toDelete);
		if (!isDeleted) {
			throw new OHAPIException(new OHExceptionMessage(null, "Opd is not deleted!", OHSeverityLevel.ERROR));
		}
		return (ResponseEntity<Boolean>) ResponseEntity.ok(isDeleted);
	}
	
	/**
	 * get the max progressive number within specified year or within current year if <code>0</code>.
	 * @return the max progressive number
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/ProgYear/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getProgYear(@PathVariable int year) throws OHServiceException {
		logger.info("Get progressive number within specified year");
		int yProg = opdManager.getProgYear(year);
		return ResponseEntity.ok(yProg);
	}
	
	/**
	 * get the last {@link Opd} in time associated with specified patient ID. 
	 * @return last Opd associated with specified patient ID or <code>null</code>
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/last/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OpdDTO> getLastOpd(@PathVariable int code) throws OHServiceException {
		logger.info("Get the last opp for patien code:" + code);
		Opd lastOpd = opdManager.getLastOpd(code);
		return ResponseEntity.ok(mapper.map2DTO(lastOpd));
	}
	
	/**
	 * Check if the given <param>opdNum<param> does already exist for the give <param>year<param>
	 * @return <code>true<code> if the given number exists in year, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/opds/check/progyear", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isExistOpdNum(@RequestParam int opdNum, @RequestParam int year) throws OHServiceException {
		logger.info("check if progYear:" + opdNum + " already exist for year :" + year);
		Boolean isExist = opdManager.isExistOpdNum(opdNum, year);
		return ResponseEntity.ok(isExist);
	}

}
