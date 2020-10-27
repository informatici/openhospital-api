package org.isf.exatype.mapper;

import org.isf.exatype.dto.ExamTypeDTO;
import org.isf.exatype.model.ExamType;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class ExamTypeMapper extends GenericMapper<ExamType, ExamTypeDTO> {

    public ExamTypeMapper() {
        super(ExamType.class, ExamTypeDTO.class);
    }
}