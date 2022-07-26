package me.shouheng.locate.engine.parser

import me.shouheng.locate.engine.parser.element.ClassFiledParser
import me.shouheng.locate.engine.parser.element.ClassInfoParser
import me.shouheng.locate.engine.parser.element.ClassMethodParser
import me.shouheng.locate.engine.parser.element.ConstantPoolParser
import me.shouheng.locate.engine.parser.model.ClassInfo
import me.shouheng.locate.engine.source.CompiledResource
import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readAll
import java.io.File
import java.lang.IllegalStateException

/** Default class parser implementation. */
class DefaultClassParser: IClassParser {

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
        ClassInfoParser(),
        ClassFiledParser(),
        ClassMethodParser()
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

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            parseSingleJar()
        }

        /** Parse single class test. */
        private fun parseSingleClass() {
            val file = File("D:\\codes\\android\\locateme\\Base64Test.class")
            Logger.debug("${file.exists()}")
            val parser = DefaultClassParser()
            val bytes = file.readAll()
            val info = parser.parseBasic(bytes)
            parser.parseMethods(bytes)
            Logger.debug(info.toString())
        }

        /** Parse single jar test. */
        private fun parseSingleJar() {
            CompiledResource.from(File("C:\\Users\\Admin\\.gradle\\caches\\transforms-2\\files-2.1\\" +
                "01932eee3ce130a32420722a9158fccd\\jetified-core-ktx-1.2.0-runtime.jar"), true).travel { bytes ->
                val parser = DefaultClassParser()
                parser.parseBasic(bytes).let {
                    parser.parseMethods(bytes)
                }
            }
        }
    }
}
