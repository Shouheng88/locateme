package me.shouheng.locate.engine.search

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.resource.CompiledResources

/** Interface for keyword searcher. */
interface IKeywordSearcher {

    /** Do search. */
    fun doSearch(
        keywords: SearchKeywords,
        resources: CompiledResources,
        parser: IClassParser,
        filters: List<ResourceFilter>
    )
}