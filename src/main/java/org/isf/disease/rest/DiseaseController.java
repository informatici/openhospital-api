package org.isf.disease.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.shared.rest.OHApiAbstractController;
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

@RestController
@Api(value = "/diseases", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class DiseaseController extends OHApiAbstractController<Disease, DiseaseDTO> {

    @Autowired
    protected DiseaseBrowserManager diseaseManager;

    private final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

    @GetMapping(value = "/diseasesAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseAll() throws OHServiceException {
        logger.info("getDiseaseAll");
        ArrayList<Disease> diseaseOpd = diseaseManager.getDiseaseAll();
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseaseOpd);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesOpd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseOpd() throws OHServiceException {
        logger.info("getDiseaseOpd");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseOpd();
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesOpd/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseOpdByTypeCode(@PathVariable String typecode) throws OHServiceException {
        logger.info("getDiseaseOpdByTypeCode");
        ArrayList<Disease> diseaseOpd = diseaseManager.getDiseaseOpd(typecode);
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseaseOpd);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesIpdOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdOut() throws OHServiceException {
        logger.info("getDiseaseIpdOut");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdOut();
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesIpdOut/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdOutByTypeCode(@PathVariable String typecode) throws OHServiceException {
        logger.info("getDiseaseIpdOutByTypeCode");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdOut(typecode);
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesIpdIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdIn() throws OHServiceException {
        logger.info("getDiseaseIpdIn");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdIn();
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseasesIpdIn/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseIpdInByTypeCode(@PathVariable String typecode) throws OHServiceException {
        logger.info("getDiseaseIpdInByTypeCode");
        ArrayList<Disease> diseases = diseaseManager.getDiseaseIpdIn(typecode);
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDisease() throws OHServiceException {
        logger.info("getDisease");
        ArrayList<Disease> diseases = diseaseManager.getDisease();
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/{typecode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiseaseDTO>> getDiseaseByTypeCode(@PathVariable String typecode) throws OHServiceException {
        logger.info("getDiseaseByTypeCode");
        ArrayList<Disease> diseases = diseaseManager.getDisease(typecode);
        List<DiseaseDTO> diseaseDTOS = toDTOList(diseases);
        if (diseaseDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(diseaseDTOS);
        } else {
            return ResponseEntity.ok(diseaseDTOS);
        }
    }

    @GetMapping(value = "/diseases/disease/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiseaseDTO> getDiseaseByCode(@PathVariable int code) throws OHServiceException {
        logger.info("getDiseaseByCode");
        Disease disease = diseaseManager.getDiseaseByCode(code);
        if (disease == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(toDTO(disease));
    }

    @Override
    protected Class<DiseaseDTO> getDTOClass() {
        return DiseaseDTO.class;
    }

    @Override
    protected Class<Disease> getModelClass() {
        return Disease.class;
    }
}
