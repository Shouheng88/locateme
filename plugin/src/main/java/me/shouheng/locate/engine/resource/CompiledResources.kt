package me.shouheng.locate.engine.resource

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput

/** Compiled resources. */
class CompiledResources private constructor(
    val resources: List<CompiledResource>
) {
    companion object {
        /** Get compiled resources from jar inputs and directory inputs. */
        fun from(
            jarInputs: Collection<JarInput>,
            directoryInputs: Collection<DirectoryInput>
        ): CompiledResources {
            val resources = mutableListOf<CompiledResource>()
            resources.addAll(jarInputs.map { CompiledResource.from(it) })
            resources.addAll(directoryInputs.map { CompiledResource.from(it) })
            return CompiledResources(resources)
        }
    }
}