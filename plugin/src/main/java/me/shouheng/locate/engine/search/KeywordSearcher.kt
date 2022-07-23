package me.shouheng.locate.engine.search

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.resource.CompiledResources

/** Keyword searcher. */
class KeywordSearcher: IKeywordSearcher {

    private lateinit var keywords: SearchKeywords
    private lateinit var resources: CompiledResources
    private lateinit var parser: IClassParser
    private lateinit var filters: List<ResourceFilter>

    override fun doSearch(
        keywords: SearchKeywords,
        resources: CompiledResources,
        parser: IClassParser,
        filters: List<ResourceFilter>
    ) {
        this.keywords = keywords
        this.resources = resources
        this.parser = parser
        this.filters = filters
        search()
    }

    /** Do search business. */
    private fun search() {

    }
}
