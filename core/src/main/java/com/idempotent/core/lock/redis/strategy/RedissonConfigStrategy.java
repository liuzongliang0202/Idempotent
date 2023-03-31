package com.idempotent.core.lock.redis.strategy;

import com.idempotent.core.lock.redis.RedisProperties;

import org.redisson.config.Config;

/**
 * {@link RedissonConfigStrategy}
 * Redisson配置构建接口
 *
 * @author crz
 * @date 2018/10/10 下午9:04
 */
public interface RedissonConfigStrategy {

    /**
     * 根据不同的Redis配置策略创建对应的Config
     *
     * @param redisProperties
     * @return
     *
     * @see RedisProperties
     */
    Config createRedissonConfig(RedisProperties redisProperties);

}
