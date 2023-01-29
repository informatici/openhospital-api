/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.opd.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.opd.dto.OpdDTO;
import org.isf.opd.model.Opd;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.shared.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpdMapper extends GenericMapper<Opd, OpdDTO> {
	
	public OpdMapper() {
		super(Opd.class, OpdDTO.class);
	}

	@Autowired
	protected PatientBrowserManager patientManager = new PatientBrowserManager();
	
	@Override
	public OpdDTO map2DTO(Opd fromObj) {
		OpdDTO opdDTO = super.map2DTO(fromObj);
		if (fromObj.getPatient() != null) {
			opdDTO.setPatientCode(fromObj.getPatient().getCode());
			opdDTO.setPatientName(fromObj.getFullName());
			if (fromObj.getAge() == 0 ) {
				opdDTO.setAgeType("d0");
			}
			if (fromObj.getAge() > 0 && fromObj.getAge() <= 5) {
				opdDTO.setAgeType("d1");
			}
			if (fromObj.getAge() > 5 && fromObj.getAge() <= 12) {
				opdDTO.setAgeType("d2");
			}
			if (fromObj.getAge() > 12 && fromObj.getAge() <= 24) {
				opdDTO.setAgeType("d3");
			}
			if (fromObj.getAge() > 24 && fromObj.getAge() <= 59) {
				opdDTO.setAgeType("d4");
			}
			if (fromObj.getAge() > 59 && fromObj.getAge() < 200) {
				opdDTO.setAgeType("d5");
			}
		}
		return opdDTO;

	}
	
	@Override
	public List<OpdDTO> map2DTOList(List<Opd> list) {
		return list.stream().map(it -> map2DTO(it)).collect(Collectors.toList());
	}

	@Override
	public List<Opd> map2ModelList(List<OpdDTO> list) {
		return list.stream().map(it -> map2Model(it)).collect(Collectors.toList());
	}
}