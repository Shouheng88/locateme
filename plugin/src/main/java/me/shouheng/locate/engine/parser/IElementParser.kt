package me.shouheng.locate.engine.parser

/** The class element parse interface. */
interface IElementParser {

    /** Set start offset of the parser. */
    fun setStart(offset: Int)

    /**
     * Do parse business.
     *
     * @param bytes the byte array to parse
     * @param info the final info of class, used to store parsed messages.
     */
    fun parse(bytes: ByteArray, info: ClassInfo)

    /**
     * Get ending offset.
     *
     * @return the ending offset.
     */
    fun ending(): Int
}