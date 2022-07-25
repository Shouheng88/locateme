package me.shouheng.locate.engine.parser.model

/** Class method information. */
class ClassMethod(
    val isPrivate: Boolean,
    val name: String,
    val descriptor: String
) {

    /** Referenced methods of current method. */
    val methodRefs = mutableListOf<MethodRefInfo>()

    override fun toString(): String {
        return "ClassMethod(isPrivate=$isPrivate, name='$name', descriptor='$descriptor', methodRefs=$methodRefs)"
    }
}