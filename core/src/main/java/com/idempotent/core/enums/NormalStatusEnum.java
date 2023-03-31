
/*
 * File Name:com.idempotent.core.enums.NormalStatusEnum is created on 2023/3/313:22 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liuzongliang
 * @Description:
 * @date: 2023/3/31 3:22 下午
 * @since JDK 1.8
 */
public enum NormalStatusEnum {
    VALID(0, "正常"),
    INVALID(1, "无效");

    private Integer code;
    private String msg;
    private static final Map<Integer, NormalStatusEnum>
        MAP = (Map)Arrays.stream(values()).collect(Collectors.toMap(NormalStatusEnum::getCode, Function.identity()));

    public static Boolean isCode(NormalStatusEnum normalStatusEnum, Integer code) {
        return MAP.get(code) == normalStatusEnum;
    }

    public static NormalStatusEnum getByCode(Integer code) {
        return (NormalStatusEnum)MAP.get(code);
    }

    private NormalStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
