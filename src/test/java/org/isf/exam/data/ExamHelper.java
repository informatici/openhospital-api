/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.exam.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.isf.exam.dto.ExamDTO;
import org.isf.exam.dto.ExamWithRowsDTO;
import org.isf.exatype.dto.ExamTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * Helper class to generate DTOs and Entities for users endpoints test
 * @author Silevester D.
 * @since 1.15
 */
public class ExamHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExamHelper.class);

	private static final ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new ParameterNamesModule())
		.registerModule(new Jdk8Module())
		.registerModule(new JavaTimeModule());

	private static ExamTypeDTO generateExamType() {
		ExamTypeDTO examType = new ExamTypeDTO();
		examType.setCode("HB");
		examType.setDescription("1.Haematology");
		return examType;
	}
	public static ExamDTO generateExam() {
		return new ExamDTO("44.02", "1.2 MDR Lite", 1, "NORMAL", generateExamType());
	}

	public static List<ExamDTO> generateExamList(int count) {
		return IntStream.range(1, count + 1).mapToObj(index -> {
			ExamDTO exam = generateExam();
			exam.setCode(String.format("44.0%d", index));
			exam.setDescription(String.format("1.0%d MDR Lite 0%d", index, index));
			return exam;
		}).collect(Collectors.toList());
	}

	public static List<String> generateExamRows() {
		return List.of("NORMAL", "DANGER", "URI");
	}

	public static ExamWithRowsDTO generateExamWithRowsDTO() {
		return new ExamWithRowsDTO(generateExam(), ExamHelper.generateExamRows());
	}

	public static ExamWithRowsDTO generateExamWithRowsDTO(ExamDTO examDTO) {
		return new ExamWithRowsDTO(examDTO, ExamHelper.generateExamRows());
	}

	// TODO: to be moved in a general package?
	public static <T> String asJsonString(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error converting object to JSON", e);
			return null;
		}
	}
}
