package me.shouheng.locate.engine.parser

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword

/** Class information. */
class ClassInfo {

    /** All methods of class. */
    val methods = mutableListOf<MethodInfo>()

    /** All strings of class. */
    val strings = mutableListOf<String>()

    /** Is base info contains keyword. */
    fun containsKeyword(keyword: SearchKeyword): Boolean {
        return false
    }

    /** Should ignore given class. */
    fun shouldIgnore(filters: List<ResourceFilter>): Boolean = false

    override fun toString(): String {
        return "ClassInfo(methods=$methods, strings=$strings)"
    }
}

/** Class method information. */
data class MethodInfo(
    private val className: String,
    private val methodName: String,
    private val methodType: String
)
