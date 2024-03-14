package com.linxiao.framework.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.linxiao.framework.common.globalContext

/**
 * SharedPreferences封装
 *
 * 与PreferencesOperateObject配合封装SharedPreferences, 简化SharedPreferences存取操作
 *
 * @author lx8421bcd
 * @since 2014-8-21.
 */
object AppPreferences {

    /**
     * 获取默认的SharedPreferences
     *
     * 默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
     */
    fun getDefault(): PreferenceOperator {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(globalContext)
        return PreferenceOperator(sharedPreferences)
    }

    /**
     * 获取私有的SharedPreferences
     *
     * Private模式代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
     */
    fun getPrivate(context: Context, name: String): PreferenceOperator? {
        return getPreferencesByMode(context, name, Context.MODE_PRIVATE)
    }

    fun getPrivate(name: String): PreferenceOperator? {
        return getPreferencesByMode(globalContext, name, Context.MODE_PRIVATE)
    }

    /**
     * 获取Append模式的SharedPreferences
     *
     * Append模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件.
     */
    fun getAppend(context: Context, name: String): PreferenceOperator? {
        return getPreferencesByMode(context, name, Context.MODE_APPEND)
    }

    fun getAppend(name: String): PreferenceOperator? {
        return getPreferencesByMode(globalContext, name, Context.MODE_APPEND)
    }

    private fun getPreferencesByMode(
        context: Context,
        name: String,
        mode: Int
    ): PreferenceOperator? {
        val sharedPreferences = context.getSharedPreferences(name, mode)
        return sharedPreferences?.let { PreferenceOperator(it) }
    }
}