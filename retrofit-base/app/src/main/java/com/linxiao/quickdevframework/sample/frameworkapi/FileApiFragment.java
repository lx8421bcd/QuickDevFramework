package com.linxiao.quickdevframework.sample.frameworkapi;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.ToastWrapper;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.framework.support.file.FileSizeListener;
import com.linxiao.framework.support.file.FileCountListener;
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
import butterknife.OnClick;

public class FileApiFragment extends BaseFragment {
    private String totalFilePath = FileWrapper.getExternalStorageRoot() + File.separator + "QuickDevFramework";

    @BindView(R.id.tvTotalSize)
    TextView tvTotalSize;
    @BindView(R.id.tvCurrentSize)
    TextView tvCurrentSize;
    @BindView(R.id.tvTotalSum)
    TextView tvTotalSum;
    @BindView(R.id.tvCurrentSum)
    TextView tvCurrentSum;

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
        PermissionWrapper.performWithPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                Logger.d(TAG, FileWrapper.getExternalStorageRoot());
                Logger.d(TAG, FileWrapper.getInternalStorageRoot());
                try {
                    FileWrapper.pathStringToFile(totalFilePath).mkdir();
                    File txtFile = FileWrapper.pathStringToFile(totalFilePath + File.separator + "text.txt");
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
                    Logger.e(TAG, e);
                }
            }

            @Override
            public void onDenied() {
                AlertDialogWrapper.showAlertDialog("请授予文件管理权限");
            }
        });
    }

    @OnClick(R.id.btnCopyFileSimple)
    public void OnCopyFileSimple() {
        FileWrapper.pathStringToFile(totalFilePath + File.separator + "Copy").mkdir();
        FileWrapper.copyFileOperate(FileWrapper.pathStringToFile(totalFilePath + File.separator + "text.txt"),
                totalFilePath + File.separator + "Copy", getContext()).setFileSizeListener(new FileSizeListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final double count, final double current) {
                tvCurrentSize.setText("currentSize:" + current + "KB");
                tvTotalSize.setText("totalSize:" + count + "KB");
            }

            @Override
            public void onSuccess() {
                ToastWrapper.showToast(getContext(), "copy success");
            }

            @Override
            public void onFail(String failMsg) {}
        }).setFileCountListener(new FileCountListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final long count, final long current) {
                tvCurrentSum.setText("currentSum:" + current + "");
                tvTotalSum.setText("totalSum:"  + count + "");
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFail(String failMsg) {}
        }).execute();
    }

    @OnClick(R.id.btnCopyFolderSimple)
    public void OnCopyFolderSimple() {
        try {
            FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample").mkdir();
            FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample"+ File.separator + "1").mkdir();
            FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample"+ File.separator + "2").mkdir();
            FileWrapper.pathStringToFile(totalFilePath + File.separator + "CopyFolder").mkdir();
            FileWrapper.pathStringToFile(totalFilePath).mkdir();

            File txtFile = FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample"+
                    File.separator + "2" + File.separator + "text.txt");
            File txtFile_ = FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample"+
                    File.separator + "text.txt");
            txtFile.createNewFile();
            txtFile.createNewFile();
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
            Logger.e(TAG, e);
        }

        FileWrapper.copyFileOperate(FileWrapper.pathStringToFile(totalFilePath + File.separator + "FolderExample"),
                totalFilePath + File.separator + "CopyFolder", getContext()).setFileSizeListener(new FileSizeListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final double count, final double current) {
                tvCurrentSize.setText("currentSize:" + current + "KB");
                tvTotalSize.setText("totalSize:" + count + "KB");

            }

            @Override
            public void onSuccess() {
                ToastWrapper.showToast(getContext(), "copy success");
            }

            @Override
            public void onFail(String failMsg) {}
        }).setFileCountListener(new FileCountListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final long count, final long current) {
                tvCurrentSum.setText("currentSum:" + current + "");
                tvTotalSum.setText("totalSum:"  + count + "");

            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFail(String failMsg) {}
        }).execute();
    }

    @OnClick(R.id.btnDeleteFolderSimple)
    public void OnDeleteFolderSimple() {
        FileWrapper.deleteFileOperate(FileWrapper.pathStringToFile(totalFilePath + File.separator +
                "FolderExample"), getContext()).setFileCountListener(new FileCountListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final long count, final long current) {
                tvCurrentSum.setText("currentSum:" + current + "");
                tvTotalSum.setText("totalSum:"  + count + "");
            }

            @Override
            public void onSuccess() {
                ToastWrapper.showToast(getContext(), "delete success");
            }

            @Override
            public void onFail(String failMsg) {
                ToastWrapper.showToast(getContext(), failMsg);
            }
        }).execute();
        tvHasSDCard.setText(getString(R.string.is_exist_sd_card) + ": " + FileWrapper.existExternalStorage());
        tvHasPermission.setText(getString(R.string.has_file_permission) + ": " + FileWrapper.hasFileOperatePermission());
    }

}
