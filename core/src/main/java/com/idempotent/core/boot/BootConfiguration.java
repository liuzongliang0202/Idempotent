
/*
 * File Name:com.idempotent.core.boot.BootConfiguration is created on 2023/3/313:56 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.boot;

import org.springframework.context.annotation.ComponentScan;

/**
 * @author liuzongliang
 * @Description: spi加载
 * @date: 2023/3/31 3:56 下午
 * @since JDK 1.8
 */
@ComponentScan(basePackages = {"com.idempotent.core"})
public class BootConfiguration {
}