package com.base.commons.exceptions;

import com.base.commons.model.exception.Status;

public class BaseServiceException extends BaseException {
    public BaseServiceException(Status status) {
        super(status);
    }

    public BaseServiceException(String message, Status status) {
        super(message, status);
    }

    public BaseServiceException(String message, Throwable cause, Status status) {
        super(message, cause, status);
    }

    public BaseServiceException(Throwable cause, Status status) {
        super(cause, status);
    }

    public BaseServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace, status);
    }
}
