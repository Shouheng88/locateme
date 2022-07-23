package me.shouheng.locate.engine.keyword

/** The keyword to search. */
open class SearchKeyword(
    /** The keyword to search. */
    val keyword: String,
    /** Traceback count. */
    val traceback: Int = 0,
    /** Found keyword usages. */
    val usages: List<KeywordUsage> = mutableListOf()
) {
    override fun toString(): String {
        return "SearchKeyword(keyword='$keyword', traceback=$traceback, usages=$usages)"
    }
}