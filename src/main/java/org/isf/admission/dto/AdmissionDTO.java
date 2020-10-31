/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.admission.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disease.dto.DiseaseDTO;
import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.operation.dto.OperationDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.pregtreattype.dto.PregnantTreatmentTypeDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author gildas
 *
 */
@Getter
@Setter
public class AdmissionDTO {

	@ApiModelProperty(notes = "admission key", example = "12", position = 1)
	private int id;

	@NotNull
	@ApiModelProperty(notes = "if admitted or not", example = "0", position = 2)
	private int admitted;

	@NotNull
	@ApiModelProperty(notes = "type of admission", example = "N", position = 3)
	private String type;

	@ApiModelProperty(notes = "ward", position = 4)
	private WardDTO ward;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year for each ward", example = "1", position = 5)
	private int yProg;

	@ApiModelProperty(notes = "patient", position = 6)
	private PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "admission date", position = 7)
	private Date admDate;

	@ApiModelProperty(notes = "admission type", position = 8)
	private AdmissionTypeDTO admType;

	@ApiModelProperty(notes = "FromHealthUnit", position = 9)
	private String FHU;

	@ApiModelProperty(notes = "disease in ", position = 10)
	private DiseaseDTO diseaseIn;

	@ApiModelProperty(notes = "disease out ", position = 11)
	private DiseaseDTO diseaseOut1;

	@ApiModelProperty(notes = "disease out ", position = 12)
	private DiseaseDTO diseaseOut2;

	@ApiModelProperty(notes = "disease out ",  position = 13)
	private DiseaseDTO diseaseOut3;

	@ApiModelProperty(notes = "operation ", position = 14)
	private OperationDTO operation;

	@ApiModelProperty(notes = "operation date", position = 15)
	private Date opDate;

	@ApiModelProperty(notes = "operation result value is 'P' or 'N' ", example = "N", position = 16)
	private String opResult;

	@ApiModelProperty(notes = "discharge date", position = 17)
	private Date disDate;

	@ApiModelProperty(notes = "disChargeType ", position = 18)
	private DischargeTypeDTO disType;

	@ApiModelProperty(notes = "free note", position = 19)
	private String note;

	@ApiModelProperty(notes = "transfusional unit", position = 20)
	private Float transUnit;

	@ApiModelProperty(notes = "visit date", position = 21)
	private Date visitDate;

	@ApiModelProperty(notes = "treatmentType ", position = 22)
	private PregnantTreatmentTypeDTO pregTreatmentType;

	@ApiModelProperty(notes = "delivery date", position = 23)
	private Date deliveryDate;

	@ApiModelProperty(notes = "delivery type", position = 24)
	private DeliveryTypeDTO deliveryType;

	@ApiModelProperty(notes = "delivery type ", position = 25)
	private DeliveryResultTypeDTO deliveryResult;

	@ApiModelProperty(notes = "weight", position = 26)
	private Float weight;

	private Date ctrlDate1;

	private Date ctrlDate2;

	private Date abortDate;

	@ApiModelProperty(notes = "weight", position = 30)
	private String userID;

	private int lock;
	
	private int hashCode = 0;

	@NotNull
	@ApiModelProperty(notes = "flag record deleted, values are 'Y' OR 'N' ", example = "N", position = 32)
	private String deleted;

	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

}
