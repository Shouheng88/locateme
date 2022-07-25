package me.shouheng.locate.engine.parser.element

import me.shouheng.locate.engine.parser.IElementParser
import me.shouheng.locate.engine.parser.model.ClassInfo
import me.shouheng.locate.engine.parser.model.ClassMethod
import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readInt
import me.shouheng.locate.utils.readUnsignedShort

/** Class method parser. */
class ClassMethodParser: IElementParser {

    companion object {
        private const val ATTRIBUTES_CODE               = "Code"

        private const val METHOD_ACCESS_FLAT_PRIVATE    = 0x0002
    }

    private var offset: Int = 0

    private val codeParser = ByteCodeParser()

    override fun isBasic(): Boolean = false

    override fun setStart(offset: Int) {
        this.offset = offset
    }

    override fun parse(bytes: ByteArray, info: ClassInfo) {
        var methodCount = bytes.readUnsignedShort(offset)
        offset += 2

        // method_info {
        //    u2             access_flags;
        //    u2             name_index;
        //    u2             descriptor_index;
        //    u2             attributes_count;
        //    attribute_info attributes[attributes_count];
        //}
        while (methodCount-- > 0) {
            val accessFlag = bytes.readUnsignedShort(offset)
            val isPrivate = accessFlag and METHOD_ACCESS_FLAT_PRIVATE == METHOD_ACCESS_FLAT_PRIVATE
            offset += 2 // access_flags (u2)

            val nameIndex = bytes.readUnsignedShort(offset)
            val name = info.utf8s[nameIndex]!!
            offset += 2 // name_index (u2)
            Logger.debug("Parsing method [${info.clazz}][$name]")

            val descriptorIndex = bytes.readUnsignedShort(offset)
            val descriptor = info.utf8s[descriptorIndex]!!
            offset += 2 // descriptor_index (u2)

            var attributesCount = bytes.readUnsignedShort(offset)
            offset += 2 // attributes_count (u2)

            val method = ClassMethod(isPrivate, name, descriptor)

            // attribute_info {
            //    u2 attribute_name_index;
            //    u4 attribute_length;
            //    u1 info[attribute_length];
            //}
            while (attributesCount-- > 0) {
                val attrNameIndex = bytes.readUnsignedShort(offset)
                val attrName = info.utf8s[attrNameIndex]!!
                offset += 2 // attribute_name_index (u2)
                val attributeLength = bytes.readInt(offset)
                offset += 4 // attribute_length (u4)

                // Parse "Code" attribute.
                if (ATTRIBUTES_CODE == attrName) {
                    codeParser.setStart(offset)
                    val methodRefs = codeParser.parse(bytes, info)
                    method.methodRefs.addAll(methodRefs)
                }

                offset += attributeLength
            }

            info.methods.add(method)
        }
    }

    override fun ending(): Int = offset
}