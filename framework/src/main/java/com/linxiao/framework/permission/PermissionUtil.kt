package com.linxiao.framework.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.linxiao.framework.common.globalContext

/**
 * 权限管理类
 *
 * @author lx8421bcd
 * @since 2016-11-24.
 */
object PermissionUtil {
    private val TAG = PermissionUtil::class.java.getSimpleName()

    /**
     * 检查权限是否已经被授予
     * @param context 所需context
     * @param requestPermissions 所需检查的权限
     */
    fun isPermissionsGranted(context: Context, vararg requestPermissions: String): Boolean {
        for (permission in requestPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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
