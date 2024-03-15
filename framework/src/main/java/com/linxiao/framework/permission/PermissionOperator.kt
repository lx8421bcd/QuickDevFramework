package com.linxiao.framework.permission

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.linxiao.framework.common.ApplicationUtil.openAppDetail
import com.linxiao.framework.dialog.AlertDialogFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 权限操作对象
 *
 * @author lx8421bcd
 * @since 2016-11-24.
 */
class PermissionOperator internal constructor() {

    companion object {
        private val TAG = PermissionOperator::class.java.getSimpleName()
        const val PERMISSION_REQUEST_CODE = 1001
        const val MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1002
        const val WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 1003
        const val INSTALL_PACKAGE_REQUEST_CODE = 1004

        //权限被禁止的默认处理
        @JvmField
        var defaultProhibitedListener: PermissionProhibitedListener? = null
    }

    private val requestPermissions: ArrayList<String> = ArrayList()
    private var currCallback: RequestPermissionCallback? = null
    private var prohibitedListener: PermissionProhibitedListener? = null
    private var requestDesc: String = ""
    private var requestCode: Int

    init {
        requestCode = PERMISSION_REQUEST_CODE
    }

    fun addRequestPermission(permission: String): PermissionOperator {
        if (!requestPermissions.contains(permission)) {
            requestPermissions.add(permission)
        }
        return this
    }

    fun requestCalendar(): PermissionOperator {
        addRequestPermission(Manifest.permission.READ_CALENDAR)
        addRequestPermission(Manifest.permission.WRITE_CALENDAR)
        return this
    }

    fun requestReadPhoneState(): PermissionOperator {
        return addRequestPermission(Manifest.permission.READ_PHONE_STATE)
    }

    fun requestAudioRecord(): PermissionOperator {
        return addRequestPermission(Manifest.permission.RECORD_AUDIO)
    }

    fun requestCamera(): PermissionOperator {
        return addRequestPermission(Manifest.permission.CAMERA)
    }

    fun requestSDCard(): PermissionOperator {
        addRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        addRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return this
    }

    fun requestLocation(): PermissionOperator {
        addRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        return this
    }

    fun requestManageOverlayPermission(): PermissionOperator {
        requestCode = MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
        return this
    }

    fun requestWriteSystemSettingsPermission(): PermissionOperator {
        requestCode = WRITE_SETTINGS_PERMISSION_REQUEST_CODE
        return this
    }

    fun requestInstallPackagePermission(): PermissionOperator {
        requestCode = INSTALL_PACKAGE_REQUEST_CODE
        return this
    }

    /**
     * 在申请权限前显示申请权限的解释
     *
     * 将会在执行时弹出AlertDialog显示解释内容
     * @param rationale 解释文本
     */
    fun showRationaleBeforeRequest(rationale: String): PermissionOperator {
        requestDesc = rationale
        return this
    }

    /**
     * 设置权限被完全禁止时的操作
     *
     * 考虑到潜在的归一化处理与特殊处理的需求，没有将其定义在RequestPermissionListener中
     * @param prohibitedListener 监听器
     */
    fun doOnProhibited(prohibitedListener: PermissionProhibitedListener?): PermissionOperator {
        this.prohibitedListener = prohibitedListener
        return this
    }

