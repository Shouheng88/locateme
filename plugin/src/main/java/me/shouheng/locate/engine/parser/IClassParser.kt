package me.shouheng.locate.engine.parser

import me.shouheng.locate.engine.parser.model.ClassInfo

/** The class parser. */
interface IClassParser {

    /**
     *  Do basic parse. If the keyword to search found in constant pool,
     *  do method and code parse later, otherwise, methods and byte
     *  code won't be parsed.
     */
    fun parseBasic(bytes: ByteArray): ClassInfo

    /** Parse methods. */
    fun parseMethods(bytes: ByteArray): ClassInfo

    /** Reset state, call this method after parse job. */
    fun release()
}
