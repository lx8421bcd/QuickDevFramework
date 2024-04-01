package com.linxiao.framework.file;

import android.os.AsyncTask;

import com.linxiao.framework.permission.PermissionException;
import com.linxiao.framework.permission.PermissionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * file copy async task
 * <p>
 * used to copy multiple files or large file, which needs to run in background
 * and needs progress callback
 * </p>
 *
 * @author lbc,linxiao
 * @since 2017-03-14
 */
public class FileCopyTask extends AsyncTask<Void, Double, String> {
    private List<File> srcFiles = new LinkedList<>();
    private String targetPath;
    private FileModifyListener fileModifyListener;

    private long totalSize;
    private long totalCount;
    private long finishedCount = 0;
    private long finishedSize = 0;
    private Throwable error;

    private FileCopyTask() {}

    public static FileCopyTask newInstance(File src, String targetPath) {
        FileCopyTask task = new FileCopyTask();
        task.addSrc(src);
        task.setTargetPath(targetPath);
        return task;
    }

    public static FileCopyTask newInstance(List<File> srcFiles, String targetPath) {
        FileCopyTask task = new FileCopyTask();
        task.setSrcFiles(srcFiles);
        task.setTargetPath(targetPath);
        return task;
    }


    public FileCopyTask addSrc(File src) {
        this.srcFiles.add(src);
        return this;
    }

    public FileCopyTask setSrcFiles(List<File> srcFiles) {
        this.srcFiles = srcFiles;
        return this;
    }

    public FileCopyTask setTargetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    public FileCopyTask setFileModifyListener(FileModifyListener fileModifyListener) {
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
        boolean needPermission = !FileUtil.isAppDataPath(targetPath);
        for (File src : srcFiles) {
            if (!FileUtil.isAppDataPath(src.getPath())) {
                needPermission = true;
            }
            totalCount += FileSizeUtil.calculateSubFileCount(src);
            totalSize  += FileSizeUtil.calculateSize(src, FileSizeUtil.SIZE_UNIT_BYTE);
        }
        if (needPermission && !PermissionUtil.hasSDCardPermission()) {
            error = new PermissionException();
            return "error";
        }

        publishProgress();
        File target = new File(targetPath);
        for (File src : srcFiles) {
            try {
                if (src.isDirectory()) {
                    copyDirectory(src, target);
                }
                else {
                    copyFile(src, target);
                }
            } catch (Exception e) {
                error = e;
                return "error";
            }
        }
        return "success";
    }

    @Override
    protected void onProgressUpdate(Double... values) {
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

    private void copyDirectory(File src, File target) throws Exception {
        File[] srcFiles = src.listFiles();
        if (srcFiles == null) {
            return;
        }
        for (File srcFile : srcFiles) {
            if (srcFile.isFile()) {
                copyFile(srcFile, target);
            }
            else if (srcFile.isDirectory()) {
                File targetDir = new File(target, srcFile.getName());
                if (!targetDir.exists()) {
                    if (!targetDir.mkdirs()) {
                        return;
                    }
                }
                copyDirectory(srcFile, targetDir);
            }
        }
    }


    private void copyFile(File src, File target) throws Exception {
        InputStream input;
        OutputStream output;
        input = new FileInputStream(src);
        output = new FileOutputStream(new File(target, src.getName()));
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) > 0) {
            output.write(buf, 0, bytesRead);
            finishedSize += bytesRead;
        }
        input.close();
        output.close();
        finishedCount++;
        onProgressUpdate();
    }
}
