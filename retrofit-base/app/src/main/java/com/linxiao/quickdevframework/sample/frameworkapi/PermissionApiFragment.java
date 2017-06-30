package com.linxiao.quickdevframework.sample.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.dialog.AlertDialogWrapper;
import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.permission.PermissionProhibitedListener;
import com.linxiao.framework.permission.PermissionWrapper;
import com.linxiao.framework.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PermissionApiFragment extends BaseFragment {

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_permission_api;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.btnRequestSample)
    public void onRequestPermissionClick(View v) {
        PermissionWrapper.performWithPermission(Manifest.permission.READ_CONTACTS)
        .perform(getActivity(),new RequestPermissionCallback() {
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

    @OnClick(R.id.btnRequestWithRationale)
    public void onRationaleClick(View v) {
        PermissionWrapper.performWithPermission(Manifest.permission.SEND_SMS)
        .showRationaleBeforeRequest("请授予发送短信权限权限以启用功能")
        .perform(getActivity(),new RequestPermissionCallback() {
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

    @OnClick(R.id.btnDoOnProhibited)
    public void OnProhibitedClick(View v) {
        PermissionWrapper.performWithPermission(Manifest.permission.READ_PHONE_STATE)
        .showRationaleBeforeRequest("请两次以上请求申请权限然后勾选\"不再提醒\"查看功能")
        .doOnProhibited(new PermissionProhibitedListener() {
            @Override
            public void onProhibited(String permission) {
                PermissionWrapper.showPermissionProhibitedDialog(getActivity(), permission);
            }
        })
        .perform(getActivity(),new RequestPermissionCallback() {
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

    @OnClick(R.id.btnRequestAlertWindow)
    public void onReqSysAlertClick(View v) {
        PermissionWrapper.requestSystemAlertWindowPermission(getActivity(), new RequestPermissionCallback() {
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

    @OnClick(R.id.btnRequestWriteSettings)
    public void onReqWriteSettingsClick(View v) {
        PermissionWrapper.requestWriteSystemSettingsPermission(getActivity(), new RequestPermissionCallback() {
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
}
