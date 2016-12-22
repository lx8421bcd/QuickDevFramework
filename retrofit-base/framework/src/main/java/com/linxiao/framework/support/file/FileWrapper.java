package com.linxiao.framework.support.file;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;

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
        String ext = getExternalStorage();
        if (ext.length() > path.length()) {
            return false;
        }
        if (path.substring(0, ext.length()).equals(ext)) {
            return true;
        }
        String intern = getInternalStorage();
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
        Log.i(TAG, "unknown path string : " + path);
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
     * 获取当前SD卡的根路径
     * */
    public static String getExternalStorage() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取内部存储路径
     * */
    public static String getInternalStorage() {
        return BaseApplication.getAppContext().getFilesDir().getPath();
    }

    /**
     * 检查文件是否存在
     * */
    public static boolean isExist(String dir) {
        return new File(dir).exists();
    }

    /**
     * 新建文件夹
     * */
    public boolean mkdir(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 新建文件
     * */
    public static void mkFile(String path, String fileName) {
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重命名文件或文件夹
     * */
    public static void rename(String oldName, String newName) {

    }

    public static void copy(String from, String to) {

    }

    public static void move(String from, String to) {

    }

    /**
     * 删除文件或文件夹
     * */
    public static void delete() {

    }
}
