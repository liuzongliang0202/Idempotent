package com.idempotent.core.lock.redis.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.idempotent.core.lock.redis.RedisProperties;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;


/**
 * {@link RedissonConfigStrategy}
 *
 * @author crz
 * @date 2018/10/11 下午3:33
 * @desc 主从方式Redisson配置
 */
public class MasterslaveRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(RedisProperties redisProperties) {
        Config config = new Config().setCodec(new JsonJacksonCodec());
        String[] addrTokens = redisProperties.getAddress().split(",");
        String masterNodeAddr = addrTokens[0];
        config.useMasterSlaveServers().setMasterAddress(masterNodeAddr)
            .setConnectTimeout(redisProperties.getConnectTimeout())
            .setMasterConnectionPoolSize(redisProperties.getPoolSize())
            .setSlaveConnectionPoolSize(redisProperties.getPoolSize());
        List<String> slaveList = Stream.of(addrTokens).skip(1).collect(Collectors.toList());
        config.useMasterSlaveServers().addSlaveAddress((String[])slaveList.toArray());
        if (redisProperties.getThread() != null) {
            config.setThreads(redisProperties.getThread());
        }
        if (redisProperties.getNettyThread() != null) {
            config.setNettyThreads(redisProperties.getNettyThread());
        }
        if (redisProperties.getRetryAttempts() != null) {
            config.useMasterSlaveServers().setRetryAttempts(redisProperties.getRetryAttempts());
        }
        if (redisProperties.getRetryInterval() != null) {
            config.useMasterSlaveServers().setRetryInterval(redisProperties.getRetryInterval());
        }
        if (redisProperties.getConnectTimeout() != null) {
            config.useMasterSlaveServers().setConnectTimeout(redisProperties.getConnectTimeout());
        }
        return config;
    }

}
