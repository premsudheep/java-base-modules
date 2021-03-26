package com.base.commons.exceptions;

import com.base.commons.constants.BaseCommonErrorTypes;
import com.base.commons.model.exception.AdditionalSeverity;
import com.base.commons.model.exception.AdditionalStatus;
import com.base.commons.model.exception.Severity;
import com.base.commons.model.exception.Status;
import com.base.commons.types.BaseErrorType;
import org.springframework.http.HttpStatus;

public class BaseException extends ApiException {

    private BaseErrorType errorType = null;


    public BaseException(Status status) {
        super(status);
    }

    public BaseException(String message, Status status) {
        super(message, status);
    }

    public BaseException(String message, Throwable cause, Status status) {
        super(message, cause, status);
    }

    public BaseException(Throwable cause, Status status) {
        super(cause, status);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace, status);
    }

    public BaseException(BaseErrorType errorType, String errorMessage) {
        super(null, errorMessage);
        this.errorType = errorType;
    }

    public BaseException(BaseErrorType errorType, String errorMessage, Throwable e) {
        super(null, errorMessage, e);
        this.errorType = errorType;
    }

    public BaseErrorType getErrorType() {
        if (errorType == null || errorType.getValue() == null || errorType.getValue().trim().length() == 0) {
            return BaseCommonErrorTypes.BASE_UNDEFINED_ERROR_STATUS_EXCEPTION;
        }
        return errorType;
    }

    public String getErrorCode() {
        return getErrorType().getValue();
    }

    @Override
    public Status getStatus() {
        if(super.getStatus() != null) {
            return super.getStatus();
        }
        Status status = new Status();
        status.setServerStatusCode(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        status.setSeverity(Severity.Error);
        AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setServerStatusCode(getErrorType().getValue());
        additionalStatus.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        additionalStatus.setSeverity(AdditionalSeverity.Error);
        status.setAdditionalStatuses(new AdditionalStatus[]{additionalStatus});
        return status;
    }
}
