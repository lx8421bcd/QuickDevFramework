package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.file.FileSizeListener;
import com.linxiao.framework.file.FileCountListener;
import com.linxiao.framework.file.FileManager;
import com.linxiao.framework.log.Logger;
import com.linxiao.framework.permission.PermissionManager;
import com.linxiao.framework.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileApiFragment extends BaseFragment {
    private String totalFilePath = FileManager.getExternalStorageRoot() + File.separator + "QuickDevFramework";

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
    protected void onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_file_api, container);
        ButterKnife.bind(this, getContentView());
        PermissionManager.createPermissionOperator()
        .requestSDCard()
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {
                Logger.d(TAG, FileManager.getExternalStorageRootString());
                Logger.d(TAG, FileManager.getInternalStorageRootString());
                try {
                    FileManager.pathStringToFile(totalFilePath).mkdir();
                    File txtFile = FileManager.pathStringToFile(totalFilePath + File.separator + "text.txt");
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
                AlertDialogManager.showAlertDialog("请授予文件管理权限");
            }
        });
    }

    @OnClick(R.id.btnCopyFileSimple)
    public void OnCopyFileSimple() {
        FileManager.pathStringToFile(totalFilePath + File.separator + "Copy").mkdir();
        FileManager.copyFileOperate(FileManager.pathStringToFile(totalFilePath + File.separator + "text.txt"),
                totalFilePath + File.separator + "Copy").setFileSizeListener(new FileSizeListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final double count, final double current) {
                tvCurrentSize.setText("currentSize:" + current + "KB");
                tvTotalSize.setText("totalSize:" + count + "KB");
            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getContext(), "copy success");
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
            FileManager.pathStringToFile(totalFilePath + File.separator + "FolderExample").mkdir();
            FileManager.pathStringToFile(totalFilePath + File.separator + "FolderExample"+ File.separator + "1").mkdir();
            FileManager.pathStringToFile(totalFilePath + File.separator + "FolderExample"+ File.separator + "2").mkdir();
            FileManager.pathStringToFile(totalFilePath + File.separator + "CopyFolder").mkdir();
            FileManager.pathStringToFile(totalFilePath).mkdir();

            File txtFile = FileManager.pathStringToFile(totalFilePath + File.separator + "FolderExample"+
                    File.separator + "2" + File.separator + "text.txt");
            File txtFile_ = FileManager.pathStringToFile(totalFilePath + File.separator + "FolderExample"+
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

        FileManager.copyFileOperate(
                FileManager.pathStringToFile(
                    totalFilePath + File.separator + "FolderExample"),
                    totalFilePath + File.separator + "CopyFolder"
        ).setFileSizeListener(new FileSizeListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final double count, final double current) {
                tvCurrentSize.setText("currentSize:" + current + "KB");
                tvTotalSize.setText("totalSize:" + count + "KB");

            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getContext(), "copy success");
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
        FileManager.deleteFileOperate(FileManager.pathStringToFile(totalFilePath + File.separator +
                "FolderExample")).setFileCountListener(new FileCountListener() {
            @Override
            public void onStart() {}

            @Override
            public void onProgressUpdate(final long count, final long current) {
                tvCurrentSum.setText("currentSum:" + current + "");
                tvTotalSum.setText("totalSum:"  + count + "");
            }

            @Override
            public void onSuccess() {
                ToastAlert.showToast(getContext(), "delete success");
            }

            @Override
            public void onFail(String failMsg) {
                ToastAlert.showToast(getContext(), failMsg);
            }
        }).execute();
        tvHasSDCard.setText(getString(R.string.is_exist_sd_card) + ": " + FileManager.existExternalStorage());
        tvHasPermission.setText(getString(R.string.has_file_permission) + ": " + FileManager.hasFileOperatePermission());
    }

}
