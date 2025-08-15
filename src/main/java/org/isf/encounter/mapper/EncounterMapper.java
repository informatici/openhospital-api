package org.isf.encounter.mapper;

import org.isf.encounter.dto.EncounterDTO;
import org.isf.encouter.model.Encounter;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class EncounterMapper extends GenericMapper<Encounter, EncounterDTO> {

	public EncounterMapper() {
		super(Encounter.class, EncounterDTO.class);
	}
}
