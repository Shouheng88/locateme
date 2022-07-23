package me.shouheng.locate.engine

/** The compiled resource filter. */
interface ResourceFilter {

    /**
     * Filter class compiled resource.
     * @return true if should ignore given resource.
     */
    fun filterClass(): Boolean

    /**
     * Filter jar compiled resource.
     * @return true if should ignore given resource.
     */
    fun filterJar(): Boolean
}
