package org.isf.admtype.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Not used anymore
 *
 * @author antonio
 */
@Getter
@Setter
public class AdmissionTypeDTO {
    private String code;
    @NotNull
    private String description;
}
