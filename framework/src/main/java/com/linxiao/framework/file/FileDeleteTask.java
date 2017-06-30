package com.linxiao.framework.file;

import android.content.Context;
import android.os.AsyncTask;

import com.linxiao.framework.toast.ToastWrapper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件删除
 * Created by lbc on 2017/3/16.
 */

public class FileDeleteTask extends AsyncTask<Void, Long, String> {
    private Context context;
    private List<File> srcFiles = new LinkedList<>();
    private FileCountListener fileCountListener;
    private long sum;
    private long curSum = 0;

    public FileDeleteTask(Context context) {
        this.context = context;
    }

    public FileDeleteTask(File src) {
        this.srcFiles.add(src);
    }

    public FileDeleteTask addSrc(File src) {
        this.srcFiles.add(src);
        return this;
    }

    public FileDeleteTask setSrcFiles(List<File> srcFiles) {
        this.srcFiles = srcFiles;
        return this;
    }

    public FileDeleteTask setFileCountListener(FileCountListener fileCountListener) {
        this.fileCountListener = fileCountListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!FileWrapper.existExternalStorage()) {
            ToastWrapper.showToast(context, "未找到SD卡");
            if (fileCountListener != null) {
                fileCountListener.onFail("未找到SD卡");
            }
        }
        if (!FileWrapper.hasFileOperatePermission()) {
            ToastWrapper.showToast(context, "请授予文件管理权限");
            if (fileCountListener != null) {
                fileCountListener.onFail("请授予文件管理权限");
            }
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
            publishProgress((long)0);
        }
        for (File src : srcFiles) {
            String strSrc = deleteFile(src);
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
    }

    /**
     * 删除文件
     * @param src
     * @return 失败返回文件名 成功返回null
     */
    private String deleteFile(File src) {
        if (src.isDirectory()) {
            return deleteDirectory(src);
        }
        if (src.exists()) {
            if (src.delete()){
                curSum++;
                if (fileCountListener != null) {
                    publishProgress(curSum);
                }
                return null;
            } else {
                return "删除失败";
            }
        } else {
            return "文件不存在";
        }
    }

    /**
     * 删除文件夹
     * @param src
     * @return
     */
    private String deleteDirectory(File src) {
        if (!src.exists()) {
            return "文件不存在";
        }
        if (src.isFile()) {
            if (src.delete()) {
                curSum++;
                if (fileCountListener != null) {
                    publishProgress(curSum);
                }
                return null;
            }
        }
        if (src.isDirectory()) {
            File[] srcFiles = src.listFiles();
            if (srcFiles == null || srcFiles.length == 0) {
                if (src.delete()) {
                    return null;
                }
            }
            for (File srcFile : srcFiles) {
                deleteDirectory(srcFile);
            }
            if (src.delete()) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        fileCountListener.onProgressUpdate(sum, values[0]);
    }
}
