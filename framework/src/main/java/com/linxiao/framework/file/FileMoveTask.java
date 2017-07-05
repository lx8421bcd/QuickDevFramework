package com.linxiao.framework.file;

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
 * 文件移动异步操作类
 * Created by linxiao on 2017/7/3.
 */
public class FileMoveTask extends AsyncTask<Void, Double, String> {
    
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
    
    public FileMoveTask() {
        
    }
    
    public FileMoveTask(File src, String targetPath) {
        this.srcFiles.add(src);
        this.targetPath = targetPath;
    }
    
    public FileMoveTask addSrc(File src) {
        this.srcFiles.add(src);
        return this;
    }
    
    public FileMoveTask setSrcFiles(List<File> srcFiles) {
        this.srcFiles = srcFiles;
        return this;
    }
    
    public FileMoveTask setTargetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }
    
    public FileMoveTask setFileSizeListener(FileSizeListener fileSizeListener) {
        this.fileSizeListener = fileSizeListener;
        return this;
    }
    
    public FileMoveTask setFileCountListener(FileCountListener fileCountListener) {
        this.fileCountListener = fileCountListener;
        return this;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!FileManager.existExternalStorage()) {
            if (fileSizeListener != null) {
                fileSizeListener.onFail("未找到SD卡");
            }
            if (fileCountListener != null) {
                fileCountListener.onFail("未找到SD卡");
            }
        }
        if (!FileManager.hasFileOperatePermission()) {
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
            String strSrc = move(src, new File(targetPath));
            if (strSrc != null) {
                result = strSrc;
            }
        }
        return result;
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
    
    private String move(File src, File target) {
        if (src.isDirectory()) {
            return moveDirectory(src, target);
        }
        else {
            return moveFile(src, target);
        }
    }
    
    /**
     * 复制文件
     * @param src
     * @return 失败返回文件名 成功返回null
     */
    private String moveFile(File src, File target) {
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
            src.delete();
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
    private String moveDirectory(File src, File target) {
        File[] srcFiles = src.listFiles();
        for (File srcFile : srcFiles) {
            if (srcFile.isFile()) {
                moveFile(srcFile, target);
            }
            else if (srcFile.isDirectory()) {
                File targetDir = new File(target, srcFile.getName());
                if (!targetDir.exists()) {
                    if (!targetDir.mkdirs()) {
                        return "false";
                    }
                }
                moveDirectory(srcFile, targetDir);
            }
        }
        src.delete();
        return null;
    }
}
