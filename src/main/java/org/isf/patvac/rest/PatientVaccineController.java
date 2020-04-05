package org.isf.patvac.rest;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.patvac.manager.PatVacManager;
import org.isf.patvac.model.PatientVaccine;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PatientVaccineController extends OHApiAbstractController<PatientVaccine, PatientVaccineDTO> {

    @Autowired
    private PatVacManager patVacManager;

    private final Logger logger = LoggerFactory.getLogger(PatientVaccineController.class);

    @Autowired
    protected ModelMapper modelMapper;

    public PatientVaccineController(PatVacManager patVacManager, ModelMapper modelMapper) {
        super(modelMapper);
        this.patVacManager = patVacManager;
    }

    /**
     * returns all {@link PatientVaccine}s of today or one week ago
     *
     * @param minusOneWeek - if <code>true</code> return the last week
     * @return the list of {@link PatientVaccine}s
     * @throws OHServiceException
     */
    @ApiOperation(value = "returns all PatientVaccines of today or one week ago")
    @GetMapping(value="/patvac/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientVaccineDTO>> getPatientVaccine(boolean minusOneWeek) throws OHServiceException {
        logger.info(String.format("getPatientVaccine minusOneWeek [%b]", minusOneWeek));
        List<PatientVaccineDTO> patientVaccineDTOS = toDTOList(patVacManager.getPatientVaccine(minusOneWeek));
        return ResponseEntity.status(HttpStatus.FOUND).body(toDTOList(patientVaccineDTOS));
    }

    /**
     * returns all {@link PatientVaccine}s within <code>dateFrom</code> and
     * <code>dateTo</code>
     *
     * @param vaccineTypeCode
     * @param vaccineCode
     * @param dateFrom
     * @param dateTo
     * @param sex
     * @param ageFrom
     * @param ageTo
     * @return the list of {@link PatientVaccine}s
     * @throws OHServiceException
     */
    @GetMapping(value="/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientVaccineDTO>> getPatientVaccine(@RequestParam String vaccineTypeCode,
                                                     @RequestParam String vaccineCode,
                                                     @RequestParam GregorianCalendar dateFrom,
                                                     @RequestParam GregorianCalendar dateTo,
                                                     @RequestParam char sex,
                                                     @RequestParam int ageFrom,
                                                     @RequestParam int ageTo) throws OHServiceException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        logger.info(String.format("getPatientVaccine vaccineTypeCode [%s] vaccineCode [%s] dateFrom [%s] dateTo [%s] sex [%c] ageFrom [%d] ageTo [%d]", vaccineTypeCode, vaccineCode, sdf.format(dateFrom.getTime()), sdf.format(dateTo.getTime()), sex, ageFrom, ageTo));
        List<PatientVaccineDTO> patientVaccineDTOS = toDTOList(patVacManager.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo));
        return ResponseEntity.status(HttpStatus.FOUND).body(toDTOList(patientVaccineDTOS));
    }

    /**
     * inserts a {@link PatientVaccine} in the DB
     *
     * @param patVac - the {@link PatientVaccine} to insert
     * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newPatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("newPatientVaccine code [%d]", patVac.getCode()));
        return patVacManager.newPatientVaccine(toModel(patVac))
                ? ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * updates a {@link PatientVaccine}
     *
     * @param patVac - the {@link PatientVaccine} to update
     * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @PatchMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updatePatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("updatePatientVaccine code [%d]", patVac.getCode()));
        return patVacManager.updatePatientVaccine(toModel(patVac))
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * deletes a {@link PatientVaccine}
     *
     * @param patVac - the {@link PatientVaccine} to delete
     * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deletePatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("deletePatientVaccine code [%d]", patVac.getCode()));
        return patVacManager.deletePatientVaccine(toModel(patVac))
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(Boolean.TRUE)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
    }

    /**
     * Returns the max progressive number within specified year or within current year if <code>0</code>.
     *
     * @param year
     * @return <code>int</code> - the progressive number in the year
     * @throws OHServiceException
     */
    @ApiOperation(value="Returns the max progressive number within specified year or within current year if 0.")
    @GetMapping(value = "/patvac/prog-year", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getProgYear(@RequestParam int year) throws OHServiceException {
        logger.info(String.format("getProgYear year [%d]", year));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patVacManager.getProgYear(year));
    }

    @Override
    protected Class<PatientVaccineDTO> getDTOClass() {
        return PatientVaccineDTO.class;
    }

    @Override
    protected Class<PatientVaccine> getModelClass() {
        return PatientVaccine.class;
    }
}
