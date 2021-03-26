package com.base.logging;

import java.io.IOException;
import java.io.InputStream;

import com.base.commons.types.BaseErrorType;
import com.base.commons.utils.StringUtils;
import com.base.logging.model.LogEventMetaData;
import com.base.logging.model.PerfLogEventMetaData;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

public class BaseLogger {
    private ExtendedLoggerWrapper logger;
    private static final String FQCN = BaseLogger.class.getName();
    public static final Level PERF = Level.forName("PERF", 150);
    private static final Level MESSAGE = Level.forName("MESSAGE",250);
    //Monitor level to be used for activities such as content caching meta-data, schedulers start/end, singleton initialization etc.
    private static final Level MONITOR = Level.forName("MONITOR",350);
    private BaseLogger(Logger logger){
        this.logger = new ExtendedLoggerWrapper((AbstractLogger) logger, logger.getName(), logger.getMessageFactory());
    }
    public static BaseLogger create(final Class<?> loggerName) {
        final Logger wrapped = LogManager.getLogger(loggerName);
        return new BaseLogger(wrapped);
    }
    public void trace(String msg, Object... params) {
        logger.logIfEnabled(FQCN, Level.TRACE, null, msg, params);
    }
    public void debug(String msg) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, msg);
    }
    public void debug(String msg, Object...params) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, msg, params);
    }
    public void info(String msg) {
        logger.logIfEnabled(FQCN, Level.INFO, null, msg);
    }
    public void info(String msg, Object... params) {
        logger.logIfEnabled(FQCN, Level.INFO, null, msg, params);
    }
    public void info(String msg, LogEventMetaData logEventMetaData) {
        logger.logIfEnabled(FQCN, Level.INFO, null, msg, logEventMetaData);
    }
    public void warn(String msg) {
        logger.logIfEnabled(FQCN, Level.WARN, null, msg);
    }
    public void warn(String msg, Object... params) {
        logger.logIfEnabled(FQCN, Level.WARN, null, msg, params);
    }
    public void warn(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.WARN, null, msg, e);
    }
    public void error(String msg) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, msg);
    }
    public void error(String msg, Object... params) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, msg, params);
    }
    public void error(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, msg, e);
    }
    public void error(String msg, BaseErrorType errorType, Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, msg +  " ErrorType = [{}], Exception = {}", errorType, e);
    }
    public void error(String msg, BaseErrorType errorType) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, msg + " ErrorType = [{}] ", errorType);
    }
    public void error(String msg, BaseErrorType ... errorType) {
        if(errorType == null)
            error(msg);
        else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < errorType.length; i++)
                builder.append("{}, ");
            String placeholders = builder.toString();
            if(StringUtils.isNotEmpty(placeholders) && placeholders.endsWith(", "))
                placeholders = placeholders.substring(0, placeholders.length() - 2);
            logger.logIfEnabled(FQCN, Level.ERROR, null, msg + " ErrorType = [" + placeholders + "]", errorType);
        }
    }
    public void fatal(String msg) {
        logger.logIfEnabled(FQCN, Level.FATAL, null, msg);
    }
    public void monitor(String msg) {
        logger.logIfEnabled(FQCN, MONITOR, null, msg);
    }
    public void message(String msg) {
        logger.logIfEnabled(FQCN, MESSAGE, null, msg);
    }
    public void perf(PerfLogEventMetaData perfLogEventMetaData) {
        logger.logIfEnabled(FQCN, PERF, null,"PERF_PLACEHOLDER", perfLogEventMetaData);
    }
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
    public int getLevelWeight() {
        return logger.getLevel().intLevel();
    }
    public static void reconfigure(InputStream inputStream){
        try {
            LoggerContext loggerContext  = (LoggerContext) LogManager.getContext(false);
            ConfigurationSource configurationSource;
            configurationSource = new ConfigurationSource(inputStream);
            XmlConfiguration xmlConfig = new XmlConfiguration(loggerContext,configurationSource);
            loggerContext.reconfigure();
            loggerContext.start(xmlConfig);
        } catch (IOException e) {
            //Even if the configuration failed the default logger should be used instead which should print to the server logs using the default log4j settings & message layout
            BaseLogger.create(BaseLogger.class).error("Error in reconfiguring log4j",e);
        } catch (Exception e) {
            //Even if the configuration failed the default logger should be used instead which should print to the server logs using the default log4j settings & message layout
            BaseLogger.create(BaseLogger.class).error("Error in reconfiguring log4j",e);
        }
    }
}

