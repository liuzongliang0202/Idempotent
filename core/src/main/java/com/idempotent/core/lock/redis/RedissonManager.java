package com.idempotent.core.lock.redis;

import com.idempotent.core.lock.redis.enums.RedisConnectionType;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RedissonManager}
 *
 * @author crz
 * @date 2018/10/10 下午8:34
 */
@Slf4j
@Getter
public class RedissonManager {
    private Config config = new Config();

    private Redisson redisson = null;

    public RedissonManager(RedisProperties redisProperties) {
        try {
            config = RedissonConfigFactory.getInstance().createConfig(redisProperties);
            redisson = (Redisson)Redisson.create(config);
        } catch (Exception e) {
            log.error("Redisson init error," + e);
        }
    }

    /**
     * redissonConfig工厂,单实例
     */
    @NoArgsConstructor
    static class RedissonConfigFactory {

        private static volatile RedissonConfigFactory redissonConfigFactory = null;

        public static RedissonConfigFactory getInstance() {
            if (redissonConfigFactory == null) {
                synchronized (RedissonConfigFactory.class) {
                    if (redissonConfigFactory == null) {
                        redissonConfigFactory = new RedissonConfigFactory();
                    }
                }
            }
            return redissonConfigFactory;
        }

        Config createConfig(RedisProperties redisProperties) {
            Assert.notNull(redisProperties);
            Assert.notNull(redisProperties.getAddress());
            Assert.notNull(redisProperties.getType());

            RedisConnectionType connectionTypeEnum = RedisConnectionType.getByType(redisProperties.getType());
            if (connectionTypeEnum != null) {
                RedissonConfigContext redissonConfigContext =
                    new RedissonConfigContext(connectionTypeEnum.getStrategyClass());
                return redissonConfigContext.createRedissonConfig(redisProperties);
            }
            throw new RuntimeException("创建Redisson连接Config失败！当前连接方式:" + redisProperties.getType());
        }

    }
}
