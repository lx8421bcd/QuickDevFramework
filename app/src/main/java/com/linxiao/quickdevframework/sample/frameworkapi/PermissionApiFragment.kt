package com.linxiao.quickdevframework.sample.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.dialog.DialogExtensionsKt;
import com.linxiao.framework.permission.PermissionManager;
import com.linxiao.framework.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.databinding.FragmentPermissionApiBinding;
import com.linxiao.framework.architecture.SimpleViewBindingFragment;

public class PermissionApiFragment extends SimpleViewBindingFragment<FragmentPermissionApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().btnRequestSample.setOnClickListener(this::onRequestPermissionClick);
        getViewBinding().btnRequestWithRationale.setOnClickListener(this::onRationaleClick);
        getViewBinding().btnDoOnProhibited.setOnClickListener(this::OnProhibitedClick);
        getViewBinding().btnRequestAlertWindow.setOnClickListener(this::onReqSysAlertClick);
        getViewBinding().btnRequestWriteSettings.setOnClickListener(this::onReqWriteSettingsClick);
    }

    public void onRequestPermissionClick(View v) {
        PermissionManager.createPermissionOperator()
        .requestAudioRecord()
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                DialogExtensionsKt.showAlert("权限已授予");
            }

            @Override
            public void onDenied() {
                DialogExtensionsKt.showAlert("未授予权限");
            }
        });
    }

    public void onRationaleClick(View v) {
        PermissionManager.createPermissionOperator()
        .addRequestPermission(Manifest.permission.SEND_SMS)
        .showRationaleBeforeRequest("请授予发送短信权限权限以启用功能")
        .perform(getActivity(),new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                DialogExtensionsKt.showAlert("权限已授予");
            }

            @Override
            public void onDenied() {
                DialogExtensionsKt.showAlert("未授予权限");
            }
        });
    }

    public void OnProhibitedClick(View v) {
        PermissionManager.createPermissionOperator()
        .addRequestPermission(Manifest.permission.READ_PHONE_STATE)
        .showRationaleBeforeRequest("请两次以上请求申请权限然后勾选\"不再提醒\"查看功能")
        .doOnProhibited(permission -> {
            // default handle
            PermissionManager.showPermissionProhibitedDialog(getActivity(), permission);
        })
        .perform(getActivity(),new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                DialogExtensionsKt.showAlert("权限已授予");
            }

            @Override
            public void onDenied() {
                DialogExtensionsKt.showAlert("未授予权限");
            }
        });
    }

    public void onReqSysAlertClick(View v) {
        PermissionManager.createPermissionOperator()
        .requestManageOverlayPermission()
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                DialogExtensionsKt.showAlert("权限已授予");
            }

            @Override
            public void onDenied() {
                DialogExtensionsKt.showAlert("未授予权限");
            }
        });
    }

    public void onReqWriteSettingsClick(View v) {
        PermissionManager.createPermissionOperator()
        .requestManageOverlayPermission()
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                DialogExtensionsKt.showAlert("权限已授予");
            }

            @Override
            public void onDenied() {
                DialogExtensionsKt.showAlert("未授予权限");
            }
        });
    }
}
