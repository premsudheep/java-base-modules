package com.base.commons.resources;

import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_INPUT_STREAM_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_INPUT_STREAM_OBJECT_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_NO_CACHE_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_NO_CACHE_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_NODE_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_NODE_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_JSON_OBJECT_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PLAIN_STRING_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PLAIN_STRING_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_PROPERTY_NOT_FOUND_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_PROPERTIES_PROPERTY_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_XML_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR;
import static com.base.commons.constants.BaseCommonErrorTypes.BASE_RESOURCE_HANDLER_XML_OBJECT_CONTENT_NOT_FOUND_ERROR;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.ThreadContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.base.commons.exceptions.BaseException;
import com.base.commons.resources.ApiConfig;
import com.base.commons.constants.BaseCommonConstants;
//import com.td.dcts.eso.base.logging.EsoLogger;
import org.springframework.util.StringUtils;


public class BaseResourceHandler {
    //private static final EsoLogger logger = EsoLogger.create(EsoResourceHandler.class);
    private static BaseResourceHandler instance = new BaseResourceHandler();
    public static BaseResourceHandler getInstance() {
        return instance;
    }
    public String getStringFromFile(String resource) throws BaseException {
        String content = null;
        try {
            content = ApiConfig.getInstance().getStringFromFile(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_PLAIN_STRING_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load plain resource (" + resource
                                + "), confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load plain resource (" + resource + ")");
            throw new BaseException(BASE_RESOURCE_HANDLER_PLAIN_STRING_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public Map<String, Object> getJsonMapConfig(String resource) throws BaseException {
        Map<String, Object> content = null;
        try {
            content = ApiConfig.getInstance().getJsonMapConfig(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment, check content if it is not according to JSON specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_CONTENT_NOT_FOUND_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public Map<String, Object> getJsonMapConfigNoCache(String resource) throws BaseException {
        Map<String, Object> content = null;
        try {
            content = ApiConfig.getInstance().getJsonMapConfig(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_NO_CACHE_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment, check content if it is not according to JSON specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_JSON_MAP_CONFIG_NO_CACHE_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public Properties getPropertiesConfig(String resource) throws BaseException {
        Properties content = null;
        try {
            content = ApiConfig.getInstance().getPropertiesConfig(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment,  check content if it is not according to properties specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public Object getConfigPropertyValue(String resource, String propertyName) throws BaseException {
        Object propertyValue = null;
        try {
            Properties content = getPropertiesConfig(resource);
            propertyValue = content.get(propertyName);
            if (propertyValue == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_PROPERTIES_CONTENT_PROPERTY_NOT_FOUND_ERROR,
                        "Couldn't load property: " + propertyName + " from resource:" + resource
                                + ". Please confirm if it exists.");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment,  check content if it is not according to properties specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_PROPERTIES_PROPERTY_LOADING_EXCEPTION_ERROR,
                    "Couldn't load value for property:" + propertyName, e);
        }
        return propertyValue;
    }
    public JsonNode getJsonNodeConfig(String resource) throws BaseException {
        JsonNode content = null;
        try {
            content = ApiConfig.getInstance().getJsonNodeConfig(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_JSON_NODE_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment,  check content if it is not according to JSON specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_JSON_NODE_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public <T> T getJsonObjectConfig(String resource, Class<T> clazz) throws BaseException {
        Object content = null;
        try {
            content = ApiConfig.getInstance().getJsonObjectConfig(getResourceName(resource), clazz);
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_JSON_OBJECT_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
            return clazz.cast(content);
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment,  check content if it is not according to JSON specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_JSON_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
    }
    public <T> T getXmlObjectConfig(String resource, Class<T> clazz) throws BaseException {
        Object content = null;
        try {
            content = ApiConfig.getInstance().getXmlObjectConfig(getResourceName(resource), clazz);
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_XML_OBJECT_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
            return clazz.cast(content);
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment,  check content if it is not according to XML specifications");
            throw new BaseException(BASE_RESOURCE_HANDLER_XML_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
    }
    public InputStream getInputStreamFromFile(String resource) throws BaseException {
        InputStream content = null;
        try {
            content = ApiConfig.getInstance().getInputStreamFromFile(getResourceName(resource));
            if (content == null) {
                throw new BaseException(BASE_RESOURCE_HANDLER_INPUT_STREAM_OBJECT_CONTENT_NOT_FOUND_ERROR,
                        "Couldn't load resource (" + resource
                                + ") from any environment, confirm if file exist in the conifguration folder structure");
            }
        } catch (Exception e) {
            //logger.error("Couldn't load resource (" + resource + ") from any environment");
            throw new BaseException(BASE_RESOURCE_HANDLER_INPUT_STREAM_OBJECT_CONTENT_LOADING_EXCEPTION_ERROR,
                    "Couldn't load content for resource:" + resource, e);
        }
        return content;
    }
    public ObjectMapper getMapper() throws BaseException {
        return ApiConfig.getInstance().getMapper();
    }
    public void clear() {
        ApiConfig.getInstance().clear();
    }
    public String getResourceName(String resourceName) {
        String configFolder = ThreadContext.get(BaseCommonConstants.CONFIG_FOLDER);
        if (StringUtils.isEmpty(configFolder)) {
            return resourceName;
        } else {
            return "config/" + configFolder + "/" + resourceName;
        }
    }
}

