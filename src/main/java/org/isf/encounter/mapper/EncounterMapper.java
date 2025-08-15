package org.isf.encounter.mapper;

import java.util.List;

import org.isf.encounter.dto.EncounterDTO;
import org.isf.encouter.model.Encounter;
import org.isf.encouter.model.EncounterStatus;
import org.isf.shared.GenericMapper;

public class EncounterMapper extends GenericMapper<Encounter, EncounterDTO> {

	public EncounterMapper() {
		super(Encounter.class, EncounterDTO.class);
	}

	@Override
	public EncounterDTO map2DTO(Encounter fromObj) {
		EncounterDTO encounterDTO = super.map2DTO(fromObj);
		encounterDTO.setStatus(fromObj.getStatus().toString());
		return encounterDTO;
	}
	
	@Override
	public Encounter map2Model(EncounterDTO toObj) {
		Encounter encounter = super.map2Model(toObj);
		encounter.setStatus(EncounterStatus.valueOf(toObj.getStatus()));
		return encounter;
	}
}
