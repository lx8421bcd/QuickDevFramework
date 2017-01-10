package com.linxiao.quickdevframework.frameworkapi;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.file.FileWrapper;
import com.linxiao.framework.support.log.LogManager;
import com.linxiao.quickdevframework.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileApiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_api);
        PermissionWrapper.getInstance().performWithPermission(this, null,
        new PermissionWrapper.OnRequestPermissionCallback() {
            @Override
            public void onGranted() {
                LogManager.d(TAG, FileWrapper.getExternalStorageRoot());
                LogManager.e(TAG, new IOException("sss"));
                System.out.println(FileWrapper.getInternalStorageRoot());

                System.out.println(FileWrapper.checkIsAvailablePathString("/storage/emulated/0"));

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
                    LogManager.e(TAG, e);
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
        // /storage/emulated/0
        // /data/data/com.linxiao.quickdevframework/files


    }
}
