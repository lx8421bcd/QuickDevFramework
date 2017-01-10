package com.linxiao.framework.util;

import android.util.Log;

import com.linxiao.framework.support.log.LogManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 *
 * Created by LinXiao on 2016-07-28.
 */
public class MD5Util {

    public static final int MD5_UPPER_CASE = 0;
    public static final int MD5_LOWER_CASE = 1;

    /**
     * MD5加密
     */
    public static String getMD5Str(String str, int caseCode) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            LogManager.e("MD5Util", "NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();

        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
        }
        switch (caseCode) {
            case MD5_LOWER_CASE :
                return md5StrBuff.toString().toLowerCase(Locale.getDefault());
            case MD5_UPPER_CASE :
                return md5StrBuff.toString().toUpperCase(Locale.getDefault());
        }
        return md5StrBuff.toString().toUpperCase(Locale.getDefault());
    }
}
