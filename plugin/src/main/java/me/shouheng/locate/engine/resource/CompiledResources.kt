package me.shouheng.locate.engine.resource

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import me.shouheng.locate.engine.filter.IResourceFilter

/** Compiled resources. */
class CompiledResources private constructor(
    private val resources: List<CompiledResource>
) {

    /** Filtered resources. */
    private val filtered = mutableListOf<CompiledResource>()

    /** Do filter for input resources. */
    fun doFilter(filters: List<IResourceFilter>) {
        resources.forEach { resource ->
            if (filters.none { it.filter(resource) }) {
                filtered.add(resource)
            }
        }
    }

    /** Get resources to search. */
    fun getResources(): List<CompiledResource> = filtered

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