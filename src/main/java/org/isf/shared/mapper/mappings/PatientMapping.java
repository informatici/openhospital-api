package org.isf.shared.mapper.mappings;

import org.isf.patient.dto.PatientDTO;
import org.isf.patient.model.Patient;
import org.modelmapper.ModelMapper;

public class PatientMapping {

	public static void addMapping(ModelMapper modelMapper) {

//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//		modelMapper.getConfiguration().setAmbiguityIgnored(false);
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.typeMap(Patient.class, PatientDTO.class).addMappings(mapper -> {
			mapper.<Boolean> map(src -> src.getPatientConsensus().isAdministrativeFlag(), PatientDTO::setConsensusAdministrativeFlag);
			mapper.<Boolean> map(src -> src.getPatientConsensus().isConsensusFlag(), PatientDTO::setConsensusFlag);
			mapper.<Boolean> map(src -> src.getPatientConsensus().isServiceFlag(), PatientDTO::setConsensusServiceFlag);
			mapper.<Byte[]> map(src -> src.getPatientProfilePhoto().getPhoto(), (dto, value)->dto.setBlobPhoto(value));
		});

		modelMapper.typeMap(PatientDTO.class, Patient.class).addMappings(mapper -> {
			mapper.<Boolean> map(src -> src.isConsensusAdministrativeFlag(), (db, value) -> db.getPatientConsensus().setAdministrativeFlag(value));
			mapper.<Boolean> map(src -> src.isConsensusFlag(), (db, value) -> db.getPatientConsensus().setConsensusFlag(value));
			mapper.<Boolean> map(src -> src.isConsensusServiceFlag(), (db, value) -> db.getPatientConsensus().setServiceFlag(value));
//			mapper.<Byte[]>map(src->src.getBlobPhoto(), (db, value) -> db.getPatientProfilePhoto().setPhoto(value));
		});

//		modelMapper.validate();
	}
}
