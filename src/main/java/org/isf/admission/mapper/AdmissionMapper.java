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
package org.isf.admission.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.admission.dto.AdmissionDTO;
import org.isf.admission.model.Admission;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.disease.mapper.DiseaseMapper;
import org.isf.disease.model.Disease;
import org.isf.shared.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdmissionMapper extends GenericMapper<Admission, AdmissionDTO> {
	public AdmissionMapper() {
		super(Admission.class, AdmissionDTO.class);
	}
	@Autowired
	protected DiseaseMapper mapper = new DiseaseMapper();
	
	@Override
	public Admission map2Model(AdmissionDTO toObj) {

		Admission admission = super.map2Model(toObj);

		if (toObj.getDiseaseOut() != null) {
			Disease disease = mapper.map2Model(toObj.getDiseaseOut());
			admission.setDiseaseOut1(disease);
		}

		return admission;
	 }
	
	@Override
	public AdmissionDTO map2DTO(Admission toObj) {

		AdmissionDTO admission = super.map2DTO(toObj);

		if (toObj.getDiseaseOut1() != null) {
			DiseaseDTO disease = mapper.map2DTO(toObj.getDiseaseOut1());
			admission.setDiseaseOut(disease);
		}

		return admission;
	 }
	@Override
	public List<AdmissionDTO> map2DTOList(List<Admission> list) {
		return list.stream().map(it -> map2DTO(it)).collect(Collectors.toList());
	}

	@Override
	public List<Admission> map2ModelList(List<AdmissionDTO> list) {
		return list.stream().map(it -> map2Model(it)).collect(Collectors.toList());
	}
}
