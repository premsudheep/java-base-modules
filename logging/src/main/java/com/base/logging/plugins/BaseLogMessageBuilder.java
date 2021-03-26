package com.base.logging.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.base.commons.exceptions.ApiException;
import com.base.commons.model.exception.Status;
import com.base.commons.types.BaseErrorType;
import com.base.logging.BaseLogger;
import com.base.logging.model.LogEventMetaData;
import com.base.logging.model.PerfLogEventMetaData;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@Plugin(name = "BaseLogMessageBuilder", category = "Converter")
@ConverterKeys({"baseLogMessageBuilder"})
public class BaseLogMessageBuilder extends LogEventPatternConverter {
    protected BaseLogMessageBuilder(String name, String style) {
        super(name, style);
    }
    public static BaseLogMessageBuilder newInstance(final String[] options) {
        return new BaseLogMessageBuilder("baseLogMessageBuilder", "baseLogMessageBuilder");
    }
    @Override
    public void format(LogEvent logEvent, StringBuilder stringBuilder) {
        Level activeLevel = logEvent.getLevel();
        appendMDContextContent(logEvent, stringBuilder);

        if(logEvent.getLevel().equals(BaseLogger.PERF)) {
            buildPerformanceLogMessage(logEvent, stringBuilder);
            //Return the execution at this point as the PERF log message formatter had executed all the required logic for
            //logging the PERF event
            return;
        }
        else if(Level.ERROR.equals(activeLevel) || Level.WARN.equals(activeLevel)) {
            buildErrorDetails(logEvent, stringBuilder);
            return;
        }
        appendLogEventMetaData(logEvent, stringBuilder);
        stringBuilder.append(" | message="+logEvent.getMessage().getFormattedMessage());

    }

    private void buildPerformanceLogMessage(LogEvent logEvent, StringBuilder stringBuilder) {
        Object[] parameters = logEvent.getMessage().getParameters();
        if(parameters != null) {
            for(Object parameter:parameters) {
                if(parameter instanceof PerfLogEventMetaData) {
                    PerfLogEventMetaData perfLogEventMetaData = (PerfLogEventMetaData)parameter;
                    if(perfLogEventMetaData.getCategory() != null && perfLogEventMetaData.getCategory().trim().length() > 0) {
                        stringBuilder.append(" | PerfCategory="+perfLogEventMetaData.getCategory());
                    }
                    else {
                        stringBuilder.append(" | PerfCategory=UNDEFINED");
                    }

                    if(perfLogEventMetaData.getGroup() != null && perfLogEventMetaData.getGroup().trim().length() > 0) {
                        stringBuilder.append(" | PerfGroup="+perfLogEventMetaData.getGroup());
                    }
                    else {
                        stringBuilder.append(" | PerfGroup=UNDEFINED");
                    }

                    if(perfLogEventMetaData.getSubGroup() != null && perfLogEventMetaData.getSubGroup().trim().length() > 0) {
                        stringBuilder.append(" | PerfSubGroup="+perfLogEventMetaData.getSubGroup());
                    }
                    else {
                        stringBuilder.append(" | PerfSubGroup=UNDEFINED");
                    }

                    if(perfLogEventMetaData.getOperationURI() != null && perfLogEventMetaData.getOperationURI().trim().length() > 0) {
                        stringBuilder.append(" | PerfOperationURI="+perfLogEventMetaData.getOperationURI());
                    }
                    else {
                        stringBuilder.append(" | PerfOperationURI=UNDEFINED");
                    }

                    if(perfLogEventMetaData.getEventStatus() != null) {
                        stringBuilder.append(" | PerfOperationStatus="+perfLogEventMetaData.getEventStatus().getValue());
                    }
                    else {
                        stringBuilder.append(" | PerfOperationStatus=UNDEFINED");
                    }

                    if(perfLogEventMetaData.getEndTime() != null) {
                        stringBuilder.append(" | PerfOperationDuration="+(perfLogEventMetaData.getEndTime()-perfLogEventMetaData.getStartTime()));
                    }
                    else {
                        stringBuilder.append(" | PerfOperationDuration=-1");
                    }
                }
            }
        }
    }

