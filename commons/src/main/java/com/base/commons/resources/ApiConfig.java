package com.base.commons.resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
/*import org.owasp.encoder.Encode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;*/
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public abstract class ApiConfig {
    // a protected constructor to prevent create object directly
    protected ApiConfig() {
    }
    // abstract methods that need be implemented by all implementations
    public abstract Map<String, Object> getJsonMapConfig(String configName);
    public abstract Map<String, Object> getJsonMapConfigNoCache(String configName);

    public abstract Map<String, Object> getYamlMapConfig(String configName);
    public abstract Map<String, Object> getYamlMapConfigNoCache(String configName);
    public abstract JsonNode getJsonNodeConfig(String configName);
    public abstract Object getJsonObjectConfig(String configName, Class clazz);
    public abstract Properties getPropertiesConfig(String configName);
    public abstract Object getXmlObjectConfig(String configName, Class clazz);
    public abstract String getStringFromFile(String filename);
    public abstract InputStream getInputStreamFromFile(String filename);
    public abstract ObjectMapper getMapper();
    public abstract void clear();
    // static getDefault is the entry point.
    public static ApiConfig getInstance() {
        return FileConfigImpl.DEFAULT;
    }
    private static final class FileConfigImpl extends ApiConfig {
        static final String CONFIG_EXT_JSON = ".json";
        static final String CONFIG_EXT_XML = ".xml";
        static final String CONFIG_EXT_PROPERTIES = ".properties";
        static final String CONFIG_EXT_YAML = ".yaml";

        static final String API_ENV = "API_ENV";
        static final String API_ENV_DEV = "dev";
        //static final XLogger logger = XLoggerFactory.getXLogger(ApiConfig.class);
        static final String EXTERNAL_FWK_SYSTEM_PROPERTY = "com.td.coreapi.common.config.dir";
        static final String EXTERNAL_LOB_SYSTEM_PROPERTY = "com.td.lob.common.config.dir";

        static String EXTERNAL_FWK_PROPERTY_DIR = System.getProperty(EXTERNAL_FWK_SYSTEM_PROPERTY, "");
        static String EXTERNAL_LOB_PROPERTY_DIR = System.getProperty(EXTERNAL_LOB_SYSTEM_PROPERTY, "");

        // cache expiration time and set it to next day's midnight if it passes its current time value
        private long cacheExpirationTime = 0L;
        private static final ApiConfig DEFAULT = initialize();
        // Memory image of all the configuration object. Each config will be loaded on the first time is is accessed.
        Map<String, Object> configImage = new ConcurrentHashMap<String, Object>(10, 0.9f, 1);
        // An instance of Jackson ObjectMapper that can be used anywhere else for Json.
        ObjectMapper mapper = new ObjectMapper();
        //ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        private static ApiConfig initialize() {
            Iterator<ApiConfig> it = null;
            it = ServiceLoader.load(ApiConfig.class).iterator();
            return it != null && it.hasNext() ? it.next() : new FileConfigImpl();
        }
        // Return instance of Jackson Object Mapper
        @Override
        public ObjectMapper getMapper() {
            return mapper;
        }
        public void clear() {
            configImage.clear();
            EXTERNAL_FWK_PROPERTY_DIR = System.getProperty(EXTERNAL_FWK_SYSTEM_PROPERTY, "");
            EXTERNAL_LOB_PROPERTY_DIR = System.getProperty(EXTERNAL_LOB_SYSTEM_PROPERTY, "");
        }
        /**
         * Get String by the name of the file.
         *
         * @param filename
         * @return String
         */
        @Override
        public String getStringFromFile(String filename) {
            //checkCacheExpiration();
            String content = (String)configImage.get(filename);
            if(content == null) {
                synchronized (FileConfigImpl.class) {
                    content = (String)configImage.get(filename);
                    if(content == null) {
                        content = loadStringFromFile(filename);
                        if(content != null) configImage.put(filename, content);
                    }
                }
            }
            return content;
        }
        /**
         * Get InputStream by the name of the file. This can be used to load keystore
         * from absolute path in the config.
         *
         * Note 1: The caller is responsible for closing the InputStream.
         * Note 2: There is no cache for InputStream and it is one off access.
         *
         * @param filename String
         * @return InputStream
         */
        @Override
        public InputStream getInputStreamFromFile(String filename) {
            return getConfigStream(filename);
        }
        /**
         * Get Object by the name of the config in xml format.
         *
         * @param configName
         * @return
         */
        /*@Override
        public Object getXmlObjectConfig(String configName, Class clazz) {
            //checkCacheExpiration();
            Object config = (Object)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (Object)configImage.get(configName);
                    if(config == null) {
                        config = loadXmlObjectConfig(configName, clazz);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }*/
        /**
         * Get Properties by the name of the config in properties format.
         *
         * @param configName
         * @return
         */
        @Override
        public Properties getPropertiesConfig(String configName) {
            //checkCacheExpiration();
            Properties config = (Properties)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (Properties)configImage.get(configName);
                    if(config == null) {
                        config = loadPropertiesConfig(configName);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }

        @Override
        public Object getXmlObjectConfig(String configName, Class clazz) {
            return null;
        }

        /**
         * Get Object by the name of the config and class in JSON format.
         *
         * @param configName
         * @return
         */
        @Override
        public Object getJsonObjectConfig(String configName, Class clazz) {
            //checkCacheExpiration();
            Object config = (Object)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (Object)configImage.get(configName);
                    if(config == null) {
                        config = loadJsonObjectConfig(configName, clazz);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }
        /**
         * Get JsonNode by the name of the config in JSON format.
         *
         * @param configName
         * @return
         */
        @Override
        public JsonNode getJsonNodeConfig(String configName) {
            //checkCacheExpiration();
            JsonNode config = (JsonNode)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (JsonNode)configImage.get(configName);
                    if(config == null) {
                        config = loadJsonNodeConfig(configName);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }
        /**
         * Get map by the name of the config in JSON format.
         *
         * @param configName
         * @return
         */
        @Override
        public Map<String, Object> getJsonMapConfig(String configName) {
            //checkCacheExpiration();
            Map<String, Object> config = (Map<String, Object>)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (Map<String, Object>)configImage.get(configName);
                    if(config == null) {
                        config = loadJsonMapConfig(configName);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }
        /**
         * Get map by the name of the config in JSON format.
         *
         * @param configName
         * @return
         */
        @Override
        public Map<String, Object> getJsonMapConfigNoCache(String configName) {
            return loadJsonMapConfig(configName);
        }

        @Override
        public Map<String, Object> getYamlMapConfig(String configName) {
            return null;
        }

        @Override
        public Map<String, Object> getYamlMapConfigNoCache(String configName) {
            return null;
        }

        //@SuppressWarnings("unchecked")
        /*@Override
        public Map<String, Object> getYamlMapConfig(String configName) {
            Map<String, Object> config = (Map<String, Object>)configImage.get(configName);
            if(config == null) {
                synchronized (FileConfigImpl.class) {
                    config = (Map<String, Object>)configImage.get(configName);
                    if(config == null) {
                        config = loadYamlMapConfig(configName);
                        if(config != null) configImage.put(configName, config);
                    }
                }
            }
            return config;
        }*/

        /*@Override
        public Map<String, Object> getYamlMapConfigNoCache(String configName) {
            return loadYamlMapConfig(configName);
        }*/

        private String loadStringFromFile(String filename) {
            String content = null;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(filename);
                if(inStream != null) {
                    content = convertStreamToString(inStream);
                }
            } catch (Exception ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return content;
        }
        /*private Object loadXmlObjectConfig(String configName, Class clazz) {
            Object config = null;
            String configFilename = configName + CONFIG_EXT_XML;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    config = unmarshaller.unmarshal(inStream);
                }
            } catch (Exception ioe) {
                logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        logger.catching(ioe);
                    }
                }
            }
            return config;
        }*/
        private Properties loadPropertiesConfig(String configName) {
            Properties config = new Properties();
            String configFilename = configName + CONFIG_EXT_PROPERTIES;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    config.load(inStream);
                }
            } catch (IOException ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return config;
        }
        private Object loadJsonObjectConfig(String configName, Class clazz) {
            JsonNode configJsonNode = null;
            Object config = null;
            String configFilename = configName + CONFIG_EXT_JSON;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    configJsonNode = mapper.readValue(inStream, JsonNode.class);
                    substituteVariables(configJsonNode, null, null);
                    config = mapper.convertValue(configJsonNode, clazz);
                    //config = mapper.readValue(inStream, clazz);
                }
            } catch (IOException ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return config;
        }
        private JsonNode loadJsonNodeConfig(String configName) {
            JsonNode config = null;
            String configFilename = configName + CONFIG_EXT_JSON;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    config = mapper.readValue(inStream, JsonNode.class);
                    substituteVariables(config, null, null);
                }
            } catch (IOException ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return config;
        }

        private void substituteVariables(JsonNode node, JsonNode parent, String name) {
            if (node.isValueNode()) {
                if (node.isTextual() && parent != null) {
                    ((ObjectNode) parent).
                            put(name, new StringSubstitutor(StringLookupFactory.INSTANCE.interpolatorStringLookup()).replace(node.asText()));
                }
            } else if (node.isArray()) {
                // process array
                ArrayNode arrNode = (ArrayNode) node;
                if (arrNode.size() > 0) {
                    for (int i = 0; i < arrNode.size(); i++ )
                    {
                        if (arrNode.get(i).isValueNode()) {
                            if (arrNode.get(i).isTextual()) {
                                arrNode.set(i,  new TextNode(
                                        new StringSubstitutor(StringLookupFactory.INSTANCE.interpolatorStringLookup()).
                                                replace(arrNode.get(i).asText())));
                            }
                        }
                        else {
                            substituteVariables(arrNode.get(i), null, null);
                        }
                    }
                }
            } else {
                //object
                Iterator<Entry<String, JsonNode>> it = node.fields();
                while (it.hasNext()) {
                    Entry<String, JsonNode> item = it.next();
                    substituteVariables(item.getValue(),node, item.getKey());

                }
            }

        }
        private Map<String, Object> loadJsonMapConfig(String configName) {
            JsonNode configJsonNode = null;
            Map<String, Object> config = null;
            String configFilename = configName + CONFIG_EXT_JSON;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    configJsonNode = mapper.readValue(inStream, JsonNode.class);
                    substituteVariables(configJsonNode, null, null);
                    config = mapper.convertValue(configJsonNode, new TypeReference<HashMap<String, Object>>() {});
                    //config = mapper.readValue(inStream, new TypeReference<HashMap<String, Object>>() {});
                }
            } catch (IOException ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return config;
        }
        /*private Map<String, Object> loadYamlMapConfig(String configName) {
            Map<String, Object> config = null;
            String configFilename = configName + CONFIG_EXT_YAML;
            InputStream inStream = null;
            try {
                inStream = getConfigStream(configFilename);
                if(inStream != null) {
                    config = yamlMapper.readValue(inStream, new TypeReference<HashMap<String, Object>>() {});
                }
            } catch (IOException ioe) {
                //logger.catching(ioe);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException ioe) {
                        //logger.catching(ioe);
                    }
                }
            }
            return config;
        }*/

        /**
         * Load configuration file in the following sequence.
         * 1. From database if configDb.json exists and db can be connected and config can be loaded.
         * 1. From classpath for file system based config
         * 2. From app resources /config/dev for file system based config
         * 3. From module resources /config for file system based config
         */
        private InputStream getConfigStream(String configFilename) {
            if (!validateConfigFilename(configFilename)) {
                return null;
            }
            String env = System.getProperty(API_ENV, API_ENV_DEV);
            InputStream inStream = null;


            // load from system classpath first. Externalized on the target deployment environment
            inStream = getConfigStreamFromDirectory(configFilename, EXTERNAL_FWK_PROPERTY_DIR);
            if (inStream != null) {
                return inStream;
            }
            else {
                inStream = getConfigStreamFromDirectory(configFilename, EXTERNAL_LOB_PROPERTY_DIR);
                if (inStream != null)
                    return inStream;
            }

            //logger.info("Trying to load config from classpath directory for file " + Encode.forJava(configFilename));
            inStream = getClass().getClassLoader().getResourceAsStream(configFilename);
            if(inStream != null) {
                //if(logger.isInfoEnabled()) {
                    //logger.info("config loaded from classpath for " + Encode.forJava(configFilename));
                //}
                return inStream;
            }
            // load from app config/dev
            //logger.info("Trying to load config from application config directory " + "config/" + Encode.forJava(env + "/" + configFilename));
            inStream = getClass().getClassLoader().getResourceAsStream("config/" + env + "/" + configFilename);
            if(inStream != null) {
                /*if(logger.isInfoEnabled()) {
                    logger.info("config loaded from app environmental folder in resources for " + Encode.forJava(configFilename + " in " + env));
                }*/
                return inStream;
            }
            //logger.info("Trying to load config from module config directory " + "config/" + Encode.forJava(configFilename));
            inStream = getClass().getClassLoader().getResourceAsStream("config/" + configFilename);
            if(inStream != null) {
                // couldn't load it from classpath or app resource, then use the one from resource /config folder.
                /*if(logger.isInfoEnabled()) {
                    logger.info("config loaded from module resources for " + Encode.forJava(configFilename));
                }*/
                return inStream;
            }
            //logger.error("Unable to load config " + Encode.forJava(configFilename));
            return inStream;
        }
        private InputStream getConfigStreamFromDirectory(String configFilename, String dir) {

            InputStream inStream = null;

            //logger.info("Trying to load config from system properties directory " + Encode.forJava(dir + "/" + configFilename));
            try{
                inStream = new FileInputStream(dir + "/" + configFilename);
                if (inStream != null) {
                    /*if(logger.isInfoEnabled()) {
                        logger.info("Config loaded from external filesystem directory for " + Encode.forJava(configFilename + " in " + dir));
                    }*/
                }
            } catch (FileNotFoundException ex){
                /*if(logger.isInfoEnabled()) {
                    logger.info("Config not found in filesystem.");
                }*/
            }
            return inStream;
        }

        private boolean validateConfigFilename(String configFilename) {
            if (configFilename.matches(".*[^-_/.A-Za-z0-9].*")) {
                //logger.error("Invalid chars in filename: " + Encode.forJava(configFilename));
                return false;
            };
            if (configFilename.contains("..")) {
                //logger.error(".. not allowed in filename: " + Encode.forJava(configFilename));
                return false;
            }
            return true;
        }
        /**
         * based on current system date to get next day's midnight time
         * @return next mid night time
         */
        private static long getNextMidNightTime() {
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            // next day
            cal.add(Calendar.DAY_OF_MONTH, 1);
            return cal.getTimeInMillis();
        }
        private void checkCacheExpiration() {
            if(System.currentTimeMillis() > cacheExpirationTime) {
                //it's time to clear whole cache for all configurations.
                clear();
                //logger.info("daily config cache refresh");
                //update cacheExpirationTime to next cache expiration time which is Next midnight time based on current system date.
                cacheExpirationTime = getNextMidNightTime();
            }
        }
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

