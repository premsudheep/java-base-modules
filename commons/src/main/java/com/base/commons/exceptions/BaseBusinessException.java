package com.base.commons.exceptions;

import com.base.commons.model.exception.Status;

public class BaseBusinessException extends BaseException {
    public BaseBusinessException(Status status) {
        super(status);
    }

    public BaseBusinessException(String message, Status status) {
        super(message, status);
    }

    public BaseBusinessException(String message, Throwable cause, Status status) {
        super(message, cause, status);
    }

    public BaseBusinessException(Throwable cause, Status status) {
        super(cause, status);
    }

    public BaseBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace, status);
    }
}
