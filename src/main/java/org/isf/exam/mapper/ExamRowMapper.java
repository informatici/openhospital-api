package org.isf.exam.mapper;

import org.isf.exa.model.ExamRow;
import org.isf.exam.dto.ExamRowDTO;
import org.isf.shared.GenericMapper;
import org.springframework.stereotype.Component;

@Component
public class ExamRowMapper extends GenericMapper<ExamRow, ExamRowDTO> {

    public ExamRowMapper() {
        super(ExamRow.class, ExamRowDTO.class);
    }
}