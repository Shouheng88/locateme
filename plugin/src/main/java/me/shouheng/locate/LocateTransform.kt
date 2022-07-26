package me.shouheng.locate

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import me.shouheng.locate.engine.LocateEngine
import me.shouheng.locate.engine.filter.DefaultResourceFilter
import me.shouheng.locate.engine.keyword.SearchKeyword
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.engine.source.CodeSources
import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.isBlank
import org.gradle.api.Project

/** The locate me transform. */
class LocateTransform(
    private val project: Project,
    private val sources: CodeSources
) : Transform() {

    override fun getName(): String = javaClass.simpleName

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        mutableSetOf<QualifiedContent.ContentType>().apply {
            addAll(TransformManager.CONTENT_JARS)
            addAll(TransformManager.CONTENT_CLASS)
//            addAll(TransformManager.CONTENT_DEX)
        }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        mutableSetOf<QualifiedContent.Scope>().apply {
            add(QualifiedContent.Scope.PROJECT)
            add(QualifiedContent.Scope.SUB_PROJECTS)
            add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        }

    override fun isIncremental(): Boolean = false

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val directoryInputs = mutableListOf<DirectoryInput>()
        val jarInputs = mutableListOf<JarInput>()
        transformInvocation?.inputs?.forEach { input ->
            directoryInputs.addAll(input.directoryInputs)
            jarInputs.addAll(input.jarInputs)
        }
        val resources = CompiledResources.from(jarInputs, directoryInputs)
        val keywords = mutableListOf<SearchKeyword>()
        val searchKeywords = SearchKeywords(keywords)
        val extension = project.extensions.findByType(LocateExtension::class.java)
        Logger.debug("Found extension: [$extension] [${extension?.keywords}] [${extension?.traceback}]")
        extension?.keywords?.forEach {
            if (!it.isBlank()) {
                val keyword = SearchKeyword(it, extension.traceback)
                keywords.add(keyword)
            }
        }
        val engine = LocateEngine(searchKeywords, resources, sources)
        engine.addFilter(DefaultResourceFilter())
        engine.start()
    }
}
