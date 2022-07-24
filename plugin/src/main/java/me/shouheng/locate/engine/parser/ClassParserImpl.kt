package me.shouheng.locate.engine.parser

/** Default class parser implementation. */
class ClassParserImpl: IClassParser {
    override fun parseBasic(bytes: ByteArray): ClassBasicInfo {
        return ClassBasicInfo()
    }

    override fun parseMethods(bytes: ByteArray, info: ClassBasicInfo): ClassMethods {
        return ClassMethods()
    }
}