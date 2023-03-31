
/*
 * File Name:com.sdd.asker.utils.idempotent.IdempotentLogDO is created on 2023/3/305:06 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.store;

import java.util.Arrays;

import com.idempotent.core.enums.NormalStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuzongliang
 * @Description: 幂等性日志
 * @date: 2023/3/30 5:06 下午
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotentLogDO {
    /**
     * ID
     */
    private Long id;
    /**
     * 业务签名
     */
    private String transactionSignature;
    /**
     * 幂等参数列表
     */
    private String idempotentParamList;
    /**
     * 幂等参数值hash值
     */
    private String idempotentValueHash;
    /**
     * 幂等参数值
     */
    private String idempotentValue;
    /**
     * 全部参数值
     */
    private byte[] fullParamValue;
    /**
     * 返回值
     */
    private byte[] returnValue;
    /**
     * 创建时间
     */
    private Long gmtCreate;
    /**
     * 状态
     * @see NormalStatusEnum
     */
    private Integer status;

    @Override
    public String toString() {
        return "IdempotentLogDO{" + "id=" + id + ", transactionSignature='" + transactionSignature + '\''
            + ", idempotentParamList='" + idempotentParamList + '\'' + ", idempotentValueHash='" + idempotentValueHash
            + '\'' + ", idempotentValue='" + idempotentValue + '\'' + ", fullParamValue=" + Arrays
            .toString(fullParamValue) + ", returnValue=" + Arrays.toString(returnValue) + ", gmtCreate=" + gmtCreate
            + ", status=" + status + '}';
    }
}