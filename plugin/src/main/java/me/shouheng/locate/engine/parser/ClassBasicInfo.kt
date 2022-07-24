package me.shouheng.locate.engine.parser

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword

/** Base info for class. */
class ClassBasicInfo {

    /** Is base info contains keyword. */
    fun containsKeyword(keyword: SearchKeyword): Boolean {
        return false
    }

    /** Should ignore given class. */
    fun shouldIgnore(filters: List<ResourceFilter>): Boolean = false
}
