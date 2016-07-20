package test.tencent.com.aop.aspect;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import test.tencent.com.aop.DebugLog;
import test.tencent.com.aop.anonation.PermissionCheck;

/**
 * Created by brzhang on 16/7/20.
 * Description :
 */

@Aspect
public class PermissionAspect {

    private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;

    //private static final String POINTCUT_METHOD_PERMISS_CHECK = "execution(@test.tencent.com.aop.anonation.PermissionCheck * *TakePhoto(..))";//所有方法已TakePhoto结尾的方法  ，不带参数的写发
    private static final String POINTCUT_METHOD_PERMISS_CHECK = "execution(@test.tencent.com.aop.anonation.PermissionCheck * *TakePhoto(..)) && @annotation(permissionCheck)";//所有方法已TakePhoto结尾的方法，这里的@annotation(permissionCheck) ，里面一定是首字母小写，否则失效


    @Pointcut(POINTCUT_METHOD_PERMISS_CHECK)
    public void methodAnnotatedWithtakePhoto(PermissionCheck permissionCheck) {
        //Log.e("PermissionCheck", "methodAnnotatedWithtakePhoto");
    }

    //PermissionCheck 参数一个切面函数有的话，所有的Before，after都要有，

    @Around("methodAnnotatedWithtakePhoto(permissionCheck)")
    public Object around(ProceedingJoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        if (permissionCheck.permession() != null && permissionCheck.permession().length != 0) {
            for (int i = 0; i < permissionCheck.permession().length; i++) {
                if (ContextCompat.checkSelfPermission((Context) joinPoint.getTarget(),
                        permissionCheck.permession()[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    DebugLog.log(className, className + ":" + methodName + "没有" + permissionCheck.permession()[i] + "的权限执行");
                }
            }
        }
        result = joinPoint.proceed();
        return result;
    }

    @Before("methodAnnotatedWithtakePhoto(permissionCheck)")
    public void before(JoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        DebugLog.log(className, className + ":" + methodName + "on before");

        if (permissionCheck.permession() == null || permissionCheck.permession().length == 0) {
            return;
        } else {
            boolean needRequestPermissions = false;
            List<String> stringList = new ArrayList<>(Arrays.asList(permissionCheck.permession()));
            for (int i = 0; i < permissionCheck.permession().length; i++) {
                DebugLog.log(className, "检查" + permissionCheck.permession()[i] + "权限，in before ");
                if (joinPoint.getTarget() instanceof FragmentActivity) {
                    if (ContextCompat.checkSelfPermission((Context) joinPoint.getTarget(),
                            permissionCheck.permession()[i])
                            != PackageManager.PERMISSION_GRANTED) {
                        DebugLog.log(className, "没有" + permissionCheck.permession()[i] + "权限，正在申请权限 in before");
                        needRequestPermissions = true;
                    } else {
                        DebugLog.log(className, "已经有" + permissionCheck.permession()[i] + "权限， in before");
                        stringList.remove(permissionCheck.permession()[i]);//不用申请这个权限，移除掉
                    }
                }
            }
            if (needRequestPermissions) {
                String[] needToRequestPermission = new String[stringList.size()];
                ActivityCompat.requestPermissions((Activity) joinPoint.getTarget(),
                        stringList.toArray(needToRequestPermission),
                        MY_PERMISSIONS_REQUEST_PERMISSION);
            }
        }
    }

    @After("methodAnnotatedWithtakePhoto(permissionCheck)")
    public void after(JoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        DebugLog.log(className, className + ":" + methodName + "on after");
    }

}
