package me.shouheng.locate.engine

import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.engine.source.CodeSources
import me.shouheng.locate.utils.Logger

/** Locate Me engine. */
class LocateEngine(
    val keywords: SearchKeywords,
    val resources: CompiledResources,
    val sourceCode: CodeSources
) {

    /** Compiled resource filters. */
    private val resourceFilters = mutableListOf<ResourceFilter>()

    /** Do locate. */
    fun locate() {
        Logger.debug(sourceCode.toString())
        Logger.debug(keywords.toString())
    }

    /** Register compiled resource filter. */
    fun registerResourceFilter(filter: ResourceFilter) {
        if (!resourceFilters.contains(filter)) {
            resourceFilters.add(filter)
        }
    }

    /** Remove compiled resource filter. */
    fun removeResourceFilter(filter: ResourceFilter) {
        resourceFilters.remove(filter)
    }
}