    private void appendMDContextContent(LogEvent logEvent, StringBuilder stringBuilder) {
        ReadOnlyStringMap readOnlyStringMap = logEvent.getContextData();
        if(readOnlyStringMap.getValue("UserIP") != null) {
            stringBuilder.append(" | userIP="+readOnlyStringMap.getValue("UserIP"));
        }
        if(readOnlyStringMap.getValue("Tracebilityid") != null) {
            stringBuilder.append(" | sessionID="+readOnlyStringMap.getValue("Tracebilityid"));
        }
        if(readOnlyStringMap.getValue("MessageID") != null) {
            stringBuilder.append(" | messageID="+readOnlyStringMap.getValue("MessageID"));
        }
        if(readOnlyStringMap.getValue("RequestURI") != null) {
            stringBuilder.append(" | requestURI="+readOnlyStringMap.getValue("RequestURI"));
        }
        if(readOnlyStringMap.getValue("appIDMDCContextKey") != null) {
            stringBuilder.append(" | appID="+readOnlyStringMap.getValue("appIDMDCContextKey"));
        }
        if(readOnlyStringMap.getValue("subAppIDMDCContextKey") != null) {
            stringBuilder.append(" | subAppID="+readOnlyStringMap.getValue("subAppIDMDCContextKey"));
        }
        if(readOnlyStringMap.getValue("appRefNoMDCContextKey") != null) {
            stringBuilder.append(" | appRefNo="+readOnlyStringMap.getValue("appRefNoMDCContextKey"));
        }
        if(readOnlyStringMap.getValue("productIDMDCContextKey") != null) {
            stringBuilder.append(" | prodID="+readOnlyStringMap.getValue("productIDMDCContextKey"));
        }
        if(readOnlyStringMap.getValue("connectIDMDCContextKey") != null) {
            stringBuilder.append(" | connectID="+readOnlyStringMap.getValue("connectIDMDCContextKey"));
        }
    }

    public void appendLogEventMetaData(LogEvent logEvent, StringBuilder stringBuilder) {
        Object[] parameters = logEvent.getMessage().getParameters();
        if(parameters != null) {
            for(Object parameter:parameters) {
                if(parameter instanceof LogEventMetaData) {
                    LogEventMetaData logEventMetaData = (LogEventMetaData) parameter;
                    Set<String> keySet = logEventMetaData.keySet();
                    for(String key:keySet) {
                        stringBuilder.append(" | "+key+"="+logEventMetaData.get(key));
                    }
                }
            }
        }
    }

    private void buildErrorDetails(LogEvent logEvent, StringBuilder stringBuilder) {
        List<String> errorCodes = new ArrayList<>();

        if (logEvent.getThrown() != null) {
            stringBuilder.append(" | exceptionType=").append(logEvent.getThrown().getClass().getSimpleName());
            stringBuilder.append(" | exceptionMessage=").append(logEvent.getThrown().getMessage());
            if(logEvent.getThrown() instanceof ApiException) {
                Status status = ((ApiException)logEvent.getThrown()).getStatus();
                if(status != null) {
                    stringBuilder.append(" | serverStatusCode=").append(status.getServerStatusCode());
                    if(status.getAdditionalStatuses() != null && status.getAdditionalStatuses().length > 0) {
                        for(int i = 0; i < status.getAdditionalStatuses().length; i++) {
                            errorCodes.add(status.getAdditionalStatuses()[i].getServerStatusCode());
                        }
                    }
                }
            }
        }


        Object[] parameters = logEvent.getMessage().getParameters();

        if(parameters!= null && parameters.length > 0) {
            for(Object parameter:parameters) {
                if(parameter instanceof Status) {
                    Status status = (Status) parameter;
                    stringBuilder.append(" | serverStatusCode=").append(status.getServerStatusCode());
                    if(status.getAdditionalStatuses() != null && status.getAdditionalStatuses().length > 0) {
                        for(int i = 0; i < status.getAdditionalStatuses().length; i++) {
                            errorCodes.add(status.getAdditionalStatuses()[i].getServerStatusCode());
                        }
                    }
                }
                if(parameter instanceof BaseErrorType) {
                    BaseErrorType baseErrorType = (BaseErrorType) parameter;
                    errorCodes.add(baseErrorType.getValue());
                }
            }
        }

        if(errorCodes.size() > 0) {
            StringBuilder additionalServerStatusCodesStringBuilder = new StringBuilder();
            additionalServerStatusCodesStringBuilder.append(" | errorCodes=[");
            for(int i = 0; i < errorCodes.size(); i++) {
                additionalServerStatusCodesStringBuilder.append("\"").append(errorCodes.get(i)).append("\"");
                if(i == (errorCodes.size()-1)) {
                    additionalServerStatusCodesStringBuilder.append("]");
                }
                else {
                    additionalServerStatusCodesStringBuilder.append(",");
                }
            }
            stringBuilder.append(additionalServerStatusCodesStringBuilder);
        }
        stringBuilder.append(" | message="+logEvent.getMessage().getFormattedMessage());

        if (logEvent.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            logEvent.getThrown().printStackTrace(printWriter);
            stringBuilder.append("\n"+stringWriter.toString());
        }
    }




}

