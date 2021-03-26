package com.base.commons.mappers;


import java.io.IOException;
import java.util.Map;

import com.base.commons.exceptions.BaseException;
import com.base.commons.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import static com.base.commons.constants.BaseCommonErrorTypes.*;

public class JSONMapperUtils {
    //private static final EsoLogger logger = EsoLogger.create(JSONMapperUtils.class);
    private final static ObjectMapper mapper = new ObjectMapper();

    private JSONMapperUtils() {
    }
    public static Map<String, Object> convertJsonToMap(String json)  throws BaseException {
        //In case the JSON string is null or empty return a null object (MAP)
        if(StringUtils.isEmpty(json)){
            return null;
        }

        try {
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            //logger.error("Couldn't deserialize JSON MAP, check json String if it is a valid JSON format");
            throw new BaseException(BASE_JSON_MAPPERS_UTIL_PARSING_JSON_MAP_STRING_EXCPETION,"Couldn't deserialize JSON MAP",e);
        }
    }

    public static <T> T convertJsonToObject(String json, Class<T> clazz)  throws BaseException {
        if (json != null) {
            try {
                return mapper.readValue(json, clazz);
            } catch (IOException e) {
                //logger.error("Couldn't deserialize JSON object, check json String if it is a valid JSON format");
                throw new BaseException(BASE_JSON_MAPPERS_UTIL_PARSING_JSON_STRING_EXCPETION,"Couldn't deserialize JSON object",e);
            }
        }
        return null;
    }
    public static String convertObjectToJson(Object object) throws BaseException {
        if(object == null) {
            return null;
        }
        try {
            if (mapper.canSerialize(object.getClass())) {
                return mapper.writeValueAsString(object);
            } else {
                throw new BaseException(BASE_JSON_MAPPERS_UTIL_SERIALIZING_INVALID_OBJECT_EXCPETION,"Invalid serialization object");
            }
        } catch (Exception e) {
            //logger.error("Couldn't serialize JSON object");
            throw new BaseException(BASE_JSON_MAPPERS_UTIL_SERIALIZING_JSON_OBJECT_EXCPETION,"Couldn't serialize JSON object",e);
        }
    }
    //The difference between this and convertObjectToJson is on the wrapping
    public static String convertObjectToPlainJson(Object object) throws BaseException {
        if(object == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            //logger.error("Couldn't serialize plain JSON object");
            throw new BaseException(BASE_JSON_MAPPERS_UTIL_SERIALIZING_OBJECT_TO_PLAIN_JSON_EXCPETION,"Couldn't serialize plain JSON object",e);
        }
    }

    public static <T> T convertJsonToObjectIgnoreUnknown(String json, Class<T> clazz)  throws BaseException {
        if (json != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper.readValue(json, clazz);
            } catch (IOException e) {
                //logger.error("Couldn't deserialize JSON object, check json String if it is a valid JSON format");
                throw new BaseException(BASE_JSON_MAPPERS_UTIL_PARSING_JSON_STRING_EXCPETION,"Couldn't deserialize JSON object",e);
            }
        }
        return null;
    }
}

