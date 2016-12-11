package com.linxiao.quickdevframework.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.quickdevframework.R;

public class PermissionApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_api);
    }

    public void onRequestPermissionClick(View v) {
        PermissionWrapper.getInstance().performWithPermission(this, "请授予相机权限",
                new String[]{Manifest.permission.CAMERA}, new PermissionWrapper.OnRequestPermissionCallback() {
                    @Override
                    public void onGranted() {
                        AlertDialogWrapper.showAlertDialog("权限已授予");
                    }

                    @Override
                    public void onDenied() {
                        AlertDialogWrapper.showAlertDialog("未授予权限");
                    }
                });
    }

    public void onReqSysAlertClick(View v) {
        PermissionWrapper.getInstance().requestSystemAlertWindowPermission(this);
    }

    public void onReqWriteSettingsClick(View v) {
        PermissionWrapper.getInstance().requestWriteSystemSettingsPermission(this);
    }
}
