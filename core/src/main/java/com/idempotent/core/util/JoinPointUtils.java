package com.idempotent.core.util;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * {@link}
 * 切面utils
 *
 * @author crz
 * @date 2018/10/12 下午4:41
 */
public class JoinPointUtils {

    public static Method getMethod(ProceedingJoinPoint joinPoint) {
        //获取方法签名
        String methodName = joinPoint.getSignature().getName();
        //获取目标类的所有方法
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method resultMethod = null;
        //查询当前调用的方法
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                //找到当前要执行的方法
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }
}
