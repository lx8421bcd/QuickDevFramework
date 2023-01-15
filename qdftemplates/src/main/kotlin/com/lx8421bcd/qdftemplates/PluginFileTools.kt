package com.lx8421bcd.qdftemplates

import java.io.*

object PluginFileTools {
    fun loadStringFromResource(filePath: String): String {
        val fin: InputStream
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            fin = this.javaClass.getResourceAsStream(filePath)?:return ""
            reader = BufferedReader(InputStreamReader(fin))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
    fun loadStringFromFile(file: File): String {
        if (!file.exists()) {
            return "file not exists: file path = ${file.path}"
        }
        val fin: FileInputStream
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            fin = FileInputStream(file)
            reader = BufferedReader(InputStreamReader(fin))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}