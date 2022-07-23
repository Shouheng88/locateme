package me.shouheng.locate.engine.source

import java.io.File

/** Source code. */
class CodeSources(
    /** Java source directories. */
    val srcDirs: List<File>
) {
    override fun toString(): String {
        var str = "CodeSources:{"
        val len = srcDirs.size
        srcDirs.forEachIndexed { index, file ->
            str += "[${file}]"
            if (index != len-1) {
                str += "\n"
            }
        }
        str += "}"
        return str
    }
}
