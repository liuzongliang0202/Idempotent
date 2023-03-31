/*
 * File Name:KeyGenerator is created on 2021/10/301:35 PM by crz
 *
 * Copyright (c) 2021, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.util;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author crz
 * @Description:
 * @date: 2021/10/30 1:35 PM
 * @since JDK 1.8
 */
public class KeyGenerator {
    private static final ObjectMapper OBJECT_MAPPER;
    String COLON = ":";

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    ExpressionParser PARSER = new SpelExpressionParser();

    ThreadLocal<EvaluationContext> THREAD_LOCAL = new ThreadLocal<EvaluationContext>() {
        @Override
        protected EvaluationContext initialValue() {
            return new StandardEvaluationContext();
        }
    };

    /**
     * 缓存key
     *
     * @param joinPoint
     * @param prefix         key前缀
     * @param argNames       null 或 空数组时 将使用全部参数
     * @param argsAssociated key中是否包含参数
     * @return
     *
     * @throws JsonProcessingException
     * @author crz
     * @date: 2021/01/25 14:27
     */
    public StringBuilder generate(ProceedingJoinPoint joinPoint, String prefix, String[] argNames,
        boolean argsAssociated)
        throws JsonProcessingException {

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(prefix)) {
            builder =
                builder.append(joinPoint.getTarget().getClass().getName()).append(COLON).append(signature.getName());
        } else {
            builder = builder.append("auto-cache" + prefix);
        }
        String[] parameterNames = signature.getParameterNames();
        if (!argsAssociated || parameterNames.length <= 0) {
            return builder;
        }
        String[] names;
        Object[] values;
        // argsNames为空时默认为全部参数
        if (null == argNames || argNames.length <= 0) {
            names = parameterNames;
            values = args;
        } else {
            Map<String, Object> argMap = Maps.newHashMapWithExpectedSize(parameterNames.length);
            for (int index = 0; index < parameterNames.length; index++) {
                argMap.put(parameterNames[index], args[index]);
            }
            names = new String[argNames.length];
            values = new Object[argNames.length];
            for (int index = 0; index < argNames.length; index++) {
                String[] expression = StringUtils.split(argNames[index], '.');
                names[index] = expression[expression.length - 1];
                String argName = expression[0];
                Object arg = argMap.get(argName);
                if (null == arg || expression.length == 1) {
                    values[index] = arg;
                    continue;
                }
                EvaluationContext context = THREAD_LOCAL.get();
                context.setVariable(argName, arg);
                values[index] = PARSER.parseExpression("#" + argNames[index]).getValue(context);
            }
            THREAD_LOCAL.remove();
        }

        return builder.append(COLON).append(simpleJoinToBuilder(names, values, "=", "|"));
    }

    /**
     * 数组转StringBuilder
     *
     * @param argNames    参数列表
     * @param args        参数值列表, 与argNames长度相等
     * @param separatorKV 参数名和参数值分隔符
     * @param separator   分隔符
     * @return
     *
     * @throws JsonProcessingException
     * @author piaoruiqing
     * @date: 2019/05/05 23:14
     */
    public static StringBuilder simpleJoinToBuilder(String[] argNames, Object[] args, String separatorKV,
        String separator) throws JsonProcessingException {

        if (argNames == null || args == null) {
            return null;
        }
        if (argNames.length != args.length) {
            throw new IllegalArgumentException("Inconsistent parameter length !");
        }
        if (argNames.length <= 0) {
            return new StringBuilder(0);
        }
        int bufSize =
            argNames.length * (argNames[0].toString().length() + Optional.ofNullable(args[0]).map(String::valueOf)
                .map(String::length).orElse(4) + 2);
        StringBuilder builder = new StringBuilder(bufSize);
        for (int index = 0; index < argNames.length; index++) {
            if (index > 0) {
                builder.append(separator);
            }
            appendObject(builder, argNames[index], separatorKV, args[index]);
        }

        return builder;
    }

    /**
     * 追加字符串
     *
     * @param builder 待追加StringBuilder
     * @param object  追加参数
     * @return
     *
     * @throws JsonProcessingException
     * @author crz
     * @date: 2021/01/14 09:59
     */
    public static StringBuilder appendObject(StringBuilder builder, Object... object) throws JsonProcessingException {

        for (Object item : object) {
            if (item instanceof Number || item instanceof String || item instanceof Boolean
                || item instanceof Character) {
                builder.append(item);
            } else {
                builder.append(OBJECT_MAPPER.writeValueAsString(item));
            }
        }
        return builder;
    }

}
