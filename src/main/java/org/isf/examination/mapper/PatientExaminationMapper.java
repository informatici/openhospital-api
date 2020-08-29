package org.isf.examination.mapper;

import org.isf.examination.dto.PatientExaminationDTO;
import org.isf.examination.model.PatientExamination;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class PatientExaminationMapper extends GenericMapper<PatientExamination, PatientExaminationDTO> {

    public PatientExaminationMapper() {
        super(PatientExamination.class, PatientExaminationDTO.class);
    }
}
