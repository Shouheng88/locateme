package me.shouheng.locate.engine.parser.element

import me.shouheng.locate.engine.parser.ClassInfo
import me.shouheng.locate.engine.parser.IElementParser
import me.shouheng.locate.utils.readInt
import me.shouheng.locate.utils.readUnsignedShort

/** Class filed parser. */
class ClassFiledParser: IElementParser {

    private var offset: Int = 0

    override fun isBasic(): Boolean = false

    override fun setStart(offset: Int) {
        this.offset = offset
    }

    // field_info {
    //    u2             access_flags;
    //    u2             name_index;
    //    u2             descriptor_index;
    //    u2             attributes_count;
    //    attribute_info attributes[attributes_count];
    //}
    override fun parse(bytes: ByteArray, info: ClassInfo) {
        var fieldCount = bytes.readUnsignedShort(offset)
        offset += 2 // fields_count (u2)

        while (fieldCount-- > 0) {
            offset += 6 // access_flags (u2) + name_index (u2) + descriptor_index (u2)

            // attribute_info {
            //    u2 attribute_name_index;
            //    u4 attribute_length;
            //    u1 info[attribute_length];
            //}
            var attributesCount = bytes.readUnsignedShort(offset)
            offset += 2 // attributes_count (u2)
            while (attributesCount-- > 0) {
                offset += 2 // attribute_name_index (u2)
                val attributeLength = bytes.readInt(offset)
                offset += 4 // attribute_length (u4)
                offset += attributeLength
            }
        }
    }

    override fun ending(): Int = offset
}