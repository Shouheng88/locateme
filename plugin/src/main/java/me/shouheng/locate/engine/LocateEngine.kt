package me.shouheng.locate.engine

import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.notify.ConsoleNotifier
import me.shouheng.locate.engine.notify.ILocateNotifier
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

    /** The searcher. */
    private val searcher: IKeywordSearcher = KeywordSearcher()

    /** Compiled resource filters. */
    private val filters = mutableListOf<IResourceFilter>()

    /** Used to locate result in source code. */
    private val locate: ISourceLocate = SourceLocate(keywords, sourceCode)

    /** The notifier for result. */
    private val notifier: ILocateNotifier = ConsoleNotifier(keywords)

    /** Do locate. */
    fun start() {
        resources.doFilter(filters)
        searcher.doSearch(keywords, resources, filters)
        locate.doLocate()
        notifier.doNotify()
    }

    /** Register compiled resource filter. */
    fun addFilter(filter: IResourceFilter) {
        if (!filters.contains(filter)) {
            filters.add(filter)
        }
    }

    /** Remove compiled resource filter. */
    fun removeFilter(filter: IResourceFilter) {
        filters.remove(filter)
    }
}
