package com.base.commons.exceptions;

import com.base.commons.model.exception.Status;

public class BaseValidationException extends BaseException {
    public BaseValidationException(Status status) {
        super(status);
    }

    public BaseValidationException(String message, Status status) {
        super(message, status);
    }

    public BaseValidationException(String message, Throwable cause, Status status) {
        super(message, cause, status);
    }

    public BaseValidationException(Throwable cause, Status status) {
        super(cause, status);
    }

    public BaseValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace, status);
    }
}
