package com.linxiao.framework.common

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.collection.ArrayMap
import com.google.gson.reflect.TypeToken
import com.linxiao.framework.common.DateUtil.timeMillsToDateString
import com.linxiao.framework.json.GsonParser.parser
import com.linxiao.framework.json.GsonParser.toJson
import com.linxiao.framework.preferences.AppPreferences
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.Arrays

/**
 * <h3>全局捕获异常处理</h3> <br></br>
 * <h5>当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志</h5>
 *
 * @author relish-wang
 * @since 2017-03-08
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

    private const val CACHE_KEY = "cache_report_data"

    private val TAG = CrashHandler::class.java.simpleName
    private val defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()

    var onSubmitLogs: ((info: Map<String, Any?>) -> Unit)? = null

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun init(onSubmitLogs: ((info: Map<String, Any?>) -> Unit)?) {
        this.onSubmitLogs = onSubmitLogs
        uploadCachedLog()
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val info: MutableMap<String, Any?> = HashMap()
        info["thread"] = t.name
        info["time"] = timeMillsToDateString("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis())
        info["app_info"] = collectAppVersionInfo()
        info["device_info"] = collectDeviceInfo()
        info["network_info"] = collectNetworkInfo()
        info["stack_trace"] = getStackTraceStr(e)
        onSubmitLogs?.invoke(info)
    }

    private fun collectAppVersionInfo(): Map<String, Any> {
        val ret: MutableMap<String, Any> = ArrayMap()
        try {
            val pm = globalContext.packageManager
            val pi = pm.getPackageInfo(globalContext.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                ret["versionName"] = pi.versionName
                ret["versionCode"] = pi.versionCode
            }
        } catch (ignored: Exception) {}
//        ret.put("debug_mode", GlobalConfig.isDebugMode());
//        ret.put("beta_mode", GlobalConfig.isBetaMode());
        return ret
    }

    private fun collectDeviceInfo(): Map<String, Any> {
        val ret: MutableMap<String, Any> = ArrayMap()
        ret["MODEL"] = Build.MODEL
        ret["ID"] = Build.ID
        ret["HOST"] = Build.HOST
        ret["OS_VERSION"] = Build.VERSION.RELEASE + "(API" + Build.VERSION.SDK_INT + ")"
        ret["DISPLAY"] = Build.DISPLAY
        ret["PRODUCT"] = Build.PRODUCT
        ret["MANUFACTURER"] = Build.MANUFACTURER
        ret["CPU_ABI"] = Build.CPU_ABI
        ret["SUPPORTED_ABIS"] = Arrays.asList(*Build.SUPPORTED_ABIS)
        val extraInfo: List<String> = mutableListOf("SOC_MODEL", "SOC_MANUFACTURER", "IS_EMULATOR")
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            if (extraInfo.contains(field.name)) {
                try {
                    field.isAccessible = true
                    ret[field.name] = field[null].toString()
                } catch (ignored: Exception) {
                }
            }
        }
        return ret
    }

    private fun collectNetworkInfo(): Map<String, Any> {
        val ret: MutableMap<String, Any> = ArrayMap()
        val manager = globalContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo
        ret["connected"] = activeNetworkInfo!!.isConnected
        ret["type"] = activeNetworkInfo.typeName
        ret["detail_state"] = activeNetworkInfo.detailedState.toString()
        ret["extra_info"] = activeNetworkInfo.extraInfo
        return ret
    }

    private fun getStackTraceStr(ex: Throwable): String {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.flush()
        printWriter.close()
        val result = writer.toString()
        Log.e(TAG, result)
        return result
    }

    private fun saveToLocal(info: Map<String, Any?>) {
        AppPreferences.getDefault().put(CACHE_KEY, info.toJson())
    }

    private fun uploadCachedLog() {
        val cacheStr = AppPreferences.getDefault().getString(CACHE_KEY, "")
        Log.d(TAG, "cached log: $cacheStr")
        if (cacheStr.isNullOrEmpty()) {
            return
        }
        val convertType = object : TypeToken<Map<String?, Any?>?>() {}.type
        val reportData = parser.fromJson<Map<String, Any?>>(cacheStr, convertType) ?: return
        AppPreferences.getDefault().remove(CACHE_KEY)
        onSubmitLogs?.invoke(reportData)
    }

}
