package com.linxiao.quickdevframework.sample.netapi;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.net.SimpleDownloadTask;
import com.linxiao.quickdevframework.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * download test fragment
 * <p>
 * class usage summary
 * </p>
 *
 * @author linxiao
 * @since 2019-12-07
 */
public class DownloadTestFragment extends BaseFragment {


    @BindView(R.id.etUrl)
    EditText etUrl;
    @BindView(R.id.btnStart)
    Button btnStart;

    ProgressDialog progressDialog;
    private SimpleDownloadTask downloadTask;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_download_test, container);
        ButterKnife.bind(this, getContentView());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", (dialog, which) -> {
            if (downloadTask != null) {
                downloadTask.cancel();
            }
            dialog.dismiss();
        });
        etUrl.setText("https://pkg1.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-6.23.0(1778).apk");
    }


    @OnClick(R.id.btnStart)
    public void onClickStart() {
        String url = etUrl.getText().toString();
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
