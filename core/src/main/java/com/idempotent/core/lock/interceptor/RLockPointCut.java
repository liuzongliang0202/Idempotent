
/*
 * File Name:com.sdd.asker.cache.aspect.RLockPointCut is created on 2023/3/303:12 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.lock.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.idempotent.core.annotation.Rlock;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author liuzongliang
 * @Description: @Rlock 切点
 * @date: 2023/3/30 3:12 下午
 * @since JDK 1.8
 */
public class RLockPointCut implements Pointcut {
    private ClassFilter classFilter;
    private MethodMatcher methodMatcher;
    private Class<? extends Annotation> annotationType;

    @Override
    public ClassFilter getClassFilter() {
        return classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public RLockPointCut() {
        annotationType = Rlock.class;
        classFilter = new RLockClassFilter();
        methodMatcher = new RLockMethodMatcher();
    }

    public class RLockClassFilter implements ClassFilter {

        @Override
        public boolean matches(Class<?> clazz) {
            return AnnotationUtils.isCandidateClass(clazz, annotationType);
        }
    }

    public class RLockMethodMatcher extends StaticMethodMatcher {

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return AnnotatedElementUtils.isAnnotated(method, annotationType) || AnnotatedElementUtils
                .hasMetaAnnotationTypes(method, annotationType);
        }
    }
}