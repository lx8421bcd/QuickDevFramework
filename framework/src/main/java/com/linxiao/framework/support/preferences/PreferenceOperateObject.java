package com.linxiao.framework.support.preferences;

import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * SharedPreferences操作对象
 * <p>用于对SharedPreferences的常见操作进行封装，提供扩展功能</p>
 * Created by linxiao on 2014/08/21.
 */
public class PreferenceOperateObject {
    private SharedPreferences mPreferences;

    public PreferenceOperateObject(SharedPreferences preferences) {
        this.mPreferences = preferences;
    }

    public void clear() {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.clear();
        edit.apply();
    }

    public void remove(String... keys) {
        if (keys == null) {
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

    public boolean checkExist(String key) {
        return mPreferences.contains(key);
    }

    public void put(String key, boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void put(String key, int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void put(String key, long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void put(String key, float value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void put(String key, double value) {
        String valueString = String.valueOf(value);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, valueString);
        editor.apply();
    }

    public void put(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void put(String key, Serializable value) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            String valueBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(key, valueBase64);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    /**
     * 如果获取的值非法则返回默认值
     * */
    public double getDouble(String key, double defValue) {
        String valueString = mPreferences.getString(key, String.valueOf(defValue));
            try {
            return Double.parseDouble(valueString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defValue;
        }
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getSerializable(String key) {
        String objBase64 = mPreferences.getString(key, null);
        if (objBase64 != null && !objBase64.equals("")) {
            try {
                byte[] base64 = Base64.decode(objBase64, Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(base64);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object obj = ois.readObject();
                return (T) obj;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
