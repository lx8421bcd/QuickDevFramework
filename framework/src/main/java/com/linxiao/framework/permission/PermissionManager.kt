package com.linxiao.framework.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.linxiao.framework.R
import com.linxiao.framework.common.ApplicationUtil.openAppDetail
import com.linxiao.framework.common.globalContext
import com.linxiao.framework.dialog.AlertDialogFragment

/**
 * 权限管理类
 *
 *
 * 用于适配Android 6.0+ 的权限问题。<br></br>
 * Google定义的Runtime权限使用 createPermissionOperator() 方法处理即可,
 * 务必在申请权限操作所属 Activity的onRequestPermissionResult() 中调用
 * [.handleCallback], 否则不会执行回调
 *
 *
 *
 * SYSTEM_ALERT_WINDOW 权限请使用 requestManageOverlayPermission() 函数申请,
 * **必须在申请操作所属Activity的onActivityResult()中调用
 * [.onActivityResult] (Activity, int)}，
 * 否则无法通知权限是否授予
 * <br>
 *
 *
 *
 * WRITE_SETTINGS 权限请使用 requestWriteSystemSettingsPermission() 函数申请,
 * **必须在申请操作所属Activity的onActivityResult()中调用
 * [.onActivityResult] (Activity, int)}，
 * 否则无法通知权限是否授予<br/>
 *
 * @author lx8421bcd
 * @since 2016-11-24.
 */
object PermissionManager {
    private val TAG = PermissionManager::class.java.getSimpleName()
    private var currPermissionOperator: PermissionOperator? = null

    /**
     * 检查权限是否已经被授予
     * @param context 所需context
     * @param requestPermissions 所需检查的权限
     */
    fun isPermissionsGranted(context: Context?, vararg requestPermissions: String?): Boolean {
        for (permission in requestPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 是否拥有相机权限
     */
    fun hasCameraPermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.CAMERA
        )
    }

    /**
     * 是否拥有录音权限
     */
    fun hasRecordAudioPermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.RECORD_AUDIO
        )
    }

    /**
     * 是否拥有管理通话状态权限
     */
    fun hasReadPhoneStatePermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.READ_PHONE_STATE
        )
    }

    /**
     * 是否拥有SD卡读权限
     */
    @JvmStatic
    fun hasSDCardPermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /**
     * 是否拥有定位权限
     */
    fun hasLocationPermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * 是否拥有浮窗权限
     */
    fun hasManageOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(globalContext)
    }

    /**
     * 是否拥有修改系统设置权限
     */
    fun hasWriteSystemSettingsPermission(): Boolean {
        return Settings.System.canWrite(globalContext)
    }

    /**
     * 是否拥有日历权限
     */
    fun hasCalendarPermission(): Boolean {
        return isPermissionsGranted(
            globalContext,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    }

    /**
     * 是否拥有安装apk的权限
     */
    fun hasInstallPackagePermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            true
        } else globalContext.packageManager.canRequestPackageInstalls()
    }

    /**
     * 设置申请权限被完全禁止时的默认操作
     * @param listener 默认监听器
     */
    fun setDefaultActionOnPermissionProhibited(listener: PermissionProhibitedListener?) {
        PermissionOperator.defaultProhibitedListener = listener
    }

    /**
     * 执行需要检查权限的代码
     *
     * 将功能代码写在声明的onRequestPermissionCallback对象中，可以一次申请多个权限，
     * 如果描述了申请权限的理由，会弹出对话框告诉用户，然后弹出系统申请权限的dialog，
     * 如果不需要写理由传入null即可
     */
    @JvmStatic
    fun createPermissionOperator(): PermissionOperator? {
        currPermissionOperator = PermissionOperator()
        return currPermissionOperator
    }

    /**
     * 读写SD卡相关权限申请
     */
    fun requestSDCardPermission(): PermissionOperator? {
        currPermissionOperator = PermissionOperator()
        currPermissionOperator!!.addRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        currPermissionOperator!!.addRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return currPermissionOperator
    }

    /**
     * 申请系统级Dialog权限
     *
     * 可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请
     */
    fun requestManageOverlayPermission(): PermissionOperator? {
        currPermissionOperator = PermissionOperator().requestManageOverlayPermission()
        return currPermissionOperator
    }

    /**
     * 申请修改系统设置的权限
     */
    fun requestWriteSystemSettingsPermission(): PermissionOperator? {
        currPermissionOperator = PermissionOperator().requestWriteSystemSettingsPermission()
        return currPermissionOperator
    }

    /**
     * 处理申请权限的回调
     *
     * 检查用户当前申请的权限是否被授予并执行相关的操作。
     * **请务必在申请权限代码所属Activity的onActivityResult()中调用此方法**
     */
    fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        if (currPermissionOperator == null) {
            return
        }
        currPermissionOperator!!.onActivityResult(activity, requestCode, resultCode, data)
        currPermissionOperator = null
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
        Log.d(TAG, "handleCallback: requestCode = $requestCode")
        if (requestCode != PermissionOperator.PERMISSION_REQUEST_CODE) {
            return
        }
        currPermissionOperator?.handleCallback(
            activity,
            requestCode,
            permissions,
            grantResults
        )
        currPermissionOperator = null
    }

    /**
     * 当权限申请Dialog被用户禁止弹出时使用，引导用户前往应用权限页面开启权限
     */
    @JvmStatic
    fun showPermissionProhibitedDialog(context: FragmentActivity, permission: String) {
        val permissionGroupName = getPermissionGroupName(context, permission)
        val message = permissionGroupName + context.getString(R.string.toast_permission_denied)
        val dialogFragment = AlertDialogFragment()
        dialogFragment.setMessage(message)
        .setPositiveButton { dialog: DialogInterface, which: Int ->
            // jump to application detail
            openAppDetail(context)
            dialog.dismiss()
        }
        .setNegativeButton { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        .show(context.supportFragmentManager)
    }

    /**
     * 获取权限名称
     *
     * 在需要显示某个具体权限的名称时使用
     */
    fun getPermissionName(context: Activity, permission: String): String {
        val packageManager = context.packageManager
        try {
            val permissionInfo = packageManager.getPermissionInfo(permission, 0)
            return permissionInfo.loadLabel(packageManager).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return permission
    }

    /**
     * 获取权限所属权限组的名称
     *
     * 在需要显示某个权限所属的一组权限的名称时使用
     */
    fun getPermissionGroupName(context: Context, permission: String): String {
        val packageManager = context.packageManager
        try {
            val permissionInfo = packageManager.getPermissionInfo(permission, 0)
            val permissionGroupInfo =
                packageManager.getPermissionGroupInfo(permissionInfo.group!!, 0)
            return permissionGroupInfo.loadDescription(packageManager).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return permission
    }
}
