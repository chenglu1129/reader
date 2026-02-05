package com.htmake.reader.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5工具类
 */
public class MD5Utils {

    /**
     * MD5 32位编码
     */
    public static String md5Encode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * MD5 16位编码
     */
    public static String md5Encode16(String input) {
        String md5 = md5Encode(input);
        if (md5.length() >= 16) {
            return md5.substring(8, 24);
        }
        return md5;
    }
}
