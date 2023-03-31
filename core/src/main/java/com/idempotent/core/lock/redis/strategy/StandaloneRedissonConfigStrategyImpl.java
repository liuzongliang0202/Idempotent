package com.idempotent.core.lock.redis.strategy;

import com.idempotent.core.lock.redis.Constants;
import com.idempotent.core.lock.redis.RedisProperties;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RedissonConfigStrategy} 单节点部署方式
 *
 * @author crz
 * @date 2018/10/10 下午9:06
 */
@Slf4j
public class StandaloneRedissonConfigStrategyImpl implements RedissonConfigStrategy {
    @Override
    public Config createRedissonConfig(RedisProperties redisProperties) {
        Config config = new Config().setCodec(new JsonJacksonCodec());
        String redisAddress = Constants.PREFIX + redisProperties.getAddress();
        config.useSingleServer().setAddress(redisAddress).setPassword(redisProperties.getPassword())
            .setConnectionPoolSize(redisProperties.getPoolSize());
        if (redisProperties.getThread() != null) {
            config.setThreads(redisProperties.getThread());
        }
        if (redisProperties.getNettyThread() != null) {
            config.setNettyThreads(redisProperties.getNettyThread());
        }
        if (redisProperties.getRetryAttempts() != null) {
            config.useSingleServer().setRetryAttempts(redisProperties.getRetryAttempts());
        }
        if (redisProperties.getRetryInterval() != null) {
            config.useSingleServer().setRetryInterval(redisProperties.getRetryInterval());
        }
        if (redisProperties.getConnectTimeout() != null) {
            config.useSingleServer().setConnectTimeout(redisProperties.getConnectTimeout());
        }
        return config;
    }
}
