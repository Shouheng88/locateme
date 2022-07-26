package me.shouheng.locate.engine.jobs

import me.shouheng.locate.engine.IEngineJob
import me.shouheng.locate.engine.filter.IResourceFilter
import me.shouheng.locate.engine.source.CompiledResource
import me.shouheng.locate.utils.Logger

/** Compiled resources. */
class ResourceFilterJob(
    private val resources: MutableList<CompiledResource>,
    private val filters: List<IResourceFilter>
): IEngineJob {

    override fun startJob() {
        val it = resources.iterator()
        while (it.hasNext()) {
            val resource = it.next()
            if (filters.any { it.ignoreResource(resource) }) {
                Logger.info("Resource ignored [$resource]")
                it.remove()
            }
        }
    }
}
