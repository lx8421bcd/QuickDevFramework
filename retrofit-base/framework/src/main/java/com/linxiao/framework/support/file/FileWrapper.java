package com.linxiao.framework.support.file;

import java.io.File;

/**
 *
 * Created by linxiao on 2016/12/14.
 */
public class FileWrapper {
    private static final String TAG = FileWrapper.class.getSimpleName();

    /**
     * 将文件操作对象指定到某一路径，如果不存在则打开
     * */
    public static void openOrCreateDir(String dir) {

    }

    /**
     * 打开某一路径，不存在则操作失败
     * */
    public static void openDir(String dir) {

    }

    /**
     * 检查文件是否存在
     * */
    public static boolean isExist() {
        return false;
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
    public static void mkFile() {

    }

    /**
     * 重命名文件或文件夹
     * */
    public static void rename() {

    }

    /**
     * 删除文件或文件夹
     * */
    public static void delete() {

    }
}
