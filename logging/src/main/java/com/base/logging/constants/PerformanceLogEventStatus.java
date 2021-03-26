package com.base.logging.constants;

public enum PerformanceLogEventStatus {

    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private String value;

    PerformanceLogEventStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
