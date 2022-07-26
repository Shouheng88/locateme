package me.shouheng.locate.engine.parser.model

import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword

/** Class information. */
class ClassInfo {

    ///////////////////////////////////////////////////////////// ORIGIN CONSTANTS

    private var utf8Set: MutableSet<String>? = null

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
        if (utf8Set == null) {
            utf8Set = mutableSetOf<String>().apply {
                this.addAll(utf8s.values)
            }
        }
        return utf8Set?.contains(keyword.keyword) == true
    }

    /** Should ignore given class. */
    fun shouldIgnore(filters: List<IResourceFilter>): Boolean {
        return filters.any { filter -> filter.ignoreClass(clazz ?: "") }
    }

    override fun toString(): String {
        return "ClassInfo(utf8s=$utf8s, classes=$classes, methodRefs=$methodRefs, strings=$strings, clazz=$clazz, methods=$methods)"
    }
}
