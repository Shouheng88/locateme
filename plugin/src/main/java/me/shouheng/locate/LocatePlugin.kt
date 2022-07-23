package me.shouheng.locate

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import me.shouheng.locate.engine.source.CodeSources
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import java.io.File

/** The plugin. */
class LocatePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val srcDirs = mutableListOf<File>()

        project.gradle.projectsEvaluated {
            project.rootProject.allprojects.forEach { p ->
                p.extensions.findByType(LibraryExtension::class.java)?.let {
                    it.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.let { set ->
                        srcDirs.addAll(set.java.srcDirs)
                    }
                }
                p.extensions.findByType(AppExtension::class.java)?.let {
                    it.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.let { set ->
                        srcDirs.addAll(set.java.srcDirs)
                    }
                }
            }
        }

        // Register locate custom transform.
        val sources = CodeSources(srcDirs)
        project.extensions.findByType(AppExtension::class.java)
            ?.registerTransform(LocateTransform(project, sources))

        // Create extension.
        project.extensions.create("locateMe", LocateExtension::class.java)
    }
}
