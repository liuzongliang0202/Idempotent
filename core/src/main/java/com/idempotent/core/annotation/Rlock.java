
/*
 * File Name:com.idempotent.core.annotation.Rlock is created on 2023/3/313:19 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * @author liuzongliang
 * @Description: lock注解
 * @date: 2023/3/22 10:14 下午
 * @since JDK 1.8
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Rlock {

    /**
     * 等待时间 默认五秒
     *
     * @return
     */
    long waitTime()

        default 5;

    /**
     * 锁释放时间 默认十秒
     *
     * @return
     */
    long leaseTime()

        default 10;

    /**
     * 时间格式 默认：秒
     *
     * @return
     */
    TimeUnit timeUnit()

        default TimeUnit.SECONDS;

    /**
     * 关注目标方法参数<br/>
     * 设置为false时, 粒度较大, 相同方法不同参数也为同一把锁
     */
    boolean argsAssociated() default true;

    /**
     * 参数列表<br/>
     * 默认全部参数
     */
    String[] argNames() default {};

    /**
     * key前缀
     * @return
     */
    String prefix() default "";

    /**
     * 是否开启锁，默认开启，主要是给元注解使用
     * @return
     */
    boolean enableLock() default true;
}
