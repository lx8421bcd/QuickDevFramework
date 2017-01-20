package com.linxiao.framework.support.file;

import android.Manifest;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.support.PermissionWrapper;
import com.linxiao.framework.support.log.Logger;

import java.io.File;
import java.io.IOException;

/**
 *
 * Created by linxiao on 2016/12/14.
 */
public class FileWrapper {
    private static final String TAG = FileWrapper.class.getSimpleName();

    private static FileWrapper instance;

    private FileWrapper() {

    }

    public static FileWrapper getInstance() {
        if (instance == null) {
            instance = new FileWrapper();
        }
        return instance;
    }

    public static boolean checkIsAvailablePathString(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String ext = getExternalStorageRoot();
        if (ext.length() > path.length()) {
            return false;
        }
        if (path.substring(0, ext.length()).equals(ext)) {
            return true;
        }
        String intern = getInternalStorageRoot();
        if (intern.length() > path.length()) {
            return false;
        }
        if (path.substring(0, intern.length()).equals(intern)) {
            return true;
        }
        // 如果有使用系统路径的可能则添加此判断
//        if (path.indexOf("/") == 0) {
//            return true;
//        }
        Logger.i(TAG, "unknown path string : " + path);
        return false;
    }

    /**
     * 是否挂载sd卡
     * @return true 挂载; false 未挂载
     */
    public static boolean existExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查是否有文件操作权限
     * */
    public static boolean hasFileOperatePermission() {
        boolean hasPermission = PermissionWrapper.getInstance().checkPermissionsGranted(BaseApplication.getAppContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasPermission) {
            Logger.e(TAG, "can't operate files, permission denied");
        }
        return hasPermission;
    }

    /**
     * 获取当前SD卡的根路径
     * */
    public static String getExternalStorageRoot() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取内部存储路径
     * */
    public static String getInternalStorageRoot() {
        return BaseApplication.getAppContext().getFilesDir().getPath();
    }

    /**
     * 检查文件是否存在
     * */
    public static boolean isExist(String dir) {
        return hasFileOperatePermission() && new File(dir).exists();
    }

    /**
     * 新建文件夹
     * */
    public boolean mkdir(String path) {
        if (!hasFileOperatePermission()) {
            return false;
        }
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 新建文件
     * */
    public static boolean createFile(String path, String fileName) {
        if (!hasFileOperatePermission()) {
            return false;
        }
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 重命名文件或文件夹
     * */
    public static void rename(String filePath, String oldName, String newName) {
        if (!hasFileOperatePermission()) {
            return;
        }
        File renameFile = new File(filePath + oldName);
        if (!renameFile.exists()) {
            return;
        }
        if (!renameFile.renameTo(new File(filePath + newName))) {
            Log.e(TAG, String.format("rename failed: %s ! directory: %s", oldName, filePath));
        }
    }

    /**
     * 移动文件
     * */
    public static void move(String src, String target, String... files) {
        if (!hasFileOperatePermission()) {
            return;
        }
        if (!FileWrapper.checkIsAvailablePathString(target)) {
            Logger.i(TAG, "illegal target path: " + target);
            return;
        }
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            return;
        }
        for (String fileName : files) {
            File file = new File(src, fileName);
            if (!file.exists()) {
                Logger.i(TAG, String.format("no such file or directory: %s in directory %s", fileName, src));
            }
            if (file.isFile()) {
                moveFile(file, target);
                continue;
            }
            if (file.isDirectory()) {
                moveDirectory(file, target);
            }
        }

    }

    private static void moveFile(File src, String targetDir) {
        File target = new File(targetDir);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                Logger.e(TAG, String.format("move file failed: %s ! can't create directory: %s", src.getName(), targetDir));
                return;
            }
        }
        File newPath = new File(targetDir + File.separator + src.getName());
        if (!src.renameTo(newPath)) {
            Logger.e(TAG, String.format("move file failed: %s ! target path: %s", src.getName(), newPath));
        }
    }

    private static void moveDirectory(File src, String targetDir) {
        File target = new File(targetDir);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                Logger.e(TAG, String.format("move file failed: %s ! can't create directory: %s", src.getName(), targetDir));
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

    public static void copy(String src, String target) {
        if (!hasFileOperatePermission()) {
            return;
        }
        if (!FileWrapper.checkIsAvailablePathString(target)) {
            Logger.i(TAG, "illegal target path: " + target);
            return;
        }
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            return;
        }
        if (!targetFile.isDirectory()) {
            Logger.i(TAG, "illegal target path, not a directory : " + target);
        }

    }




    /**
     * 删除文件
     * */
    public void delete(String path, String... files) {
        for (String fileName : files) {
            File file = new File(path, fileName);
            if (!file.exists()) {
                Log.i(TAG, String.format("file %s not exist in directory %s", fileName, path));
            }
            if (!file.delete()) {
                Log.e(TAG, String.format("delete failed: %s ! directory: %s", fileName, path));
            }
        }
    }
}
