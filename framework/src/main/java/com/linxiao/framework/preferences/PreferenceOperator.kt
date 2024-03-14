package com.linxiao.framework.preferences

import android.content.SharedPreferences
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * SharedPreferences操作对象
 *
 * 用于对SharedPreferences的常见操作进行封装，提供扩展功能
 *
 * @author lx8421bcd
 * @since 2014-08-21.
 */
class PreferenceOperator(
    private val preferences: SharedPreferences
) {
    fun clear() {
        val edit = preferences.edit()
        edit.clear()
        edit.apply()
    }

    fun remove(vararg keys: String?) {
        val editor = preferences.edit()
        for (key in keys) {
            editor.remove(key)
        }
        editor.apply()
    }

    fun checkExist(key: String?): Boolean {
        return preferences.contains(key)
    }

    fun put(key: String?, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun put(key: String?, value: Int) {
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun put(key: String?, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun put(key: String?, value: Float) {
        val editor = preferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun put(key: String?, value: Double) {
        val valueString = value.toString()
        val editor = preferences.edit()
        editor.putString(key, valueString)
        editor.apply()
    }

    fun put(key: String?, value: String?) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun put(key: String?, value: Serializable?) {
        try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            val valueBase64 = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            val editor = preferences.edit()
            editor.putString(key, valueBase64)
            editor.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getInt(key: String?, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getLong(key: String?, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun getFloat(key: String?, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun getDouble(key: String?, defValue: Double): Double {
        val valueString = preferences.getString(key, defValue.toString())
        return try {
            valueString!!.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            defValue
        }
    }

    fun getString(key: String?, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable?> getSerializable(key: String?): T? {
        val objBase64 = preferences.getString(key, null)
        if (!objBase64.isNullOrEmpty()) {
            try {
                val base64 = Base64.decode(objBase64, Base64.DEFAULT)
                val bais = ByteArrayInputStream(base64)
                val ois = ObjectInputStream(bais)
                val obj = ois.readObject()
                return obj as T
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return null
    }
}