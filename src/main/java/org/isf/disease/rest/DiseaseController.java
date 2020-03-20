package org.isf.disease.rest;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value="/diseases",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="basicAuth")})
public class DiseaseController {
	@Autowired
	protected DiseaseBrowserManager diseaseManager;

	private final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

	public DiseaseController(DiseaseBrowserManager diseaseManager) {
		this.diseaseManager = diseaseManager;
	}
	
	@GetMapping(value = "/diseases/opd", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesOpd() throws OHServiceException {
        logger.info("Get opd diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseOpd();
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting opd diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/opd/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesOpdByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        logger.info("Get opd diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseOpd(typeCode);
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting opd diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/ipd/out", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdOut() throws OHServiceException {
        logger.info("Get ipd out diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdOut();
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting ipd out diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/ipd/out/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdOutByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        logger.info("Get ipd out diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdOut(typeCode);
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting ipd out diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/ipd/in", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdIn() throws OHServiceException {
        logger.info("Get ipd-in diseases");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdIn();
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting ipd-in diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/ipd/in/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseasesIpdInByCode(@PathVariable("typecode") String typeCode) throws OHServiceException {
        logger.info("Get ipd-in diseases by type code");
	    List<Disease> diseases = diseaseManager.getDiseaseIpdIn(typeCode);
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting ipd-in diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/both", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseases() throws OHServiceException {
        logger.info("Get both ipd and opd diseases");
	    List<Disease> diseases = diseaseManager.getDisease();
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/both/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getDiseases(@PathVariable("typecode") String typeCode) throws OHServiceException {
        logger.info("Get both ipd and opd diseases by type code");
	    List<Disease> diseases = diseaseManager.getDisease(typeCode);
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting diseases by type code", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DiseaseDTO>> getAllDiseases() throws OHServiceException {
        logger.info("Get all diseases, deleted ones too");
	    List<Disease> diseases = diseaseManager.getDiseaseAll();
	    if(diseases != null) {
	    	return computeResponse(diseases);
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "error while getting all diseases", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "/diseases/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> getDiseaseByCode(@PathVariable("code") int code) throws OHServiceException {
        logger.info("Get disease by code");
	    Disease disease = diseaseManager.getDiseaseByCode(code);
	    if(disease != null) {
	    	return ResponseEntity.ok(getObjectMapper().map(disease, DiseaseDTO.class));
	    } else {
	    	throw new OHAPIException(new OHExceptionMessage(null, "no disease found with the specified code", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@PostMapping(value="/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> newDisease(@Valid @RequestBody DiseaseDTO diseaseDTO) throws OHServiceException {
		Disease disease = getObjectMapper().map(diseaseDTO, Disease.class);
		if(diseaseManager.codeControl(disease.getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "duplicated disease code", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		} else if(diseaseManager.descriptionControl(disease.getDescription(), disease.getType().getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "duplicated disease description for the same disease type", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(diseaseManager.newDisease(disease)) {
        	return ResponseEntity.status(HttpStatus.CREATED).body(diseaseDTO);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, "disease is not created!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PutMapping(value="/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiseaseDTO> updateDisease(@Valid @RequestBody DiseaseDTO diseaseDTO) throws OHServiceException {
		Disease disease = getObjectMapper().map(diseaseDTO, Disease.class);
		if(!diseaseManager.codeControl(disease.getCode())) {
			throw new OHAPIException(new OHExceptionMessage(null, "disease not found", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(diseaseManager.updateDisease(disease)) {
        	return ResponseEntity.ok(diseaseDTO);
        } else {
        	throw new OHAPIException(new OHExceptionMessage(null, "disease is not updated!", OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@DeleteMapping(value = "/diseases/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Boolean>> deleteDisease(@PathVariable("code") int code) throws OHServiceException {
		Disease disease = diseaseManager.getDiseaseByCode(code);
		if(disease != null) {
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			result.put("deleted", diseaseManager.deleteDisease(disease));
			return ResponseEntity.ok(result);
		} else {
			throw new OHAPIException(new OHExceptionMessage(null, 
					"no disease found with the specified code", 
					OHSeverityLevel.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private ResponseEntity<List<DiseaseDTO>> computeResponse(List<Disease> diseases) {
		List<DiseaseDTO> diseasesDTO = diseases
        		.stream()
        		.map(item -> getObjectMapper().map(item, DiseaseDTO.class))
        		.collect(Collectors.toList());
        if(diseasesDTO.size() == 0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseasesDTO);
        }else{
            return ResponseEntity.ok(diseasesDTO);
        }
	}

}
