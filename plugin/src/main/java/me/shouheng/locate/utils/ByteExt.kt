package me.shouheng.locate.utils

/** Read int from byte array. */
fun ByteArray.readInt(offset: Int): Int {
    return ((this[offset    ].toInt() and 0xFF) shl 24
        or ((this[offset + 1].toInt() and 0xFF) shl 16)
        or ((this[offset + 2].toInt() and 0xFF) shl 8)
        or ((this[offset + 3].toInt() and 0xFF)))
}

/** Read unsigned short from byte array. */
fun ByteArray.readUnsignedShort(offset: Int): Int =
    (((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF))

/** Read short from byte array. */
fun ByteArray.readShort(offset: Int): Short =
    (((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF)).toShort()

/** Read byte from byte array. */
fun ByteArray.readByte(offset: Int): Int = this[offset].toInt() and 0xFF
