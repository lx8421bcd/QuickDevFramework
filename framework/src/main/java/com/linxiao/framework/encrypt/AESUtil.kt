package com.linxiao.framework.encrypt;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    public static String genAesSecret() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            String secret = Base64.encodeToString(b, Base64.DEFAULT);
            return secret;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, String key) throws Exception {
        // 生成密钥对象
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        SecretKey secKey = new SecretKeySpec(keyBytes, "AES");
        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, secKey);
        // 加密数据, 返回密文
        byte[] encodedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encodedData, Base64.DEFAULT);
    }

    /**
     * 数据解密: 密文 -> 明文
     */
    public static String decrypt(String data, String key) throws Exception {
        // 生成密钥对象
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        SecretKey secKey = new SecretKeySpec(keyBytes, "AES");
        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化密码器（解密模型）
        cipher.init(Cipher.DECRYPT_MODE, secKey);
        // 解密数据, 返回明文
        byte[] decodedData = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
        return new String(decodedData);
    }
}
