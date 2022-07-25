package me.shouheng.locate.engine.parser.model

/** Class referenced method wrapper class. */
class MethodRefInfo(
    /** Class of method. */
    val clazz: String,
    /** Name of method. */
    val name: String,
    /** Type: parameter and return type. */
    val type: String
): Cloneable {

    /** Number in code. */
    var codeNumber: Int? = null

    /** Called at line ... */
    var lineNumber: Int? = null

    public override fun clone(): MethodRefInfo {
        return MethodRefInfo(clazz, name, type)
    }

    override fun toString(): String {
        return "MethodRefInfo(clazz='$clazz', name='$name', type='$type', codeNumber=$codeNumber, lineNumber=$lineNumber)"
    }
}
