package com.idempotent.core.lock.redis.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.idempotent.core.lock.redis.strategy.ClusterRedissonConfigStrategyImpl;
import com.idempotent.core.lock.redis.strategy.MasterslaveRedissonConfigStrategyImpl;
import com.idempotent.core.lock.redis.strategy.RedissonConfigStrategy;
import com.idempotent.core.lock.redis.strategy.StandaloneRedissonConfigStrategyImpl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link RedisConnectionType}
 *
 * @author crz
 * @date 2018/10/10 下午8:15
 */

@AllArgsConstructor
@Getter
public enum RedisConnectionType {
    STANDALONE("standalone", "单节点部署方式") {
        @Override
        public RedissonConfigStrategy getStrategyClass() {
            return new StandaloneRedissonConfigStrategyImpl();
        }
    },
    CLUSTER("cluster", "集群方式") {
        @Override
        public RedissonConfigStrategy getStrategyClass() {
            return new ClusterRedissonConfigStrategyImpl();
        }
    },
    MASTERSLAVE("masterslave", "主从部署方式") {
        @Override
        public RedissonConfigStrategy getStrategyClass() {
            return new MasterslaveRedissonConfigStrategyImpl();
        }
    };
    private String type;

    private String desc;

    private static Map<String, RedisConnectionType> map =
        Stream.of(values()).collect(Collectors.toMap(RedisConnectionType::getType, Function.identity()));

    public static RedisConnectionType getByType(String type) {
        return map.get(type);
    }

    public abstract RedissonConfigStrategy getStrategyClass();
}
