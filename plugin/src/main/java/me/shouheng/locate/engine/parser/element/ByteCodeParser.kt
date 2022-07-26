package me.shouheng.locate.engine.parser.element

import me.shouheng.locate.engine.parser.model.ClassInfo
import me.shouheng.locate.engine.parser.model.MethodRefInfo
import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.readInt
import me.shouheng.locate.utils.readUnsignedShort

/** Byte code parser. */
class ByteCodeParser {

    private var offset: Int = 0
    private var codeLength: Int = 0

    fun setStart(offset: Int) {
        this.offset = offset
    }

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
    fun parse(bytes: ByteArray, info: ClassInfo): List<MethodRefInfo> {
        val methods = mutableListOf<MethodRefInfo>()
        offset += 4 // max_stack (u2) + max_locals (u2)
        codeLength = bytes.readInt(offset)
        offset += 4 // code_length (u4)
        val start = offset
        val end = offset + codeLength
        while (offset < end) {
            val bytecodeOffset = offset - start
            val code = bytes[offset].toInt() and 0xFF
            Logger.debug("Meet code #${bytecodeOffset} [$code]")
            when (code) {
                NOP, ACONST_NULL, ICONST_M1,
                ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
                LCONST_0, LCONST_1,
                FCONST_0, FCONST_1, FCONST_2,
                DCONST_0, DCONST_1,
                IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
                IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
                POP, POP2,
                DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2,
                SWAP,
                IADD, LADD, FADD, DADD,
                ISUB, LSUB, FSUB, DSUB,
                IMUL, LMUL, FMUL, DMUL,
                IDIV, LDIV, FDIV, DDIV,
                IREM, LREM, FREM, DREM,
                INEG, LNEG, FNEG, DNEG,
                ISHL, LSHL, ISHR, LSHR,
                IUSHR, LUSHR,
                IAND, LAND,
                IOR, LOR,
                IXOR, LXOR,
                I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
                LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
                IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
                ARRAYLENGTH, ATHROW, MONITORENTER, MONITOREXIT,
                ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3,
                LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3,
                FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3,
                DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3,
                ALOAD_0, ALOAD_1, ALOAD_2, ALOAD_3,
                ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3,
                LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3,
                FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3,
                DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3,
                ASTORE_0, ASTORE_1, ASTORE_2, ASTORE_3 -> {
                    offset += 1
                }
                IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL, IFNONNULL -> {
                    offset += 3
                }
                ASM_IFEQ, ASM_IFNE, ASM_IFLT, ASM_IFGE, ASM_IFGT, ASM_IFLE, ASM_IF_ICMPEQ, ASM_IF_ICMPNE,
                ASM_IF_ICMPLT, ASM_IF_ICMPGE, ASM_IF_ICMPGT, ASM_IF_ICMPLE, ASM_IF_ACMPEQ,
                ASM_IF_ACMPNE, ASM_GOTO, ASM_JSR, ASM_IFNULL, ASM_IFNONNULL -> {
                    offset += 3
                }
                GOTO_W, JSR_W, ASM_GOTO_W -> {
                    offset += 5
                }
                WIDE -> offset += when (bytes[offset + 1].toInt() and 0xFF) {
                    ILOAD, FLOAD, ALOAD, LLOAD, DLOAD, ISTORE, FSTORE, ASTORE, LSTORE, DSTORE, RET -> 4
                    IINC -> 6
                    else -> throw IllegalArgumentException()
                }
                TABLESWITCH -> {
                    // Skip 0 to 3 padding bytes.
                    offset += 4 - (bytecodeOffset and 3)
                    var numTableEntries = bytes.readInt(offset + 8) - bytes.readInt(offset + 4) + 1
                    offset += 12
                    // Read the table labels.
                    while (numTableEntries-- > 0) {
                        offset += 4
                    }
                }
                LOOKUPSWITCH -> {
                    // Skip 0 to 3 padding bytes.
                    offset += 4 - (bytecodeOffset and 3)
                    var numSwitchCases = bytes.readInt(offset + 4)
                    offset += 8
                    // Read the switch labels.
                    while (numSwitchCases-- > 0) {
                        offset += 8
                    }
                }
                ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE, RET, BIPUSH, NEWARRAY, LDC -> {
                    offset += 2
                }
                SIPUSH, LDC_W, LDC2_W, GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD,
                INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, NEW, ANEWARRAY, CHECKCAST, INSTANCEOF, IINC -> {
                    val methodIndex = bytes.readUnsignedShort(offset + 1)
                    info.methodRefs[methodIndex]?.let { method ->
                        val methodRef = method.clone()
                        methodRef.codeNumber = bytecodeOffset
                        methods.add(methodRef)
                    }
                    offset += 3
                }
                INVOKEINTERFACE, INVOKEDYNAMIC -> {
                    offset += 5
                }
                MULTIANEWARRAY -> {
                    offset += 4
                }
                else -> throw IllegalArgumentException()
            }
        }

        // Read the 'exception_table_length' and 'exception_table' field to create a label for each
        // referenced instruction, and to make methodVisitor visit the corresponding try catch blocks.
        var exceptionTableLength = bytes.readUnsignedShort(offset)
        offset += 2
        while (exceptionTableLength-- > 0) {
            offset += 8
        }

        val lineNumbers = mutableListOf<LineNumber>()
        var attributesCount = bytes.readUnsignedShort(offset)
        offset += 2 // attributes_count (u2)
        while (attributesCount-- > 0) {
            val attrNameIndex = bytes.readUnsignedShort(offset)
            val attrName = info.utf8s[attrNameIndex]!!
            offset += 2 // attribute_name_index (u2)
            val attributeLength = bytes.readInt(offset)
            offset += 4 // attribute_length (u4)

            if (ATTRIBUTES_LINE_NUMBER == attrName) {
                var lineNumberOffset = offset
                var lineNumberTableLength = bytes.readUnsignedShort(lineNumberOffset)
                lineNumberOffset += 2
                var lastLineNumber: LineNumber? = null
                while (lineNumberTableLength-- > 0) {
                    val startPc = bytes.readUnsignedShort(lineNumberOffset)
                    val lineNumber = bytes.readUnsignedShort(lineNumberOffset + 2)
                    lastLineNumber?.end = startPc - 1
                    lastLineNumber = LineNumber(startPc, lineNumber)
                    lineNumbers.add(lastLineNumber)
                    Logger.debug("LineNumber start_pc[$startPc] lineNumber[$lineNumber]")
                    lineNumberOffset += 4
                }
            }

            offset += attributeLength
        }

        methods.forEach { method ->
            lineNumbers.firstOrNull { lineNumber ->
                lineNumber.inRegion(method.codeNumber!!)
            }?.let { lineNumber ->
                method.lineNumber = lineNumber.line
            }?:Logger.error("Line number not found for method ref [$method], lines [${lineNumbers}] .")
        }

        return methods
    }

