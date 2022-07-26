package me.shouheng.locate

/** Locate extension. */
open class LocateExtension {
    /** Keywords to search. */
    var keywords = emptyList<String>()
    /** Traceback count. */
    var traceback: Int = 0
    /** Packages to scan. */
    var packages = emptyList<String>()
}