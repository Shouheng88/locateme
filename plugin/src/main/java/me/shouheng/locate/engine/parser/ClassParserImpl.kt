package me.shouheng.locate.engine.parser

import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readAll
import java.io.File

/** Default class parser implementation. */
class ClassParserImpl: IClassParser {

    private val constantPoolParser = ConstantPoolParser()

    override fun parseBasic(bytes: ByteArray): ClassInfo {
        val info = ClassInfo()
        parseConstantPool(bytes, info)
        return info
    }

    override fun parseMethods(bytes: ByteArray, info: ClassInfo): ClassMethods {
        return ClassMethods()
    }

    /** Parse constant pool. */
    private fun parseConstantPool(bytes: ByteArray, info: ClassInfo) {
        constantPoolParser.parse(bytes, info)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val file = File("D:\\codes\\android\\locateme\\Base64Test.class")
            Logger.debug("${file.exists()}")
            val parser = ClassParserImpl()
            val info = parser.parseBasic(file.readAll())
            Logger.debug(info.toString())
        }
    }
}
