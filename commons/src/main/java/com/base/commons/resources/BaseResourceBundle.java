package com.base.commons.resources;

import com.base.commons.exceptions.BaseException;

import java.util.Properties;
import com.base.commons.resources.BaseResourceHandler;

public abstract class BaseResourceBundle {
    private Properties properties;
    private String resourceBundleBaseName;

    protected BaseResourceBundle(String resourceBundleBaseName) {
        this.resourceBundleBaseName = resourceBundleBaseName;
        loadResources();
    }

    private void loadResources() {
        try {
            System.out.println("Loading locale neutral resource"+resourceBundleBaseName);
            properties = BaseResourceHandler.getInstance().getPropertiesConfig(resourceBundleBaseName);
        } catch (BaseException e) {
            System.out.println("errpr"+e);
        }
    }

    public String getProperties(String key) {
        if(properties != null) {
            return properties.getProperty(key);
        }
        return null;
    }

    public String getProperties(String key, String defaultValue) {
        if(properties != null) {
            return properties.getProperty(key, defaultValue);
        }
        return null;
    }
}
