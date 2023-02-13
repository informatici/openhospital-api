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
package org.isf.shared;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.dto.PatientSTATUS;
import org.isf.patient.model.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericMapper<SourceType, DestType> implements Mapper<SourceType, DestType> {

	@Autowired
	protected ModelMapper modelMapper;
	private Type sourceClass;
	private Type destClass;
	
	public GenericMapper(Class<SourceType> sourceClass, Class<DestType> destClass) {
		this.sourceClass = sourceClass;
		this.destClass = destClass;
	}

	@Override
	public DestType map2DTO(SourceType fromObj) {
		return modelMapper.map(fromObj, destClass);
	}

	@Override
	public SourceType map2Model(DestType toObj) {
		return modelMapper.map(toObj, sourceClass);
	}

	@Override
	public List<DestType> map2DTOList(List<SourceType> list) {
		return (List<DestType>) list.stream().map(it -> modelMapper.map(it, destClass)).collect(Collectors.toList());
	}

	@Override
	public List<SourceType> map2ModelList(List<DestType> list) {
		return (List<SourceType>) list.stream().map(it -> modelMapper.map(it, sourceClass)).collect(Collectors.toList());
	}

	public PatientDTO map2DTOWS(Patient fromObj, Boolean status) {
		// TODO Auto-generated method stub
		PatientDTO patientDTO = modelMapper.map(fromObj, destClass);
		
		if (status) {
			patientDTO.setStatus(PatientSTATUS.I);
		} else {
			patientDTO.setStatus(PatientSTATUS.O);
		}
		
		return patientDTO;
	}
}