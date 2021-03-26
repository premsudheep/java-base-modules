package com.base.commons.mappers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.base.commons.exceptions.BaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static com.base.commons.constants.BaseCommonErrorTypes.BASE_XML_MAPPERS_UTIL_PARSING_XML_STRING_EXCPETION;

/**
 *
 * @author ALHAJS2
 * @version 1.0
 * @since 2018-07-31
 *
 */
public class XMLMapperUtils{
    //private static final EsoLogger logger = EsoLogger.create(XMLMapperUtils.class);

    private final static ObjectMapper xmlMapper = new XmlMapper();

    private XMLMapperUtils() {

    }

    public static <T extends Object> T loadXMLResourceObject(String sourceString, Class<T> valueType) throws BaseException {
        try {
            T response = xmlMapper.readValue(sourceString, valueType);
            return response;
        } catch (Exception e) {
            //logger.error("Couldn't load XML Resource object resource, check sourceString if it is a valid XML content");
            throw new BaseException(BASE_XML_MAPPERS_UTIL_PARSING_XML_STRING_EXCPETION,"Couldn't load XML Resource object",e);
        }
    }

    public static DocumentBuilder disableXXEWhileParsing(DocumentBuilderFactory factory) throws ParserConfigurationException {
        String FEATURE = null;
        try {
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            factory.setFeature(FEATURE, true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            factory.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            factory.setFeature(FEATURE, false);

            // Disable external DTDs as well
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            factory.setFeature(FEATURE, false);

            // and these as well: "XML Schema, DTD, and Entity Attacks"
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

        } catch (ParserConfigurationException e) {
            //TODO Confirm if it is ok not to throw exception here, also the log message should be switched to WARN
            //This should catch a failed setFeature feature
            //logger.error("ParserConfigurationException was thrown. The feature '" + FEATURE + "' is probably not supported by your XML processor." +e);
            throw e;
        }
        return factory.newDocumentBuilder();
    }

}

