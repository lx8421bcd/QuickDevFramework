package com.linxiao.quickdevframework.sample.netapi;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.net.SimpleDownloadTask;
import com.linxiao.quickdevframework.databinding.FragmentDownloadTestBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingFragment;

/**
 * download test fragment
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2019-12-07
 */
public class DownloadTestFragment extends SimpleViewBindingFragment<FragmentDownloadTestBinding> {

    ProgressDialog progressDialog;
    private SimpleDownloadTask downloadTask;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", (dialog, which) -> {
            if (downloadTask != null) {
                downloadTask.cancel();
            }
            dialog.dismiss();
        });
        getViewBinding().etUrl.setText("https://pkg1.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-6.23.0(1778).apk");
        getViewBinding().btnStart.setOnClickListener(v -> onClickStart());
    }

    public void onClickStart() {
        String url = getViewBinding().etUrl.getText().toString();
        downloadTask = SimpleDownloadTask.newInstance(url)
        .setDownloadTo(ContextProvider.get().getExternalCacheDir().getPath(), "test.apk")
//        .hideNotification()
        .setNotification("TestDownload", "download test")
        ;
        downloadTask.start(new SimpleDownloadTask.DownloadListener() {
            @Override
            public void onStart() {
                progressDialog.show();
                progressDialog.setMessage("download pending...");
            }

            @Override
            public void onProgress(int total, int downloaded) {
                double percent = 0;
                if (total != 0) {
                    percent = downloaded * 1.0 / total * 100;
                }
                progressDialog.setMessage(String.format("downloading %d/%d bytes(%.1f%%)", downloaded, total, percent));
            }

            @Override
            public void onCompleted(String localPath) {
                ToastAlert.show("download completed");
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastAlert.show(e.getMessage());
            }
        });
    }
}
