package com.linxiao.framework.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.R;
import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.framework.log.Logger;
import com.linxiao.framework.util.ApplicationUtil;

/**
 * 权限管理类
 * <p>
 *     用于适配Android 6.0+ 的权限问题。<br/>
 *     Google定义的Runtime权限使用 performWithPermission() 方法处理即可,
 *     <strong>务必在申请权限操作所属 Activity的onRequestPermissionResult() 中调用
 *     {@link #handleCallback(Activity, int, String[], int[])}, 否则不会执行回调</strong>
 * </p>
 * <p>
 *     SYSTEM_ALERT_WINDOW 权限请使用 requestSystemAlertWindowPermission() 函数申请,
 *     <strong>必须在申请操作所属Activity的onActivityResult()中调用
 *     {@link #onSysAlertPermissionResult(Activity, int)}，
 *     否则无法通知权限是否授予</strong>
 *     <br/>
 * </p>
 * <p>
 *     WRITE_SETTINGS 权限请使用 requestWriteSystemSettingsPermission() 函数申请,
 *     <strong>必须在申请操作所属Activity的onActivityResult()中调用
 *     {@link #onWriteSysSettingsPermissionResult(Activity, int)}，
 *     否则无法通知权限是否授予</strong><br/>
 * </p>
 *
 * Created by LinXiao on 2016-11-24.
 */
public class PermissionManager {
    private static final String TAG = PermissionManager.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int SYSTEM_ALERT_PERMISSION_REQUEST_CODE = 1002;
    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 1003;

    private static PermissionProhibitedListener defaultProhibitedListener;

    private static PermissionOperator currPermissionOperator;
    private static RequestPermissionCallback reqSysAlertCallback;
    private static RequestPermissionCallback reqSysSettingsCallback;

    private PermissionManager() {}

