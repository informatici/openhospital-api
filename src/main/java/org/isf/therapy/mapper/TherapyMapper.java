package org.isf.therapy.mapper;

import org.isf.shared.GenericMapper;
import org.isf.therapy.dto.TherapyDTO;
import org.isf.therapy.model.Therapy;
import org.springframework.stereotype.Component;

@Component
public class TherapyMapper extends GenericMapper<Therapy, TherapyDTO> {
	public TherapyMapper() {
		super(Therapy.class, TherapyDTO.class);
	}
}
