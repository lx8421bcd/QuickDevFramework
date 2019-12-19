package com.linxiao.framework.file;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * tools for file size operate
 *
 * @author lbc,linxiao
 * @since 2017-3-12
 */
public class FileSizeUtil {
    private static final String TAG = FileSizeUtil.class.getSimpleName();

    public static final int SIZE_UNIT_BYTE = 1;
    public static final int SIZE_UNIT_KB = 2;
    public static final int SIZE_UNIT_MB = 3;
    public static final int SIZE_UNIT_GB = 4;

    public static final long KB_BYTES = 1024;
    public static final long MB_BYTES = KB_BYTES * 1024;
    public static final long GB_BYTES = MB_BYTES * 1024;


    /**
     * get file size value by input size unit
     *
     * @param size     file size
     * @param sizeType one of the defined SIZE_UNIT type in this class
     * @return formatted value
     */
    public static double getSizeValueByUnit(long size, int sizeType) {
        switch (sizeType) {
        case SIZE_UNIT_BYTE:
            return size;
        case SIZE_UNIT_KB:
            return Math.round(size * 1.0 / KB_BYTES * 100) * 1.0 / 100;
        case SIZE_UNIT_MB:
            return Math.round(size * 1.0 / MB_BYTES * 100) * 1.0 / 100;
        case SIZE_UNIT_GB:
            return Math.round(size * 1.0 / GB_BYTES * 100) * 1.0 / 100;
        }
        return size;
    }

    /**
     * get formatted file size string
     *
     * <p>
     * convert following these rules: <br>
     * fileSize < 0:            wrong value, output "wrong$input_number" <br>
     * fileSize < 1KB:          output "fileSizeB"                       <br>
     * 1KB <= fileSize < 1MB:   output "fileSizeKB"                      <br>
     * 1MB <= fileSize < 1GB:   output "fileSizeMB"                      <br>
     * fileSize >= 1GB:         output "fileSizeGB"                      <br>
     * </p>
     *
     * @param fileSize file size value, byte unit
     * @return formatted file size string
     */
    public static String getFormattedSizeString(long fileSize) {
        if (fileSize < 0) {
            return "wrong" + fileSize;
        }
        if (fileSize < KB_BYTES) {
            return fileSize + "B";
        }
        if (fileSize < MB_BYTES) {
            return getSizeValueByUnit(fileSize, SIZE_UNIT_KB) + "KB";
        }
        if (fileSize < GB_BYTES) {
            return getSizeValueByUnit(fileSize, SIZE_UNIT_MB) + "MB";
        } else {
            return getSizeValueByUnit(fileSize, SIZE_UNIT_GB) + "GB";
        }
    }

    /**
     * get formatted file size string for input file or directory
     *
     * @param file file or directory
     * @return formatted file size string
     */
    public static String getFileOrDirectorySizeString(File file) {
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirectorySize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return getFormattedSizeString(blockSize);
    }

    /**
     * get file or directory size on disk
     *
     * @param file     file
     * @param sizeType one of the defined SIZE_UNIT type in this class
     * @return file size value
     */
    public static double calculateSize(File file, int sizeType) {
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirectorySize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return getSizeValueByUnit(blockSize, sizeType);
    }

    /**
     * get total file count in input directory
     *
     * <p>
     * if input file object is not directory, method will returns 1
     * </p>
     * @param file input directory
     * @return total file count in the directory
     */
    public static long calculateSubFileCount(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return 1;
        }
        long filesSum = 0;
        File[] fList = file.listFiles();
        if (fList == null) {
            return 0;
        }
        for (File value : fList) {
            if (value.isDirectory()) {
                filesSum = filesSum + calculateSubFileCount(value);
            } else {
                filesSum++;
            }
        }
        return filesSum;
    }

    private static long getFileSize(File file) throws IOException {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    private static long getDirectorySize(File f) throws Exception {
        long size = 0;
        File[] fList = f.listFiles();
        if (fList == null) {
            return 0;
        }
        for (File file : fList) {
            if (file.isDirectory()) {
                size = size + getDirectorySize(file);
            } else {
                size = size + getFileSize(file);
            }
        }
        return size;
    }
}
