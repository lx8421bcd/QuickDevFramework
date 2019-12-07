package com.linxiao.framework.net;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.permission.PermissionException;
import com.linxiao.framework.permission.PermissionManager;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * simple download tool
 * <p>
 * a simple download tool based on {@link android.app.DownloadManager},
 * using to handle simple download demands like download update apk.
 * </p>
 *
 * @author linxiao
 * @since 2019-12-07
 */
public class SimpleDownloadTask {

    public interface DownloadListener {

        void onStart();

        void onProgress(int total, int downloaded);

        void onCompleted(String localPath);

        void onError(Throwable e);

    }

    private static DownloadManager getDownloadManager() {
        return (DownloadManager) ContextProvider.get()
                .getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private static final int MIN_UPDATE_PERIOD = 100;

    private DownloadManager.Request request;
    private File downloadTo;
    private long downloadId = 0;
    private Timer timer;
    private TimerTask task;
    private Handler callbackHandler = new Handler();
    private int updatePeriod = MIN_UPDATE_PERIOD;

    private DownloadListener downloadListener;

    public static SimpleDownloadTask newInstance(String url) {
        SimpleDownloadTask instance = new SimpleDownloadTask();
        instance.request = new DownloadManager.Request(Uri.parse(url));
        instance.request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_MOBILE |
                        DownloadManager.Request.NETWORK_WIFI);
        instance.request.setAllowedOverMetered(true);
        instance.request.setAllowedOverRoaming(true);
        return instance;
    }

    public SimpleDownloadTask setDownloadTo(String fullPath) {
        downloadTo = new File(fullPath);
        return this;
    }

    public SimpleDownloadTask setDownloadTo(String path, String fileName) {
        downloadTo = new File(path, fileName);
        return this;
    }

    public SimpleDownloadTask setNotification(String title, String desc) {
        request.setTitle(title);
        request.setDescription(desc);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        return this;
    }

    public SimpleDownloadTask hideNotification() {
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        return this;
    }

    public SimpleDownloadTask addRequestHeader(String name, Object value) {
        request.addRequestHeader(name, String.valueOf(value));
        return this;
    }

    public SimpleDownloadTask setProgressUpdatePeriod(int period) {
        updatePeriod = period < MIN_UPDATE_PERIOD ? MIN_UPDATE_PERIOD : period;
        return this;
    }

    public void start(DownloadListener listener) {
        if (downloadId != 0) {
            return; // task has downloadId, means download in progress
        }
        downloadListener = listener;
        if (downloadTo == null) {
            if (downloadListener != null) {
                downloadListener.onError(new IOException("download destination is empty"));
            }
            return;
        }
        String downloadPath = downloadTo.getPath();
        if (!isAppDataPath(downloadPath) && !PermissionManager.hasSDCardPermission()) {
            if (downloadListener != null) {
                downloadListener.onError(new PermissionException());
            }
            return;
        }
        if (downloadTo.getParentFile() == null) {
            if (downloadListener != null) {
                downloadListener.onError(new IOException("invalid file path: " + downloadPath));
            }
            return;
        }
        if (!downloadTo.getParentFile().exists()) {
            downloadTo.getParentFile().mkdirs();
        }

        request.setDestinationUri(Uri.fromFile(downloadTo));
        downloadId = getDownloadManager().enqueue(request);
        if (downloadListener != null) {
            downloadListener.onStart();
        }
        startProgressQuery();
    }

    public void cancel() {
        if (downloadId == 0) {
            return;
        }
        getDownloadManager().remove(downloadId);
        downloadId = 0;
    }

    private boolean isAppDataPath(String path) {
        File extCacheRoot = ContextProvider.get().getExternalCacheDir();
        if (extCacheRoot != null && path.contains(extCacheRoot.getPath())) {
            return true;
        }
        File extFileRoot = ContextProvider.get().getExternalFilesDir(null);
        if (extFileRoot != null && path.contains(extFileRoot.getPath())) {
            return true;
        }
        if (path.contains(ContextProvider.get().getCacheDir().getPath())) {
            return true;
        }
        if (path.contains(ContextProvider.get().getFilesDir().getPath())) {
            return true;
        }
        return false;
    }

    private void startProgressQuery() {
        stopProgressQuery();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Cursor cursor = getDownloadManager().query(new DownloadManager.Query().setFilterById(downloadId));
                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
//                    String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    int downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (downloadListener != null) {
                        callbackHandler.post(() -> downloadListener.onProgress(total, downloaded));
                    }
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        if (downloadListener != null) {
                            callbackHandler.post(() -> downloadListener.onCompleted(localPath));
                        }
                        stopProgressQuery();
                    }
                }
                cursor.close();
            }
        };
        timer.schedule(task, 0,updatePeriod);
    }

    private void stopProgressQuery() {
        if (task != null) {
            task.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
