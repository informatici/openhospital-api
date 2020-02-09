package org.isf.disease.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value = "/diseases", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class DiseaseController {

    @Autowired
    protected DiseaseBrowserManager diseaseManager;

    private final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

    public DiseaseController(DiseaseBrowserManager diseaseBrowserManager) {
        this.diseaseManager = diseaseBrowserManager;
    }

    @GetMapping(value = "/diseases/diseasesOpd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseOpd() throws OHServiceException {
        logger.info("Get DiseaseOpd");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseOpd();
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseasesAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseAll() throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseaseOpd = diseaseManager.getDiseaseAll();
        List<DiseaseDTO> diseaseDTOS = diseaseOpd.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseasesOpd/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseOpd(@PathVariable String typecode) throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseaseOpd = diseaseManager.getDiseaseOpd(typecode);
        List<DiseaseDTO> diseaseDTOS = diseaseOpd.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseaseIpdOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdOut() throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdOut();
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseaseIpdOut/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdOut(@PathVariable String typecode) throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdOut(typecode);
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseaseIpdIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdIn() throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdIn();
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/diseaseIpdIn/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdIn(@PathVariable String typecode) throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdIn(typecode);
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/disease", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDisease() throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDisease();
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/disease/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDisease(@PathVariable String typecode) throws OHServiceException {
        logger.info("Get DiseaseAll");
        ArrayList<Disease> diseases = diseaseManager.getDisease(typecode);
        List<DiseaseDTO> diseaseDTOS = diseases.stream().map(it -> getObjectMapper().map(it, DiseaseDTO.class)).collect(Collectors.toList());
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiseaseDTO> getDisease(@PathVariable int code) throws OHServiceException {
        logger.info("Get DiseaseAll");
        Disease disease = diseaseManager.getDiseaseByCode(code);
        if (disease == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(getObjectMapper().map(disease, DiseaseDTO.class));
    }

}
