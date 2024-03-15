package com.linxiao.framework.net

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import com.linxiao.framework.common.globalContext
import com.linxiao.framework.file.FileUtil
import com.linxiao.framework.permission.PermissionException
import com.linxiao.framework.permission.PermissionManager
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max

/**
 * simple download tool
 *
 *
 * a simple download tool based on [DownloadManager],
 * using to handle simple download demands like download update apk.
 *
 *
 * @author lx8421bcd
 * @since 2019-12-07
 */
class SimpleDownloadTask(
    private val url: String,
    private val downloadToPath: String,
) {
    interface DownloadListener {
        fun onStart()
        fun onProgress(total: Int, downloaded: Int)
        fun onCompleted(localPath: String?)
        fun onError(e: Throwable?)
    }

    companion object {
        private val TAG = SimpleDownloadTask::class.java.getSimpleName()
        private const val MIN_UPDATE_PERIOD = 100
    }

    private var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
    private var downloadTo: File = File(downloadToPath)
    private var downloadId: Long = 0
    private var timer: Timer? = null
    private var task: TimerTask? = null
    private val callbackHandler = Handler()
    private var updatePeriod = MIN_UPDATE_PERIOD
    private var downloadListener: DownloadListener? = null

    init {
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
        )
        getMimeType(url)?.let {
            Log.d(TAG, "mimeType: $it")
            request.setMimeType(it)
        }
        request.setAllowedOverMetered(true)
        request.setAllowedOverRoaming(true)
    }

    /**
     * set download notification params
     *
     * this will set notification visibility to VISIBILITY_VISIBLE_NOTIFY_COMPLETED
     * @param title title
     * @param desc description
     */
    fun setNotification(title: String?, desc: String?): SimpleDownloadTask {
        request.setTitle(title)
        request.setDescription(desc)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        return this
    }

    /**
     * download without notification
     *
     *
     * need to declare android.permission.DOWNLOAD_WITHOUT_NOTIFICATION in manifest
     * to enable this setting
     *
     */
    fun hideNotification(): SimpleDownloadTask {
        if (PermissionManager.isPermissionsGranted(
                globalContext,
                "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"
            )
        ) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        }
        return this
    }

    /**
     * set specific file MIME type
     *
     *
     * the SimpleDownloadTask will auto generate MIME type for download file, use this
     * method to set a specific type if auto generate was failed or incorrect
     *
     * @param mimeType mimeType string
     */
    fun setDownloadMimeType(mimeType: String?): SimpleDownloadTask {
        request.setMimeType(mimeType)
        return this
    }

    /**
     * add extra http request header for download
     * @param name header name
     * @param value header value
     */
    fun addRequestHeader(name: String?, value: Any): SimpleDownloadTask {
        request.addRequestHeader(name, value.toString())
        return this
    }

    /**
     * set download progress query period, min value is 100ms
     * @param period query period
     */
    fun setProgressUpdatePeriod(period: Int): SimpleDownloadTask {
        updatePeriod = max(period.toDouble(), MIN_UPDATE_PERIOD.toDouble())
            .toInt()
        return this
    }

    fun start(listener: DownloadListener?) {
        if (downloadId != 0L) {
            return  // task has downloadId, means download in progress
        }
        downloadListener = listener
        val downloadPath = downloadTo.path
        if (!FileUtil.isAppDataPath(downloadPath) && !PermissionManager.hasSDCardPermission()) {
            downloadListener?.onError(PermissionException())
            return
        }
        if (downloadTo.getParentFile() == null) {
            downloadListener?.onError(IOException("invalid file path: $downloadPath"))
            return
        }
        if (downloadTo.getParentFile()?.exists() == false) {
            downloadTo.getParentFile()?.mkdirs()
        }
        request.setDestinationUri(Uri.fromFile(downloadTo))
        downloadId = downloadManager.enqueue(request)
        downloadListener?.onStart()
        startProgressQuery()
    }

    fun cancel() {
        if (downloadId == 0L) {
            return
        }
        stopProgressQuery()
        downloadManager.remove(downloadId)
        downloadId = 0
    }

    private fun startProgressQuery() {
        stopProgressQuery()
        timer = Timer()
        task = object : TimerTask() {
            override fun run() {
                val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId)) ?: return
                if (cursor.moveToFirst()) {
//                    String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    val status = cursor.getInt(
                        max(0.0, cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).toDouble())
                            .toInt()
                    )
                    val localPath = cursor.getString(
                        max(0.0, cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI).toDouble())
                            .toInt()
                    )
                    val downloaded = cursor.getInt(
                        max(
                            0.0,
                            cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                .toDouble()
                        ).toInt()
                    )
                    val total = cursor.getInt(
                        max(0.0,
                            cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                .toDouble()
                        )
                            .toInt()
                    )
                    callbackHandler.post { downloadListener?.onProgress(total, downloaded) }
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        callbackHandler.post { downloadListener?.onCompleted(localPath) }
                        stopProgressQuery()
                    }
                }
                cursor.close()
            }
        }
        timer?.schedule(task, 0, updatePeriod.toLong())
    }

    private fun stopProgressQuery() {
        task?.cancel()
        timer?.cancel()
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private val downloadManager: DownloadManager
        get() = globalContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
}
