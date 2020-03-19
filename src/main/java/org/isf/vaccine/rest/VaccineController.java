package org.isf.vaccine.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class VaccineController {

    private final Logger logger = LoggerFactory.getLogger(VaccineController.class);

    @Autowired
    protected VaccineBrowserManager vaccineManager;

    public VaccineController(VaccineBrowserManager vaccineManager) {
        this.vaccineManager = vaccineManager;
    }

    @GetMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccines() throws OHServiceException {
        logger.info("Get vaccines");
        ArrayList<Vaccine> vaccines = vaccineManager.getVaccine();
        List<VaccineDTO> listVaccines = vaccines.stream().map(it -> getObjectMapper().map(it, VaccineDTO.class)).collect(Collectors.toList());
        if (listVaccines.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    @GetMapping(value = "/vaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccines(@PathVariable String code) throws OHServiceException {
        logger.info("Get vaccine by code:" + code);
        ArrayList<Vaccine> vaccines = vaccineManager.getVaccine(code);
        List<VaccineDTO> listVaccines = vaccines.stream().map(it -> getObjectMapper().map(it, VaccineDTO.class)).collect(Collectors.toList());
        if (listVaccines.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    @PostMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> newVaccine(@RequestBody VaccineDTO newVaccine) throws OHServiceException {
        logger.info("Create vaccine: " + newVaccine.toString());
        boolean isCreated = vaccineManager.newVaccine(getObjectMapper().map(newVaccine, Vaccine.class));
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newVaccine.getCode());
    }

    @PutMapping(value = "/vaccines/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateVaccine(@RequestBody VaccineDTO updateVaccine) throws OHServiceException {
        logger.info("Update vaccine: " + updateVaccine.toString());
        boolean isUpdated = vaccineManager.updateVaccine(getObjectMapper().map(updateVaccine, Vaccine.class));
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(updateVaccine.getCode());

    }

    @DeleteMapping(value = "/vaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteVaccine(@RequestBody VaccineDTO vaccineToDelete) throws OHServiceException {
        logger.info("Delete vaccine: " + vaccineToDelete.toString());
        boolean isDeleted = vaccineManager.deleteVaccine(getObjectMapper().map(vaccineToDelete, Vaccine.class));
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not deleted!", OHSeverityLevel.ERROR));
        }
        return (ResponseEntity) ResponseEntity.ok(isDeleted);
    }

    @GetMapping(value = "/vaccines/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkVaccineCode(@PathVariable String code) throws OHServiceException {
        logger.info("Check vaccine code: " + code);
        boolean check = vaccineManager.codeControl(code);
        return ResponseEntity.ok(check);
    }
}
