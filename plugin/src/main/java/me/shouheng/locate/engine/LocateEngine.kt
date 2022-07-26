package me.shouheng.locate.engine

import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.jobs.ConsoleNotifyJob
import me.shouheng.locate.engine.source.CompiledResource
import me.shouheng.locate.engine.jobs.KeywordSearchJob
import me.shouheng.locate.engine.jobs.ResourceFilterJob
import me.shouheng.locate.engine.source.CodeSources
import me.shouheng.locate.engine.jobs.SourceLocateJob

/** Locate Me engine. */
class LocateEngine(
    keywords: SearchKeywords,
    resources: MutableList<CompiledResource>,
    sourceCode: CodeSources
) {

    /** Compiled resource filters. */
    private val filters = mutableListOf<IResourceFilter>()

    /** Engine jobs. */
    private val engineJobs = listOf(
        ResourceFilterJob(resources, filters),
        KeywordSearchJob(keywords, resources, filters),
        SourceLocateJob(keywords, sourceCode),
        ConsoleNotifyJob(keywords)
    )

    /** Do locate. */
    fun start() {
        engineJobs.forEach {
            it.startJob()
        }
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
