package me.shouheng.locate.engine.parser.element

import me.shouheng.locate.engine.parser.ClassInfo
import me.shouheng.locate.engine.parser.IElementParser
import me.shouheng.locate.utils.readUnsignedShort

/** The class information parser. */
class ClassInfoParser: IElementParser {

    private var offset: Int = 0

    private var interfacesCount: Int = 0

    override fun isBasic(): Boolean = true

    override fun setStart(offset: Int) {
        this.offset = offset
    }

    override fun parse(bytes: ByteArray, info: ClassInfo) {
        val thisClassIndex = bytes.readUnsignedShort(offset + 2)
        val classPath = info.classes[thisClassIndex]
        info.clazz = classPath

        // 6: access_flags (u2) + this_class (u2) + super_class (u2)
        interfacesCount = bytes.readUnsignedShort(offset + 6)
    }

    override fun ending(): Int = offset +
        6 + // access_flags (u2) + this_class (u2) + super_class (u2)
        2 + // interfaces_count (u2)
        2 * interfacesCount // interfaces[interfaces_count]
}