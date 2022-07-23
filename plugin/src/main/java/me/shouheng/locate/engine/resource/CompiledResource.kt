package me.shouheng.locate.engine.resource

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import java.io.File

/** Compiled resource wrapper. */
class CompiledResource private constructor(
    val file: File,
    val isJar: Boolean
) {

    companion object {

        /** Get compiled resource from jar input. */
        fun from(jarInput: JarInput): CompiledResource {
            return CompiledResource(jarInput.file, true)
        }

        /** Get compiled resource from directory input. */
        fun from(directoryInput: DirectoryInput): CompiledResource {
            return CompiledResource(directoryInput.file, false)
        }
    }
}
