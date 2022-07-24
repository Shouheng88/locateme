package me.shouheng.locate.engine.parser

/** The class element parse interface. */
interface IElementParser {

    fun parse(bytes: ByteArray, info: ClassInfo)
}