package me.shouheng.locate.utils

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream

private const val sBufferSize = 8192

/** Read all bytes from input stream.  */
fun <T : InputStream> T?.readBytes(): ByteArray {
    if (this == null) return ByteArray(0)
    var os: ByteArrayOutputStream? = null
    return try {
        os = ByteArrayOutputStream()
        val b = ByteArray(sBufferSize)
        var len: Int
        while (this.read(b, 0, sBufferSize).also { len = it } != -1) {
            os.write(b, 0, len)
        }
        os.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
        ByteArray(0)
    } finally {
        this.safeClose()
    }
}

/** Save close all. */
fun Closeable?.safeClose() {
    try {
        this?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}