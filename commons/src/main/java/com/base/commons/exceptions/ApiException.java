package com.base.commons.exceptions;

import com.base.commons.model.exception.Status;

public class ApiException extends Exception {
    private Status status;

    public ApiException(Status status) {
        this.status = status;
    }

    public ApiException(String message, Status status) {
        super(message);
        this.status = status;
    }

    public ApiException(String message, Throwable cause, Status status) {
        super(message, cause);
        this.status = status;
    }

    public ApiException(Throwable cause, Status status) {
        super(cause);
        this.status = status;
    }

    public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public ApiException(Status status , String errorMessage) {
        super(errorMessage);
        this.status = status;
    }

    public ApiException(Status status , String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

