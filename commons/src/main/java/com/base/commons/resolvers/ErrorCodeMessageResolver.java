package com.base.commons.resolvers;

import com.base.commons.resources.ErrorCodeMessageResourceBundle;
import org.springframework.util.StringUtils;

public class ErrorCodeMessageResolver {

    public static String resolveErrorMessage(String messageCode) {
        if(StringUtils.isEmpty(messageCode)) {
            return resolveHirearchialMessage("BASE.ERROR");
        }
        return resolveHirearchialMessage(messageCode);
    }

    private static String resolveHirearchialMessage(String messageCode) {
        String message = ErrorCodeMessageResourceBundle.getInstance().getProperties(messageCode);
        if(StringUtils.isEmpty(message)) {
            message = ErrorCodeMessageResourceBundle.getInstance().getProperties("BASE.ERROR");
        }
        if(StringUtils.isEmpty(message)) {
            message = "We're sorry. Due to technical issue we are unable to process your request at this tome. Please try again later.";
        }
        return message;
    }
}
