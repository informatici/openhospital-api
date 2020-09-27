package org.isf.opd.mapper;

import org.isf.opd.dto.OpdDTO;
import org.isf.opd.model.Opd;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class OpdMapper extends GenericMapper<Opd, OpdDTO> {
	public OpdMapper() {
		super(Opd.class, OpdDTO.class);
	}
}