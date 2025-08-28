package org.isf.conditioning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ConditioningDTO {

	@Schema(description = "Conditioning key", example = "12")
	private int id;

	@NotNull
	@Schema(description = "Aspiration conditioning", example = "true")
	private Boolean aspiration;

	@NotNull
	@Schema(description = "Mce duree conditioning", example = "4")
	private Integer mceDuree;

	@NotNull
	@Schema(description = "Ventilation duree conditioning", example = "2")
	private Integer ventilationDuree;

	@NotNull
	@Schema(description = "Oxygene debit conditioning", example = "3")
	private Double oxygeneDebit;

	@NotNull
	@Schema(description = "Sg volume conditioning", example = "10")
	private Double sgVolume;

	@NotNull
	@Schema(description = "Diazepam dose conditioning", example = "12")
	private Double diazepamDose;

	@NotNull
	@Schema(description = "Bolus volume conditioning", example = "3")
	private Double bolusSsVolume;

	@NotNull
	@Schema(description = "Conditioning number sng", example = "3434634")
	private String sngNumero;

	@NotNull
	@Schema(description = "Conditioning others", example = "others")
	private String others;

	@NotNull
	@Schema(description = "Id for user perform", example = "222343")
	private String performById;

	@NotNull
	@Schema(description = "Perform date", example = "String")
	private LocalDateTime performAt;

	public Boolean getAspiration() {
		return aspiration;
	}

	public void setAspiration(Boolean aspiration) {
		this.aspiration = aspiration;
	}

	public Integer getMceDuree() {
		return mceDuree;
	}

	public void setMceDuree(Integer mceDuree) {
		this.mceDuree = mceDuree;
	}

	public Integer getVentilationDuree() {
		return ventilationDuree;
	}

	public void setVentilationDuree(Integer ventilationDuree) {
		this.ventilationDuree = ventilationDuree;
	}

	public Double getOxygeneDebit() {
		return oxygeneDebit;
	}

	public void setOxygeneDebit(Double oxygeneDebit) {
		this.oxygeneDebit = oxygeneDebit;
	}

	public Double getSgVolume() {
		return sgVolume;
	}

	public void setSgVolume(Double sgVolume) {
		this.sgVolume = sgVolume;
	}

	public Double getDiazepamDose() {
		return diazepamDose;
	}

	public void setDiazepamDose(Double diazepamDose) {
		this.diazepamDose = diazepamDose;
	}

	public Double getBolusSsVolume() {
		return bolusSsVolume;
	}

	public void setBolusSsVolume(Double bolusSsVolume) {
		this.bolusSsVolume = bolusSsVolume;
	}

	public String getSngNumero() {
		return sngNumero;
	}

	public void setSngNumero(String sngNumero) {
		this.sngNumero = sngNumero;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getPerformById() {
		return performById;
	}

	public void setPerformById(String performById) {
		this.performById = performById;
	}

	public LocalDateTime getPerformAt() {
		return performAt;
	}

	public void setPerformAt(LocalDateTime performAt) {
		this.performAt = performAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
