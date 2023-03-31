package com.idempotent.core.lock.redis;

import com.idempotent.core.lock.redis.strategy.RedissonConfigStrategy;

import org.redisson.config.Config;

/**
 * {@link RedissonConfigContext}
 *
 * @author crz
 * @date 2018/10/10 下午9:25
 */
public class RedissonConfigContext {

    private RedissonConfigStrategy redissonConfigStrategy = null;

    public RedissonConfigContext(RedissonConfigStrategy redissonConfigStrategy) {
        this.redissonConfigStrategy = redissonConfigStrategy;
    }

    /**
     * 上下文根据构造中传入的具体策略产出真实的Redisson的Config
     *
     * @param redisProperties
     * @return
     */
    public Config createRedissonConfig(RedisProperties redisProperties) {
        return this.redissonConfigStrategy.createRedissonConfig(redisProperties);
    }
}
