package com.linxiao.framework.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;

import com.linxiao.framework.R;
import com.linxiao.framework.common.ApplicationUtil;
import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.dialog.AlertDialogFragment;

/**
 * 权限管理类
 * <p>
 *     用于适配Android 6.0+ 的权限问题。<br/>
 *     Google定义的Runtime权限使用 createPermissionOperator() 方法处理即可,
 *     <strong>务必在申请权限操作所属 Activity的onRequestPermissionResult() 中调用
 *     {@link #handleCallback(Activity, int, String[], int[])}, 否则不会执行回调</strong>
 * </p>
 * <p>
 *     SYSTEM_ALERT_WINDOW 权限请使用 requestManageOverlayPermission() 函数申请,
 *     <strong>必须在申请操作所属Activity的onActivityResult()中调用
 *     {@link #onActivityResult(Activity, int, int, Intent)} (Activity, int)}，
 *     否则无法通知权限是否授予</strong>
 *     <br/>
 * </p>
 * <p>
 *     WRITE_SETTINGS 权限请使用 requestWriteSystemSettingsPermission() 函数申请,
 *     <strong>必须在申请操作所属Activity的onActivityResult()中调用
 *     {@link #onActivityResult(Activity, int, int, Intent)} (Activity, int)}，
 *     否则无法通知权限是否授予</strong><br/>
 * </p>
 *
 * Created by linxiao on 2016-11-24.
 */
public class PermissionManager {
    private static final String TAG = PermissionManager.class.getSimpleName();
    
    private static PermissionOperator currPermissionOperator;

    private PermissionManager() {}

    /**
     * 检查是否需要RuntimePermission，即 System API >= API 23
     */
    public static boolean hasRuntimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查权限是否已经被授予
     * @param context 所需context
     * @param requestPermissions 所需检查的权限
     */
    public static boolean isPermissionsGranted(Context context, String... requestPermissions) {
        for (String permission : requestPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否拥有相机权限
     */
    public static boolean hasCameraPermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.CAMERA
        );
    }

    /**
     * 是否拥有录音权限
     */
    public static boolean hasRecordAudioPermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.RECORD_AUDIO
        );
    }

    /**
     * 是否拥有管理通话状态权限
     */
    public static boolean hasReadPhoneStatePermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.READ_PHONE_STATE
        );
    }

    /**
     * 是否拥有SD卡读权限
     */
    public static boolean hasSDCardPermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
    }

    /**
     * 是否拥有定位权限
     */
    public static boolean hasLocationPermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.ACCESS_FINE_LOCATION
        );
    }

    /**
     * 是否拥有浮窗权限
     */
    public static boolean hasManageOverlayPermission() {
        if (hasRuntimePermission()) {
            return Settings.canDrawOverlays(ContextProvider.get());
        }
        return true;
    }

    /**
     * 是否拥有修改系统设置权限
     */
    public static boolean hasWriteSystemSettingsPermission() {
        if (hasRuntimePermission()) {
            return Settings.System.canWrite(ContextProvider.get());
        }
        return true;
    }

    /**
     * 是否拥有日历权限
     */
    public static boolean hasCalendarPermission() {
        return isPermissionsGranted(ContextProvider.get(),
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        );
    }

    /**
     * 是否拥有安装apk的权限
     */
    public static boolean hasInstallPackagePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        return ContextProvider.get().getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 设置申请权限被完全禁止时的默认操作
     * @param listener 默认监听器
     */
    public static void setDefaultActionOnPermissionProhibited(PermissionProhibitedListener listener) {
        PermissionOperator.defaultProhibitedListener = listener;
    }

    /**
     * 执行需要检查权限的代码
     * <p>将功能代码写在声明的onRequestPermissionCallback对象中，可以一次申请多个权限，
     * 如果描述了申请权限的理由，会弹出对话框告诉用户，然后弹出系统申请权限的dialog，
     * 如果不需要写理由传入null即可</p>
     */
    public static PermissionOperator createPermissionOperator() {
        currPermissionOperator = new PermissionOperator();
        return currPermissionOperator;
    }

    /**
     * 读写SD卡相关权限申请
     */
    public static PermissionOperator requestSDCardPermission() {
        currPermissionOperator = new PermissionOperator();
        currPermissionOperator.addRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        currPermissionOperator.addRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return currPermissionOperator;
    }

    /**
     * 申请系统级Dialog权限
     * <p>可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请</p>
     */
    public static PermissionOperator requestManageOverlayPermission() {
        currPermissionOperator = new PermissionOperator().requestManageOverlayPermission();
        return currPermissionOperator;
    }

    /**
     * 申请修改系统设置的权限
     */
    public static PermissionOperator requestWriteSystemSettingsPermission() {
        currPermissionOperator = new PermissionOperator().requestWriteSystemSettingsPermission();
        return currPermissionOperator;
    }

    /**
     * 处理申请权限的回调
     * <p>检查用户当前申请的权限是否被授予并执行相关的操作。
     * <strong>请务必在申请权限代码所属Activity的onActivityResult()中调用此方法</strong></p>
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (currPermissionOperator == null) {
            return;
        }
        currPermissionOperator.onActivityResult(activity, requestCode, resultCode, data);
        currPermissionOperator = null;
    }

    /**
     * 处理申请权限的回调
     * <p>检查用户当前申请的权限是否被授予并执行相关的操作。
     * <strong>请务必在申请权限代码所属Activity的onRequestPermissionResult()中调用此方法</strong></p>
     */
    public static void handleCallback(final Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "handleCallback: requestCode = " + requestCode);
        if (requestCode != PermissionOperator.PERMISSION_REQUEST_CODE) {
            return;
        }
        if (currPermissionOperator != null) {
            currPermissionOperator.handleCallback(activity, requestCode, permissions, grantResults);
        }
        currPermissionOperator = null;
    }
    
    /**
     * 当权限申请Dialog被用户禁止弹出时使用，引导用户前往应用权限页面开启权限
     */
    public static void showPermissionProhibitedDialog(final FragmentActivity context, String permission) {
        String permissionGroupName = PermissionManager.getPermissionGroupName(context, permission);
        String message = permissionGroupName + context.getString(R.string.toast_permission_denied);
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setMessage(message)
        .setPositiveButton((dialog, which) -> {
            // jump to application detail
            ApplicationUtil.openAppDetail(context);
            dialog.dismiss();
        })
        .setNegativeButton((dialog, which) -> dialog.dismiss())
        .show(context.getSupportFragmentManager());
    }

    /**
     * 获取权限名称
     * <p>在需要显示某个具体权限的名称时使用</p>
     */
    public static String getPermissionName(Activity context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            return permissionInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permission;
    }

    /**
     * 获取权限所属权限组的名称
     * <p>在需要显示某个权限所属的一组权限的名称时使用</p>
     */
    public static String getPermissionGroupName(Context context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
            return permissionGroupInfo.loadDescription(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permission;
    }
}
