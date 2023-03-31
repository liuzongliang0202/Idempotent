
/*
 * File Name:com.sdd.asker.utils.idempotent.IdempotentTransactionAnnotationParser is created on 2023/3/302:26 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.interceptor;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import com.idempotent.core.annotation.Idempotent;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author liuzongliang
 * @Description: 幂等注解事务解析器
 * @date: 2023/3/23 4:45 下午
 * @since JDK 1.8
 */
@SuppressWarnings("serial")
public class IdempotentTransactionAnnotationParser implements TransactionAnnotationParser, Serializable {
    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, Idempotent.class);
    }

    @Override
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        AnnotationAttributes idempotentAttributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
            element, Idempotent.class, false, false);
        if (idempotentAttributes == null) {
            return null;
        }
        final boolean enableTransaction = idempotentAttributes.getBoolean("enableTransaction");
        if (!enableTransaction) {
            return null;
        }
        AnnotationAttributes transactionalAttributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
            element, Idempotent.Transactional.class, false, false);
        if (transactionalAttributes == null) {
            return null;
        }
        return parseTransactionAnnotation(transactionalAttributes);
    }

    public TransactionAttribute parseTransactionAnnotation(Idempotent.Transactional ann) {
        return parseTransactionAnnotation(AnnotationUtils.getAnnotationAttributes(ann, false, false));
    }

    protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

        Propagation propagation = attributes.getEnum("propagation");
        rbta.setPropagationBehavior(propagation.value());
        Isolation isolation = attributes.getEnum("isolation");
        rbta.setIsolationLevel(isolation.value());
        rbta.setTimeout(attributes.getNumber("timeout").intValue());
        rbta.setReadOnly(attributes.getBoolean("readOnly"));
        rbta.setQualifier(attributes.getString("value"));

        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
            rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
        }
        for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
            rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
        }
        rbta.setRollbackRules(rollbackRules);

        return rbta;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || other instanceof IdempotentTransactionAnnotationParser);
    }

    @Override
    public int hashCode() {
        return IdempotentTransactionAnnotationParser.class.hashCode();
    }
}