package com.idempotent.core.lock.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link RedisProperties}
 * redis配置类
 *
 * @author crz
 * @date 2018/10/10 下午8:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "redis.pool")
public class RedisProperties {

    /**
     * redis主机地址，ip：port，有多个用半角逗号分隔
     */
    private String address;
    /**
     * 连接类型，支持standalone-单机节点，sentinel-哨兵，cluster-集群，masterslave-主从
     */
    private String type;
    /**
     * 密码
     */
    private String password;
    /**
     * 链接池大小
     */
    private int poolSize = 100;
    /**
     * 超时时间
     */
    private Integer connectTimeout ;

    private Integer thread;

    private Integer nettyThread;

    private Integer retryInterval;

    private Integer retryAttempts;

}
