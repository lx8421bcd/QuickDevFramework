package com.linxiao.quickdevframework.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.file.FileWrapper;
import com.linxiao.framework.support.log.Logger;
import com.linxiao.quickdevframework.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;

public class FileApiFragment extends BaseFragment {

    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_file_api;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
        PermissionWrapper.getInstance().performWithPermission(getActivity(), null,
            new PermissionWrapper.OnRequestPermissionCallback() {
                @Override
                public void onGranted() {
                    Logger.d(TAG, FileWrapper.getExternalStorageRoot());
                    Logger.e(TAG, new IOException("sss"));
                    System.out.println(FileWrapper.getInternalStorageRoot());

//                System.out.println(FileWrapper.checkIsAvailablePathString("/storage/emulated/0"));

                    try {
                        new File(FileWrapper.getExternalStorageRoot() + File.separator + "QuickDevFramework" + File.separator + "text.txt").createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(new File(FileWrapper.getExternalStorageRoot() + File.separator + "QuickDevFramework" + File.separator + "text.txt"));
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                        byte[] buff = new byte[1024];
                        for (int i = 0; i < 1024; i++) {
                            buff[i] = 1;
                        }
                        bufferedOutputStream.write(buff, 0, 1024);
                        bufferedOutputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        Logger.e(TAG, e);
//                    e.printStackTrace();
                    }
                }

                @Override
                public void onDenied() {
                    AlertDialogWrapper.showAlertDialog("请授予文件管理权限");
                }
            },
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}
