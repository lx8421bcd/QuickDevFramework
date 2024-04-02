package com.linxiao.framework.file

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.linxiao.framework.common.globalContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * file operate methods collection
 *
 *
 * Android storage and file operate methods
 *
 *
 * @author linxiao
 * @since 2019-12-16
 */
object FileUtil {
    private val TAG = FileUtil::class.java.getSimpleName()

    /**
     * check phone has external storage
     *
     *
     *
     * usually return true on most phone recently manufactured, however, in some older types,
     * the return value will be determined by whether the SD Card is mounted
     *
     */
    @JvmStatic
    fun hasExt(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     * return external storage root directory as file type
     */
    @JvmStatic
    fun extRoot(): File {
        return Environment.getExternalStorageDirectory()
    }

    /**
     * check whether the input directory have .nomedia file
     *
     *
     * The .nomedia file tell the Android system that this directory is a special directory
     * for the application, do not scan and collect the media files into photo gallery
     *
     * @param dirPath directory path
     */
    fun addNoMedia(dirPath: String) {
        addNoMedia(File(dirPath))
    }

    /**
     * check whether the input directory have .nomedia file
     *
     *
     * The .nomedia file tell the Android system that this directory is a special directory
     * for the application, do not scan and collect the media files into photo gallery
     *
     * @param dir directory File
     */
    fun addNoMedia(dir: File) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return
            }
        } else if (!dir.isDirectory()) {
            return
        }
        val file = File(dir, ".nomedia")
        if (file.exists()) {
            return
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * clear all files under the directory
     * @param path directory full path
     */
    fun clearDirectory(path: String) {
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "clearExtDir: empty directory name")
            return
        }
        val cacheDir = File(path)
        if (!cacheDir.exists()) {
            Log.w(TAG, "clearExtDir: no such cache directory: $path")
            return
        }
        if (!cacheDir.isDirectory()) {
            Log.w(TAG, "clearExtDir: the target is not a directory: $path")
            return
        }
        cacheDir.delete()
        cacheDir.mkdirs()
    }

    /**
     * load file content as String Object
     * @param filePath file path
     * @param charset charset name, default is UTF-8
     */
    fun readAsString(filePath: String, charset: Charset = Charsets.UTF_8): String? {
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        var fin: FileInputStream? = null
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            fin = FileInputStream(file)
            reader = BufferedReader(InputStreamReader(fin, charset))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
                fin?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * write String object to local file
     *
     * file will be replaced if exists
     *
     * @param filePath file path
     * @param content  content string
     * @throws IOException IOException
     */
    @Throws(IOException::class)
    fun writeToFile(filePath: String, content: String?) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(content)) {
            return
        }
        val dest = File(filePath)
        if (!dest.exists()) {
            if (!dest.createNewFile()) {
                Log.e(TAG, "writeTo: create dest file failed, path = $filePath")
                return
            }
        }
        FileWriter(filePath).use { fileWriter ->
            fileWriter.write(content)
            fileWriter.flush()
        }
    }

    /**
     * check the path is belong to application's data or cache path
     *
     *
     *
     * modify the application's cache and data directory do not need
     * any runtime permission
     *
     * @param path path string
     * @return true path belongs to app cache or data path
     */
    @JvmStatic
    fun isAppDataPath(path: String): Boolean {
        val extCacheRoot = globalContext.externalCacheDir
        if (extCacheRoot != null && path.contains(extCacheRoot.path)) {
            return true
        }
        val extFileRoot = globalContext.getExternalFilesDir(null)
        if (extFileRoot != null && path.contains(extFileRoot.path)) {
            return true
        }
        if (path.contains(globalContext.cacheDir.path)) {
            return true
        }
        return path.contains(globalContext.filesDir.path)
    }
}
