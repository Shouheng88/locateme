package me.shouheng.locate.utils

import java.io.File

/** Plugin utils */
object FileUtils {

    /** Is given file class file. */
    fun isClassFile(file: File) = file.path.endsWith(".class")

    /** Is class path/zip entry a class file. */
    fun isClassFile(path: String) = path.endsWith(".class")

    /** Should ignore given file. */
    fun shouldIgnoreFile(path: String): Boolean {
        val name = getFileName(path)
        return name.startsWith("R$") || name == "R.class"
    }

    /** Should ignore given zip entry. */
    fun shouldIgnoreZipEntry(entryName: String): Boolean {
        val name = getZipEntryName(entryName)
        return name.startsWith("R$") || name == "R.class"
    }

    /** Get file name from path. */
    fun getFileName(path: String): String {
        if (path.isBlank()) {
            return ""
        }
        val index = path.lastIndexOf(File.separatorChar)
        return if (index >= 0) {
            path.substring(index+1)
        } else path
    }

    /** Get zip entry name. */
    fun getZipEntryName(name: String): String {
        if (name.isBlank()) {
            return ""
        }
        val index = name.lastIndexOf('/')
        return if (index >= 0) {
            name.substring(index+1)
        } else name
    }
}