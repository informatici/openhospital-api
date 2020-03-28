package org.isf.patvac.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.isf.patvac.dto.PatientVaccineDTO;
import org.isf.patvac.manager.PatVacManager;
import org.isf.patvac.model.PatientVaccine;
import org.isf.shared.rest.OHApiAbstractController;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@Api(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PatientVaccineController extends OHApiAbstractController<PatientVaccine, PatientVaccineDTO> {

    @Autowired
    private PatVacManager ioOperations;

    private final Logger logger = LoggerFactory.getLogger(PatientVaccineController.class);

    /**
     * returns all {@link PatientVaccine}s of today or one week ago
     *
     * @param minusOneWeek - if <code>true</code> return the last week
     * @return the list of {@link PatientVaccine}s
     * @throws OHServiceException
     */
    @ApiOperation(value = "returns all PatientVaccines of today or one week ago")
    @GetMapping(value="/patvac/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PatientVaccineDTO> getPatientVaccine(boolean minusOneWeek) throws OHServiceException {
        logger.info(String.format("getPatientVaccine minusOneWeek [%b]", minusOneWeek));
        return  toDTOList(ioOperations.getPatientVaccine(minusOneWeek));
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
    public List<PatientVaccineDTO> getPatientVaccine(@RequestParam String vaccineTypeCode,
                                                     @RequestParam String vaccineCode,
                                                     @RequestParam GregorianCalendar dateFrom,
                                                     @RequestParam GregorianCalendar dateTo,
                                                     @RequestParam char sex,
                                                     @RequestParam int ageFrom,
                                                     @RequestParam int ageTo) throws OHServiceException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        logger.info(String.format("getPatientVaccine vaccineTypeCode [%s] vaccineCode [%s] dateFrom [%s] dateTo [%s] sex [%c] ageFrom [%d] ageTo [%d]", vaccineTypeCode, vaccineCode, sdf.format(dateFrom.getTime()), sdf.format(dateTo.getTime()), sex, ageFrom, ageTo));
        return toDTOList(ioOperations.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo));
    }

    /**
     * inserts a {@link PatientVaccine} in the DB
     *
     * @param patVac - the {@link PatientVaccine} to insert
     * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean newPatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("newPatientVaccine code [%d]", patVac.getCode()));
        return ioOperations.newPatientVaccine(toModel(patVac));
    }

    /**
     * updates a {@link PatientVaccine}
     *
     * @param patVac - the {@link PatientVaccine} to update
     * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @PatchMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updatePatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("updatePatientVaccine code [%d]", patVac.getCode()));
        return ioOperations.updatePatientVaccine(toModel(patVac));
    }

    /**
     * deletes a {@link PatientVaccine}
     *
     * @param patVac - the {@link PatientVaccine} to delete
     * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/patvac", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deletePatientVaccine(@RequestBody PatientVaccineDTO patVac) throws OHServiceException {
        logger.info(String.format("deletePatientVaccine code [%d]", patVac.getCode()));
        return ioOperations.deletePatientVaccine(toModel(patVac));
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
    public int getProgYear(@RequestParam int year) throws OHServiceException {
        logger.info(String.format("getProgYear year [%d]", year));
        return ioOperations.getProgYear(year);
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
