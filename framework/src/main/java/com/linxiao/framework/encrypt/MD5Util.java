package com.linxiao.framework.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * MD5 string generate tool
 *
 * @author lx8421bcd
 * @since 2016-07-28
 */
public class MD5Util {

    public enum CASE {
        LOWER, UPPER
    }

    private MD5Util() {}

    public static String getMD5String32(String str, CASE caseType) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));

            byte[] byteArray = messageDigest.digest();
            StringBuilder md5StrBuff = new StringBuilder();

            for (byte aByteArray : byteArray) {
                if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
            }
            switch (caseType) {
                case LOWER:
                    return md5StrBuff.toString().toLowerCase(Locale.getDefault());
                case UPPER:
                    return md5StrBuff.toString().toUpperCase(Locale.getDefault());
            }
            return md5StrBuff.toString().toUpperCase(Locale.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMD5String16(String str, CASE caseType) {
        String md5Str = getMD5String32(str, caseType);
        return md5Str.isEmpty() ? md5Str : md5Str.substring(8, 24);
    }
}
