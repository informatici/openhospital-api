package org.isf.shared.controller;

public enum ResponseCode {


    SUCCESS("success"),
    INVALID_PARAMETER("Invalid parameter Error"),
    VALIDATION_ERROR("Validation Errors"),
    OPERATION_NOT_ALLOWED_ERROR("Operation Not Allowed Errors"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    REPORT_ERROR("Report Error"),
    DICOM_ERROR("Dicom Error"),
    DATABASE_CONNECTION_ERROR("Database Connection Error"),
    DATA_INTEGRITY_ERROR("Data Integrity Error"),
    DATA_LOCK_ERROR("Data Lock Failure Error"),
    INVALID_SQL_ERROR("Invalid Sql Error"),
    NOT_FOUND("Resource Not Found");

    private String value;

    ResponseCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