    /** Code line number wrapper class. */
    private inner class LineNumber(
        /** Start of code number. */
        val start: Int,
        /** Line number in source code. */
        val line: Int,
        /** End of code number. */
        var end: Int = Int.MAX_VALUE
    ) {

        /** Is given position in region [start, end]. */
        fun inRegion(position: Int) = position in start..end

        override fun toString(): String {
            return "LineNumber(start=$start, line=$line, end=$end)"
        }
    }

    private companion object {
        private const val ATTRIBUTES_LINE_NUMBER = "LineNumberTable"

        // The JVM opcode values (with the MethodVisitor method name used to visit them in comment, and
        // where '-' means 'same method name as on the previous line').
        // See https,//docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html.
        private const val NOP = 0 // visitInsn
        private const val ACONST_NULL   = 1 // -
        private const val ICONST_M1= 2 // -
        private const val ICONST_0 = 3 // -
        private const val ICONST_1 = 4 // -
        private const val ICONST_2 = 5 // -
        private const val ICONST_3 = 6 // -
        private const val ICONST_4 = 7 // -
        private const val ICONST_5 = 8 // -
        private const val LCONST_0 = 9 // -
        private const val LCONST_1 = 10 // -
        private const val FCONST_0 = 11 // -
        private const val FCONST_1 = 12 // -
        private const val FCONST_2 = 13 // -
        private const val DCONST_0 = 14 // -
        private const val DCONST_1 = 15 // -
        private const val BIPUSH = 16 // visitIntInsn
        private const val SIPUSH = 17 // -
        private const val LDC = 18 // visitLdcInsn

        // The JVM opcode values which are not part of the ASM public API.
        // See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html.
        private const val LDC_W = 19
        private const val LDC2_W = 20

        private const val ILOAD = 21 // visitprivate const valInsn
        private const val LLOAD = 22 // -
        private const val FLOAD = 23 // -
        private const val DLOAD = 24 // -
        private const val ALOAD = 25 // -

        private const val ILOAD_0 = 26
        private const val ILOAD_1 = 27
        private const val ILOAD_2 = 28
        private const val ILOAD_3 = 29
        private const val LLOAD_0 = 30
        private const val LLOAD_1 = 31
        private const val LLOAD_2 = 32
        private const val LLOAD_3 = 33
        private const val FLOAD_0 = 34
        private const val FLOAD_1 = 35
        private const val FLOAD_2 = 36
        private const val FLOAD_3 = 37
        private const val DLOAD_0 = 38
        private const val DLOAD_1 = 39
        private const val DLOAD_2 = 40
        private const val DLOAD_3 = 41
        private const val ALOAD_0 = 42
        private const val ALOAD_1 = 43
        private const val ALOAD_2 = 44
        private const val ALOAD_3 = 45

        private const val IALOAD = 46 // visitInsn
        private const val LALOAD = 47 // -
        private const val FALOAD = 48 // -
        private const val DALOAD = 49 // -
        private const val AALOAD = 50 // -
        private const val BALOAD = 51 // -
        private const val CALOAD = 52 // -
        private const val SALOAD = 53 // -
        private const val ISTORE = 54 // visitprivate const valInsn
        private const val LSTORE = 55 // -
        private const val FSTORE = 56 // -
        private const val DSTORE = 57 // -
        private const val ASTORE = 58 // -

        private const val ISTORE_0 = 59
        private const val ISTORE_1 = 60
        private const val ISTORE_2 = 61
        private const val ISTORE_3 = 62
        private const val LSTORE_0 = 63
        private const val LSTORE_1 = 64
        private const val LSTORE_2 = 65
        private const val LSTORE_3 = 66
        private const val FSTORE_0 = 67
        private const val FSTORE_1 = 68
        private const val FSTORE_2 = 69
        private const val FSTORE_3 = 70
        private const val DSTORE_0 = 71
        private const val DSTORE_1 = 72
        private const val DSTORE_2 = 73
        private const val DSTORE_3 = 74
        private const val ASTORE_0 = 75
        private const val ASTORE_1 = 76
        private const val ASTORE_2 = 77
        private const val ASTORE_3 = 78

        private const val IASTORE = 79 // visitInsn
        private const val LASTORE = 80 // -
        private const val FASTORE = 81 // -
        private const val DASTORE = 82 // -
        private const val AASTORE = 83 // -
        private const val BASTORE = 84 // -
        private const val CASTORE = 85 // -
        private const val SASTORE = 86 // -
        private const val POP = 87 // -
        private const val POP2 = 88 // -
        private const val DUP = 89 // -
        private const val DUP_X1 = 90 // -
        private const val DUP_X2 = 91 // -
        private const val DUP2 = 92 // -
        private const val DUP2_X1 = 93 // -
        private const val DUP2_X2 = 94 // -
        private const val SWAP = 95 // -
        private const val IADD = 96 // -
        private const val LADD = 97 // -
        private const val FADD = 98 // -
        private const val DADD = 99 // -
        private const val ISUB = 100 // -
        private const val LSUB = 101 // -
        private const val FSUB = 102 // -
        private const val DSUB = 103 // -
        private const val IMUL = 104 // -
        private const val LMUL = 105 // -
        private const val FMUL = 106 // -
        private const val DMUL = 107 // -
        private const val IDIV = 108 // -
        private const val LDIV = 109 // -
        private const val FDIV = 110 // -
        private const val DDIV = 111 // -
        private const val IREM = 112 // -
        private const val LREM = 113 // -
        private const val FREM = 114 // -
        private const val DREM = 115 // -
        private const val INEG = 116 // -
        private const val LNEG = 117 // -
        private const val FNEG = 118 // -
        private const val DNEG = 119 // -
        private const val ISHL = 120 // -
        private const val LSHL = 121 // -
        private const val ISHR = 122 // -
        private const val LSHR = 123 // -
        private const val IUSHR = 124 // -
        private const val LUSHR = 125 // -
        private const val IAND = 126 // -
        private const val LAND = 127 // -
        private const val IOR = 128 // -
        private const val LOR = 129 // -
        private const val IXOR = 130 // -
        private const val LXOR = 131 // -
        private const val IINC = 132 // visitIincInsn
        private const val I2L = 133 // visitInsn
        private const val I2F = 134 // -
        private const val I2D = 135 // -
        private const val L2I = 136 // -
        private const val L2F = 137 // -
        private const val L2D = 138 // -
        private const val F2I = 139 // -
        private const val F2L = 140 // -
        private const val F2D = 141 // -
        private const val D2I = 142 // -
        private const val D2L = 143 // -
        private const val D2F = 144 // -
        private const val I2B = 145 // -
        private const val I2C = 146 // -
        private const val I2S = 147 // -
        private const val LCMP = 148 // -
        private const val FCMPL = 149 // -
        private const val FCMPG = 150 // -
        private const val DCMPL = 151 // -
        private const val DCMPG = 152 // -
        private const val IFEQ = 153 // visitJumpInsn
        private const val IFNE = 154 // -
        private const val IFLT = 155 // -
        private const val IFGE = 156 // -
        private const val IFGT = 157 // -
        private const val IFLE = 158 // -
        private const val IF_ICMPEQ = 159 // -
        private const val IF_ICMPNE = 160 // -
        private const val IF_ICMPLT = 161 // -
        private const val IF_ICMPGE = 162 // -
        private const val IF_ICMPGT = 163 // -
        private const val IF_ICMPLE = 164 // -
        private const val IF_ACMPEQ = 165 // -
        private const val IF_ACMPNE = 166 // -
        private const val GOTO = 167 // -
        private const val JSR = 168 // -
        private const val RET = 169 // visitprivate const valInsn
        private const val TABLESWITCH = 170 // visiTableSwitchInsn
        private const val LOOKUPSWITCH = 171 // visitLookupSwitch
        private const val IRETURN = 172 // visitInsn
        private const val LRETURN = 173 // -
        private const val FRETURN = 174 // -
        private const val DRETURN = 175 // -
        private const val ARETURN = 176 // -
        private const val RETURN = 177 // -
        private const val GETSTATIC = 178 // visitFieldInsn
        private const val PUTSTATIC = 179 // -
        private const val GETFIELD = 180 // -
        private const val PUTFIELD = 181 // -
        private const val INVOKEVIRTUAL = 182 // visitMethodInsn
        private const val INVOKESPECIAL = 183 // -
        private const val INVOKESTATIC = 184 // -
        private const val INVOKEINTERFACE = 185 // -
        private const val INVOKEDYNAMIC = 186 // visitInvokeDynamicInsn
        private const val NEW = 187 // visitTypeInsn
        private const val NEWARRAY = 188 // visitIntInsn
        private const val ANEWARRAY = 189 // visitTypeInsn
        private const val ARRAYLENGTH = 190 // visitInsn
        private const val ATHROW = 191 // -
        private const val CHECKCAST = 192 // visitTypeInsn
        private const val INSTANCEOF = 193 // -
        private const val MONITORENTER = 194 // visitInsn
        private const val MONITOREXIT = 195 // -

        private const val WIDE = 196

        private const val MULTIANEWARRAY = 197 // visitMultiANewArrayInsn
        private const val IFNULL = 198 // visitJumpInsn
        private const val IFNONNULL = 199 // -

        private const val GOTO_W = 200
        private const val JSR_W = 201

        private const val ASM_OPCODE_DELTA = 49
        private const val ASM_IFNULL_OPCODE_DELTA = 20

        // ASM specific opcodes, used for long forward jump instructions.
        // Constants to convert JVM opcodes to the equivalent ASM specific opcodes, and vice versa.
        private const val ASM_IFEQ = IFEQ + ASM_OPCODE_DELTA
        private const val ASM_IFNE = IFNE + ASM_OPCODE_DELTA
        private const val ASM_IFLT = IFLT + ASM_OPCODE_DELTA
        private const val ASM_IFGE = IFGE + ASM_OPCODE_DELTA
        private const val ASM_IFGT = IFGT + ASM_OPCODE_DELTA
        private const val ASM_IFLE = IFLE + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPEQ = IF_ICMPEQ + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPNE = IF_ICMPNE + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPLT = IF_ICMPLT + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPGE = IF_ICMPGE + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPGT = IF_ICMPGT + ASM_OPCODE_DELTA
        private const val ASM_IF_ICMPLE = IF_ICMPLE + ASM_OPCODE_DELTA
        private const val ASM_IF_ACMPEQ = IF_ACMPEQ + ASM_OPCODE_DELTA
        private const val ASM_IF_ACMPNE = IF_ACMPNE + ASM_OPCODE_DELTA
        private const val ASM_GOTO = GOTO + ASM_OPCODE_DELTA
        private const val ASM_JSR = JSR + ASM_OPCODE_DELTA
        private const val ASM_IFNULL = IFNULL + ASM_IFNULL_OPCODE_DELTA
        private const val ASM_IFNONNULL = IFNONNULL + ASM_IFNULL_OPCODE_DELTA
        private const val ASM_GOTO_W = 220
    }
}