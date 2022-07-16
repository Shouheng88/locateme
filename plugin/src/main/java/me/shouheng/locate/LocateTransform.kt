package me.shouheng.locate

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

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
                println("**** transform directory input $it")
            }
            input.jarInputs.forEach {
                println("**** transform jar input $it")
            }
        }
    }
}