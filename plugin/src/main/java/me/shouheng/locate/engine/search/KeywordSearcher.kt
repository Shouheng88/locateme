package me.shouheng.locate.engine.search

import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.parser.DefaultClassParser
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.utils.Logger

/** Keyword searcher. */
class KeywordSearcher: IKeywordSearcher {

    private lateinit var keywords: SearchKeywords
    private lateinit var resources: CompiledResources
    private lateinit var filters: List<IResourceFilter>

    override fun doSearch(
        keywords: SearchKeywords,
        resources: CompiledResources,
        filters: List<IResourceFilter>
    ) {
        this.keywords = keywords
        this.resources = resources
        this.filters = filters
        search()
    }

    /** Do search business. */
    private fun search() {
        resources.getResources().forEach { resource ->
            resource.travel { bytes ->
                try {
                    doTravel(bytes)
                } catch (e: Exception) {
                    Logger.error("Error while traveling resource [$resource].")
                    throw e
                }
            }
        }
    }

    /** Do travel business internal. */
    private fun doTravel(bytes: ByteArray) {
        val parser = getClassParser()
        parser.parseBasic(bytes).takeIf { basic ->
            val ignore = basic.shouldIgnore(filters)
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

    /** Get a class parser. */
    private fun getClassParser(): IClassParser = DefaultClassParser()
}
