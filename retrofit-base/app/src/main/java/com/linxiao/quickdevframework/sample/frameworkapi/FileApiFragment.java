package com.linxiao.quickdevframework.sample.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.file.FileWrapper;
import com.linxiao.framework.support.log.Logger;
import com.linxiao.framework.support.permission.PermissionWrapper;
import com.linxiao.framework.support.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileApiFragment extends BaseFragment {

    @BindView(R.id.tvHasSDCard)
    TextView tvHasSDCard;
    @BindView(R.id.tvHasPermission)
    TextView tvHasPermission;

    @Override
    protected int rootViewResId() {
        return R.layout.fragment_file_api;
    }

    @Override
    protected void onCreateContentView(View rootView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
        tvHasSDCard.setText(getString(R.string.is_exist_sd_card) + ": " + FileWrapper.existExternalStorage());
        tvHasPermission.setText(getString(R.string.has_file_permission) + ": " + FileWrapper.hasFileOperatePermission());

    }

}