    /**
     * 根据配置参数执行检查/申请权限并执行回调
     * @param activity 执行代码的Activity，局部变量，用于执行申请权限方法
     * @param callback 权限检查/申请后的回调监听
     */
    fun perform(activity: FragmentActivity, callback: RequestPermissionCallback) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (requestPermissions.isEmpty()) {
                callback.onGranted()
                return
            }
            val requestArr = requestPermissions.toArray(arrayOf<String>())
            if (PermissionManager.isPermissionsGranted(activity, *requestArr)) {
                callback.onGranted()
                return
            }
        }
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE &&
            PermissionManager.hasManageOverlayPermission()
        ) {
            callback.onGranted()
            return
        }
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE &&
            PermissionManager.hasWriteSystemSettingsPermission()
        ) {
            callback.onGranted()
            return
        }
        if (requestCode == INSTALL_PACKAGE_REQUEST_CODE &&
            PermissionManager.hasInstallPackagePermission()
        ) {
            callback.onGranted()
            return
        }
        currCallback = callback
        // 权限未授予，进入申请
        if (requestDesc.isEmpty()) {
            execRequest(activity, callback)
            return
        }
        AlertDialogFragment()
        .setMessage(requestDesc)
        .setPositiveButton{ dialog: DialogInterface, which: Int ->
            execRequest(activity, callback)
            dialog.dismiss()
        }
        .setNegativeButton{ dialog: DialogInterface, which: Int ->
            Log.d(TAG, "onClick: on permission denied")
            dialog.dismiss()
            currCallback!!.onDenied()
        }
        .setDialogCancelable(false)
        .show(activity.supportFragmentManager)
    }

    fun performRx(activity: FragmentActivity): Observable<Any?> {
        return Observable.create { emitter: ObservableEmitter<Any?> ->
            perform(activity, object : RequestPermissionCallback {
                override fun onGranted() {
                    emitter.onNext(Any())
                    emitter.onComplete()
                }

                override fun onDenied() {
                    emitter.onError(PermissionException())
                    emitter.onComplete()
                }
            })
        }
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    private fun execRequest(activity: Activity, callback: RequestPermissionCallback) {
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            requestManageOverlayPermission(activity, callback)
            return
        }
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            requestWriteSystemSettingsPermission(activity, callback)
            return
        }
        if (requestCode == INSTALL_PACKAGE_REQUEST_CODE) {
            requestInstallPackagePermission(activity, callback)
            return
        }
        val requestArr = requestPermissions.toArray(arrayOf<String>())
        ActivityCompat.requestPermissions(activity, requestArr, PERMISSION_REQUEST_CODE)
    }

    /**
     * 申请系统级Dialog权限
     *
     * 可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     */
    private fun requestManageOverlayPermission(
        activity: Activity,
        callback: RequestPermissionCallback?
    ) {
        if (PermissionManager.hasManageOverlayPermission()) {
            callback?.onGranted()
            return
        }
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.setData(Uri.parse("package:" + activity.packageName))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(intent, MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            currCallback = callback
        } else {
            openAppDetail(activity)
        }
    }

    /**
     * 申请修改系统设置的权限
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     */
    private fun requestWriteSystemSettingsPermission(
        activity: Activity,
        callback: RequestPermissionCallback?
    ) {
        if (PermissionManager.hasWriteSystemSettingsPermission()) {
            callback?.onGranted()
            return
        }
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.setData(Uri.parse("package:" + activity.packageName))
        activity.startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE)
        currCallback = callback
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun requestInstallPackagePermission(
        activity: Activity,
        callback: RequestPermissionCallback?
    ) {
        if (PermissionManager.hasInstallPackagePermission()) {
            callback?.onGranted()
            return
        }
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.setData(Uri.parse("package:" + activity.packageName))
        activity.startActivityForResult(intent, requestCode)
        currCallback = callback
    }

    /**
     * 处理申请权限的回调
     *
     * 检查用户当前申请的权限是否被授予并执行相关的操作。
     * **请务必在申请权限代码所属Activity的onRequestPermissionResult()中调用此方法**
     */
    fun handleCallback(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (currCallback == null) {
            return
        }
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue
            }

            // 如果这个值返回true，代表权限申请被拒绝，可以弹理由了
            // 如果返回false，代表权限申请被用户完全禁止（第一次返回false表示可以不弹理由，在申请权限前调用）
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions[i]!!)) {
                currCallback!!.onDenied()
                currCallback = null
                return
            }
            if (prohibitedListener != null) {
                prohibitedListener!!.onProhibited(permissions[i])
            } else if (defaultProhibitedListener != null) {
                defaultProhibitedListener!!.onProhibited(permissions[i])
            } else {
                Log.e(TAG, "permission request prohibited without callback handle")
                Log.e(
                    TAG, "if you haven't seen permission request dialog, " +
                            "check you have declared permission in the manifest"
                )
            }
            currCallback = null
            return
        }
        currCallback!!.onGranted()
        currCallback = null
    }

    fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(activity)) {
                Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted")
                if (currCallback != null) {
                    currCallback!!.onGranted()
                }
            } else {
                Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied")
                if (currCallback != null) {
                    currCallback!!.onDenied()
                }
            }
            currCallback = null
        } else if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (Settings.System.canWrite(activity)) {
                Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted")
                if (currCallback != null) {
                    currCallback!!.onGranted()
                }
            } else {
                Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied")
                if (currCallback != null) {
                    currCallback!!.onDenied()
                }
            }
            currCallback = null
        } else if (requestCode == INSTALL_PACKAGE_REQUEST_CODE) {
            if (PermissionManager.hasInstallPackagePermission()) {
                if (currCallback != null) {
                    currCallback!!.onGranted()
                }
            } else {
                if (currCallback != null) {
                    currCallback!!.onDenied()
                }
            }
        }
    }
}
