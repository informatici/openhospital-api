/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.operation.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.isf.accounting.dto.BillDTO;
import org.isf.admission.dto.AdmissionDTO;
import org.isf.opd.dto.OpdDTO;

import io.swagger.annotations.ApiModelProperty;


public class OperationRowDTO {

    private int id;

    @NotNull
    private OperationDTO operation;

    @NotNull
    private String prescriber;

    @NotNull
    private String opResult;

    @NotNull
    private Date opDate;

    private String remarks;

    @NotNull
    private AdmissionDTO admission;

    @NotNull
    private OpdDTO opd;

    private BillDTO bill;

    private Float transUnit;
    
    private int hashCode = 0;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OperationDTO getOperation() {
        return operation;
    }

    public void setOperation(OperationDTO operation) {
        this.operation = operation;
    }

    public String getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }

    public String getOpResult() {
        return opResult;
    }

    public void setOpResult(String opResult) {
        this.opResult = opResult;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public AdmissionDTO getAdmission() {
        return admission;
    }

    public void setAdmission(AdmissionDTO admission) {
        this.admission = admission;
    }

    public OpdDTO getOpd() {
        return opd;
    }

    public void setOpd(OpdDTO opd) {
        this.opd = opd;
    }

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
    }

    public Float getTransUnit() {
        return transUnit;
    }

    public void setTransUnit(Float transUnit) {
        this.transUnit = transUnit;
    }

    @ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	
    public String toString() {
        return this.operation.getDescription() + " " + this.admission.getUserID();
    }
}
