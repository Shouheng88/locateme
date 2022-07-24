package me.shouheng.locate.engine.parser.model

/** Class method information. */
class ClassMethod(
    val isPrivate: Boolean,
    val name: String
) {
    override fun toString(): String {
        return "ClassMethod(isPrivate=$isPrivate, name='$name')"
    }
}