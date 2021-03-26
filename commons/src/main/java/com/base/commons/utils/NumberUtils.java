package com.base.commons.utils;

import com.base.commons.constants.BaseCommonErrorTypes;
import com.base.commons.exceptions.BaseException;

public class NumberUtils {

    private NumberUtils() { }

    public static boolean isInteger(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isLong(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isDouble(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static Integer toInteger(String str) throws BaseException {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            throw new BaseException(BaseCommonErrorTypes.BASE_ERROR_PARSING_NUMERIC_STRING,"Unable to Parse Number as Integer", e);
        }
    }
}

