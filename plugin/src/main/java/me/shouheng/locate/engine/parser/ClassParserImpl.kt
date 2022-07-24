package me.shouheng.locate.engine.parser

import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readAll
import java.io.File

/** Default class parser implementation. */
class ClassParserImpl: IClassParser {

    /** Class element parsers. */
    private val elementParsers = listOf(
        ConstantPoolParser()
    )

    override fun parseBasic(bytes: ByteArray): ClassInfo {
        val info = ClassInfo()
        val it = elementParsers.iterator()
        var last: IElementParser? = null
        while (it.hasNext()) {
            val parser = it.next()
            parser.setStart(last?.ending()?:0)
            parser.parse(bytes, info)
            last = parser
        }
        return info
    }

    override fun parseMethods(bytes: ByteArray, info: ClassInfo): ClassMethods {
        return ClassMethods()
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
