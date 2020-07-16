package org.isf.therapy.mapper;

import org.isf.shared.GenericMapper;
import org.isf.therapy.dto.TherapyRowDTO;
import org.isf.therapy.model.TherapyRow;
import org.springframework.stereotype.Component;

@Component
public class TherapyRowMapper extends GenericMapper<TherapyRow, TherapyRowDTO> {
	public TherapyRowMapper() {
		super(TherapyRow.class, TherapyRowDTO.class);
	}
}
