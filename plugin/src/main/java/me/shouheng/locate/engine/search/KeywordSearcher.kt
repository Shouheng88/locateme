package me.shouheng.locate.engine.search

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.utils.Logger

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
        resources.getResources().forEach { resource ->
            resource.travel { bytes ->
                parser.parseBasic(bytes).takeIf { basic ->
                    val ignore = !basic.shouldIgnore(filters)
                    if (ignore) {
                        Logger.debug("Class ignored [$basic]")
                    }
                    ignore
                }?.let { basic ->
                    keywords.keywords.forEach { keyword ->
                        if (basic.containsKeyword(keyword)) {
                            parser.parseMethods(bytes)
                        }
                    }
                }
            }
        }
    }
}
