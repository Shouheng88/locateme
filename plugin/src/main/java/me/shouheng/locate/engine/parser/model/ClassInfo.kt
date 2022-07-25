package me.shouheng.locate.engine.parser.model

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword

/** Class information. */
class ClassInfo {

    ///////////////////////////////////////////////////////////// ORIGIN CONSTANTS

    /** Origin utf-8 constants, mapped from constant index to utf-8. */
    val utf8s = mutableMapOf<Int, String>()

    /** Referenced classes of current class, mapped from constant index to class string. */
    val classes = mutableMapOf<Int, String>()

    /////////////////////////////////////////////////////////////

    /** All referenced methods of current class. */
    val methodRefs = mutableMapOf<Int, MethodRefInfo>()

    /** All referenced strings of current class. */
    val strings = mutableListOf<String>()

    /** Full name of current class. */
    var clazz: String? = null

    /** Method of current class. */
    val methods = mutableListOf<ClassMethod>()

    /** Is base info contains keyword. */
    fun containsKeyword(keyword: SearchKeyword): Boolean {
        return false
    }

    /** Should ignore given class. */
    fun shouldIgnore(filters: List<ResourceFilter>): Boolean = false

    override fun toString(): String {
        return "ClassInfo(utf8s=$utf8s, classes=$classes, methodRefs=$methodRefs, strings=$strings, clazz=$clazz, methods=$methods)"
    }
}
