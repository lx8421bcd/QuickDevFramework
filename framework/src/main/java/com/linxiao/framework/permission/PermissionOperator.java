package com.linxiao.framework.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.common.ApplicationUtil;
import com.linxiao.framework.dialog.AlertDialogManager;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class PermissionOperator {

    private static final String TAG = PermissionOperator.class.getSimpleName();

    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final int MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    public static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 1003;
    public static final int INSTALL_PACKAGE_REQUEST_CODE = 1004;

    private ArrayList<String> requestPermissions = new ArrayList<>();
    private RequestPermissionCallback currCallback;
    private PermissionProhibitedListener prohibitedListener;
    private String requestDesc = null;

    private int requestCode;

    //权限被禁止的默认处理
    static PermissionProhibitedListener defaultProhibitedListener;

    PermissionOperator() {
        requestCode = PERMISSION_REQUEST_CODE;
    }
    
    public PermissionOperator addRequestPermission(String permission) {
        if (!requestPermissions.contains(permission)) {
            requestPermissions.add(permission);
        }
        return this;
    }

    public PermissionOperator requestCalendar() {
        addRequestPermission(Manifest.permission.READ_CALENDAR);
        addRequestPermission(Manifest.permission.WRITE_CALENDAR);
        return this;
    }

    public PermissionOperator requestReadPhoneState() {
        return addRequestPermission(Manifest.permission.READ_PHONE_STATE);
    }

    public PermissionOperator requestAudioRecord() {
        return addRequestPermission(Manifest.permission.RECORD_AUDIO);
    }

    public PermissionOperator requestCamera() {
        return addRequestPermission(Manifest.permission.CAMERA);
    }

    public PermissionOperator requestSDCard() {
        addRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        addRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return this;
    }

    public PermissionOperator requestLocation() {
        addRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        return this;
    }
    
    public PermissionOperator requestManageOverlayPermission() {
        requestCode = MANAGE_OVERLAY_PERMISSION_REQUEST_CODE;
        return this;
    }
    
    public PermissionOperator requestWriteSystemSettingsPermission() {
        requestCode = WRITE_SETTINGS_PERMISSION_REQUEST_CODE;
        return this;
    }

    public PermissionOperator requestInstallPackagePermission() {
        requestCode = INSTALL_PACKAGE_REQUEST_CODE;
        return this;
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (requestPermissions == null || requestPermissions.isEmpty()) {
                callback.onGranted();
                return;
            }
            String requestArr[] = requestPermissions.toArray(new String[]{});
            if (PermissionManager.isPermissionsGranted(activity, requestArr)) {
                callback.onGranted();
                return;
            }
        }
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE &&
                PermissionManager.hasManageOverlayPermission()) {
            callback.onGranted();
            return;
        }
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE &&
                PermissionManager.hasWriteSystemSettingsPermission()) {
            callback.onGranted();
            return;
        }
        if (requestCode == INSTALL_PACKAGE_REQUEST_CODE &&
                PermissionManager.hasInstallPackagePermission()) {
            callback.onGranted();
            return;
        }
        currCallback = callback;
        // 权限未授予，进入申请
        if (TextUtils.isEmpty(requestDesc)) {
            execRequest(activity, callback);
            return;
        }
        AlertDialogManager.createAlertDialogBuilder()
        .setMessage(requestDesc)
        .setPositiveButton((dialog, which) -> {
            execRequest(activity, callback);
            dialog.dismiss();
        })
        .setNegativeButton((dialog, which) -> {
            Log.d(TAG, "onClick: on permission denied");
            dialog.dismiss();
            currCallback.onDenied();
        })
        .setCancelable(false)
        .show();
    }

    public Observable<Object> performRx(final Activity activity) {
        return Observable.create(emitter -> perform(activity, new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                emitter.onNext(new Object());
                emitter.onComplete();
            }

            @Override
            public void onDenied() {
                emitter.onError(new PermissionException());
                emitter.onComplete();
            }
        }))
        .subscribeOn(AndroidSchedulers.mainThread());
    }

    private void execRequest(final Activity activity, RequestPermissionCallback callback) {
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            requestManageOverlayPermission(activity, callback);
            return;
        }
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            requestWriteSystemSettingsPermission(activity, callback);
            return;
        }
        if (requestCode == INSTALL_PACKAGE_REQUEST_CODE) {
            requestInstallPackagePermission(activity, callback);
            return;
        }
        String requestArr[] = requestPermissions.toArray(new String[]{});
        ActivityCompat.requestPermissions(activity, requestArr, PERMISSION_REQUEST_CODE);
    }

    /**
     * 申请系统级Dialog权限
     * <p>可用于Service弹出Dialog，悬浮窗等功能，一般应用不建议申请</p>
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestManageOverlayPermission(Activity activity, RequestPermissionCallback callback) {
        if (PermissionManager.hasManageOverlayPermission()) {
            if (callback != null) {
                callback.onGranted();
            }
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            currCallback = callback;
        }
        //某些机型找不到浮窗设置页面，会弹出no activity handle exception，此时跳转应用详情，无法回调
        else {
            ApplicationUtil.jumpToApplicationDetail(activity);
        }

    }

    /**
     * 申请修改系统设置的权限
     * @param activity 申请权限的Activity，Fragment申请权限请传入getActivity()
     * @param callback 申请权限回调
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestWriteSystemSettingsPermission(Activity activity, RequestPermissionCallback callback) {
        if (PermissionManager.hasWriteSystemSettingsPermission()) {
            if (callback != null) {
                callback.onGranted();
            }
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE );
        currCallback = callback;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestInstallPackagePermission(Activity activity, RequestPermissionCallback callback) {
        if (PermissionManager.hasInstallPackagePermission()) {
            if (callback != null) {
                callback.onGranted();
            }
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
        currCallback = callback;
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

            // 如果这个值返回true，代表权限申请被拒绝，可以弹理由了
            // 如果返回false，代表权限申请被用户完全禁止（第一次返回false表示可以不弹理由，在申请权限前调用）
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                currCallback.onDenied();
                currCallback = null;
                return;
            }
            if (prohibitedListener != null) {
                prohibitedListener.onProhibited(permissions[i]);
            }
            else if (defaultProhibitedListener != null) {
                defaultProhibitedListener.onProhibited(permissions[i]);
            }
            else {
                Log.e(TAG, "permission request prohibited without callback handle");
                Log.e(TAG, "if you haven't seen permission request dialog, " +
                        "check you have declared permission in the manifest");
            }
            currCallback = null;
            return;
        }
        currCallback.onGranted();
        currCallback = null;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (PermissionManager.hasRuntimePermission()) {
                if (Settings.canDrawOverlays(activity)) {
                    Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted");
                    if (currCallback != null) {
                        currCallback.onGranted();
                    }
                }
                else {
                    Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied");
                    if (currCallback != null) {
                        currCallback.onDenied();
                    }
                }
                currCallback = null;
            }
        }
        else if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (PermissionManager.hasRuntimePermission()) {
                if (Settings.System.canWrite(activity)) {
                    Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW granted");
                    if (currCallback != null) {
                        currCallback.onGranted();
                    }
                }
                else {
                    Log.d(TAG, "onSysAlertPermissionResult: SYSTEM_ALERT_WINDOW denied");
                    if (currCallback != null) {
                        currCallback.onDenied();
                    }
                }
            }
            currCallback = null;
        }
        else if (requestCode == INSTALL_PACKAGE_REQUEST_CODE) {
            if (PermissionManager.hasInstallPackagePermission()) {
                if (currCallback != null) {
                    currCallback.onGranted();
                }
            }
            else {
                if (currCallback != null) {
                    currCallback.onDenied();
                }
            }
        }
    }
}
