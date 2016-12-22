package com.linxiao.quickdevframework.frameworkapi;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.file.FileWrapper;
import com.linxiao.quickdevframework.R;

public class FileApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_api);
        PermissionWrapper.getInstance().performWithPermission(this, null,
        new PermissionWrapper.OnRequestPermissionCallback() {
            @Override
            public void onGranted() {
                System.out.println(FileWrapper.getExternalStorage());
                System.out.println(FileWrapper.getInternalStorage());

                System.out.println(FileWrapper.checkIsAvailablePathString("/storage/emulated/0"));
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
