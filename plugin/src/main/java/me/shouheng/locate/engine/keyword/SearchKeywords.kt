package me.shouheng.locate.engine.keyword

/** Searched keywords. */
class SearchKeywords(
    /** Keyword to search. */
    val keywords: List<SearchKeyword>
) {
    override fun toString(): String {
        return "SearchKeywords(keywords=$keywords)"
    }
}