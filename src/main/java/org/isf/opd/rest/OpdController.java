package org.isf.opd.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.opd.dto.OpdDTO;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class OpdController extends OHApiAbstractController<Opd, OpdDTO> {

    private static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
    protected OpdBrowserManager opdManager;

    private final Logger logger = LoggerFactory.getLogger(OpdController.class);

    public OpdController(OpdBrowserManager opdBrowserManager) {
        this.opdManager = opdBrowserManager;
    }

    @PostMapping(value = "/opds", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> newOpd(@RequestBody OpdDTO newOpd) throws OHServiceException {
        logger.info(String.format("Create opd for patient [%d]", newOpd.getPatient().getCode()));
        Boolean createOpd = opdManager.newOpd(toModel(newOpd));
        return ResponseEntity.status(HttpStatus.CREATED).body(createOpd);
    }

    @GetMapping(value = "/opds/{patientcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OpdDTO>> getOpds(@PathVariable Integer patientcode) throws OHServiceException {
        logger.info(String.format("Get opds by patientcode [%d]", patientcode));
        ArrayList<Opd> opds = opdManager.getOpdList(patientcode);
        List<OpdDTO> opdDTOS = toDTOList(opds);
        if (opdDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOS);
        } else {
            return ResponseEntity.ok(opdDTOS);
        }
    }

    @GetMapping(value = "/opds/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OpdDTO>> searchOpds(
            @RequestParam(value = "diseaseTypeCode", required = false, defaultValue = "") String diseaseTypeCode,
            @RequestParam(value = "diseaseCode", required = false, defaultValue = "") String diseaseCode,
            @RequestParam(value = "dateFrom", required = false) GregorianCalendar dateFrom,
            @RequestParam(value = "dateTo", required = false) GregorianCalendar dateTo,
            @RequestParam(value = "ageFrom", required = false, defaultValue = "0") Integer ageFrom,
            @RequestParam(value = "ageTo", required = false, defaultValue = "100") Integer ageTo,
            @RequestParam(value = "sex", required = false, defaultValue = "A") char sex,
            @RequestParam(value = "newPatient", required = false, defaultValue = "R") char newPatient)  throws OHServiceException {
        logger.info(String.format("Search opds by diseaseTypeCode [%s], diseaseCode [%s], dateFrom [%s], dateTo [%s], ageFrom [%d], ageTo [%d], sex [%c], newPatient [%c]"
                , diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient));
        if (dateFrom == null) {
            dateFrom = new GregorianCalendar();
            dateFrom.add(Calendar.DAY_OF_MONTH, - 7);
        }
        if (dateTo == null) {
            dateTo = new GregorianCalendar();
        }
        ArrayList<Opd> opds = opdManager.getOpd(diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
        List<OpdDTO> opdDTOS = toDTOList(opds);
        if (opdDTOS.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(opdDTOS);
        } else {
            return ResponseEntity.ok(opdDTOS);
        }
    }

    @Override
    protected Class<OpdDTO> getDTOClass() {
        return OpdDTO.class;
    }

    @Override
    protected Class<Opd> getModelClass() {
        return Opd.class;
    }
}
