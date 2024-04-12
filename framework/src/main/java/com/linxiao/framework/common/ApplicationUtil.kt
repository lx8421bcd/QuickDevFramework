package com.linxiao.framework.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Locale

/**
 * application util methods
 *
 * method collections of application and android os,
 *
 * @author linxiao
 * @since 2016-12-12.
 */
object ApplicationUtil {

    @JvmStatic
    private val TAG = ApplicationUtil.javaClass.simpleName

    /**
     * get AndroidID which is Google allowed
     *
     * [doc from](https://android-developers.googleblog.com/2017/04/changes-to-device-identifiers-in.html)
     * <p>
     * Additionally in Android O:
     * 1. The ANDROID_ID value won't change on package uninstall/reinstall,as long as the package name and signing key are the same.
     * Apps can rely on this value to maintain state across reinstalls.
     * 2. If an app was installed on a device running an earlier version of Android,
     * the Android ID remains the same when the device is updated to Android O, unless the app is uninstalled and reinstalled.
     * 3. The Android ID value only changes if the device is factory reset or if the signing key rotates between uninstall and reinstall events.
     * 4. This change is only required for device manufacturers shipping with Google Play services and Advertising ID.
     * Other device manufacturers may provide an alternative resettable ID or continue to provide ANDROID ID.
     * </p>
     *
     * see
     * [base practice about device identifier](https://developer.android.com/training/articles/user-data-ids?hl=zh-cn#kotlin)
     *
     * 需要注意的是，在某些国产手机上（如MIUI 13+）用户可以禁止应用获取ANDROID_ID，此方法会返回假数据
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(): String {
        return Settings.Secure.getString(globalContext.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }

    /**
     * check this application is foreground
     *
     * application is visible to user on screen and screen is not lock,
     * which means there must have least one activity is resumed state.
     */
    @JvmStatic
    fun isAppForeground(): Boolean {
        val context = globalContext
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.processName != packageName) {
                continue
            }
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    /**
     * close application
     *
     * Stop all active activities and call Runtime.exit() after 500ms
     *
     * @param activity activity instance
     */
    @JvmStatic
    fun exitApplication(activity: Activity?) {
        activity?.finishAffinity()
        MainScope().launch(Dispatchers.IO) {
            delay(200)
            Runtime.getRuntime().exit(0)
        }
    }

    /**
     * restart application
     *
     * Stop all active activities and launch application again with default launch intent.
     * Start activity from background without permission is not allowed since Android Q,
     * AlarmManager method won't work without granted permission
     *
     * @param activity activity instance
     */
    @JvmStatic
    fun restartApplication(activity: Activity?) {
        activity?:return
        activity.finishAffinity()
        val packageName = globalContext.packageName
        val mStartActivity = globalContext.packageManager.getLaunchIntentForPackage(packageName) ?: return
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        globalContext.startActivity(mStartActivity)
    }

    /**
     * open application detail page of this app in Settings
     */
    @JvmStatic
    fun openAppDetail(context: Context) {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(localIntent)
    }

    /**
     * is system system orientation enabled
     */
    fun isSystemOrientationEnabled(): Boolean {
        return Settings.System.getInt(
            globalContext.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION, 0
        ) == 1
    }

    /**
     * get system boot time as millisecond unit
     *
     * @return system running time since last boot, unit ms
     */
    @JvmStatic
    val systemBootTime: Long
        get() = System.currentTimeMillis() - SystemClock.elapsedRealtime()

