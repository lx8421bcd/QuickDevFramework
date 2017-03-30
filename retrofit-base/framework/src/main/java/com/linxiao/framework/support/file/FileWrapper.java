package com.linxiao.framework.support.file;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.linxiao.framework.BaseApplication;
import com.linxiao.framework.support.log.Logger;
import com.linxiao.framework.support.permission.PermissionWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件管理封装，提供常见文件管理功能
 * Created by linxiao on 2016/12/14.
 */
public class FileWrapper {
    private static final String TAG = FileWrapper.class.getSimpleName();

    private FileWrapper() {}

    /**
     * 复制文件操作
     * @param src 源文件
     * @param targetPath 目标路径
     * @param context
     * @return
     */
    public static FileCopyTask copyFileOperate(File src, String targetPath, Context context) {
        return new FileCopyTask().addSrc(src).setTargetPath(targetPath);
    }

    /**
     * 删除文件操作
     * @param src 源文件
     * @param context
     * @return
     */
    public static FileDeleteTask deleteFileOperate(File src, Context context) {
        return new FileDeleteTask(context).addSrc(src);
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
        boolean hasPermission = PermissionWrapper.checkPermissionsGranted(BaseApplication.getAppContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasPermission) {
            Logger.e(TAG, "can't operate files, permission denied");
        }
        return hasPermission;
    }

    /**
     * 检查是否为有效的路径字符串
     * */
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
     * 获取当前SD卡的根路径
     * */
    @NonNull
    public static String getExternalStorageRoot() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取内部存储路径
     * */
    @NonNull
    public static String getInternalStorageRoot() {
        return BaseApplication.getAppContext().getFilesDir().getPath();
    }

    /**
     * 通过路径字符串生成File对象，包含安全检查
     *
     * */
    public static File pathStringToFile(String path) {
        if (!hasFileOperatePermission()) {
            return null;
        }
        if (checkIsAvailablePathString(path)) {
            return new File(path);
        } else {
            return null;
        }
    }

    /**
     * 检查文件是否存在
     * */
    public static boolean isExist(String dir) {
        return hasFileOperatePermission() && new File(dir).exists();
    }

    /**
     * 重命名文件或文件夹
     * */
    public static boolean rename(String filePath, String oldName, String newName) {
        if (!hasFileOperatePermission()) {
            return false;
        }
        File renameFile = new File(filePath + File.separator + newName);
        if (renameFile.exists()) {
            Logger.e(TAG, "rename failed, new name");
            return false;
        }
        return renameFile.renameTo(new File(filePath + newName));
    }

    /**
     * 新建文件夹
     * */
    public static boolean mkdir(String path) {
        if (!hasFileOperatePermission()) {
            return false;
        }
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 复制文件
     * */
    private static boolean copyFile(File src, File target) {
        InputStream input;
        OutputStream output;
        boolean result;
        try {
            input = new FileInputStream(src);
            output = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            input.close();
            output.close();
            result = true;
        } catch (IOException e) {
            Logger.e(TAG, e);
            result = false;
        }
        return result;
    }

    private static boolean copyDirectory(File src, File target) {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                return false;
            }
        }
        File[] srcFiles = src.listFiles();
        for (File sourceFile : srcFiles) {
            if (sourceFile.isFile()) {
                copyFile(sourceFile, target);
            }
            else if (sourceFile.isDirectory()) {
                copyDirectory(sourceFile, new File(target, sourceFile.getName()));
            }

        }
        return true;
    }

    public static boolean copy(File src, File target) {
        if (!src.exists()) {
           return false;
        }
        if (src.isDirectory()) {
            return copyDirectory(src, target);
        }
        else {
            return copyFile(src, target);
        }
    }

    private static boolean moveFile(File src, File target) {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                return false;
            }
        }
        File newPath = new File(target, src.getName());
        return src.renameTo(newPath);
    }

    private static boolean moveDirectory(File src, File target) {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                return false;
            }
        }
        File[] srcFiles = src.listFiles();
        for (File sourceFile : srcFiles) {
            if (sourceFile.isFile()) {
                moveFile(sourceFile, target);
            }
            else if (sourceFile.isDirectory()) {
                moveDirectory(sourceFile, new File(target, sourceFile.getName()));
            }
        }
        return true;
    }

    private static boolean move(File src, File target) {
        if (!src.exists()) {
            return false;
        }
        if (src.isDirectory()) {
            return moveDirectory(src, target);
        }
        else {
            return moveFile(src, target);
        }
    }

}
