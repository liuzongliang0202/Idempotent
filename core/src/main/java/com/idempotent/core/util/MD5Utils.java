
/*
 * File Name:MD5Utils is created on 2021/11/92:24 下午 by liuzongliang
 *
 * Copyright (c) 2021, xiaoyujiaoyu technology All Rights Reserved.
 *
 */
package com.idempotent.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author liuzongliang
 * @Description:
 * @date: 2021/11/9 2:24 下午
 * @since JDK 1.8
 */
public class MD5Utils {
    protected static final String MD5_KEY = "MD5";
    protected static final String SHA_KEY = "SHA1";

    MD5Utils() {
    }

    protected static String encrypt(String value, String key) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(key);
            byte[] inputByteArray = value.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException var5) {
            return null;
        }
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        byte[] var4 = byteArray;
        int var5 = byteArray.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            byte b = var4[var6];
            resultCharArray[index++] = hexDigits[b >>> 4 & 15];
            resultCharArray[index++] = hexDigits[b & 15];
        }

        return new String(resultCharArray);
    }
}