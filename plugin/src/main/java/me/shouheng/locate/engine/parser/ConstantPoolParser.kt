package me.shouheng.locate.engine.parser

import me.shouheng.locate.utils.readUnsignedShort
import java.nio.charset.Charset

/** Constant pool parser. */
class ConstantPoolParser: IElementParser {

    companion object {
        /** Length for per constant exclude the tag. */
        private val CONSTANTS_LENGTHS = mapOf(
            3 to 4,  4 to 4,  5 to 8,  6 to 8,
            7 to 2,  8 to 2,  9 to 4,  10 to 4,
            11 to 4, 12 to 4, 15 to 3, 16 to 2,
            17 to 4, 18 to 4, 19 to 2, 20 to 2
        )

        private const val CONSTANT_COUNT_INDEX          = 8
        private const val FIRST_CONSTANT_INDEX          = 10 // CONSTANT_COUNT_INDEX + 2

        private const val CONSTANT_UTF8                 = 1
        private const val CONSTANT_LONG                 = 5
        private const val CONSTANT_DOUBLE               = 6
        private const val CONSTANT_CLASS                = 7
        private const val CONSTANT_STRING               = 8
        private const val CONSTANT_METHOD               = 10
        private const val CONSTANT_NAME_AND_TYPE        = 12
        private const val CONSTANT_METHOD_TYPE          = 16

        /** Length of tag of every constant in byte array. */
        private const val CONSTANT_TAG_LENGTH          = 1
        /** Length of constant utf-8 string length. */
        private const val CONSTANT_UTF8_LENGTH         = 2
    }

    override fun parse(bytes: ByteArray, info: ClassInfo) {
        val count = bytes.readUnsignedShort(CONSTANT_COUNT_INDEX)
        // Current constant index
        var index = 1
        // Offset in bytes
        var offset = FIRST_CONSTANT_INDEX
        val sequences = mutableMapOf<Int, String>()
        val strings = mutableMapOf<Int, Int>()
        val classes = mutableMapOf<Int, Int>()
        val methodTypes = mutableMapOf<Int, Int>()
        val nameAndTypes = mutableMapOf<Int, Pair<Int, Int>>()
        val methods = mutableMapOf<Int, Pair<Int, Int>>()
        while (index < count) {
            when (val code = bytes[offset].toInt()) {
                CONSTANT_UTF8 -> {
                    val length = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                    val start = CONSTANT_TAG_LENGTH + CONSTANT_UTF8_LENGTH + offset
                    val end = start + length
                    val sequence = String(
                        bytes.slice(IntRange(start, end-1)).toByteArray(),
                        Charset.forName("utf-8")
                    )
                    sequences[index] = sequence
                    offset += (CONSTANT_TAG_LENGTH + CONSTANT_UTF8_LENGTH + length)
                    index += 1
                }
                CONSTANT_LONG, CONSTANT_DOUBLE -> {
                    // Special guideline for double and long type, see,
                    // https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.5
                    offset += (CONSTANT_TAG_LENGTH + CONSTANTS_LENGTHS[code]!!)
                    index += 2
                }
                else -> {
                    when (code) {
                        CONSTANT_CLASS -> {
                            classes[index] = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                        }
                        CONSTANT_STRING -> {
                            strings[index] = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                        }
                        CONSTANT_METHOD -> {
                            val classIndex = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                            val nameAndTypeIndex = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset + 2)
                            methods[index] = Pair(classIndex, nameAndTypeIndex)
                        }
                        CONSTANT_NAME_AND_TYPE -> {
                            // Method or field name and type
                            val nameIndex = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                            val typeIndex = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset + 2)
                            nameAndTypes[index] = nameIndex to typeIndex
                        }
                        CONSTANT_METHOD_TYPE -> {
                            methodTypes[index] = bytes.readUnsignedShort(CONSTANT_TAG_LENGTH + offset)
                        }
                    }
                    offset += (CONSTANT_TAG_LENGTH + CONSTANTS_LENGTHS[code]!!)
                    index += 1
                }
            }
        }

        // Build constants.
        buildConstant(info, sequences, strings, classes, methodTypes, nameAndTypes, methods)
    }

    /** Build constant. */
    private fun buildConstant(
        info: ClassInfo,
        sequences: Map<Int, String>,
        strings: Map<Int, Int>,
        classes: Map<Int, Int>,
        methodTypes: Map<Int, Int>,
        nameAndTypes: Map<Int, Pair<Int, Int>>,
        methods: Map<Int, Pair<Int, Int>>
    ) {
        val methodInfos = mutableListOf<MethodInfo>()
        methods.values.forEach {
            val classIndex = classes[it.first]!!
            val nameAndType = nameAndTypes[it.second]!!
            val nameIndex = nameAndType.first
            val typeIndex =  nameAndType.second
            methodInfos.add(MethodInfo(sequences[classIndex]!!, sequences[nameIndex]!!, sequences[typeIndex]!!))
        }
        info.methods.addAll(methodInfos)
        info.strings.addAll(strings.values.map { sequences[it]!! })
    }
}
