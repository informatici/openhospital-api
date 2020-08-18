package org.isf.medicalstockward.dto;

import io.swagger.annotations.ApiModelProperty;

public class MedicalWardDTO {
	@ApiModelProperty(notes="The medical ward's id", example="1", position = 1)
	private MedicalWardIdDTO id;
	
	@ApiModelProperty(notes="The in-quantity", example="150", position = 2)
	private float in_quantity;
	
	@ApiModelProperty(notes="The out-quantity", example="89", position = 3)
	private float out_quantity;

	public MedicalWardDTO() {
		super();
	}

	public MedicalWardDTO(MedicalWardIdDTO id, float in_quantity, float out_quantity) {
		super();
		this.id = id;
		this.in_quantity = in_quantity;
		this.out_quantity = out_quantity;
	}

	public MedicalWardIdDTO getId() {
		return id;
	}

	public void setId(MedicalWardIdDTO id) {
		this.id = id;
	}

	public float getIn_quantity() {
		return in_quantity;
	}

	public void setIn_quantity(float in_quantity) {
		this.in_quantity = in_quantity;
	}

	public float getOut_quantity() {
		return out_quantity;
	}

	public void setOut_quantity(float out_quantity) {
		this.out_quantity = out_quantity;
	}
	
}
