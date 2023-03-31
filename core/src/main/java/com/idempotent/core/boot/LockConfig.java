
/*
 * File Name:com.idempotent.core.boot.LockConfig is created on 2023/3/313:52 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.boot;

import com.idempotent.core.lock.interceptor.RLockInterceptor;
import com.idempotent.core.lock.interceptor.RLockPointCut;
import com.idempotent.core.lock.redis.RedisProperties;
import com.idempotent.core.lock.redis.RedissonManager;

import org.aopalliance.intercept.MethodInterceptor;
import org.redisson.Redisson;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuzongliang
 * @Description:
 * @date: 2023/3/31 3:52 下午
 * @since JDK 1.8
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@Slf4j
@EnableConfigurationProperties(RedisProperties.class)
public class LockConfig {
    @Bean
    @Order(value = 3)
    @ConditionalOnMissingBean
    public Redisson redisson(RedissonManager redissonManager) {
        Redisson redisson = redissonManager.getRedisson();
        log.info("[redisson]组装完毕");
        return redisson;
    }

    @Bean
    @Order(value = 1)
    @ConditionalOnMissingBean
    public RedissonManager redissonManager(RedisProperties redisProperties) {
        RedissonManager redissonManager = new RedissonManager(redisProperties);
        log.info("[RedissonManager]组装完毕,当前连接方式:" + redisProperties.getType() + ",连接地址:" + redisProperties.getAddress());
        return redissonManager;
    }

    @Bean
    public Codec JsonJacksonCodec() {
        return new JsonJacksonCodec();
    }
    /**
     * Rlock切面
     * @return
     */
    @Bean
    public DefaultPointcutAdvisor RLockAdvisor() {
        final DefaultPointcutAdvisor advisor =
            new DefaultPointcutAdvisor(new RLockPointCut(), RLockInterceptor());
        advisor.setOrder(0);
        return advisor;
    }

    /**
     * Rlock拦截器
     * @return
     */
    @Bean
    public MethodInterceptor RLockInterceptor() {
        return new RLockInterceptor();
    }
}