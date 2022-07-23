package me.shouheng.locate

import me.shouheng.locate.utils.Logger
import java.io.File
import java.nio.charset.Charset

/** Parse constant pool. */
object ConstantPool {

    private val CONSTANTS_LENGTHS = mapOf(
        3 to 4,  4 to 4,  5 to 8,  6 to 8,
        7 to 2,  8 to 2,  9 to 4,  10 to 4,
        11 to 4, 12 to 4, 15 to 3, 16 to 2,
        17 to 4, 18 to 4, 19 to 2, 20 to 2
    )

    private const val CONSTANT_UTF8     = 1
    private const val CONSTANT_INT      = 3
    private const val CONSTANT_FLOAT    = 4
    private const val CONSTANT_LONG     = 5
    private const val CONSTANT_DOUBLE   = 6
    private const val CONSTANT_CLASS    = 7

    private const val ATTRIBUTES_CODE   = "Code"

    private const val BYTE_UNIT = 16*16

    /** Parse constant pool of class file path. */
    fun parse(path: String): List<String> {
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            return emptyList()
        }
        return parse(file)
    }

    /** Parse constant pool of class file. */
    fun parse(file: File): List<String> {
        try {
            val bytes = file.inputStream().readAllBytes()
            return parse(bytes)
        } catch (e: Exception) {
            Logger.error("error while paring constant pool for file [$file]", e)
        }
        return emptyList()
    }

    /** Parse constant pool from byte array. */
    fun parse(bytes: ByteArray, path: String? = ""): List<String> {
        try {
            return parseInternal(bytes)
        } catch (e: Exception) {
            Logger.error("error while paring constant pool for file [$path]", e)
        }
        return emptyList()
    }

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
    //
    private fun parseInternal(bytes: ByteArray): List<String> {
        val sequences = mutableMapOf<Int, String>()
        val constantCount = bytes[8]*16*16 + bytes[9]
        var constantIndex = 1
        var index = 10
        val classNos = mutableListOf<Int>()
        while (constantIndex < constantCount) {
            when (val code = bytes[index].toInt()) {
                CONSTANT_UTF8 -> {
                    val length = readShort(bytes, index+1) // TODO unsigned
                    val start = index + 3
                    val end = start + length
                    val sequence = String(bytes.slice(IntRange(start, end-1)).toByteArray()
                        , Charset.forName("utf-8"))
                    sequences[constantIndex] = sequence
                    index += (2 + length + 1)
                    constantIndex += 1
                }
                CONSTANT_LONG, CONSTANT_DOUBLE -> {
                    // Special guideline for double and long type, see,
                    // https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.5
                    index += (CONSTANTS_LENGTHS[code]!! + 1)
                    constantIndex += 2
                }
                else -> {
                    if (code == CONSTANT_CLASS) {
                        // Specify witch constant in pool is class.
                        val no = bytes[index+1]*16*16 + bytes[index+2]
                        classNos.add(no)
                    }
                    index += (CONSTANTS_LENGTHS[code]!! + 1)
                    constantIndex += 1
                }
            }
        }
        index += 6

        val interfacesCount = readShort(bytes, index)
        Logger.debug("interfaces count [$interfacesCount]")
        index += 2
        index += (2 * interfacesCount)

        // field_info {
        //    u2             access_flags;
        //    u2             name_index;
        //    u2             descriptor_index;
        //    u2             attributes_count;
        //    attribute_info attributes[attributes_count];
        //}
        // attribute_info {
        //    u2 attribute_name_index;
        //    u4 attribute_length;
        //    u1 info[attribute_length];
        //}
        val fieldCount = readShort(bytes, index)
        Logger.debug("field count [$fieldCount]")
        index += 2

        var currentField = 0
        while (currentField < fieldCount) {
            index += 2
            val nameIndex = readShort(bytes, index)
            val name = sequences[nameIndex] // field name
            index += 4
            val attributesCount = readShort(bytes, index)
            Logger.debug("${"%x".format(index)} currentField [$currentField] [$name] attributesCount[$attributesCount]")
            index += 2

            var attributesIndex = 0
            while (attributesIndex < attributesCount) {
                // Who cares fields ...
                index += 2
                val attributeLength = readInt(bytes, index)
                index += 4
                index += attributeLength
                attributesIndex ++
                Logger.debug("currentField [$currentField] attributesIndex[$attributesIndex] attributeLength[$attributeLength]")
            }
            currentField ++
        }

        // method_info {
        //    u2             access_flags;
        //    u2             name_index;
        //    u2             descriptor_index;
        //    u2             attributes_count;
        //    attribute_info attributes[attributes_count];
        //}
        val methodCount = readShort(bytes, index)
        Logger.debug("${"%x".format(index)} method count [$methodCount]")
        index += 2

        var methodIndex = 0
        while (methodIndex < methodCount) {
            index += 2
            val methodNameIndex = readShort(bytes, index)
            val methodName = sequences[methodNameIndex] // method name
            index += 4
            val attributesCount = readShort(bytes, index)
            Logger.debug("method ${"%x".format(index)} [${methodIndex+1}/$methodCount $methodName]")
            index += 2

            var attributesIndex = 0
            while (attributesIndex < attributesCount) {
                val attrNameIndex = readShort(bytes, index)
                val attrName = sequences[attrNameIndex]
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
                index += 2
                val attributeLength = readInt(bytes, index)
                index += 4
                Logger.debug("attribute: ${"%x".format(index)} [${attributesIndex+1}/$attributesCount $attrName] [$attributeLength]")
                if (ATTRIBUTES_CODE == attrName) {
                    val codeCount = readInt(bytes, index+4)
                    var codeIndex = 0
                    while (codeIndex < codeCount) {
                        codeIndex++
                    }
                    Logger.debug("code: ${"%x".format(index+4)} codeCount[$codeCount]")
                }
                index += attributeLength
                attributesIndex ++
            }
            methodIndex ++
        }
        return sequences.values.toList()
    }

    private fun readShort(bytes: ByteArray, start: Int): Int =
            bytes[start  ] * BYTE_UNIT + bytes[start+1]

    private fun readInt(bytes: ByteArray, start: Int): Int =
            bytes[start  ] * BYTE_UNIT * BYTE_UNIT * BYTE_UNIT +
            bytes[start+1] * BYTE_UNIT * BYTE_UNIT +
            bytes[start+2] * BYTE_UNIT +
            bytes[start+3]

    @JvmStatic fun main(args: Array<String>) {
        val file = File("D:\\codes\\android\\locateme\\Main.class")
        println("${file.exists()}")
        parse(file.path).forEach {
            println(it)
        }
    }
}

// One apache library to parse java class file, while it didn't analyse the method code attribute.
// https://mvnrepository.com/artifact/org.apache.bcel/bcel