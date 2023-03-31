
/*
 * File Name:com.sdd.asker.utils.annoation.IdempotentRelease is created on 2023/3/319:49 上午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.annotation;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;

/**
 * @author liuzongliang
 * @Description: 幂等日志释放
 * @date: 2023/3/31 9:49 上午
 * @since JDK 1.8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Rlock
public @interface IdempotentRelease {
    /**
     * 锁和幂等唯一前缀
     * @return
     */
    @AliasFor(annotation = Rlock.class)
    String prefix() default "";

    /**
     * 关注目标方法参数<br/>
     * 设置为false时, 粒度较大, 相同方法不同参数也为同一把锁
     */
    @AliasFor(annotation = Rlock.class)
    boolean argsAssociated() default true;

    /**
     * 参数列表<br/>
     * 默认全部参数
     */
    @AliasFor(annotation = Rlock.class)
    String[] argNames() default {};
}
