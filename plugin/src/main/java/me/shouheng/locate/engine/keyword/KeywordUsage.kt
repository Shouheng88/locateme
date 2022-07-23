package me.shouheng.locate.engine.keyword

/** Keyword usage. */
class KeywordUsage(
    /** The keyword to search. */
    keyword: String,
    /** The root keyword. */
    val root: String,
    /** Found keyword usages. */
    usages: List<KeywordUsage> = mutableListOf()
): SearchKeyword(keyword, 0, usages) {

    /** Total visit count. */
    var visit: Int = 0

    /** Caller of this usage from git commit history. */
    val caller: String? = null

    /** Line number of this usage. */
    val lineNumber: Int? = null

    /** The class calling this keyword. */
    val callingClass: String? = null

    /** The method of class calling this keyword. */
    val callingMethod: String? = null

    override fun toString(): String {
        return "KeywordUsage(root='$root', visit=$visit, caller=$caller, lineNumber=$lineNumber, callingClass=$callingClass, callingMethod=$callingMethod)"
    }
}
