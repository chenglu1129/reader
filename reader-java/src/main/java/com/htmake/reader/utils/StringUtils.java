package com.htmake.reader.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 字符串工具类
 */
public class StringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机字符串
     */
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String str) {
        if (str == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String str) {
        if (str == null) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(str));
        } catch (Exception e) {
            return null;
        }
    }
}
