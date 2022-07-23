package me.shouheng.locate

import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.IOException
import java.io.PrintWriter

/**
 *
 * @Author wangshouheng
 * @Time 2022/7/22
 */
object ASMViewer {

    @JvmStatic
    fun main(args: Array<String>) {
        val path = "/Users/wangshouheng/desktop/repo/github/locateme/Base64Test.class"
        val file = File(path)
//        println("${file.exists()}")
//        ConstantPool.parse(file.path).forEach {
//            println(it)
//        }

        try {
            val reader = ClassReader(file.inputStream())
            val tcv = TraceClassVisitor(PrintWriter(System.out))
            reader.accept(tcv, 0)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}