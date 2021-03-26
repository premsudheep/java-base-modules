package com.base.logging.model;

import com.base.logging.constants.PerformanceLogEventStatus;
import java.util.Date;

public class PerfLogEventMetaData extends LogEventMetaData{
    private static final long serialVersionUID = 5584977630612710469L;

    private int logEventDuration;

    private Long startTime;

    private Long endTime;

    private String category;

    private String group;

    private String subGroup;

    private String operationURI;

    private PerformanceLogEventStatus eventStatus;

    public PerfLogEventMetaData(String category) {
        this.startTime = (new Date()).getTime();
        this.category = category;
    }
    public int getLogEventDuration() {
        return logEventDuration;
    }
    public void setLogEventDuration(int logEventDuration) {
        this.logEventDuration = logEventDuration;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void endEvent() {
        this.endTime = (new Date()).getTime();
    }
    public Long getStartTime() {
        return startTime;
    }
    public Long getEndTime() {
        return endTime;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getSubGroup() {
        return subGroup;
    }
    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }
    public String getOperationURI() {
        return operationURI;
    }
    public void setOperationURI(String operationURI) {
        this.operationURI = operationURI;
    }

    public PerformanceLogEventStatus getEventStatus() {
        return eventStatus;
    }
    public void setEventStatus(PerformanceLogEventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}

