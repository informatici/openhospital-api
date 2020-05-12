package org.isf.visits.mapper;

import org.isf.shared.GenericMapper;
import org.isf.visits.dto.VisitDTO;
import org.isf.visits.model.Visit;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper extends GenericMapper<Visit, VisitDTO> {
	public VisitMapper() {
		super(Visit.class, VisitDTO.class);
	}
}