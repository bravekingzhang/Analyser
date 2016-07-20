package test.tencent.com.aop.aspect;


import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import test.tencent.com.aop.DebugLog;
import test.tencent.com.aop.StopWatch;

/**
 * Created by hoollyzhang on 16/7/20.
 * Description :
 */
@Aspect
public class TraceAspect {
    private static final String POINTCUT_METHOD      = "execution(@test.tencent.com.aop.anonation.DebugTrace * *(..))";//所有方法
    private static final String POINTCUT_CONSTRUCTOR = "execution(@test.tencent.com.aop.anonation.DebugTrace *.new(..))";//构造方法

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithDebugTrace() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedDebugTrace() {
    }


    @Around("methodAnnotatedWithDebugTrace()||constructorAnnotatedDebugTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        DebugLog.log(className, buildLogMessage(className + ":" + methodName, stopWatch.getTotalTimeMillis()));

        return result;
    }

    /*@Before("methodAnnotatedWithDebugTrace()||constructorAnnotatedDebugTrace()")
    public void before() throws Throwable {

        Log.e("DebugTrace", "xxxxxxxxxxxxxxx before");
    }


    @After("methodAnnotatedWithDebugTrace()||constructorAnnotatedDebugTrace()")
    public void after() throws Throwable {

        Log.e("DebugTrace", "xxxxxxxxxxxxxxx after");
    }*/

    
    /**
     * Create a log message.
     *
     * @param methodName     A string with the method name.
     * @param methodDuration Duration of the method in milliseconds.
     * @return A string representing message.
     */
    private static String buildLogMessage(String methodName, long methodDuration) {
        StringBuilder message = new StringBuilder();
        message.append("DebugTrace --> ");
        message.append(methodName);
        message.append(" --> ");
        message.append("[");
        message.append(methodDuration);
        message.append("ms");
        message.append("]");

        return message.toString();
    }
}
