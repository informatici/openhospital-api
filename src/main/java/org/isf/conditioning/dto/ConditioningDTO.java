/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
