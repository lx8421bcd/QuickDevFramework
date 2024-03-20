package com.linxiao.framework.encrypt

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESUtil {
    fun genAesSecret(): String? {
        try {
            val kg = KeyGenerator.getInstance("AES")
            kg.init(256)
            val sk = kg.generateKey()
            val b = sk.encoded
            return Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    fun encrypt(data: String, key: String?): String {
        // 生成密钥对象
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val secKey: SecretKey = SecretKeySpec(keyBytes, "AES")
        // 获取 AES 密码器
        val cipher = Cipher.getInstance("AES")
        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, secKey)
        // 加密数据, 返回密文
        val encodedData = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encodedData, Base64.DEFAULT)
    }

    /**
     * 数据解密: 密文 -> 明文
     */
    fun decrypt(data: String?, key: String?): String {
        // 生成密钥对象
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val secKey: SecretKey = SecretKeySpec(keyBytes, "AES")
        // 获取 AES 密码器
        val cipher = Cipher.getInstance("AES")
        // 初始化密码器（解密模型）
        cipher.init(Cipher.DECRYPT_MODE, secKey)
        // 解密数据, 返回明文
        val decodedData = cipher.doFinal(Base64.decode(data, Base64.DEFAULT))
        return String(decodedData)
    }
}
