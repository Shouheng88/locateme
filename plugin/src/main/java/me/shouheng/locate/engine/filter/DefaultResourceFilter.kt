package me.shouheng.locate.engine.filter

import me.shouheng.locate.engine.source.CompiledResource

/** Default implementation for resource filter. */
class DefaultResourceFilter(
    private val packages: List<String>
): IResourceFilter {

    override fun ignoreClass(path: String): Boolean {
        return packages.isNotEmpty() && packages.none { pkg -> path.startsWith(pkg) }
    }

    override fun ignoreResource(resource: CompiledResource): Boolean {
        return false
    }
}