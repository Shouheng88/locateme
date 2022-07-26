package me.shouheng.locate.engine.jobs

import me.shouheng.locate.engine.IEngineJob
import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.parser.DefaultClassParser
import me.shouheng.locate.engine.parser.IClassParser
import me.shouheng.locate.engine.source.CompiledResource
import me.shouheng.locate.utils.Logger

/** Keyword searcher. */
class KeywordSearchJob(
    private val keywords: SearchKeywords,
    private val resources: List<CompiledResource>,
    private val filters: List<IResourceFilter>
): IEngineJob {

    override fun startJob() {
        resources.forEach { resource ->
            resource.travel { entry, bytes ->
                // Currently, only handle jar. If its package is not included
                // in the packages configured, ignore it!
                if (resource.isJar) {
                    val pkg = resource.getPackage(entry)
                    if (filters.any { filter -> filter.ignoreClass(pkg) }) {
                        return@travel
                    }
                }
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
            !ignore
        }?.let { basic ->
            keywords.keywords.forEach { keyword ->
                if (basic.containsKeyword(keyword)) {
                    Logger.debug("Found keyword [${keyword.keyword}] under [${basic.clazz}]")
                    parser.parseMethods(bytes)
                }
            }
        }
    }

    /** Get a class parser. */
    private fun getClassParser(): IClassParser = DefaultClassParser()
}
