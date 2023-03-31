
/*
 * File Name:com.sdd.asker.utils.idempotent.LogStore is created on 2023/3/305:04 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.store;

import java.util.List;

/**
 * @author liuzongliang
 * @Description: 幂等日志存储操作
 * @date: 2023/3/30 5:04 下午
 * @since JDK 1.8
 */
public interface LogStore {
    /**
     * 插入幂等日志
     * @param idempotentLogDO
     * @return
     */
    int insert(IdempotentLogDO idempotentLogDO);

    /**
     * 查询幂等日志
     * @param transactionSignature
     * @param hash
     * @param idempotentArgs
     * @param status
     * @return
     */
    List<IdempotentLogDO> queryByParam(String transactionSignature, String hash, String idempotentArgs, Integer status);
}
