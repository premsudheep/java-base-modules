package com.base.commons.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import com.base.commons.exceptions.BaseException;
import com.base.commons.resources.BaseResourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriUtils;


public class UriUtil extends UriUtils {
    //private static final EsoLogger logger = EsoLogger.create(UriUtil.class);
    private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String ENVIRONMENT_PROPERTIES_FILE = "environment";
    private static final String HOST_BASE_KEY = "host.";
    public static String getURIHostWithDefaultContext(HttpServletRequest request, String locale) {
        StringBuilder uri = new StringBuilder();
        uri.append(request.getScheme());
        uri.append("://");
        String envHost = getHostFromRequest(request);
        if (StringUtils.isNotBlank(envHost)) {
            uri.append(envHost);
        } else if (validate(extractIPFromReqHeader(request))) {
            uri.append(request.getHeader("host"));
        } else {
            uri.append(request.getServerName());
            if ("127.0.0.1".equals(request.getRemoteHost())) {
                uri.append(":").append(request.getServerPort());
            }
        }
        //logger.debug("UriUtil :: getURIHostWithDefaultContext : URI: " + uri.toString());
        return uri.toString();
    }
    private static String getHostFromRequest(HttpServletRequest request) {
        return null != request && null != request.getHeader("host")?request.getHeader("host"):StringUtils.EMPTY;
    }
    public static boolean validate(final String ip) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    public static String extractIPFromReqHeader(HttpServletRequest request) {
        if (request.getHeader("host").indexOf(":") != -1) {
            return request.getHeader("host").substring(0, request.getHeader("host").indexOf(":"));
        }
        return "";
    }
    private static String getHostByLocale(String locale) {
        try {
            Properties envProperties = BaseResourceHandler.getInstance()
                    .getPropertiesConfig(ENVIRONMENT_PROPERTIES_FILE);
            return envProperties.getProperty(HOST_BASE_KEY + locale);
        } catch (BaseException e) {
            // logger.error("", e);
        }
        return null;
    }
}
