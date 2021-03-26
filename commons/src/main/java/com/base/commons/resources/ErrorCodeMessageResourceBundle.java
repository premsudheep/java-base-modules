package com.base.commons.resources;

public class ErrorCodeMessageResourceBundle extends BaseResourceBundle {

    private static ErrorCodeMessageResourceBundle statusMessageResourceBundle = new ErrorCodeMessageResourceBundle();

    private ErrorCodeMessageResourceBundle() {
        super("errorCodeMessagesBundle");
    }

    public static ErrorCodeMessageResourceBundle getInstance() {
        return statusMessageResourceBundle;
    }
}
