package com.airing.spring.cloud.base.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    /**
     * 将加密后的字节数组转换成十六进制字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String s;
        for (int i = 0, len = b.length; b != null && i < len; i++) {
            s = Integer.toHexString(b[i] & 0XFF);
            if (s.length() == 1)
                hs.append('0');
            hs.append(s);
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 加密
     *
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static String encrypt(String message, String secret, String algorithm) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance(algorithm);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), algorithm);
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
//            log.error("encrypt|ERROR", e);
        }
        return hash;
    }

    public static String encryptByHmacSHA256(String message, String secret) {
        return encrypt(message, secret, "HmacSHA256");
    }
}
