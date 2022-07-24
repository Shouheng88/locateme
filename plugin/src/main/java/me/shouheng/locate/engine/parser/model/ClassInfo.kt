package me.shouheng.locate.engine.parser

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword

/** Class information. */
class ClassInfo {

    /** All methods of class. */
    val methods = mutableListOf<MethodInfo>()

    /** All strings of class. */
    val strings = mutableListOf<String>()

    /** Referenced classes of current class, mapped from constant index to class string. */
    val classes = mutableMapOf<Int, String>()

    /** Full name of current class. */
    var clazz: String? = null

    /** Is base info contains keyword. */
    fun containsKeyword(keyword: SearchKeyword): Boolean {
        return false
    }

    /** Should ignore given class. */
    fun shouldIgnore(filters: List<ResourceFilter>): Boolean = false

    override fun toString(): String {
        return "ClassInfo(methods=$methods, strings=$strings, classes=$classes, clazz=$clazz)"
    }
}

/** Class method information. */
data class MethodInfo(
    private val className: String,
    private val methodName: String,
    private val methodType: String
)
