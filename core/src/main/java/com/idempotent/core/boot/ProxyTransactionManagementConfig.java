
/*
 * File Name:com.sdd.asker.utils.idempotent.ProxyTransactionManagementConfig is created on 2023/3/302:27 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.boot;

import java.util.LinkedHashSet;
import java.util.Set;

import com.idempotent.core.interceptor.IdempotentTransactionAnnotationParser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;

/**
 * @author liuzongliang
 * @Description:
 * @date: 2023/3/23 4:43 下午
 * @since JDK 1.8
 */
@Configuration(proxyBeanMethods = false)
public class ProxyTransactionManagementConfig extends AbstractTransactionManagementConfiguration {
    private static final boolean jta12Present;

    private static final boolean ejb3Present;

    static {
        ClassLoader classLoader = AnnotationTransactionAttributeSource.class.getClassLoader();
        jta12Present = ClassUtils.isPresent("javax.transaction.Transactional", classLoader);
        ejb3Present = ClassUtils.isPresent("javax.ejb.TransactionAttribute", classLoader);
    }

    @Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
        TransactionAttributeSource transactionAttributeSource,
        TransactionInterceptor transactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        advisor.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionAttributeSource transactionAttributeSource() {
        Set<TransactionAnnotationParser> annotationParsers = new LinkedHashSet<>(4);
        annotationParsers.add(new SpringTransactionAnnotationParser());
        annotationParsers.add(new IdempotentTransactionAnnotationParser());
        if (jta12Present) {
            annotationParsers.add(new JtaTransactionAnnotationParser());
        }
        if (ejb3Present) {
            annotationParsers.add(new Ejb3TransactionAnnotationParser());
        }
        return new AnnotationTransactionAttributeSource(annotationParsers);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionInterceptor transactionInterceptor(
        TransactionAttributeSource transactionAttributeSource) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(transactionAttributeSource);
        if (this.txManager != null) {
            interceptor.setTransactionManager(this.txManager);
        }
        return interceptor;
    }
}