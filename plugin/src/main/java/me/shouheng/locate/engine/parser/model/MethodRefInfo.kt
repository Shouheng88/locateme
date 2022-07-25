package me.shouheng.locate.engine.parser.model

/** Class referenced method wrapper class. */
data class MethodRefInfo(
    /** Class of method. */
    val clazz: String,
    /** Name of method. */
    val name: String,
    /** Type: parameter and return type. */
    val type: String
) {
    /** Called at line ... */
    var lineNumber: Int? = null
}
