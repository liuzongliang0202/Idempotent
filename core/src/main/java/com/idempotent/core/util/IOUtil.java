
/*
 * File Name:com.sdd.asker.utils.idempotent.IOUtil is created on 2023/3/305:43 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.util;

/**
 * @author liuzongliang
 * @Description: io关闭
 * @date: 2023/3/30 5:43 下午
 * @since JDK 1.8
 */
public class IOUtil {

    /**
     * close Closeable
     * @param closeables the closeables
     */
    public static void close(AutoCloseable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (AutoCloseable closeable : closeables) {
                close(closeable);
            }
        }
    }

    /**
     * close Closeable
     * @param closeable the closeable
     */
    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }

}