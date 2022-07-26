package me.shouheng.locate.engine.source

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import me.shouheng.locate.utils.*
import java.io.File
import java.util.zip.ZipInputStream

/** Compiled resource wrapper. */
class CompiledResource private constructor(
    val file: File,
    private val isJar: Boolean
) {

    /** Read resources. */
    fun travel(callback: (ByteArray) -> Unit) {
        if (isJar) {
            travelJar(file, callback)
        } else {
            travelDirectory(file, callback)
        }
    }

    /** Travel under directory input. */
    private fun travelDirectory(root: File, callback: (ByteArray) -> Unit) {
        Logger.info("traveling root directory [$root]")
        val files = mutableListOf(root)
        while (files.isNotEmpty()) {
            val file = files.removeAt(0)
            if (file.isFile && file.isClass() && !file.isRFile()) {
                callback.invoke(file.readAll())
            } else if (file.isDirectory) {
                file.listFiles()?.toList()?.let { children ->
                    files.addAll(children)
                }
            }
        }
    }

    /** Travel under jar input. */
    private fun travelJar(zipFile: File, callback: (ByteArray) -> Unit) {
        Logger.info("traveling jar file [$zipFile]")
        val zis = ZipInputStream(zipFile.inputStream())
        var entry = zis.nextEntry
        while (entry != null) {
            if (!entry.isDirectory && entry.name.isClass() && !entry.name.isZipEntryRFile()) {
                callback.invoke(zis.readAll(false))
            }
            entry = zis.nextEntry
        }
    }

    override fun toString(): String {
        return "CompiledResource(file=$file, isJar=$isJar)"
    }

    companion object {

        /** Get compiled resource from file and jar type. */
        fun from(file: File, isJar: Boolean): CompiledResource {
            return CompiledResource(file, isJar)
        }

        /** Get compiled resource from jar input. */
        fun from(jarInput: JarInput): CompiledResource {
            return CompiledResource(jarInput.file, true)
        }

        /** Get compiled resource from directory input. */
        fun from(directoryInput: DirectoryInput): CompiledResource {
            return CompiledResource(directoryInput.file, false)
        }
    }
}
