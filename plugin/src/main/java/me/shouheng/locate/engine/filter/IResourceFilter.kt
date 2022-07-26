package me.shouheng.locate.engine.filter

import me.shouheng.locate.engine.resource.CompiledResource

/** The compiled resource filter. */
interface IResourceFilter {

    /**
     * Filter class compiled resource.
     *
     * @return true if should ignore given resource.
     */
    fun filterClass(): Boolean

    /**
     * Filter compiled resource.
     *
     * @return true if should ignore given resource.
     */
    fun filter(resource: CompiledResource): Boolean
}