    /**
     * get ApplicationInfo Object by package name
     * @param packageName package name
     * @return [ApplicationInfo] object
     */
    fun getAppInfo(packageName: String?): ApplicationInfo? {
        try {
            val packageManager = globalContext.packageManager
            return packageManager.getApplicationInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get Application PackageInfo Object by package name
     * @param packageName package name
     * @return [PackageInfo] object
     */
    @JvmStatic
    fun getPackageInfo(packageName: String?): PackageInfo? {
        try {
            val packageManager = globalContext.packageManager
            return packageManager.getPackageInfo(packageName!!, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get application name by package name
     *
     * @param packageName package name
     * @return app name
     */
    @JvmStatic
    fun getAppName(packageName: String?): String? {
        try {
            val packageManager = globalContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName!!, 0)
            return packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get application name by package name
     *
     * @param packageName package name
     * @return app icon
     */
    @JvmStatic
    fun getAppIcon(packageName: String?): Drawable? {
        try {
            val packageManager = globalContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName!!, 0)
            return applicationInfo.loadIcon(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取本应用当前进程名称
     * <p>
     * 此方法旨在不使用遍历runningAppProcesses的方法时获取应用当前的进程名称，
     * 避免因存在遍历进程名称而导致的审核问题
     * </p>
     */
    fun getCurrentProcessName(): String? {
        try {
            val file = File("/proc/" + Process.myPid() + "/cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            return processName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get process name by pid
     * @param pid pid
     * @return process name
     */
    @JvmStatic
    fun getProcessName(pid: Int): String? {
        val am = globalContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses
        if (runningApps != null) {
            for (procInfo in runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName
                }
            }
        }
        return null
    }

    /**
     * check application is running by package name
     *
     * @param packageName package name
     * @return boolean
     */
    fun isAppRunning(packageName: String): Boolean {
        val activityManager = globalContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfo = activityManager.runningAppProcesses
        for (i in processInfo.indices) {
            if (processInfo[i].processName == packageName) {
                Log.d(TAG, String.format("the %s is running", packageName))
                return true
            }
        }
        Log.d(TAG, String.format("the %s is not running", packageName))
        return false
    }

    /**
     * check application is installed by package name
     *
     * @param packageName package name
     * @return boolean
     */
    fun isAppInstalled(packageName: String?): Boolean {
        val packageInfo: PackageInfo? = try {
            globalContext.packageManager.getPackageInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    /**
     * install application with apk
     *
     * please check package install permission is granted before install apk
     * or installation will not complete
     *
     * @param filePath full path of install apk file
     */
    fun installApk(filePath: String, providerAuth: String?) {
        val mContext = globalContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mContext.packageManager.canRequestPackageInstalls()) {
            return  // do not have package install permission
        }
        val installFile = File(filePath)
        if (!installFile.exists()) {
            Log.e(TAG, "cannot find install apk file")
            return
        }
        if (!filePath.endsWith(".apk")) {
            Log.e(TAG, "target file is not apk file")
            return
        }
        val installPackageUri: Uri = FileProvider.getUriForFile(mContext, providerAuth!!, installFile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(installPackageUri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        mContext.startActivity(intent)
    }

    /**
     * get CPU info of this device
     *
     * <p>
     * the file [/proc/cpuinfo] contains all running CPU info on the device,
     * and the SOC name usually written in the last line of this file,
     * example:
     *     "Hardware	: Qualcomm Technologies, Inc SDM845"
     * </p>
     *
     * @return the SOC name of this device
     */
    @JvmStatic
    fun getCPUName(): String {
        var fr: FileReader? = null
        var br: BufferedReader? = null
        var text: String
        try {
            fr = FileReader("/proc/cpuinfo")
            br = BufferedReader(fr)
            while (br.readLine().also { text = it } != null) {
                Log.d(TAG, "getCPUName: $text")
                if (text.lowercase(Locale.getDefault()).contains("hardware")) {
                    val array = text.split(":\\s+".toRegex(), limit = 2).toTypedArray()
                    return array[1]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fr?.close()
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    /**
     * check the system settings "don't keep activities" is opened
     *
     * @return opened - true; closed - false
     */
    @JvmStatic
    fun isAlwaysFinishActivity(): Boolean{
        return Settings.Global.getInt(
            globalContext.contentResolver,
            Settings.Global.ALWAYS_FINISH_ACTIVITIES,
            0
        ) != 0
    }

    /**
     * open Developer settings page in phone system settings
     */
    fun openDeveloperSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        context.startActivity(intent)
    }
}