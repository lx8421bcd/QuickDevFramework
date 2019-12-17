package com.linxiao.framework.file;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * file operate methods collection
 * <p>
 * Android storage and file operate methods
 * </p>
 *
 * @author linxiao
 * @since 2019-12-16
 */
public final class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * check phone has external storage
     *
     * <p>
     * usually return true on most phone recently manufactured, however, in some older types,
     * the return value will be determined by whether the SD Card is mounted
     * </p>
     */
    public static boolean hasExt() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * return external storage root directory as file type
     */
    public static File extRoot() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * check whether the input directory have .nomedia file
     * <p>
     * The .nomedia file tell the Android system that this directory is a special directory
     * for the application, do not scan and collect the media files into photo gallery
     * </p>
     * @param dirPath directory path
     */
    public static void addkNoMedia(String dirPath) {
        addNoMedia(new File(dirPath));
    }

    /**
     * check whether the input directory have .nomedia file
     * <p>
     * The .nomedia file tell the Android system that this directory is a special directory
     * for the application, do not scan and collect the media files into photo gallery
     * </p>
     * @param dir directory File
     */
    public static void addNoMedia(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }
        else if (!dir.isDirectory()) {
            return;
        }
        File file = new File(dir, ".nomedia");
        if (file.exists()) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * clear all files under the directory
     * @param path directory full path
     */
    public static void clearDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "clearExtDir: empty directory name");
            return;
        }
        File cacheDir = new File(path);
        if (!cacheDir.exists()) {
            Log.w(TAG, "clearExtDir: no such cache directory: " + path);
            return;
        }
        if (!cacheDir.isDirectory()) {
            Log.w(TAG, "clearExtDir: the target is not a directory: " + path);
            return;
        }
        cacheDir.delete();
        cacheDir.mkdirs();
    }

    /**
     * load file content as String Object
     * @param filePath file path
     * @param charset charset name, default is UTF-8
     */
    public static String readAsString(String filePath, String charset) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fin = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        try {
            fin = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fin, charset));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * write String object to local file
     *
     * <p>
     * file will be replaced if exists
     * </p>
     *
     * @param filePath file path
     * @param content  content string
     * @throws IOException IOException
     */
    public static void writeToFile(String filePath, String content) throws IOException {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(content)) {
            return;
        }
        File dest = new File(filePath);
        if (!dest.exists()) {
            if (!dest.createNewFile()) {
                Log.e(TAG, "writeTo: create dest file failed, path = " + filePath);
                return;
            }
        }
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        }
    }
}
