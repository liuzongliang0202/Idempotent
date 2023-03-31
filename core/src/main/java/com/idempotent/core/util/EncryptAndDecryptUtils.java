
/*
 * File Name:EncryptAndDecryptUtils is created on 2021/11/92:24 下午 by liuzongliang
 *
 * Copyright (c) 2021, xiaoyujiaoyu technology All Rights Reserved.
 *
 */
package com.idempotent.core.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuzongliang
 * @Description:
 * @date: 2021/11/9 2:24 下午
 * @since JDK 1.8
 */
public class EncryptAndDecryptUtils {
    public EncryptAndDecryptUtils() {
    }

    public static String md5Encrypt(String value) {
        String result = null;
        if (value != null && !"".equals(value.trim())) {
            result = MD5Utils.encrypt(value, "MD5");
        }

        return result;
    }

    public static String shaEncrypt(String value) {
        String result = null;
        if (value != null && !"".equals(value.trim())) {
            result = MD5Utils.encrypt(value, "SHA1");
        }

        return result;
    }


    public static String encryptByTwiceMD5(String context, String salt) {
        if (StringUtils.isAnyBlank(new CharSequence[]{context, salt})) {
            return "";
        } else {
            String md5Hex = DigestUtils.md5Hex(DigestUtils.md5Hex(context + salt));
            return md5Hex;
        }
    }

    public static Boolean verifyTwiceMD5(String context, String salt, String md5Hex) {
        return StringUtils.isAnyBlank(new CharSequence[]{context, salt, md5Hex}) ? false : encryptByTwiceMD5(context, salt).equals(md5Hex);
    }
}