package me.shouheng.locate.engine.filter

import me.shouheng.locate.engine.source.CompiledResource

/** The compiled resource filter. */
interface IResourceFilter {

    /**
     * Filter class compiled resource.
     *
     * @param path full class path
     * @return true if should ignore given resource.
     */
    fun ignoreClass(path: String): Boolean

    /**
     * Filter compiled resource.
     *
     * @param resource compiled resource
     * @return true if should ignore given resource.
     */
    fun ignoreResource(resource: CompiledResource): Boolean
}