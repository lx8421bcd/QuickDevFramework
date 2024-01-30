package com.linxiao.framework.common

import android.annotation.SuppressLint
import android.app.Application

/**
 * 应用全局级别context提供类
 *
 * 为所有应用模块提供Application Context，
 * static field的生命周期与Application一致，无需担心内存泄漏问题
 * 通过反射获取AppContext，避免直接使用static context
 *
 * @author linxiao
 * @since 2016-11-30
 */
val globalContext by lazy {
    @SuppressLint("PrivateApi")
    val application = try {
        Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication")
            .invoke(null) as Application
    } catch (e: Exception) {
        throw e
    }
    return@lazy application
}