package com.alianza.excel_component.DTO;

public class ExcelResponseDto {

    private String status;
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExcelResponseDto [details=" + details + ", status=" + status + "]";
    }
}