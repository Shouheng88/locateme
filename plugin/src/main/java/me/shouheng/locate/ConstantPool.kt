package me.shouheng.locate

import java.io.File
import java.nio.charset.Charset

/** Parse constant pool. */
object ConstantPool {

    private val constants = mapOf(
        3 to 4,  4 to 4,  5 to 8,  6 to 8,
        7 to 2,  8 to 2,  9 to 4,  10 to 4,
        11 to 4, 12 to 4, 15 to 3, 16 to 2,
        17 to 4, 18 to 4, 19 to 2, 20 to 2
    )

    /** Parse constant pool of class file path. */
    fun parse(path: String): List<String> {
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            return emptyList()
        }
        return parse(file)
    }

    /** Parse constant pool of class file. */
    fun parse(file: File): List<String> {
        try {
            val bytes = file.inputStream().readAllBytes()
            return parse(bytes)
        } catch (e: Exception) {
            Logger.error(" error while paring constant pool for file [$file]: $e")
        }
        return emptyList()
    }

    /** Parse constant pool from byte array. */
    fun parse(bytes: ByteArray, path: String? = ""): List<String> {
        try {
            return parseInternal(bytes)
        } catch (e: Exception) {
            Logger.error(" error while paring constant pool for file [$path]: $e")
        }
        return emptyList()
    }

    private fun parseInternal(bytes: ByteArray): List<String> {
        val sequences = mutableListOf<String>()
        val count = bytes[8]*16*16 + bytes[9]
        var current = 1
        var index = 10
        val classNos = mutableListOf<Int>()
        while (current < count) {
            when (val code = bytes[index].toInt()) {
                1 -> {
                    val length = bytes[index+1]*16*16 + bytes[index+2]
                    val start = index + 3
                    val end = start + length
                    val sequence = String(bytes.slice(IntRange(start, end-1)).toByteArray()
                        , Charset.forName("utf-8"))
                    sequences.add(sequence)
                    index += (2 + length + 1)
                    current += 1
                }
                5, 6 -> {
                    // Special guideline for double and long type, see
                    // https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.5
                    index += (constants[code]!! + 1)
                    current += 2
                }
                else -> {
                    if (code == 7) {
                        val no = bytes[index+1]*16*16 + bytes[index+2]
                        classNos.add(no)
                    }
                    index += (constants[code]!! + 1)
                    current += 1
                }
            }
        }
        return sequences
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val file = File("D:\\codes\\android\\locateme\\Main.class")
        println("${file.exists()}")
        parse(file.path).forEach {
            println(it)
        }
    }
}
