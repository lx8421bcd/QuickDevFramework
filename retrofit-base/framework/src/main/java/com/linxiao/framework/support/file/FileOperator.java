package com.linxiao.framework.support.file;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.support.PermissionWrapper;

import java.io.File;
import java.io.IOException;

/**
 * 文件管理封装
 * 封装成一个针对指定路径的操作对象
 * Created by LinXiao on 2016-12-13.
 */
public class FileOperator {

    private static final String TAG = FileOperator.class.getSimpleName();

    String mPath;
    boolean hasPermission;


    public FileOperator() {
        Context mContext = BaseApplication.getAppContext();
        //检查在Android 6.0以上是否拥有SD卡读写权限，避免异常
        hasPermission = PermissionWrapper.getInstance().checkPermissionsGranted(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasPermission) {
            Log.e(TAG, "permission denied ! all file operation will unavailable");
        }
    }

    /**
     * 将文件操作对象指定到某一路径，如果不存在则打开
     */
    public void openOrCreateDir(String dir) {
        File mFile = new File(dir);
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        mPath = dir;
    }

    /**
     * 打开某一路径，不存在则操作失败
     */
    public void openDir(String dir) {
        mPath = dir;
    }

    /**
     * 检查文件是否存在
     */
    public boolean isExist(String name) {
        return new File(name).exists();
    }

    /**
     * 新建文件夹
     */
    public static boolean mkdir(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 新建文件
     */
    public boolean mkFile(String fileName) {
        try {
            return new File(mPath + fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(String fileName) {
        File returnFile = new File(mPath, fileName);
        if (returnFile.exists()) {
            return returnFile;
        }
        return null;
    }

    /**
     * 重命名文件或文件夹
     */
    public boolean rename(String oldName, String newName) {
        File renameFile = new File(mPath + oldName);
        if (!renameFile.exists()) {
            return false;
        }
        return renameFile.renameTo(new File(mPath + newName));
    }

    /**
     * 删除文件或文件夹
     */
    public void delete(String name) {
        File file = new File(mPath, name);
        if (file.exists()) {
            file.delete();
        }
    }
}
