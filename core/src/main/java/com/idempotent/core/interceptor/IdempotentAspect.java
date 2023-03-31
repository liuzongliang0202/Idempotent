
/*
 * File Name:com.sdd.asker.utils.idempotent.IdempotentLockAspect is created on 2023/3/303:02 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idempotent.core.annotation.Idempotent;
import com.idempotent.core.enums.NormalStatusEnum;
import com.idempotent.core.store.IdempotentLogDO;
import com.idempotent.core.store.LogStore;
import com.idempotent.core.util.EncryptAndDecryptUtils;
import com.idempotent.core.util.JoinPointUtils;
import com.idempotent.core.util.KeyGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author liuzongliang
 * @Description: 幂等锁
 * @date: 2023/3/30 3:02 下午
 * @since JDK 1.8
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 50)
@Component
public class IdempotentAspect {
    private static final KeyGenerator keyGenerator = new KeyGenerator();

    ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private LogStore logStore;

    @Pointcut("@annotation(com.idempotent.core.annotation.Idempotent)")
    public void pointcut() {
    }

    /**
     * 幂等性判断和记录
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = JoinPointUtils.getMethod(proceedingJoinPoint);
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        String idempotentValue = keyGenerator
            .generate(proceedingJoinPoint, idempotent.prefix(), idempotent.argNames(), idempotent.argsAssociated()).toString();
        String transactionSignature = idempotent.prefix();
        final String[] argNames = idempotent.argNames();
        String[] paramList = new String[argNames.length];
        int index = 0;
        for (; index < argNames.length; index++) {
            String param = argNames[index];
            if (param.contains(".")) {
                paramList[index] = param.substring(param.lastIndexOf("."));
            } else {
                paramList[index] = param;
            }
        }
        String idempotentParamList = KeyGenerator.appendObject(new StringBuilder(), paramList).toString();
        String idempotentValueHash = EncryptAndDecryptUtils.md5Encrypt(idempotentValue);
        byte[] fullParamValue = objectMapper.writeValueAsBytes(proceedingJoinPoint.getArgs());
        final List<IdempotentLogDO> idempotentLogDOS = logStore.queryByParam(transactionSignature,
            idempotentValueHash, idempotentValue, NormalStatusEnum.VALID.getCode());
        // 正确处理过就直接返回原来数据
        if (CollectionUtils.isNotEmpty(idempotentLogDOS)) {
            final IdempotentLogDO idempotentLog = idempotentLogDOS.get(0);
            final Object readValue = objectMapper.readValue(idempotentLog.getReturnValue(), method.getReturnType());
            return readValue;
        }
        // 处理业务
        final Object proceed = proceedingJoinPoint.proceed();

        final TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        if (!transactionStatus.isRollbackOnly()) {
            // 如果没有异常和回滚就认为本次调用处理成功
            byte[] returnValue = objectMapper.writeValueAsBytes(proceed);
            IdempotentLogDO idempotentLogDO = new IdempotentLogDO();
            idempotentLogDO.setTransactionSignature(transactionSignature);
            idempotentLogDO.setIdempotentParamList(idempotentParamList);
            idempotentLogDO.setIdempotentValueHash(idempotentValueHash);
            idempotentLogDO.setIdempotentValue(idempotentValue);
            idempotentLogDO.setFullParamValue(fullParamValue);
            idempotentLogDO.setReturnValue(returnValue);
            idempotentLogDO.setGmtCreate(System.currentTimeMillis()/1000);
            idempotentLogDO.setStatus(NormalStatusEnum.VALID.getCode());
            final int insert = logStore.insert(idempotentLogDO);
            if (insert <= 0) {
                transactionStatus.setRollbackOnly();
            }
        }

        return proceed;
    }


    
}