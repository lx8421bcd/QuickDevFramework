package com.linxiao.framework.file;

import android.os.AsyncTask;

import com.linxiao.framework.permission.PermissionException;
import com.linxiao.framework.permission.PermissionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * file delete async task
 * <p>
 * used to delete multiple files or large file, which needs to run in background
 * and needs progress callback
 * </p>
 *
 * @author lbc,linxiao
 * @since 2017-03-16
 */
public class FileDeleteTask extends AsyncTask<Void, Long, String> {

    private List<File> srcFiles = new LinkedList<>();
    private FileModifyListener fileModifyListener;
    private long totalSize;
    private long totalCount;
    private long finishedCount = 0;
    private long finishedSize = 0;
    private Throwable error;

    private FileDeleteTask() {}

    public static FileDeleteTask newInstance(File src) {
        FileDeleteTask task = new FileDeleteTask();
        task.addSrc(src);
        return task;
    }

    public static FileDeleteTask newInstance(List<File> srcFiles) {
        FileDeleteTask task = new FileDeleteTask();
        task.setSrcFiles(srcFiles);
        return task;
    }

    public FileDeleteTask addSrc(File src) {
        this.srcFiles.add(src);
        return this;
    }

    public FileDeleteTask setSrcFiles(List<File> srcFiles) {
        this.srcFiles = srcFiles;
        return this;
    }

    public FileDeleteTask setFileModifyListener(FileModifyListener fileModifyListener) {
        this.fileModifyListener = fileModifyListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fileModifyListener != null) {
            fileModifyListener.onStart();
        }
        error = null;
        totalSize  = 0;
        totalCount = 0;
        finishedCount = 0;
        finishedSize  = 0;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (!FileUtil.hasExt()) {
            error = new FileNotFoundException("SDCard not mounted");
            return "error";
        }
        boolean needPermission = false;
        for (File src : srcFiles) {
            if (!FileUtil.isAppDataPath(src.getPath())) {
                needPermission = true;
            }
            totalCount += FileSizeUtil.calculateSubFileCount(src);
            totalSize  += FileSizeUtil.calculateSize(src, FileSizeUtil.SIZE_UNIT_BYTE);
        }
        if (needPermission && !PermissionManager.hasSDCardPermission()) {
            error = new PermissionException();
            return "error";
        }

        publishProgress();
        for (File src : srcFiles) {
            deleteFile(src);
        }
        return "success";
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (fileModifyListener != null) {
            fileModifyListener.onProgressUpdate(totalCount, finishedCount, totalSize, finishedSize);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (fileModifyListener == null) {
            if (error != null) {
                error.printStackTrace();
            }
            return;
        }
        if (error != null) {
            fileModifyListener.onError(error);
        }
        else {
            fileModifyListener.onSuccess();
        }
    }

    private void deleteFile(File src) {
        if (src.isDirectory()) {
            deleteDirectory(src);
            return;
        }
        if (src.exists()) {
            src.delete();
            finishedCount++;
            publishProgress();
        }
    }

    private void deleteDirectory(File src) {
        if (!src.exists()) {
            return;
        }
        if (src.isFile()) {
            if (src.delete()) {
                finishedCount++;
                if (fileModifyListener != null) {
                    publishProgress();
                }
                return;
            }
        }
        if (src.isDirectory()) {
            File[] srcFiles = src.listFiles();
            if (srcFiles == null || srcFiles.length == 0) {
                src.delete();
                return ;
            }
            for (File srcFile : srcFiles) {
                deleteDirectory(srcFile);
            }
            src.delete();
        }
    }
}
