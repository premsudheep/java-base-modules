package com.base.commons.model.exception;

import java.util.Objects;

public class AdditionalStatus {
    private int statusCode;
    private String serverStatusCode;
    private AdditionalSeverity severity;
    private String statusDesc;

    public AdditionalStatus() {
    }

    public AdditionalStatus(int statusCode, AdditionalSeverity severity) {
        this.statusCode = statusCode;
        this.severity = severity;
    }

    public AdditionalStatus(int statusCode, String serverStatusCode, AdditionalSeverity severity, String statusDesc) {
        this.statusCode = statusCode;
        this.serverStatusCode = serverStatusCode;
        this.severity = severity;
        this.statusDesc = statusDesc;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getServerStatusCode() {
        return serverStatusCode;
    }

    public AdditionalSeverity getSeverity() {
        return severity;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setServerStatusCode(String serverStatusCode) {
        this.serverStatusCode = serverStatusCode;
    }

    public void setSeverity(AdditionalSeverity severity) {
        this.severity = severity;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdditionalStatus)) return false;
        AdditionalStatus that = (AdditionalStatus) o;
        return statusCode == that.statusCode &&
                serverStatusCode.equals(that.serverStatusCode) &&
                severity == that.severity &&
                statusDesc.equals(that.statusDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, serverStatusCode, severity, statusDesc);
    }

    @Override
    public String toString() {
        return "AdditionalStatus{" +
                "statusCode=" + statusCode +
                ", serverStatusCode='" + serverStatusCode + '\'' +
                ", severity=" + severity +
                ", statusDesc='" + statusDesc + '\'' +
                '}';
    }
}
