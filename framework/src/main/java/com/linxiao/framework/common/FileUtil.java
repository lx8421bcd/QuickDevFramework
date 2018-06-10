package com.linxiao.framework.common;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h3>File工具类</h3> <br>
 * <h5>主要封装了一些对文件读写的操作</h5>
 *
 * @author relish-wang 2015-7-28
 */
public final class FileUtil {

    /**
     * 分隔符.
     */
    public final static String FILE_EXTENSION_SEPARATOR = ".";


    /**
     * SD卡根目录
     */
    public static final String SD_PATH = Environment
            .getExternalStorageDirectory() + File.separator;

    /**
     * 判断SD卡是否可用
     *
     * @return SD卡可用返回true
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(status);
    }

    /**
     * 读取文件的内容 <br>
     * 默认utf-8编码
     *
     * @param filePath 文件路径
     * @return 字符串
     * @throws IOException
     */
    public static String readFile(String filePath) throws IOException {
        return readFile(filePath, "utf-8");
    }

    /**
     * 读取文件的内容
     *
     * @param filePath    文件目录
     * @param charsetName 字符编码
     * @return String字符串
     */
    public static String readFile(String filePath, String charsetName)
            throws IOException {
        if (TextUtils.isEmpty(filePath))
            return null;
        if (TextUtils.isEmpty(charsetName))
            charsetName = "utf-8";
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile())
            return null;
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(
                    file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文本文件到List字符串集合中(默认utf-8编码)
     *
     * @param filePath 文件目录
     * @return 文件不存在返回null，否则返回字符串集合
     * @throws IOException 输入输出异常
     */
    public static List<String> readFileToList(String filePath)
            throws IOException {
        return readFileToList(filePath, "utf-8");
    }

    /**
     * 读取文本文件到List字符串集合中
     *
     * @param filePath    文件目录
     * @param charsetName 字符编码
     * @return 文件不存在返回null，否则返回字符串集合
     */
    public static List<String> readFileToList(String filePath,
                                              String charsetName) throws IOException {
        if (TextUtils.isEmpty(filePath))
            return null;
        if (TextUtils.isEmpty(charsetName))
            charsetName = "utf-8";
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (!file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(
                    file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 文件目录
     * @param content  要写入的内容
     * @param append   如果为 true，则将数据写入文件末尾处，而不是写入文件开始处
     * @return 写入成功返回true， 写入失败返回false
     * @throws IOException 输入输出异常
     */
    public static boolean writeFile(String filePath, String content,
                                    boolean append) throws IOException {
        if (TextUtils.isEmpty(filePath))
            return false;
        if (TextUtils.isEmpty(content))
            return false;
        FileWriter fileWriter = null;
        try {
            createFile(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.flush();
            return true;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向文件中写入数据<br>
     * 默认在文件开始处重新写入数据
     *
     * @param filePath 文件目录
     * @param stream   字节输入流
     * @return 写入成功返回true，否则返回false
     * @throws IOException 输入输出异常
     */
    public static boolean writeFile(String filePath, InputStream stream)
            throws IOException {
        return writeFile(filePath, stream, false);
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 文件目录
     * @param stream   字节输入流
     * @param append   如果为 true，则将数据写入文件末尾处； 为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     * @throws IOException 输入输出异常
     */
    public static boolean writeFile(String filePath, InputStream stream,
                                    boolean append) throws IOException {
        return writeFile(filePath != null ? new File(filePath) : null, stream,
                append);
    }

    /**
     * 向文件中写入数据 默认在文件开始处重新写入数据
     *
     * @param file   指定文件
     * @param stream 字节输入流
     * @return 写入成功返回true，否则返回false
     * @throws IOException 输入输出异常
     */
    public static boolean writeFile(File file, InputStream stream)
            throws IOException {
        return writeFile(file, stream, false);
    }

    /**
     * 向文件中写入数据
     *
     * @param file   指定文件
     * @param stream 字节输入流
     * @param append 为true时，在文件开始处重新写入数据； 为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     * @throws IOException 输入输出异常
     */
    public static boolean writeFile(File file, InputStream stream,
                                    boolean append) throws IOException {
        OutputStream out = null;
        try {
            createFile(file.getAbsolutePath());
            out = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                out.write(data, 0, length);
            }
            out.flush();
            return true;
        } finally {
            if (out != null) {
                try {
                    out.close();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFilePath 源文件目录（要复制的文件目录）
     * @param destFilePath   目标文件目录（复制后的文件目录）
     * @return 复制文件成功返回true，否则返回false
     * @throws IOException 输入输出异常
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath)
            throws IOException {
        InputStream inputStream = null;
        inputStream = new FileInputStream(sourceFilePath);
        return writeFile(destFilePath, inputStream);
    }

    /**
     * 获取某个目录下的文件名
     *
     * @param dirPath    目录
     * @param fileFilter 过滤器
     * @return 某个目录下的所有文件名
     */
    public static List<String> getFileNameList(String dirPath,
                                               FilenameFilter fileFilter) {
        if (fileFilter == null)
            return getFileNameList(dirPath);
        if (TextUtils.isEmpty(dirPath))
            return Collections.emptyList();
        File dir = new File(dirPath);
        File[] files = dir.listFiles(fileFilter);
        List<String> conList = new ArrayList<String>();
        for (File file : files) {
            if (file.isFile())
                conList.add(file.getName());
        }
        return conList;
    }

    /**
     * 获取某个目录下的文件名
     *
     * @param dirPath 目录
     * @return 某个目录下的所有文件名
     */
    public static List<String> getFileNameList(String dirPath) {
        if (TextUtils.isEmpty(dirPath))
            return Collections.emptyList();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null)
            return Collections.emptyList();
        List<String> conList = new ArrayList<String>();
        for (File file : files) {
            if (file.isFile())
                conList.add(file.getName());
        }
        return conList;
    }

    /**
     * 获取某个目录下的指定扩展名的文件名称
     *
     * @param dirPath 目录
     * @return 某个目录下的所有文件名
     */
    public static List<String> getFileNameList(String dirPath,
                                               final String extension) {
        if (TextUtils.isEmpty(dirPath))
            return Collections.emptyList();
        File dir = new File(dirPath);
        File[] files = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.indexOf("." + extension) > 0;
            }
        });
        if (files == null)
            return Collections.emptyList();
        List<String> conList = new ArrayList<String>();
        for (File file : files) {
            if (file.isFile())
                conList.add(file.getName());
        }
        return conList;
    }

    /**
     * 获得文件的扩展名
     *
     * @param filePath 文件路径
     * @return 如果没有扩展名，返回""
     */
    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int extensionPosition = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosition = filePath.lastIndexOf(File.separator);
        if (extensionPosition == -1) {
            return "";
        }
        return (filePosition >= extensionPosition) ? "" : filePath.substring(extensionPosition + 1);
    }

    /**
     * @param path 文件路径
     * @return 文件
     */
    public static File getFile(String path) {
        return getFile(new File(path));
    }

    /**
     * @param path     文件路径
     * @param filename 文件名称
     * @return 文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFile(String path, String filename) {
        File file = new File(path);
        if (!file.exists()) {
            File fileIn = new File(SD_PATH);
            if (!fileIn.exists()) {
                fileIn.mkdirs();
            }
            String folders[] = path.split("/");
            String FilePath = "";
            for (String folderName : folders) {
                if (!folderName.equals("")) {
                    FilePath = FilePath + "/" + folderName;
                    File fileExit = new File(FilePath);
                    if (!fileExit.exists()) {
                        fileExit.mkdirs();
                    }
                }
            }
        }
        return new File(path + "/" + filename);
    }

    /**
     * @param file 文件
     * @return 获取SD中的文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFile(File file) {
        try {

            if (file.exists()) {
                return file;
            }
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param path 文件的绝对路径
     * @return 是否创建成功
     */
    public static boolean createFile(String path) {
        return !TextUtils.isEmpty(path) && createFile(new File(path));
    }

    /**
     * 创建文件
     *
     * @param file 文件
     * @return 创建成功返回true
     */
    public static boolean createFile(File file) {
        if (file == null || !makeDirs(getFolderName(file.getAbsolutePath())))
            return false;
        if (!file.exists())
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return false;
    }

    /**
     * 创建目录（可以是多个）
     *
     * @param filePath 目录路径
     * @return 如果路径为空时，返回false；如果目录创建成功，则返回true，否则返回false
     */
    public static boolean makeDirs(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File folder = new File(filePath);
        return (folder.exists() && folder.isDirectory()) || folder
                .mkdirs();
    }

    /**
     * 创建目录（可以是多个）
     *
     * @param dir 目录
     * @return 如果目录创建成功，则返回true，否则返回false
     */
    public static boolean makeDirs(File dir) {
        if (dir == null)
            return false;
        return (dir.exists() && dir.isDirectory()) || dir.mkdirs();
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果路径为空或者为空白字符串，就返回false；如果文件存在，且是文件， 就返回true；如果不是文件或者不存在，则返回false
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 获得不带扩展名的文件名称
     *
     * @param filePath 文件路径
     * @return 不带扩展名的文件名称
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int extensionPosition = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosition = filePath.lastIndexOf(File.separator);
        if (filePosition == -1) {
            return (extensionPosition == -1 ? filePath : filePath.substring(0,
                    extensionPosition));
        }
        if (extensionPosition == -1) {
            return filePath.substring(filePosition + 1);
        }
        return (filePosition < extensionPosition ? filePath.substring(filePosition + 1,
                extensionPosition) : filePath.substring(filePosition + 1));
    }

    /**
     * 获得文件名
     *
     * @param filePath 文件路径
     * @return 如果路径为空或空串，返回路径名；不为空时，返回文件名
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int filePosition = filePath.lastIndexOf(File.separator);
        return (filePosition == -1) ? filePath : filePath.substring(filePosition + 1);
    }

    /**
     * 获得所在目录名称
     *
     * @param filePath 文件的绝对路径
     * @return 如果路径为空或空串，返回路径名；不为空时，如果为根目录，返回"";
     * 如果不是根目录，返回所在目录名称，格式如：C:/Windows/Boot
     */
    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int filePosition = filePath.lastIndexOf(File.separator);
        return (filePosition == -1) ? "" : filePath.substring(0, filePosition);
    }

    /**
     * 判断目录是否存在
     *
     * @param directoryPath 目录路径
     * @return 如果路径为空或空白字符串，返回false；如果目录存在且，确实是目录文件夹，
     * 返回true；如果不是文件夹或者不存在，则返回false
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 删除指定文件或指定目录内的所有文件
     *
     * @param path 文件或目录的绝对路径
     * @return 路径为空或空白字符串，返回true；文件不存在，返回true；文件删除返回true； 文件删除异常返回false
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());//递归删除当前文件夹下的所有文件及其子文件夹
            }
        }
        return file.delete();
    }

    /**
     * 删除指定目录中特定的文件
     *
     * @param dir    路径
     * @param filter 文件过滤器
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void delete(String dir, FilenameFilter filter) {
        if (TextUtils.isEmpty(dir))
            return;
        File file = new File(dir);
        if (!file.exists())
            return;
        if (file.isFile())
            file.delete();
        if (!file.isDirectory())
            return;

        File[] lists;
        if (filter != null)
            lists = file.listFiles(filter);
        else
            lists = file.listFiles();

        if (lists == null)
            return;
        for (File f : lists) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

    /**
     * 获得文件或文件夹的大小
     *
     * @param path 文件或目录的绝对路径
     * @return 返回当前目录的大小 ，注：当文件不存在，为空，或者为空白字符串，返回 -1
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }
        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件路径(绝对路径)
     * @param destFile   目标文件路径(绝对路径)
     * @return 压缩成功返回true
     * @throws IOException 输入输出异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean zipFile(String sourceFile, String destFile)
            throws IOException {
        if (!isFileExist(sourceFile))
            return false;
        File zip = new File(destFile);
        if (zip.exists())
            zip.delete();

        FileInputStream fis = null;
        FileOutputStream outputStream = null;
        ZipOutputStream zipOut = null;
        try {
            File srcFile = new File(sourceFile);
            fis = new FileInputStream(srcFile);
            outputStream = new FileOutputStream(zip);
            zipOut = new ZipOutputStream(outputStream);

            ZipEntry ze = new ZipEntry(srcFile.getName());
            ze.setSize(srcFile.length());
            ze.setTime(srcFile.lastModified());
            zipOut.putNextEntry(ze);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = fis.read(data)) != -1) {
                zipOut.write(data, 0, length);
            }
            zipOut.flush();
            zipOut.close();
            outputStream.close();
            fis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
