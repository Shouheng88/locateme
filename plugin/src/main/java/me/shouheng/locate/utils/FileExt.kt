package me.shouheng.locate.utils

import java.io.File

/** Is given file a class file. */
fun File.isClass(): Boolean = path.endsWith(".class")

/** Is given file path a class file. */
fun String.isClass(): Boolean = this.endsWith(".class")

/** Is given file a R class file. */
fun File.isRFile(): Boolean = name.startsWith("R$") || name == "R.class"

/** Is zip entry a R class file. */
fun String.isZipEntryRFile(): Boolean {
    val name = this.zipEntryFileName()
    return name.startsWith("R$") || name == "R.class"
}

/** Get zip entry file name. */
fun String.zipEntryFileName(): String {
    if (this.isBlank()) {
        return ""
    }
    val index = this.lastIndexOf('/')
    return if (index >= 0) this.substring(index+1) else this
}

/** Read all bytes of file. */
fun File?.readAll() = this?.inputStream()?.readAll()?:ByteArray(0)
