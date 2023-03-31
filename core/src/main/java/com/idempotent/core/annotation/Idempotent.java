
/*
 * File Name:com.idempotent.core.annotation.Idempotent is created on 2023/3/313:15 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * @author liuzongliang
 * @Description: 幂等性判断注解
 * @date: 2023/3/22 10:14 下午
 * @since JDK 1.8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Rlock
@Idempotent.Transactional
public @interface Idempotent {
    boolean enableTransaction() default true;

    @AliasFor(annotation = Rlock.class)
    boolean enableLock() default true;

    /**
     * 锁和幂等唯一前缀
     * @return
     */
    @AliasFor(annotation = Rlock.class)
    String prefix() default "";

    /***-------- Rlock -----------***/
    /**
     * 等待时间 默认五秒
     *
     * @return
     */
    @AliasFor(annotation = Rlock.class)
    long waitTime() default 5;

    /**
     * 锁释放时间 默认十秒
     *
     * @return
     */
    @AliasFor(annotation = Rlock.class)
    long leaseTime() default 10;

    /**
     * 时间格式 默认：秒
     *
     * @return
     */
    @AliasFor(annotation = Rlock.class) TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 关注目标方法参数<br/>
     * 设置为false时, 粒度较大, 相同方法不同参数也为同一把锁
     */
    @AliasFor(annotation = Rlock.class)
    boolean argsAssociated() default true;

    /**
     * 参数列表<br/>
     * 默认全部参数
     */
    @AliasFor(annotation = Rlock.class)
    String[] argNames() default {};

    /***--------     Rlock      -----------***/

    /***-------- Transactional -----------***/
    @AliasFor(annotation = Transactional.class)
    String transactionManager() default "";

    @AliasFor(annotation = Transactional.class)
    Propagation propagation() default Propagation.REQUIRED;

    @AliasFor(annotation = Transactional.class)
    Isolation isolation() default Isolation.DEFAULT;

    @AliasFor(annotation = Transactional.class)
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    @AliasFor(annotation = Transactional.class)
    boolean readOnly() default false;

    @AliasFor(annotation = Transactional.class)
    Class<? extends Throwable>[] rollbackFor() default {};

    @AliasFor(annotation = Transactional.class)
    String[] rollbackForClassName() default {};

    @AliasFor(annotation = Transactional.class)
    Class<? extends Throwable>[] noRollbackFor() default {};

    @AliasFor(annotation = Transactional.class)
    String[] noRollbackForClassName() default {};

    /***-------- Transactional -----------***/

    /**
     * 事务注解配置
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @interface Transactional {

        /**
         * Alias for {@link #transactionManager}.
         * @see #transactionManager
         */
        @AliasFor("transactionManager")
        String value() default "";

        /**
         * A <em>qualifier</em> value for the specified transaction.
         * <p>May be used to determine the target transaction manager,
         * matching the qualifier value (or the bean name) of a specific
         * {@link org.springframework.transaction.PlatformTransactionManager}
         * bean definition.
         * @since 4.2
         * @see #value
         */
        @AliasFor("value")
        String transactionManager() default "";

        /**
         * The transaction propagation type.
         * <p>Defaults to {@link Propagation#REQUIRED}.
         * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
         */
        Propagation propagation() default Propagation.REQUIRED;

        /**
         * The transaction isolation level.
         * <p>Defaults to {@link Isolation#DEFAULT}.
         * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
         * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
         * transactions. Consider switching the "validateExistingTransactions" flag to
         * "true" on your transaction manager if you'd like isolation level declarations
         * to get rejected when participating in an existing transaction with a different
         * isolation level.
         * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
         * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
         */
        Isolation isolation() default Isolation.DEFAULT;

        /**
         * The timeout for this transaction (in seconds).
         * <p>Defaults to the default timeout of the underlying transaction system.
         * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
         * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
         * transactions.
         * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
         */
        int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

        /**
         * A boolean flag that can be set to {@code true} if the transaction is
         * effectively read-only, allowing for corresponding optimizations at runtime.
         * <p>Defaults to {@code false}.
         * <p>This just serves as a hint for the actual transaction subsystem;
         * it will <i>not necessarily</i> cause failure of write access attempts.
         * A transaction manager which cannot interpret the read-only hint will
         * <i>not</i> throw an exception when asked for a read-only transaction
         * but rather silently ignore the hint.
         * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
         * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
         */
        boolean readOnly() default false;

        /**
         * Defines zero (0) or more exception {@link Class classes}, which must be
         * subclasses of {@link Throwable}, indicating which exception types must cause
         * a transaction rollback.
         * <p>By default, a transaction will be rolling back on {@link RuntimeException}
         * and {@link Error} but not on checked exceptions (business exceptions). See
         * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)}
         * for a detailed explanation.
         * <p>This is the preferred way to construct a rollback rule (in contrast to
         * {@link #rollbackForClassName}), matching the exception class and its subclasses.
         * <p>Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}.
         * @see #rollbackForClassName
         * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
         */
        Class<? extends Throwable>[] rollbackFor() default {};

        /**
         * Defines zero (0) or more exception names (for exceptions which must be a
         * subclass of {@link Throwable}), indicating which exception types must cause
         * a transaction rollback.
         * <p>This can be a substring of a fully qualified class name, with no wildcard
         * support at present. For example, a value of {@code "ServletException"} would
         * match {@code javax.servlet.ServletException} and its subclasses.
         * <p><b>NB:</b> Consider carefully how specific the pattern is and whether
         * to include package information (which isn't mandatory). For example,
         * {@code "Exception"} will match nearly anything and will probably hide other
         * rules. {@code "java.lang.Exception"} would be correct if {@code "Exception"}
         * were meant to define a rule for all checked exceptions. With more unusual
         * {@link Exception} names such as {@code "BaseBusinessException"} there is no
         * need to use a FQN.
         * <p>Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}.
         * @see #rollbackFor
         * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
         */
        String[] rollbackForClassName() default {};

        /**
         * Defines zero (0) or more exception {@link Class Classes}, which must be
         * subclasses of {@link Throwable}, indicating which exception types must
         * <b>not</b> cause a transaction rollback.
         * <p>This is the preferred way to construct a rollback rule (in contrast
         * to {@link #noRollbackForClassName}), matching the exception class and
         * its subclasses.
         * <p>Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}.
         * @see #noRollbackForClassName
         * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
         */
        Class<? extends Throwable>[] noRollbackFor() default {};

        /**
         * Defines zero (0) or more exception names (for exceptions which must be a
         * subclass of {@link Throwable}) indicating which exception types must <b>not</b>
         * cause a transaction rollback.
         * <p>See the description of {@link #rollbackForClassName} for further
         * information on how the specified names are treated.
         * <p>Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}.
         * @see #noRollbackFor
         * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
         */
        String[] noRollbackForClassName() default {};

    }
}