package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.framework.file.FileCopyTask;
import com.linxiao.framework.file.FileDeleteTask;
import com.linxiao.framework.file.FileModifyListener;
import com.linxiao.framework.file.FileSizeUtil;
import com.linxiao.framework.file.FileUtil;
import com.linxiao.framework.permission.PermissionManager;
import com.linxiao.framework.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentFileApiBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingFragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileApiFragment extends SimpleViewBindingFragment<FragmentFileApiBinding> {
    private String totalFilePath = FileUtil.extRoot() + File.separator + "QuickDevFramework";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PermissionManager.createPermissionOperator()
        .requestSDCard()
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                Log.d(TAG, FileUtil.extRoot().getAbsolutePath());
                try {
                    new File(totalFilePath).mkdirs();
                    File txtFile = new File(totalFilePath + File.separator + "text.txt");
                    txtFile.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(txtFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                    byte[] buff = new byte[1024000];
                    for (int i = 0; i < 1024000; i++) {
                        buff[i] = 1;
                    }
                    bufferedOutputStream.write(buff, 0, 1024000);
                    bufferedOutputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDenied() {
                AlertDialogManager.showAlertDialog("请授予文件管理权限");
            }
        });
        getViewBinding().btnCopyFileSimple.setOnClickListener(v -> OnCopyFileSimple());
        getViewBinding().btnCopyFolderSimple.setOnClickListener(v -> OnCopyFolderSimple());
        getViewBinding().btnDeleteFolderSimple.setOnClickListener(v -> OnDeleteFolderSimple());
    }

    public void OnCopyFileSimple() {
        File src = new File(totalFilePath + File.separator + "text.txt");
        File target = new File(totalFilePath + File.separator + "Copy");
        target.mkdirs();
        FileCopyTask.newInstance(src, target.getPath())
        .setFileModifyListener(new FileModifyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgressUpdate(long totalCount, long finishedCount, long totalSize, long finishedSize) {
                getViewBinding().tvTotalSum.setText("totalSum:"  + totalCount + "");
                getViewBinding().tvCurrentSum.setText("currentSum:" + finishedCount + "");
                getViewBinding().tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                getViewBinding().tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getActivity(), "copy success");
            }

            @Override
            public void onError(Throwable e) {
                ToastAlert.showToast(getActivity(), e.getMessage());
                e.printStackTrace();
            }
        })
        .execute();
    }

    public void OnCopyFolderSimple() {
        try {
            new File(totalFilePath + File.separator + "FolderExample").mkdirs();
            new File(totalFilePath + File.separator + "FolderExample"+ File.separator + "1").mkdirs();
            new File(totalFilePath + File.separator + "FolderExample"+ File.separator + "2").mkdirs();
            new File(totalFilePath + File.separator + "CopyFolder").mkdirs();
            new File(totalFilePath).mkdirs();

            File txtFile = new File(totalFilePath + File.separator + "FolderExample"+ File.separator + "2" + File.separator + "text.txt");
            File txtFile_ = new File(totalFilePath + File.separator + "FolderExample"+ File.separator + "text.txt");
            txtFile.createNewFile();
            txtFile_.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(txtFile);
            FileOutputStream outputStream_ = new FileOutputStream(txtFile_);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedOutputStream bufferedOutputStream_ = new BufferedOutputStream(outputStream_);
            byte[] buff = new byte[1024000];
            for (int i = 0; i < 1024000; i++) {
                buff[i] = 1;
            }
            bufferedOutputStream.write(buff, 0, 1024000);
            bufferedOutputStream_.write(buff, 0, 1024000);
            bufferedOutputStream.close();
            outputStream.close();
            bufferedOutputStream_.close();
            outputStream_.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileCopyTask.newInstance(new File(totalFilePath + File.separator + "FolderExample"),
                totalFilePath + File.separator + "CopyFolder")
        .setFileModifyListener(new FileModifyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgressUpdate(long totalCount, long finishedCount, long totalSize, long finishedSize) {
                getViewBinding().tvTotalSum.setText("totalSum:"  + totalCount + "");
                getViewBinding().tvCurrentSum.setText("currentSum:" + finishedCount + "");
                getViewBinding().tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                getViewBinding().tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getActivity(), "copy success");
            }

            @Override
            public void onError(Throwable e) {
                ToastAlert.showToast(getActivity(), e.getMessage());
                e.printStackTrace();
            }
        })
        .execute();
    }

    public void OnDeleteFolderSimple() {
        FileDeleteTask.newInstance(new File(totalFilePath + File.separator + "FolderExample"))
        .setFileModifyListener(new FileModifyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgressUpdate(long totalCount, long finishedCount, long totalSize, long finishedSize) {
                getViewBinding().tvTotalSum.setText("totalSum:"  + totalCount + "");
                getViewBinding().tvCurrentSum.setText("currentSum:" + finishedCount + "");
                getViewBinding().tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                getViewBinding().tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getActivity(), "delete success");
            }

            @Override
            public void onError(Throwable e) {
                ToastAlert.showToast(getActivity(), e.getMessage());
                e.printStackTrace();
            }
        })
        .execute();
        getViewBinding().tvHasSDCard.setText(getString(R.string.is_exist_sd_card) + ": " + FileUtil.hasExt());
        getViewBinding().tvHasPermission.setText(getString(R.string.has_file_permission) + ": " + PermissionManager.hasSDCardPermission());
    }

}
