package org.isf.therapy.mapper;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medicals.model.Medical;
import org.isf.shared.GenericMapper;
import org.isf.therapy.dto.TherapyRowDTO;
import org.isf.therapy.model.TherapyRow;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TherapyRowMapper extends GenericMapper<TherapyRow, TherapyRowDTO> {

	public TherapyRowMapper() {
		super(TherapyRow.class, TherapyRowDTO.class);
	}

	@Override
	public TherapyRow map2Model(TherapyRowDTO toObj) {
		TherapyRow therapyRow = super.map2Model(toObj);

		// map medical
		Medical medical = new Medical();
		medical.setCode(toObj.getMedicalId());
		therapyRow.setMedical(medical);

		return therapyRow;
	}

	@Override
	public TherapyRowDTO map2DTO(TherapyRow fromObj) {
		TherapyRowDTO therapyRowDTO = super.map2DTO(fromObj);

		// map medical
		therapyRowDTO.setMedicalId(fromObj.getMedical());

		return therapyRowDTO;
	}

	@Override
	public List<TherapyRowDTO> map2DTOList(List<TherapyRow> list) {
		return (List<TherapyRowDTO>) list.stream().map(it -> map2DTO(it)).collect(Collectors.toList());
	}

	@Override
	public List<TherapyRow> map2ModelList(List<TherapyRowDTO> list) {
		return (List<TherapyRow>) list.stream().map(it -> map2Model(it)).collect(Collectors.toList());
	}

}
