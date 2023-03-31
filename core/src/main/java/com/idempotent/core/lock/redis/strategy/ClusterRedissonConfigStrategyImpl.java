package com.idempotent.core.lock.redis.strategy;

import com.idempotent.core.lock.redis.Constants;
import com.idempotent.core.lock.redis.RedisProperties;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * {@link RedissonConfigStrategy}
 *
 * @author crz
 * @date 2018/10/11 下午3:54
 * @desc 集群方式Redisson配置
 */
public class ClusterRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(RedisProperties redisProperties) {
        Config config = new Config().setCodec(new JsonJacksonCodec());
        config.useClusterServers().setMasterConnectionPoolSize(redisProperties.getPoolSize())
            .setSlaveConnectionPoolSize(redisProperties.getPoolSize());
        String[] addrTokens = redisProperties.getAddress().split(",");
        /** 设置cluster节点的服务IP和端口 */
        for (int i = 0; i < addrTokens.length; i++) {
            config.useClusterServers().addNodeAddress(Constants.PREFIX + addrTokens[i]);
        }
        return config;
    }
}
