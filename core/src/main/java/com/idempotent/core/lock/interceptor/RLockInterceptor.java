
/*
 * File Name:com.sdd.asker.cache.aspect.RLockInterceptor is created on 2023/3/303:22 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.lock.interceptor;

import java.lang.reflect.Method;

import com.idempotent.core.annotation.Rlock;
import com.idempotent.core.lock.exception.FrequentRequestsException;
import com.idempotent.core.util.KeyGenerator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuzongliang
 * @Description: @Rlock 增强拦截器
 * @date: 2023/3/30 3:22 下午
 * @since JDK 1.8
 */
@Slf4j
public class RLockInterceptor implements MethodInterceptor {
    private static final KeyGenerator keyGenerator = new KeyGenerator();

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object object = null;
        RLock lock = null;
        String key = null;
        try {
            ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation)invocation;
            final ProceedingJoinPoint proceedingJoinPoint =
                new MethodInvocationProceedingJoinPoint(proxyMethodInvocation);
            // 获取注解信息
            Method method = invocation.getMethod();
            final Rlock rlockInfo = AnnotatedElementUtils.getMergedAnnotation(method, Rlock.class);
            if (!rlockInfo.enableLock()) {
                return proceedingJoinPoint.proceed();
            }
            // 根据名字获取锁实例
            key = keyGenerator.generate(proceedingJoinPoint, rlockInfo.prefix(), rlockInfo.argNames(),
                rlockInfo.argsAssociated()).toString();
            lock = redissonClient.getLock(key);
            if (lock != null) {
                final boolean status = lock.tryLock(rlockInfo.waitTime(), rlockInfo.leaseTime(), rlockInfo.timeUnit());
                if (status) {
                    object = proceedingJoinPoint.proceed();
                } else {
                    throw new FrequentRequestsException();
                }
            }
        } finally {
            if (lock != null) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("unlock redisson lock key:{} ,error:{}", key, e);
                }
            }
        }
        return object;
    }
}