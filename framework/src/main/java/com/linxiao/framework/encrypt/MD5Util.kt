package com.linxiao.framework.encrypt

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

/**
 * MD5 string generate tool
 *
 * @author lx8421bcd
 * @since 2016-07-28
 */
object MD5Util {

    enum class CASE {
        LOWER,
        UPPER
    }

    fun getMD5String32(str: String, caseType: CASE?): String {
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(str.toByteArray(StandardCharsets.UTF_8))
            val byteArray = messageDigest.digest()
            val md5StrBuff = StringBuilder()
            for (aByteArray in byteArray) {
                if (Integer.toHexString(0xFF and aByteArray.toInt()).length == 1) md5StrBuff.append(
                    "0"
                ).append(
                    Integer.toHexString(0xFF and aByteArray.toInt())
                ) else md5StrBuff.append(Integer.toHexString(0xFF and aByteArray.toInt()))
            }
            return when (caseType) {
                MD5Util.CASE.LOWER -> md5StrBuff.toString().lowercase(Locale.getDefault())
                MD5Util.CASE.UPPER -> md5StrBuff.toString().uppercase(Locale.getDefault())
                else -> md5StrBuff.toString().uppercase(Locale.getDefault())
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getMD5String16(str: String, caseType: CASE?): String {
        val md5Str = getMD5String32(str, caseType)
        return if (md5Str.length >= 24) md5Str.substring(8, 24) else md5Str
    }
}
