package com.base.commons.aspects;

import com.base.commons.exceptions.*;
import com.base.commons.model.exception.AdditionalStatus;
import com.base.commons.model.common.ResponseBean;
import com.base.commons.model.exception.Status;
import com.base.commons.resolvers.ErrorCodeMessageResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseGlobalExceptionAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseBean> baseExceptionHandler(BaseException e) {
        return new ResponseEntity<>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseBean> apiExceptionHandler(BaseException e) {
        return new ResponseEntity<>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BaseValidationException.class)
    public ResponseEntity<ResponseBean> validationExceptionHandler(BaseException e) {
        return new ResponseEntity<>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ResponseBean> businessExceptionHandler(BaseException e) {
        return new ResponseEntity<>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<ResponseBean> serviceExceptionHandler(BaseException e) {
        return new ResponseEntity<>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BaseWarningException.class)
    public ResponseEntity<ResponseBean> warningExceptionHandler(BaseException e) {
        // logger.error("Handler Advise (BaseWarningException)", e);
        ResponseBean bean = new ResponseBean();
        bean.setResponseStatusBean(sanitizeErrorResponse(e.getStatus()));
        return new ResponseEntity<ResponseBean>(createResponseBean(e), new HttpHeaders(), createHttpStatusFromStatusObject(e.getStatus(), HttpStatus.MULTI_STATUS));
    }

    private ResponseBean createResponseBean(ApiException e) {
        ResponseBean responseBean = new ResponseBean();
        Status status = e.getStatus();
        responseBean.setStatus(sanitizeErrorResponse(status));
        return responseBean;
    }

    private Status sanitizeErrorResponse(Status status) {
        if(status.getAdditionalStatuses() != null && status.getAdditionalStatuses().length > 0) {
            for(AdditionalStatus additionalStatus: status.getAdditionalStatuses()) {
                additionalStatus.setStatusDesc(ErrorCodeMessageResolver.resolveErrorMessage(additionalStatus.getServerStatusCode()));
            }
        }
        return status;
    }

    private HttpStatus createHttpStatusFromStatusObject(Status status, HttpStatus defaultHttpStatus) {
        HttpStatus httpStatus = defaultHttpStatus;
        if(status != null && !StringUtils.isEmpty(status.getServerStatusCode())) {
            try {
                httpStatus = HttpStatus.valueOf(Integer.parseInt(status.getServerStatusCode()));
            } catch (NumberFormatException e) {
                // do something as HttpStatus already has a value
            }
        }
        return httpStatus;
    }
}
