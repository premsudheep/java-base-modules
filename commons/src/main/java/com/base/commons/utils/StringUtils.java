package com.base.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.base.commons.constants.BaseCommonErrorTypes;
import com.base.commons.exceptions.BaseException;

public class StringUtils {

    private StringUtils() { }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean areAllAvailable(String ... strArray) {
        for(String stringValue:strArray) {
            if(isEmpty(stringValue)) {
                return false;
            }
        }
        return true;
    }
    public static String encode(String str) throws BaseException {
        return encode(str, StandardCharsets.UTF_8.toString());
    }
    public static String encode(String str, String enc) throws BaseException {
        try {
            return URLEncoder.encode(str, enc);
        } catch (UnsupportedEncodingException e) {
            throw new BaseException(BaseCommonErrorTypes.BASE_UNSUPPORTED_ENCODING_ERROR,"Unsupported Encoding Error", e);
        }
    }
}
