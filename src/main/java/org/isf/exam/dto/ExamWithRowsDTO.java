package org.isf.exam.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record ExamWithRowsDTO(
	@NotNull @Schema(name = "exam", description = "The exam to mutate") ExamDTO exam,
	@Schema(name = "rows", example = "['POSITIVE', 'NEGATIVE']", description = "Possible result for the exam(only for exams with procedure 1 and 2)") List<String> rows
) {

}
