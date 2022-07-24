package me.shouheng.locate.engine.parser

import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readAll
import java.io.File
import java.lang.IllegalStateException

/** Default class parser implementation. */
class ClassParserImpl: IClassParser {

    // ClassFile {
    //    u4             magic;
    //    u2             minor_version;
    //    u2             major_version;
    //    u2             constant_pool_count;
    //    cp_info        constant_pool[constant_pool_count-1];
    //    u2             access_flags;
    //    u2             this_class;
    //    u2             super_class;
    //    u2             interfaces_count;
    //    u2             interfaces[interfaces_count];
    //    u2             fields_count;
    //    field_info     fields[fields_count];
    //    u2             methods_count;
    //    method_info    methods[methods_count];
    //    u2             attributes_count;
    //    attribute_info attributes[attributes_count];
    //}

    private var info: ClassInfo? = null

    /** Class element parsers. */
    private val elementParsers = listOf(
        ConstantPoolParser(),
        ClassInfoParser()
    )

    override fun parseBasic(bytes: ByteArray): ClassInfo {
        info = ClassInfo()
        val it = elementParsers.iterator()
        var last: IElementParser? = null
        while (it.hasNext()) {
            val parser = it.next()
            if (parser.isBasic()) {
                parser.setStart(last?.ending()?:0)
                parser.parse(bytes, info!!)
            }
            last = parser
        }
        return info!!
    }

    override fun parseMethods(bytes: ByteArray): ClassInfo {
        info ?: throw IllegalStateException("You should call #parseBasic at first.")
        val it = elementParsers.iterator()
        var last: IElementParser? = null
        while (it.hasNext()) {
            val parser = it.next()
            if (!parser.isBasic()) {
                parser.setStart(last?.ending()?:0)
                parser.parse(bytes, info!!)
            }
            last = parser
        }
        return info!!
    }

    override fun release() {
        info = null
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val file = File("D:\\codes\\android\\locateme\\Base64Test.class")
            Logger.debug("${file.exists()}")
            val parser = ClassParserImpl()
            val bytes = file.readAll()
            val info = parser.parseBasic(bytes)
            parser.parseMethods(bytes)
            Logger.debug(info.toString())
        }
    }
}
