package me.shouheng.locate

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import java.io.File
import java.util.zip.ZipInputStream

/** The locate me transform. */
class LocateTransform(val project: Project) : Transform() {

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
        transformInvocation?.inputs?.forEach { input ->
            input.directoryInputs.forEach {
                searchKeywordBFS(it.file)
            }
            input.jarInputs.forEach {
                searchKeywordBFSOfZip(it.file)
            }
        }
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
