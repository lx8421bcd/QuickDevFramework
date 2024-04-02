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

enum class FileSizeUnit(
    value: Int
) {
    B(1),
    KB(2),
    MB(3),
    GB(4)
}

/**
 *
 * @author linxiao
 * @since 2019-12-16
 */
object FileUtil {
    private val TAG = FileUtil::class.java.getSimpleName()

    const val KB_BYTES: Long = 1024
    const val MB_BYTES = KB_BYTES * 1024
    const val GB_BYTES = MB_BYTES * 1024
    
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

    /**
     * get file size value by input size unit
     *
     * @param size     file size
     * @param sizeType one of the defined SIZE_UNIT type in this class
     * @return formatted value
     */
    fun getSizeValueByUnit(size: Long, unit: FileSizeUnit): Double {
        return when (unit) {
            FileSizeUnit.B -> size.toDouble()
            FileSizeUnit.KB -> Math.round(size * 1.0 / KB_BYTES * 100) * 1.0 / 100
            FileSizeUnit.MB -> Math.round(size * 1.0 / MB_BYTES * 100) * 1.0 / 100
            FileSizeUnit.GB -> Math.round(size * 1.0 / GB_BYTES * 100) * 1.0 / 100
        }
    }

    /**
     * get formatted file size string
     *
     *
     *
     * convert following these rules: <br></br>
     * fileSize < 0:            wrong value, output "wrong$input_number" <br></br>
     * fileSize < 1KB:          output "fileSizeB"                       <br></br>
     * 1KB <= fileSize < 1MB:   output "fileSizeKB"                      <br></br>
     * 1MB <= fileSize < 1GB:   output "fileSizeMB"                      <br></br>
     * fileSize >= 1GB:         output "fileSizeGB"                      <br></br>
     *
     *
     * @param fileSize file size value, byte unit
     * @return formatted file size string
     */
    @JvmStatic
    fun getFormattedSizeString(fileSize: Long): String {
        if (fileSize < 0) {
            return "wrong$fileSize"
        }
        if (fileSize < KB_BYTES) {
            return fileSize.toString() + "B"
        }
        if (fileSize < MB_BYTES) {
            return getSizeValueByUnit(fileSize, FileSizeUnit.KB).toString() + "KB"
        }
        return if (fileSize < GB_BYTES) {
            getSizeValueByUnit(fileSize, FileSizeUnit.MB).toString() + "MB"
        } else {
            getSizeValueByUnit(fileSize, FileSizeUnit.GB).toString() + "GB"
        }
    }

    /**
     * get formatted file size string for input file or directory
     *
     * @param file file or directory
     * @return formatted file size string
     */
    fun getFileOrDirectorySizeString(file: File): String {
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory()) {
                getDirectorySize(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getFormattedSizeString(blockSize)
    }

    /**
     * get file or directory size on disk
     *
     * @param file     file
     * @param sizeType one of the defined SIZE_UNIT type in this class
     * @return file size value
     */
    @JvmStatic
    fun calculateSize(file: File, unit: FileSizeUnit): Double {
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory()) {
                getDirectorySize(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getSizeValueByUnit(blockSize, unit)
    }

    /**
     * get total file count in input directory
     *
     *
     *
     * if input file object is not directory, method will returns 1
     *
     * @param file input directory
     * @return total file count in the directory
     */
    @JvmStatic
    fun calculateSubFileCount(file: File): Long {
        if (!file.exists()) {
            return 0
        }
        if (!file.isDirectory()) {
            return 1
        }
        var filesSum: Long = 0
        val fList = file.listFiles() ?: return 0
        for (value in fList) {
            if (value.isDirectory()) {
                filesSum += calculateSubFileCount(value)
            } else {
                filesSum++
            }
        }
        return filesSum
    }

    @Throws(IOException::class)
    private fun getFileSize(file: File?): Long {
        if (file == null) {
            return 0
        }
        var size: Long = 0
        if (file.exists()) {
            val fis = FileInputStream(file)
            size = fis.available().toLong()
            fis.close()
        }
        return size
    }

    @Throws(Exception::class)
    private fun getDirectorySize(f: File): Long {
        var size: Long = 0
        val fList = f.listFiles() ?: return 0
        for (file in fList) {
            size = if (file.isDirectory()) {
                size + getDirectorySize(file)
            } else {
                size + getFileSize(file)
            }
        }
        return size
    }
}
