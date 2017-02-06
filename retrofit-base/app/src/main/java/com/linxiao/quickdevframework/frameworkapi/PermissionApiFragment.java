package com.linxiao.quickdevframework.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.quickdevframework.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PermissionApiFragment extends BaseFragment {

    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_permission_api;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick(R.id.btnRequestSample)
    public void onRequestPermissionClick(View v) {
        PermissionWrapper.getInstance().performWithPermission(getActivity(), "请授予相机权限",
            new PermissionWrapper.OnRequestPermissionCallback() {
                @Override
                public void onGranted() {
                    AlertDialogWrapper.showAlertDialog("权限已授予");
                }

                @Override
                public void onDenied() {
                    AlertDialogWrapper.showAlertDialog("未授予权限");
                }
            },
            Manifest.permission.CAMERA);
    }

    @OnClick(R.id.btnRequestAlertWindow)
    public void onReqSysAlertClick(View v) {
        PermissionWrapper.getInstance().requestSystemAlertWindowPermission(getActivity());
    }

    @OnClick(R.id.btnRequestWriteSettings)
    public void onReqWriteSettingsClick(View v) {
        PermissionWrapper.getInstance().requestWriteSystemSettingsPermission(getActivity());
    }
}