    /**
     * 检查是否需要RuntimePermission，即 System API >= API 23
     * */
    private static boolean checkHigherThanMarshmallow() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    /**
     * 检查权限是否已经被授予
     * @param context 所需context
     * @param requestPermissions 所需检查的权限
     * */
    public static boolean checkPermissionsGranted(Context context, String... requestPermissions) {
        for (String permission : requestPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置申请权限被完全禁止时的默认操作
     * @param listener 默认监听器
     * */
    public static void setDefaultActionOnPermissionProhibited(PermissionProhibitedListener listener) {
        defaultProhibitedListener = listener;
    }

    /**
     * 执行需要检查权限的代码
     * <p>将功能代码写在声明的onRequestPermissionCallback对象中，可以一次申请多个权限，
     * 如果描述了申请权限的理由，会弹出对话框告诉用户，然后弹出系统申请权限的dialog，
     * 如果不需要写理由传入null即可</p>
     * @param permissions 所需申请的权限
     * */
    public static PermissionOperator performWithPermission(String... permissions) {
        currPermissionOperator = new PermissionOperator(permissions);
        return currPermissionOperator;
    }

    /**
     * 处理申请权限的回调
     * <p>检查用户当前申请的权限是否被授予并执行相关的操作。
     * <strong>请务必在申请权限代码所属Activity的onRequestPermissionResult()中调用此方法</strong></p>
     * */
    public static void handleCallback(final Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Logger.d(TAG, "handleCallback: requestCode = " + requestCode);
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return;
        }
        if (currPermissionOperator != null) {
            currPermissionOperator.handleCallback(activity, requestCode, permissions, grantResults);
        }
        currPermissionOperator = null;
    }

    /**
     * 申请系统级Dialog权限
     * <p>可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请</p>
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     * */
    public static void requestSystemAlertWindowPermission(Activity activity, RequestPermissionCallback callback) {
        if (checkHigherThanMarshmallow()) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, SYSTEM_ALERT_PERMISSION_REQUEST_CODE);
                reqSysAlertCallback = callback;
                return;
            }
        }
        if (callback != null) {
            callback.onGranted();
        }
    }

    /**
     * 申请系统级Dialog权限
     * <p>可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请</p>
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * */
    public static void requestSystemAlertWindowPermission(Activity activity) {
        requestSystemAlertWindowPermission(activity, null);
    }

    /**
     * 检查 SYSTEM_ALERT_WINDOW 权限是否已被授予
     * <p><strong>请务必在申请权限操作所属的Activity中调用此方法</strong></p>
     * */
    public static void onSysAlertPermissionResult(Activity activity, int requestCode) {
        if (requestCode != SYSTEM_ALERT_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (checkHigherThanMarshmallow()) {
            if (Settings.canDrawOverlays(activity)) {
                Logger.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted");
                if (reqSysAlertCallback != null) {
                    reqSysAlertCallback.onGranted();
                }
            }
            else {
                Logger.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied");
                if (reqSysAlertCallback != null) {
                    reqSysAlertCallback.onDenied();
                }
            }
            reqSysAlertCallback = null;
        }
    }

    /**
     * 申请修改系统设置的权限
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     * */
    public static void requestWriteSystemSettingsPermission(Activity activity, RequestPermissionCallback callback) {
        if (checkHigherThanMarshmallow()) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE );
                reqSysSettingsCallback = callback;
                return;
            }
        }
        if (callback != null) {
            callback.onGranted();
        }
    }

    /**
     * 申请修改系统设置的权限
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * */
    public static void requestWriteSystemSettingsPermission(Activity activity) {
        requestWriteSystemSettingsPermission(activity, null);
    }

    /**
     * 检查 WRITE_SETTINGS 权限是否已被授予
     * <p><strong>请务必在申请权限操作所属的Activity中调用此方法</strong></p>
     * */
    public static void onWriteSysSettingsPermissionResult(Activity activity, int requestCode) {
        if (requestCode != WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (checkHigherThanMarshmallow()) {
            if (Settings.System.canWrite(activity)) {
                Logger.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted");
                if (reqSysSettingsCallback != null) {
                    reqSysSettingsCallback.onGranted();
                }
            }
            else {
                Logger.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied");
                if (reqSysSettingsCallback != null) {
                    reqSysSettingsCallback.onDenied();
                }
            }
        }
        reqSysSettingsCallback = null;
    }

    /**
     * 当权限申请Dialog被用户禁止弹出时使用，引导用户前往应用权限页面开启权限
     * */
    public static void showPermissionProhibitedDialog(final Context context, String permission) {
        String permissionGroupName = PermissionManager.getPermissionGroupName(context, permission);
        String message = permissionGroupName + context.getString(R.string.toast_permission_denied);
        AlertDialogManager.createAlertDialogBuilder()
        .setMessage(message)
        .setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationUtil.jumpToApplicationDetail(context);
            }
        })
        .setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .show();
    }

    /**
     * 获取权限名称
     * <p>在需要显示某个具体权限的名称时使用</p>
     * */
    public static String getPermissionName(Context context, String permission) {
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
     * */
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


    public static class PermissionOperator {

        private String[] requestPermissions;
        private RequestPermissionCallback currCallback;
        private PermissionProhibitedListener prohibitedListener;
        private String requestDesc = null;

        PermissionOperator(String... permissions) {
            this.requestPermissions = permissions;
        }

        /**
         * 在申请权限前显示申请权限的解释
         * <p>将会在执行时弹出AlertDialog显示解释内容</p>
         * @param rationale 解释文本
         * */
        public PermissionOperator showRationaleBeforeRequest(String rationale) {
            this.requestDesc = rationale;
            return this;
        }

        /**
         * 设置权限被完全禁止时的操作
         * <p>考虑到潜在的归一化处理与特殊处理的需求，没有将其定义在RequestPermissionListener中</p>
         * @param prohibitedListener 监听器
         * */
        public PermissionOperator doOnProhibited(PermissionProhibitedListener prohibitedListener) {
            this.prohibitedListener = prohibitedListener;
            return this;
        }

        /**
         * 根据配置参数执行检查/申请权限并执行回调
         * @param activity 执行代码的Activity，局部变量，用于执行申请权限方法
         * @param callback 权限检查/申请后的回调监听
         * */
        public void perform(final Activity activity, RequestPermissionCallback callback) {
            if (requestPermissions == null || requestPermissions.length <= 0) {
                return;
            }
            if (!checkHigherThanMarshmallow() || checkPermissionsGranted(activity, requestPermissions)) {
                callback.onGranted();
                return;
            }
            currCallback = callback;
            if (TextUtils.isEmpty(requestDesc)) {
                ActivityCompat.requestPermissions(activity, requestPermissions, PERMISSION_REQUEST_CODE);
            }
            else {
                AlertDialogManager.createAlertDialogBuilder()
                .setMessage(requestDesc)
                .setPositiveButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, requestPermissions, PERMISSION_REQUEST_CODE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: on permission denied");
                        dialog.dismiss();
                        currCallback.onDenied();
                    }
                })
                .show();
            }
        }

        /**
         * 处理申请权限的回调
         * <p>检查用户当前申请的权限是否被授予并执行相关的操作。
         * <strong>请务必在申请权限代码所属Activity的onRequestPermissionResult()中调用此方法</strong></p>
         * */
        void handleCallback(final Activity activity, int requestCode, String[] permissions, int[] grantResults) {
            if (currCallback == null) {
                return;
            }
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    continue;
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    currCallback.onDenied();
                }
                else  {
                    if (prohibitedListener != null) {
                        prohibitedListener.onProhibited(permissions[i]);
                    }
                    else if (defaultProhibitedListener != null) {
                        defaultProhibitedListener.onProhibited(permissions[i]);
                    }
                }
                return;
            }
            currCallback.onGranted();
            currCallback = null;
        }
    }
}
