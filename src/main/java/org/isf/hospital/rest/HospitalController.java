/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.hospital.rest;

import org.isf.hospital.dto.HospitalDTO;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.mapper.HospitalMapper;
import org.isf.hospital.model.Hospital;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/hospitals", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class HospitalController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HospitalController.class);

    @Autowired
    private HospitalBrowsingManager hospitalBrowsingManager;

    @Autowired
    private HospitalMapper hospitalMapper;

    public HospitalController(HospitalBrowsingManager hospitalBrowsingManager, HospitalMapper hospitalMapper) {
        this.hospitalBrowsingManager = hospitalBrowsingManager;
        this.hospitalMapper = hospitalMapper;
    }

    @PutMapping(value = "/hospitals/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HospitalDTO> updateHospital(@PathVariable String code, @RequestBody HospitalDTO hospitalDTO) throws OHServiceException {

        if (!hospitalDTO.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Hospital code mismatch", OHSeverityLevel.ERROR));
        }
        if (hospitalBrowsingManager.getHospital().getCode() == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Hospital not Found!", OHSeverityLevel.WARNING));
        }

        Hospital hospital = hospitalMapper.map2Model(hospitalDTO);
        hospital.setLock(hospitalDTO.getLock());
        Hospital hospi = hospitalBrowsingManager.updateHospital(hospital);

        return ResponseEntity.ok(hospitalMapper.map2DTO(hospi));
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
