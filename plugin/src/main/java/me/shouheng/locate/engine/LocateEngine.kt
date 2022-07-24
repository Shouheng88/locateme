package me.shouheng.locate.engine

import me.shouheng.locate.engine.filter.ResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.notify.EmailNotifier
import me.shouheng.locate.engine.notify.ILocateNotifier
import me.shouheng.locate.engine.parser.element.ClassParserImpl
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.engine.search.IKeywordSearcher
import me.shouheng.locate.engine.search.KeywordSearcher
import me.shouheng.locate.engine.source.CodeSources
import me.shouheng.locate.engine.source.ISourceLocate
import me.shouheng.locate.engine.source.SourceLocate

/** Locate Me engine. */
class LocateEngine(
    private val keywords: SearchKeywords,
    private val resources: CompiledResources,
    private val sourceCode: CodeSources
) {

    /** Class parser. */
    private val parser: IClassParser = ClassParserImpl()

    /** The searcher. */
    private val searcher: IKeywordSearcher = KeywordSearcher()

    /** Compiled resource filters. */
    private val filters = mutableListOf<ResourceFilter>()

    /** Used to locate result in source code. */
    private val locate: ISourceLocate = SourceLocate(keywords, sourceCode)

    /** The notifier for result. */
    private val notifier: ILocateNotifier = EmailNotifier(keywords)

    /** Do locate. */
    fun start() {
        resources.doFilter(filters)
        searcher.doSearch(keywords, resources, parser, filters)
        locate.doLocate()
        notifier.doNotify()
    }

    /** Register compiled resource filter. */
    fun addFilter(filter: ResourceFilter) {
        if (!filters.contains(filter)) {
            filters.add(filter)
        }
    }

    /** Remove compiled resource filter. */
    fun removeFilter(filter: ResourceFilter) {
        filters.remove(filter)
    }
}
