package org.isf.admission.dto;

import java.util.GregorianCalendar;

import javax.validation.constraints.NotNull;

import org.isf.admtype.dto.AdmissionTypeDTO;
import org.isf.disctype.dto.DischargeTypeDTO;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.dto.DeliveryResultTypeDTO;
import org.isf.dlvrtype.dto.DeliveryTypeDTO;
import org.isf.operation.dto.OperationDTO;
import org.isf.patient.dto.PatientDTO;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author gildas
 *
 */
public class AdmissionDTO {

	@ApiModelProperty(notes = "admission key", example = "12", position = 1)
	private int id;

	@NotNull
	@ApiModelProperty(notes = "if admitted or not", example = "0", position = 2)
	private int admitted;

	@NotNull
	@ApiModelProperty(notes = "type of admission", example = "malnutrition", position = 3)
	private String type;

	@NotNull
	@ApiModelProperty(notes = "ward key", position = 4)
	private WardDTO ward;

	@NotNull
	@ApiModelProperty(notes = "a progr. in year for each ward", example = "1", position = 5)
	private int yProg;

	@NotNull
	@ApiModelProperty(notes = "patient key", position = 6)
	private PatientDTO patient;

	@NotNull
	@ApiModelProperty(notes = "admission date", position = 7)
	private GregorianCalendar admDate;

	@NotNull
	@ApiModelProperty(notes = "admission type key", position = 8)
	private AdmissionTypeDTO admissionType;

	@ApiModelProperty(notes = "FromHealthUnit", position = 9)
	private String FHU;

	@ApiModelProperty(notes = "disease in key", example = "1", position = 10)
	private Disease diseaseIn;

	@ApiModelProperty(notes = "disease out key", example = "1", position = 11)
	private Disease diseaseOut1;

	@ApiModelProperty(notes = "disease out key", example = "1", position = 12)
	private Disease diseaseOut2;

	@ApiModelProperty(notes = "disease out key", example = "1", position = 13)
	private Disease diseaseOut3;

	@ApiModelProperty(notes = "operation key", example = "1", position = 14)
	private OperationDTO operation;

	@ApiModelProperty(notes = "operation date", position = 15)
	private GregorianCalendar opDate;

	@ApiModelProperty(notes = "operation result value is 'P' or 'N' ", example = "N", position = 16)
	private String opResult;

	@ApiModelProperty(notes = "discharge date", position = 17)
	private GregorianCalendar disDate;

	@ApiModelProperty(notes = "disChargeType key", position = 18)
	private DischargeTypeDTO disType;

	@ApiModelProperty(notes = "free note", position = 19)
	private String note;

	@ApiModelProperty(notes = "transfusional unit", position = 20)
	private Float transUnit;

	@ApiModelProperty(notes = "visite date", position = 21)
	private GregorianCalendar visitDate;

	@ApiModelProperty(notes = "treatmentType key", position = 22)
	private PregnantTreatmentType pregTreatmentType;

	@ApiModelProperty(notes = "delivery date", position = 23)
	private GregorianCalendar deliveryDate;

	@ApiModelProperty(notes = "delivery type", position = 24)
	private DeliveryTypeDTO deliveryType;

	@ApiModelProperty(notes = "delivery type key", position = 25)
	private DeliveryResultTypeDTO deliveryResult;

	@ApiModelProperty(notes = "weight", position = 26)
	private Float weight;

	private GregorianCalendar ctrlDate1;

	private GregorianCalendar ctrlDate2;

	private GregorianCalendar abortDate;

	@ApiModelProperty(notes = "weight", position = 30)
	private String userID;

	@ApiModelProperty(notes = "lock", example="0", position = 31)
	private int lock;

	@NotNull
	@ApiModelProperty(notes = "flag record deleted, values are 'Y' OR 'N' ", example="N", position = 32)
	private String deleted;


}
