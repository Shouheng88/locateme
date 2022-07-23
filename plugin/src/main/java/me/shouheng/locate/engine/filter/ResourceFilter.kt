package me.shouheng.locate.engine.filter

/** The compiled resource filter. */
interface ResourceFilter {

    /**
     * Filter class compiled resource.
     *
     * @return true if should ignore given resource.
     */
    fun filterClass(): Boolean

    /**
     * Filter directory compiled resources.
     *
     * @return true if should ignore given resource.
     */
    fun filterDirectory(): Boolean

    /**
     * Filter jar compiled resource.
     *
     * @return true if should ignore given resource.
     */
    fun filterJar(): Boolean
}