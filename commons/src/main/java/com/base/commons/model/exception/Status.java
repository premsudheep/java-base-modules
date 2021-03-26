package com.base.commons.model.exception;

import java.util.Arrays;
import java.util.Objects;

public class Status {
    private String serverStatusCode;
    private Severity severity;
    private AdditionalStatus[] additionalStatuses;

    public Status() { }

    public Status(String serverStatusCode, Severity severity, AdditionalStatus[] additionalStatuses) {
        this.serverStatusCode = serverStatusCode;
        this.severity = severity;
        this.additionalStatuses = additionalStatuses;
    }

    public String getServerStatusCode() {
        return serverStatusCode;
    }

    public void setServerStatusCode(String serverStatusCode) {
        this.serverStatusCode = serverStatusCode;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public AdditionalStatus[] getAdditionalStatuses() {
        return additionalStatuses;
    }

    public void setAdditionalStatuses(AdditionalStatus[] additionalStatuses) {
        this.additionalStatuses = additionalStatuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status = (Status) o;
        return serverStatusCode.equals(status.serverStatusCode) &&
                severity == status.severity &&
                Arrays.equals(additionalStatuses, status.additionalStatuses);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(serverStatusCode, severity);
        result = 31 * result + Arrays.hashCode(additionalStatuses);
        return result;
    }

    @Override
    public String toString() {
        return "Status{" +
                "serverStatusCode='" + serverStatusCode + '\'' +
                ", severity=" + severity +
                ", additionalStatuses=" + Arrays.toString(additionalStatuses) +
                '}';
    }
}


