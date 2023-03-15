package org.isf.patconsensus.mapper;

import org.isf.patconsensus.dto.PatientConsensusDTO;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.shared.GenericMapper;

public class PatientConsensusMapper  extends GenericMapper<PatientConsensus, PatientConsensusDTO>{

	public PatientConsensusMapper(Class<PatientConsensus> sourceClass, Class<PatientConsensusDTO> destClass) {
		super(PatientConsensus.class, PatientConsensusDTO.class);
	}


}
