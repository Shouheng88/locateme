package me.shouheng.locate.engine.parser.element

import me.shouheng.locate.engine.parser.IElementParser
import me.shouheng.locate.engine.parser.model.ClassInfo
import me.shouheng.locate.engine.parser.model.ClassMethod
import me.shouheng.locate.utils.readInt
import me.shouheng.locate.utils.readUnsignedShort

/** Class method parser. */
class ClassMethodParser: IElementParser {

    companion object {
        private const val ATTRIBUTES_CODE               = "Code"

        private const val METHOD_ACCESS_FLAT_PRIVATE    = 0x0002
    }

    private var offset: Int = 0

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
            offset += 4 // name_index (u2) + descriptor_index (u2)
            var attributesCount = bytes.readUnsignedShort(offset)
            offset += 2 // attributes_count (u2)

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

                if (ATTRIBUTES_CODE == attrName) {
                    // Code_attribute {
                    //    u2 attribute_name_index;
                    //    u4 attribute_length;
                    //    u2 max_stack;
                    //    u2 max_locals;
                    //    u4 code_length;
                    //    u1 code[code_length];
                    //    u2 exception_table_length;
                    //    {   u2 start_pc;
                    //        u2 end_pc;
                    //        u2 handler_pc;
                    //        u2 catch_type;
                    //    } exception_table[exception_table_length];
                    //    u2 attributes_count;
                    //    attribute_info attributes[attributes_count];
                    //}
                    val codeLengthIndex = offset + 4 // max_stack (u2) + max_locals (u2)
                    var codeLength = bytes.readInt(codeLengthIndex)
                    while (codeLength-- > 0) {
                    }
                }

                offset += attributeLength
            }

            info.methods.add(ClassMethod(isPrivate, name))
        }
    }

    override fun ending(): Int = offset
}