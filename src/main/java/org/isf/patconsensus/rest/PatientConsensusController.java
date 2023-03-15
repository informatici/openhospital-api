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
package org.isf.patconsensus.rest;

import org.isf.patconsensus.dto.PatientConsensusDTO;
import org.isf.patconsensus.manager.PatientConsensusBrowserManager;
import org.isf.patconsensus.mapper.PatientConsensusMapper;
import org.isf.patient.dto.PatientDTO;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value="/patient-consensus",produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class PatientConsensusController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatientConsensusController.class);

	@Autowired
	protected PatientConsensusBrowserManager manager;

	@Autowired
	protected PatientConsensusMapper mapper;

	@GetMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PatientConsensusDTO> updatePatient(@PathVariable Integer patientId) throws OHServiceException {
		LOGGER.info("Retrieving patient consensus: {}", patientId);
		//TODO
        return ResponseEntity.ok(null);
	}

	@PutMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PatientDTO> updatePatientConsensus(@PathVariable Integer patientId, @RequestBody PatientConsensusDTO patientConsensus) throws OHServiceException {
		LOGGER.info("Update patient consensus by id: {}", patientId);
        return ResponseEntity.ok(null);
	}


	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PatientDTO> storePatientConsensus( @RequestBody PatientConsensusDTO patientConsensus) throws OHServiceException {
		LOGGER.info("create or update patient consensus");
        return ResponseEntity.ok(null);
	}


	@DeleteMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deletePatientConsensus(@PathVariable Integer patientId) throws OHServiceException {
		LOGGER.info("Delete patient consensus by patient id: {}", patientId);
        return ResponseEntity.ok(true);
    }

}
