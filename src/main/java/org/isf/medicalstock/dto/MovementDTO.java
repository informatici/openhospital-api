package org.isf.medicalstock.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.isf.medical.dto.MedicalDTO;
import org.isf.medstockmovtype.dto.MovementTypeDTO;
import org.isf.supplier.dto.SupplierDTO;
import org.isf.ward.dto.WardDTO;

import io.swagger.annotations.ApiModelProperty;

public class MovementDTO {
	@ApiModelProperty(notes="The movement code", example = "1", position = 1)
	private int code;

	@NotNull(message="The medical is required")
	@ApiModelProperty(notes="The related medical", position = 2)
	private MedicalDTO medical;

	@NotNull(message="The movement type is required")
	@ApiModelProperty(notes="The movement type", position = 3)
	private MovementTypeDTO type;

	@ApiModelProperty(notes="The target ward", position = 4)
	private WardDTO ward;

	@ApiModelProperty(notes="The lot", position = 5)
	private LotDTO lot;

	@NotNull(message="the movement's date is required")
	@ApiModelProperty(notes="The movement date", example = "2020-06-24", position = 6)
	private Date date;

	@NotNull(message="the movement's medical quantity is required")
	@ApiModelProperty(notes="The movement's medical quantity", example = "50", position = 7)
	private int quantity;

	@ApiModelProperty(notes="The movement's supplier", position = 8)
	private SupplierDTO supplier;
	
	@NotNull(message="the movement reference is required")
	@ApiModelProperty(notes="The movement reference", example = "MVN152445", position = 9)
	private String refNo;
	
	public MovementDTO() {
		super();
	}
	
	public MovementDTO(int code, MedicalDTO medical, MovementTypeDTO type, WardDTO ward, LotDTO lot, Date date,
			int quantity, SupplierDTO supplier, String refNo) {
		super();
		this.code = code;
		this.medical = medical;
		this.type = type;
		this.ward = ward;
		this.lot = lot;
		this.date = date;
		this.quantity = quantity;
		this.supplier = supplier;
		this.refNo = refNo;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public MedicalDTO getMedical() {
		return medical;
	}

	public void setMedical(MedicalDTO medical) {
		this.medical = medical;
	}

	public MovementTypeDTO getType() {
		return type;
	}

	public void setType(MovementTypeDTO type) {
		this.type = type;
	}

	public WardDTO getWard() {
		return ward;
	}

	public void setWard(WardDTO ward) {
		this.ward = ward;
	}

	public LotDTO getLot() {
		return lot;
	}

	public void setLot(LotDTO lot) {
		this.lot = lot;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public SupplierDTO getSupplier() {
		return supplier;
	}

	public void setSupplier(SupplierDTO supplier) {
		this.supplier = supplier;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	
}
