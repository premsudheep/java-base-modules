package com.base.logging.aspects;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.base.logging.BaseLogger;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.spi.StandardLevel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
public class BaseLoggableAspect {
    BaseLogger logger = BaseLogger.create(BaseLoggableAspect.class);
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}
    @Pointcut("execution(* *(..))")
    public void method() {}

    @Around("execution(* *(..)) && @annotation(com.base.logging.annotations.BaseLoggable)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //Bypass logging if log level weight is less than debug integer level
        if(logger.getLevelWeight() < StandardLevel.DEBUG.intLevel()) {
            return point.proceed();
        }

        String methodName = null;
        String className = null;

        //Try Catch Block to ensure to reflection associated exception can effect the normal execution of any flow
        try {
            Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
            methodName = method.getName();
            className = method.getDeclaringClass().getSimpleName();
            logger.debug("About to Execute Class:" + className + " with a call to Method:" + methodName + "");

            //Print More Trace Level Details in case trace logging is enabled (Arguments)
            if(logger.isTraceEnabled()) {
                Object[] arguments =  point.getArgs();
                if(arguments != null) {
                    for(Object argument:arguments) {
                        logger.trace("Method argument of type "+argument.getClass()+" and value:"+argument);
                    }
                }
            }
        } catch (Exception e1) {/*DO NOTHING*/}

        Object executionReturnedObject;
        try {
            executionReturnedObject = point.proceed();
        } catch (Throwable e) {
            logger.debug("Error with the Execution of Class:" + className + ",  with a call to Method:" + methodName + ", Exception MSG:"+e.getMessage());
            throw e;
        }

        //Try Catch Block to ensure to reflection associated exception can effect the normal execution of any flow
        try {
            //Print More Trace Level Details in case trace logging is enabled (Returned Objects)
            if(logger.isTraceEnabled()) {
                if(executionReturnedObject != null) {
                    logger.trace("Method returned an object of type "+executionReturnedObject.getClass()+" and value:"+executionReturnedObject);
                }
            }
        } catch (Exception e1) {/*DO NOTHING*/}

        logger.debug("Successfull End of the Execution of Class:" + className + " with a call to Method:" + methodName + "");
        return executionReturnedObject;
    }

    // before -> Any resource annotated with @Controller annotation
    @Before("controller() || restController() && method()")
    public void logBefore(JoinPoint joinPoint) {
        try {
            if(logger.isDebugEnabled()) {
                logger.debug("Entering in Method :  " + joinPoint.getSignature().getName());
                logger.debug("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
                logger.debug("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
                logger.debug("Target class : " + joinPoint.getTarget().getClass().getName());
                Object[] arguments =  joinPoint.getArgs();
                if(arguments != null) {
                    for(Object argument:arguments) {
                        String request = (null != argument)?convertObjectToJson(argument):null;
                        logger.debug("Method argument of type "+argument.getClass()+" and value:"+request);
                    }
                }
            }
        }catch (Exception ex) {
            logger.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " @Before " + ex);
        }
    }

    //This will be called for all controller methods after returning
    @AfterReturning(pointcut = "controller() || restController() && method()", returning="result")
    public void afterReturning(JoinPoint joinPoint , Object result)  {
        long start = System.nanoTime();
        try {
            if(logger.isDebugEnabled()) {
                logger.debug("Response sent by " + joinPoint.getSignature().getName() + " are : " + getValue(result));
            }
        } catch(Exception ex) {
            logger.error("Returned result cant be converted in JSON " , ex);
        }
        long end = System.nanoTime();
        logger.info("elapsed time : " + (end - start));
    }

    private String getValue(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }

    public static BaseLoggableAspect aspectOf(){
        return  new BaseLoggableAspect();
    }

    public String convertObjectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("An exception has been thrown in BaseLoggableAspect::Couldn't serialize JSON object");
        }
        return jsonString;
    }
}

