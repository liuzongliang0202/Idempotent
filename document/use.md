### 1.@Idempotent是标注在方法上的注解，它可以被当成通用的幂等性判断工具，当你需要做幂等判断时，不需要编写而外的逻辑，只需要添加一个注解可以完成此功能

### 2.@Rlock是使用redisson实现的分布式锁,可以单独使用

### 3.@Idempotent包含了@Rlock和@Transactional注解的功能，所以你不需要考虑再在方法上添加锁和事务

### 4.标注了@Idempotent注解的方法调用顺序是：
@Rlock              ->     @Transactional              ->      @Idempotent
RLockInterceptor    ->     TransactionInterceptor      ->      IdempotentAspect

### 5.考虑到有些业务需要做幂等判断，但是业务的状态，又有其他地方更改，需要提供一个释放幂等日志的操作，已方便下一次能进入业务逻辑而不是继续返回幂等日志的值

### 6.RLockInterceptor之所以采用spring的MethodInterceptor而不是直接使用AspectJ注解是因为 @Pointcut注解不支持元注解查找

### 7.@Idempotent参数解释：
enableLock() 表示被注释的业务方法是否需要加分布式锁，默认需要开启，而且幂等业务应该是要保证顺序性的

enableTransaction() 表示被注释的业务方法是否需要加事务等同于@Transactional，在业务方法中你可以使用TransactionAspectSupport.currentTransactionStatus()
.setsetRollbackOnly()手动回滚，默认需要开启，幂等业务需要保证幂等日志和业务数据保持一致

prefix() 表示被注释的业务方法的幂等签名，代表是哪种业务，需要全局唯一，同时它也是分布式锁key的前缀，幂等签名+涉及幂等性的方法参数来保证业务的幂等性

waitTime() 表示分布式锁在抢占锁的等待时间，超过这个时间会报频繁操作异常

leaseTime() 表示分布式锁的释放时间，如果设置成-1就表示会一直持有锁，并且会锁续期

timeUnit() 表示设置的时间单位

argsAssociated() 表示分布式锁的key和幂等值是否需要关联方法参数

argNames() 表示需要关联的方法参数有哪些

其他的都是@Idempotent.Transactional注解的参数，和spring @Transactional的功能一致


### 8.@Idempotent事务实现逻辑解析：
首先默认的spring @Transactional 的拦截功能是通过TransactionInterceptor类实现的，所以我们需要考虑更改它的Advisor中的pointCut
而spring事务的默认Advisor是通过BeanFactoryTransactionAttributeSourceAdvisor，是通过ProxyTransactionManagementConfiguration配置实现事务的Advisor
springboot的自动配置TransactionAutoConfiguration会自动加载这个配置,而这个ProxyTransactionManagementConfiguration我们是无法更改的

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PlatformTransactionManager.class)
@AutoConfigureAfter({ JtaAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class, Neo4jDataAutoConfiguration.class })
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {
    
    //...............
    
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(TransactionManager.class)
    @ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
    public static class EnableTransactionManagementConfiguration {

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false", matchIfMissing = false)
        public static class JdkDynamicAutoProxyConfiguration {

        }

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = true)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = true)
        public static class CglibAutoProxyConfiguration {

        }

    }
}
```

@ConditionalOnMissingBean可以帮助我们实现自己的AbstractTransactionManagementConfiguration事务配置ProxyTransactionManagementConfig
值得注意的是这里我们需要考虑这三个拦截器的顺序，需要保证锁到事务到幂等逻辑增强，所以需要给它们设置正确的order
