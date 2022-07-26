package me.shouheng.locate.engine.filter

import me.shouheng.locate.engine.source.CompiledResource
import me.shouheng.locate.utils.Logger

/** Default implementation for resource filter. */
class DefaultResourceFilter: IResourceFilter {
    override fun filterClass(): Boolean {
        return false
    }

    override fun filter(resource: CompiledResource): Boolean {
        Logger.debug("Resource [${resource.file}]")
        return false
    }
}