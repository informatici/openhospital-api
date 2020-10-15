package org.isf.hospital.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.hospital.dto.HospitalDTO;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.mapper.HospitalMapper;
import org.isf.hospital.model.Hospital;
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
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "/hospitals", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class HospitalController {

    private final Logger logger = LoggerFactory.getLogger(HospitalController.class);

    @Autowired
    private HospitalBrowsingManager hospitalBrowsingManager;
    @Autowired
    private HospitalMapper hospitalMapper;

    public HospitalController(HospitalBrowsingManager hospitalBrowsingManager, HospitalMapper hospitalMapper) {
        this.hospitalBrowsingManager = hospitalBrowsingManager;
        this.hospitalMapper = hospitalMapper;
    }

    @PutMapping(value = "/hospitals/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateHospital(@PathVariable String code, @RequestBody HospitalDTO hospitalDTO) throws OHServiceException {

        if (!hospitalDTO.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Hospital code mismatch", OHSeverityLevel.ERROR));
        }
        if (hospitalBrowsingManager.getHospital().getCode() == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Hospital not Found!", OHSeverityLevel.WARNING));
        }

        Hospital hospital = hospitalMapper.map2Model(hospitalDTO);
        hospitalBrowsingManager.updateHospital(hospital);

        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/hospitals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HospitalDTO> getHospital() throws OHServiceException {
        Hospital hospital = hospitalBrowsingManager.getHospital();

        if (hospital == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(hospitalMapper.map2DTO(hospital));
        }
    }

    @GetMapping(value = "/hospitals/currencyCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHospitalCurrencyCode() throws OHServiceException {
        String hospitalCurrencyCod = hospitalBrowsingManager.getHospitalCurrencyCod();

        if (hospitalCurrencyCod == null || hospitalCurrencyCod.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(hospitalCurrencyCod);
        }
    }

}
