package com.linxiao.quickdevframework.sample.frameworkapi;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.file.FileCopyTask;
import com.linxiao.framework.file.FileDeleteTask;
import com.linxiao.framework.file.FileModifyListener;
import com.linxiao.framework.file.FileSizeUtil;
import com.linxiao.framework.file.FileUtil;
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
    private String totalFilePath = FileUtil.extRoot() + File.separator + "QuickDevFramework";

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
    }

    @OnClick(R.id.btnCopyFileSimple)
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
                tvTotalSum.setText("totalSum:"  + totalCount + "");
                tvCurrentSum.setText("currentSum:" + finishedCount + "");
                tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
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

    @OnClick(R.id.btnCopyFolderSimple)
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
                tvTotalSum.setText("totalSum:"  + totalCount + "");
                tvCurrentSum.setText("currentSum:" + finishedCount + "");
                tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
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

    @OnClick(R.id.btnDeleteFolderSimple)
    public void OnDeleteFolderSimple() {
        FileDeleteTask.newInstance(new File(totalFilePath + File.separator + "FolderExample"))
        .setFileModifyListener(new FileModifyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgressUpdate(long totalCount, long finishedCount, long totalSize, long finishedSize) {
                tvTotalSum.setText("totalSum:"  + totalCount + "");
                tvCurrentSum.setText("currentSum:" + finishedCount + "");
                tvTotalSize.setText("totalSize:" + FileSizeUtil.getFormattedSizeString(totalSize));
                tvCurrentSize.setText("currentSize:" + FileSizeUtil.getFormattedSizeString(finishedSize));
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
        tvHasSDCard.setText(getString(R.string.is_exist_sd_card) + ": " + FileUtil.hasExt());
        tvHasPermission.setText(getString(R.string.has_file_permission) + ": " + PermissionManager.hasSDCardPermission());
    }

}
