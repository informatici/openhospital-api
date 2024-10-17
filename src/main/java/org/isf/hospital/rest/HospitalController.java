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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.hospital.rest;

import org.isf.hospital.dto.HospitalDTO;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.mapper.HospitalMapper;
import org.isf.hospital.model.Hospital;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Hospitals")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HospitalController {

    private final HospitalBrowsingManager hospitalBrowsingManager;

    private final HospitalMapper hospitalMapper;

    public HospitalController(HospitalBrowsingManager hospitalBrowsingManager, HospitalMapper hospitalMapper) {
        this.hospitalBrowsingManager = hospitalBrowsingManager;
        this.hospitalMapper = hospitalMapper;
    }

    @PutMapping("/hospitals/{code:.+}")
    public HospitalDTO updateHospital(
        @PathVariable String code, @RequestBody HospitalDTO hospitalDTO
    ) throws OHServiceException {
        if (!hospitalDTO.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage("Hospital code mismatch."));
        }

        if (hospitalBrowsingManager.getHospital().getCode() == null) {
            throw new OHAPIException(new OHExceptionMessage("Hospital not found."), HttpStatus.NOT_FOUND);
        }

        Hospital hospital = hospitalMapper.map2Model(hospitalDTO);
        hospital.setLock(hospitalDTO.getLock());

        return hospitalMapper.map2DTO(hospitalBrowsingManager.updateHospital(hospital));
    }

    @GetMapping("/hospitals")
    public HospitalDTO getHospital() throws OHServiceException {
        Hospital hospital = hospitalBrowsingManager.getHospital();

        if (hospital == null) {
            throw new OHAPIException(new OHExceptionMessage("Hospital not found."), HttpStatus.NOT_FOUND);
        }

        return hospitalMapper.map2DTO(hospital);
    }

    @GetMapping(value = "/hospitals/currencyCode", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getHospitalCurrencyCode() throws OHServiceException {
        String hospitalCurrencyCod = hospitalBrowsingManager.getHospitalCurrencyCod();

        if (hospitalCurrencyCod == null || hospitalCurrencyCod.isEmpty()) {
            throw new OHAPIException(new OHExceptionMessage("Hospital not found."), HttpStatus.NOT_FOUND);
        }

        return hospitalCurrencyCod;
    }
}
