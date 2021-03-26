package com.base.commons.model.common;

import com.base.commons.model.exception.Status;
import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResponseBean {
    @JsonProperty(value = "status")
    private Status status;
    @JsonIgnore
    private Map<String, Object> objectMap = new HashMap<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void setResponseStatusBean(Status status) {
        this.status = status;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }
}
