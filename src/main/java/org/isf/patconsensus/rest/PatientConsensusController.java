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
package org.isf.patconsensus.rest;

import java.util.Optional;

import org.isf.patconsensus.dto.PatientConsensusDTO;
import org.isf.patconsensus.manager.PatientConsensusBrowserManager;
import org.isf.patconsensus.mapper.PatientConsensusMapper;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController()
@RequestMapping("/patientConsensus")
@Api(value = "/patientConsensus", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = { @Authorization(value = "apiKey") })
public class PatientConsensusController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PatientConsensusController.class);

	@Autowired
	protected PatientConsensusBrowserManager manager;

	@Autowired
	protected PatientConsensusMapper mapper;

	@GetMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PatientConsensusDTO> getPatientConsensus(@PathVariable Integer patientId) throws OHServiceException {
		LOGGER.info("Retrieving patient consensus: {}", patientId);
		Optional<PatientConsensus> patientConsensus = manager.getPatientConsensusByUserId(patientId);
		if (patientConsensus.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		PatientConsensusDTO patientDTO = mapper.map2DTO(patientConsensus.get());
		return ResponseEntity.ok(patientDTO);
	}

	@PutMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PatientConsensusDTO> updatePatientConsensus(@PathVariable Integer patientId, @RequestBody PatientConsensusDTO patientConsensus)
					throws OHServiceException {
		LOGGER.info("Update patient consensus by id: {}", patientId);
		if (!patientId.equals(patientConsensus.getPatientId())) {
			throw new OHAPIException(new OHExceptionMessage("Patient code mismatch."));
		}
		Optional<PatientConsensus> patConsensusOpt = this.manager.getPatientConsensusByUserId(patientId);
		if (patConsensusOpt.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage("PatientConsensus not found."));
		}
		PatientConsensus updatedPatienConsensustModel = mapper.map2Model(patientConsensus);
		updatedPatienConsensustModel.setId(this.manager.getPatientConsensusByUserId(patientId).get().getId());
		PatientConsensus patientConsensusUpdated = manager.updatePatientConsensus(updatedPatienConsensustModel);
		if (patientConsensusUpdated == null) {
			throw new OHAPIException(new OHExceptionMessage("PatientConsensus is not updated."));
		}
		PatientConsensusDTO patientConsensusDTO = mapper.map2DTO(patientConsensusUpdated);
		return ResponseEntity.ok(patientConsensusDTO);
	}

}
