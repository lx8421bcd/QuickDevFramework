package com.linxiao.quickdevframework.sample.netapi

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.ToastAlert
import com.linxiao.framework.common.globalContext
import com.linxiao.framework.net.SimpleDownloadTask
import com.linxiao.quickdevframework.databinding.FragmentDownloadTestBinding

/**
 * download test fragment
 *
 *
 * class usage summary
 *
 *
 * @author linxiao
 * @since 2019-12-07
 */
class DownloadTestFragment : SimpleViewBindingFragment<FragmentDownloadTestBinding>() {
    var progressDialog: ProgressDialog? = null
    private var downloadTask: SimpleDownloadTask? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "cancel"
        ) { dialog: DialogInterface, which: Int ->
            if (downloadTask != null) {
                downloadTask!!.cancel()
            }
            dialog.dismiss()
        }
        viewBinding.etUrl.setText("https://pkg1.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-6.23.0(1778).apk")
        viewBinding.btnStart.setOnClickListener { v: View? -> onClickStart() }
    }

    fun onClickStart() {
        val url = viewBinding.etUrl.getText().toString()
        downloadTask = SimpleDownloadTask(
            url,
            globalContext.externalCacheDir!!.path + "test.apk"
        ) //        .hideNotification()
            .setNotification("TestDownload", "download test")
        downloadTask!!.start(object : SimpleDownloadTask.DownloadListener {
            override fun onStart() {
                progressDialog!!.show()
                progressDialog!!.setMessage("download pending...")
            }

            override fun onProgress(total: Int, downloaded: Int) {
                var percent = 0.0
                if (total != 0) {
                    percent = downloaded * 1.0 / total * 100
                }
                progressDialog!!.setMessage(
                    String.format(
                        "downloading %d/%d bytes(%.1f%%)",
                        downloaded,
                        total,
                        percent
                    )
                )
            }

            override fun onCompleted(localPath: String?) {
                ToastAlert.show("download completed")
                progressDialog!!.dismiss()
            }

            override fun onError(e: Throwable?) {
                e!!.printStackTrace()
                ToastAlert.show(e.message)
            }
        })
    }
}
