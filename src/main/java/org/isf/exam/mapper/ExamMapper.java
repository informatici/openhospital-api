package org.isf.exam.mapper;

import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper extends GenericMapper<Exam, ExamDTO> {

    public ExamMapper() {
        super(Exam.class, ExamDTO.class);
    }
}