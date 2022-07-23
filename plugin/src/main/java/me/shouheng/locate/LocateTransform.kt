package me.shouheng.locate

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import me.shouheng.locate.engine.LocateEngine
import me.shouheng.locate.engine.resource.CompiledResources
import me.shouheng.locate.engine.keyword.SearchKeyword
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.source.CodeSources
import me.shouheng.locate.utils.FileUtils
import me.shouheng.locate.utils.Logger
import me.shouheng.locate.utils.isBlank
import org.gradle.api.Project
import java.io.File
import java.util.zip.ZipInputStream

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
        engine.start()
    }

    /** Search keyword by BFS. */
    private fun searchKeywordBFS(root: File) {
        Logger.info("searching keyword for root directory [$root]")
        val files = mutableListOf(root)
        while (files.isNotEmpty()) {
            val file = files.removeAt(0)
            if (file.isFile
                && FileUtils.isClassFile(file)
                && !FileUtils.shouldIgnoreFile(file.path)) {
                val sequences = ConstantPool.parse(file)
                val any = sequences.any { sequence -> sequence.contains("Hello") }
                if (any) {
                    Logger.debug("@@@ transform hit : $file")
                } else {
                    Logger.debug("@@@ transform miss : $file-${FileUtils.getFileName(file.path)} \n\t\tpool: [$sequences]")
                }
            } else if (file.isDirectory) {
                file.listFiles()?.toList()?.let { children ->
                    files.addAll(children)
                }
            }
        }
    }

    /** Search keyword by BFS from zip/jar file. */
    private fun searchKeywordBFSOfZip(zipFile: File) {
        val zis = ZipInputStream(zipFile.inputStream())
        var entry = zis.nextEntry
        while (entry != null) {
            if (!entry.isDirectory
                && FileUtils.isClassFile(entry.name)
                && !FileUtils.shouldIgnoreZipEntry(entry.name)) {
                val bytes = zis.readAllBytes()
                val sequences = ConstantPool.parse(bytes, entry.name)
                val any = sequences.any { sequence -> sequence.contains("Hello") }
                if (any) {
                    Logger.debug("@@@ transform hit : ${entry.name}")
                } else {
                    Logger.debug("@@@ transform miss : ${entry.name}-${FileUtils.getZipEntryName(entry.name)} \n\t\tpool: [$sequences]")
                }
            }
            entry = zis.nextEntry
        }
    }
}
