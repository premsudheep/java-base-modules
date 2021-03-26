package com.base.commons.exceptions;

import com.base.commons.model.exception.AdditionalSeverity;
import com.base.commons.model.exception.AdditionalStatus;
import com.base.commons.model.exception.Severity;
import com.base.commons.model.exception.Status;
import org.springframework.http.HttpStatus;

public class BaseWarningException extends BaseException {
    public BaseWarningException(Status status) {
        super(status);
    }

    public BaseWarningException(String message, Status status) {
        super(message, status);
    }

    public BaseWarningException(String message, Throwable cause, Status status) {
        super(message, cause, status);
    }

    public BaseWarningException(Throwable cause, Status status) {
        super(cause, status);
    }

    public BaseWarningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace, status);
    }

    @Override
    public Status getStatus() {
        if(super.getStatus() != null) {
            return super.getStatus();
        }
        Status status = new Status();
        status.setServerStatusCode(Integer.toString(HttpStatus.MULTI_STATUS.value()));
        status.setSeverity(Severity.Warning);
        AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setServerStatusCode(getErrorType().getValue());
        additionalStatus.setStatusCode(HttpStatus.MULTI_STATUS.value());
        additionalStatus.setSeverity(AdditionalSeverity.Error);
        status.setAdditionalStatuses(new AdditionalStatus[]{additionalStatus});
        return status;
    }
}
