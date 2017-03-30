package com.linxiao.framework.support.file;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件复制
 * Created by lbc on 2017/3/14.
 */
public class FileCopyTask extends AsyncTask<Void, Double, String> {
    private static final double SIZE_FLAG = 1.00;
    private static final double SUM_FLAG = 2.00;
    private List<File> srcFiles = new LinkedList<>();
    private String targetPath;
    private FileSizeListener fileSizeListener;
    private FileCountListener fileCountListener;

    private double size;
    private long sum;
    private long curSum = 0;
    private long curSize = 0;

    public FileCopyTask() {

    }


    public FileCopyTask(File src, String targetPath) {
        this.srcFiles.add(src);
        this.targetPath = targetPath;
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

    public FileCopyTask setFileSizeListener(FileSizeListener fileSizeListener) {
        this.fileSizeListener = fileSizeListener;
        return this;
    }

    public FileCopyTask setFileCountListener(FileCountListener fileCountListener) {
        this.fileCountListener = fileCountListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!FileWrapper.existExternalStorage()) {
            if (fileSizeListener != null) {
                fileSizeListener.onFail("未找到SD卡");
            }
            if (fileCountListener != null) {
                fileCountListener.onFail("未找到SD卡");
            }
        }
        if (!FileWrapper.hasFileOperatePermission()) {
            if (fileSizeListener != null) {
                fileSizeListener.onFail("请授予文件管理权限");
            }
            if (fileCountListener != null) {
                fileCountListener.onFail("请授予文件管理权限");
            }
        }
        if (fileSizeListener != null) {
            for (File src : srcFiles) {
                size += FileSizeUtil.getFileOrFilesSize(src, 2);  //KB
            }
            fileSizeListener.onStart();
        }
        if (fileCountListener != null) {
            for (File src : srcFiles) {
                sum += FileSizeUtil.getFilesSum(src);
            }
            fileCountListener.onStart();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        if (fileCountListener != null){
            publishProgress(SUM_FLAG, (double)0);
        }
        for (File src : srcFiles) {
            String strSrc = copy(src, new File(targetPath));
            if (strSrc != null) {
                result = strSrc;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (fileCountListener != null) {
            if (result.equals("")) {
                fileCountListener.onSuccess();
            } else {
                fileCountListener.onFail(result);
            }
        }
        if (fileSizeListener != null) {
            if (result.equals("")) {
                fileSizeListener.onSuccess();
            } else {
                fileSizeListener.onFail(result);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        if (values[0] == SIZE_FLAG) {
            fileSizeListener.onProgressUpdate(size, values[1]);
        }
        if (values[0] == SUM_FLAG) {
            fileCountListener.onProgressUpdate(Math.round(sum), Math.round(values[1]));
        }
    }

    /**
     * 复制文件
     * @param src
     * @return 失败返回文件名 成功返回null
     */
    private String copyFile(File src, File target) {
        InputStream input;
        OutputStream output;
        try {
            input = new FileInputStream(src);
            output = new FileOutputStream(new File(target, src.getName()));
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
                curSize += bytesRead;
                if (fileSizeListener != null) {
                    publishProgress(SIZE_FLAG, FileSizeUtil.formatFileSize(curSize, 2));
                }
            }
            input.close();
            output.close();
        } catch (IOException e) {
            return e.getMessage();
        }
        curSum++;
        if (fileCountListener != null) {
            publishProgress(SUM_FLAG, (double) curSum);
        }
        return null;
    }

    /**
     * 复制文件夹
     * @param src
     * @param target
     * @return
     */
    private String copyDirectory(File src, File target) {
        File[] srcFiles = src.listFiles();
        for (File srcFile : srcFiles) {
            if (srcFile.isFile()) {
                copyFile(srcFile, target);
            }
            else if (srcFile.isDirectory()) {
                File targetDir = new File(target, srcFile.getName());
                if (!targetDir.exists()) {
                    if (!targetDir.mkdirs()) {
                        return "false";
                    }
                }
                copyDirectory(srcFile, targetDir);
            }
        }
        return null;
    }

    private String copy(File src, File target) {
        if (src.isDirectory()) {
            return copyDirectory(src, target);
        }
        else {
            return copyFile(src, target);
        }
    }
}
