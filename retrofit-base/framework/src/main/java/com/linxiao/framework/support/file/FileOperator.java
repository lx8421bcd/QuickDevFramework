package com.linxiao.framework.support.file;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
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
     * 将文件操作对象指定到某一路径，如果不存在则创建该目录
     */
    public void openOrCreateDirectory(String dir) {
        File mFile = new File(dir);
        if (mFile.exists()) {
            mPath = dir;
        }
        if (mFile.mkdirs()) {
            mPath = dir;
        }
        mPath = null;
        Log.d(TAG, "no such directory, create directory failed");
    }

    /**
     * 将文件操作对象指定到某一路径
     */
    public void openDirectory(String dir) {
        File mFile = new File(dir);
        if (mFile.exists()) {
            mPath = dir;
        }
        else {
            mPath = null;
            Log.e(TAG, "no such directory :" + dir);
        }
    }

    /**
     * 删除文件
     * */
    public void delete(String... files) {
        for (String fileName : files) {
            File file = new File(mPath, fileName);
            if (!file.exists()) {
                Log.i(TAG, String.format("file %s not exist in directory %s", fileName, mPath));
            }
            if (!file.delete()) {
                Log.e(TAG, String.format("delete failed: %s ! directory: %s", fileName, mPath));
            }
        }
    }

    /**
     * 重命名文件或文件夹
     */
    public void rename(String oldName, String newName) {
        File renameFile = new File(mPath + oldName);
        if (!renameFile.exists()) {
            return;
        }
        if (!renameFile.renameTo(new File(mPath + newName))) {
            Log.e(TAG, String.format("rename failed: %s ! directory: %s", oldName, mPath));
        }
    }

    /**
     * 移动文件
     * */
    public void move(String targetPath, String... files) {
        if (!FileWrapper.checkIsAvailablePathString(targetPath)) {
            Log.i(TAG, "illegal target path: " + targetPath);
            return;
        }
        File target = new File(targetPath);
        if (!target.exists()) {

        }
        if (TextUtils.isEmpty(targetPath))
        for (String fileName : files) {
            File file = new File(mPath, fileName);
            if (!file.exists()) {
                Log.i(TAG, String.format("no such file or directory: %s in directory %s", fileName, mPath));
            }
            if (file.isFile()) {
                moveFile(file, targetPath);
                continue;
            }
            if (file.isDirectory()) {
                moveDirectory(file, targetPath);
                continue;
            }
        }
    }

    private void moveFile(File src, String targetDir) {
        File target = new File(targetDir);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                Log.e(TAG, String.format("move file failed: %s ! can't create directory: %s", src.getName(), targetDir));
                return;
            }
        }
        File newPath = new File(targetDir + File.separator + src.getName());
        if (!src.renameTo(newPath)) {
            Log.e(TAG, String.format("move file failed: %s ! target path: %s", src.getName(), newPath));
        }
    }

    private void moveDirectory(File src, String targetDir) {
        File target = new File(targetDir);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                Log.e(TAG, String.format("move file failed: %s ! can't create directory: %s", src.getName(), targetDir));
                return;
            }
        }
        File[] srcFiles = src.listFiles();
        for (File sourceFile : srcFiles) {
            if (sourceFile.isFile()) {
                moveFile(sourceFile, target.getAbsolutePath());
            }

            else if (sourceFile.isDirectory())
                moveDirectory(sourceFile, target + File.separator + sourceFile.getName());
        }
        src.delete();
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
     * 复制文件
     * */
    public boolean copy(String fileName, String copyFileName) {
        return false;
    }

}
