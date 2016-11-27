package com.linxiao.framework.support;

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

/**
 * 权限管理类
 * <p>用于适配Android 6.0+ 的权限问题。<br/>
 *
 * Google定义的Runtime权限使用 performWithPermission() 方法处理即可,
 * <strong>务必在申请权限操作所属 Activity的onRequestPermissionResult() 中调用 handleCallback() ,否则不会执行回调</strong><br/>
 *
 * SYSTEM_ALERT_WINDOW 权限请使用 requestSystemAlertWindowPermission() 函数申请,
 * <strong>必须在申请操作所属Activity的onActivityResult()中调用onSysAlertPermissionResult，否则无法通知权限是否授予</strong><br/>
 *
 * WRITE_SETTINGS 权限请使用 requestWriteSystemSettingsPermission() 函数申请,
 * <strong>必须在申请操作所属Activity的onActivityResult()中调用onWriteSysSettingsPermissionResult，否则无法通知权限是否授予</strong><br/>
 * </p>
 * Created by LinXiao on 2016-11-24.
 */
public class PermissionWrapper {
    private static final String TAG = PermissionWrapper.class.getSimpleName();

    /**
     * 申请权限回调接口
     * */
    public interface OnRequestPermissionCallback {
        /**
         * 权限已被授予
         * */
        void onPermissionGranted();
        /**
         * 用户拒绝权限申请
         * */
        void onPermissionRejected();
    }

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int SYSTEM_ALERT_PERMISSION_REQUEST_CODE = 1002;
    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 1003;

    private static PermissionWrapper instance;

    private OnRequestPermissionCallback currCallback;

    private PermissionWrapper() {}

    public static PermissionWrapper getInstance() {
        if (instance == null) {
            instance = new PermissionWrapper();
        }
        return instance;
    }

    /**
     * 检查是否需要RuntimePermission，即 System API >= API 23
     * */
    private boolean checkNeedPermissionGrant() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    /**
     * 检查权限是否已经被授予
     * @param context 所需context
     * @param requestPermissions 所需检查的权限
     * */
    public boolean checkPermissionsGranted(Context context, String... requestPermissions) {
        for (String permission : requestPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请系统级Dialog权限
     * <p>可用于Service弹出对话框，悬浮窗等功能，一般应用不建议申请</p>
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * */
    public void requestSystemAlertWindowPermission(Activity activity) {
        if (checkNeedPermissionGrant()) {
            if (Settings.canDrawOverlays(activity)) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, SYSTEM_ALERT_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 申请修改系统设置的权限
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * */
    public void requestWriteSystemSettingsPermission(Activity activity) {
        if (checkNeedPermissionGrant()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE );
        }
    }

    /**
     * 执行需要检查权限的代码
     * <p>将功能代码写在声明的onRequestPermissionCallback对象中，可以一次申请多个权限，
     * 如果描述了申请权限的理由，会弹出对话框告诉用户，然后弹出系统申请权限的dialog，
     * 如果不需要写理由传入null即可</p>
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param requestDesc 申请权限的理由，如果不为空则会采用AlertDialog的形式告诉用户
     * @param permissions 所需申请的权限
     * @param callback 申请权限回调
     * */
    public void performWithPermission(final Activity activity, String requestDesc, final String[] permissions, OnRequestPermissionCallback callback) {
        if (checkNeedPermissionGrant() || checkPermissionsGranted(activity, permissions)) {
            callback.onPermissionGranted();
            return;
        }
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    String hintToast = getPermissionGroupName(activity, permission) + activity.getString(R.string.toast_permission_denied);
                    ToastWrapper.showToast(activity, hintToast);
                    return;
                }
            }
        }
        currCallback = callback;
        if (TextUtils.isEmpty(requestDesc)) {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
        }
        else {
            AlertDialogWrapper.buildAlertDialog()
            .setMessage(requestDesc)
            .setPositiveButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
                    dialog.dismiss();
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
    }

    /**
     * 处理申请权限的回调
     * <p>检查用户当前申请的权限是否被授予并执行相关的操作。
     * <strong>请务必在申请权限代码所属Activity的onRequestPermissionResult()中调用此方法</strong></p>
     * */
    public void handleCallback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (currCallback == null) {
            return;
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (verifyPermissionResult(grantResults)) {
                currCallback.onPermissionGranted();
            }
            else {
                currCallback.onPermissionRejected();
            }
        }
        currCallback = null;
    }

    /**
     * 检查 SYSTEM_ALERT_WINDOW 权限是否已被授予
     * <p><strong>请务必在申请权限操作所属的Activity中调用此方法</strong></p>
     * */
    public void onSysAlertPermissionResult(Activity activity, int requestCode) {
        if (checkNeedPermissionGrant()) {
            if (requestCode == SYSTEM_ALERT_PERMISSION_REQUEST_CODE) {
                if (Settings.canDrawOverlays(activity)) {
                    Log.i(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW has been granted");
                }
            }
        }
    }

    /**
     * 检查 WRITE_SETTINGS 权限是否已被授予
     * <p><strong>请务必在申请权限操作所属的Activity中调用此方法</strong></p>
     * */
    public void onWriteSysSettingsPermissionResult(Activity activity, int requestCode) {
        if (checkNeedPermissionGrant()) {
            if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
                if (Settings.System.canWrite(activity)) {
                    Log.i(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW has been granted");
                }
            }
        }
    }

    /**
     * 检查RuntimePermission 申请结果
     * */
    private boolean verifyPermissionResult(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限名称
     * <p>在需要显示某个具体权限的名称时使用</p>
     * */
    public String getPermissionName(Context context, String permission) {
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
    public String getPermissionGroupName(Context context, String permission) {
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
