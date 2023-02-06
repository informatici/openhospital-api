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
package org.isf.accounting.data;

import java.util.List;

import org.isf.accounting.dto.BillDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.mapper.PatientMapper;
import org.isf.patient.model.Patient;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class BillDTOHelper {

	public static BillDTO setup(PatientMapper patientMapper) throws OHException {
		Patient patient = new TestPatient().setup(true);
		PatientDTO patientDTO = patientMapper.map2DTO(patient);
		BillDTO billDTO = new BillDTO();
		billDTO.setPatient(patientDTO);
		return billDTO;
	}

	public static BillDTO setup(Integer id, PatientMapper patientMapper) throws OHException {
		BillDTO billDTO = BillDTOHelper.setup(patientMapper);
		billDTO.setId(id);
		return billDTO;
	}

	public static String asJsonString(BillDTO billDTO) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String asJsonString(List<BillDTO> billDTOList) {
		try {
			return BillHelper.getObjectMapper().writeValueAsString(billDTOList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}