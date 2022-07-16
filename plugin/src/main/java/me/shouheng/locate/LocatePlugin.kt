package me.shouheng.locate

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

/** The plugin. */
class LocatePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.gradle.projectsEvaluated {
            project.rootProject.allprojects.forEach { p ->
                p.extensions.findByType(LibraryExtension::class.java)?.let {
                    it.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.let { set ->
                        set.java.srcDirs.forEach { file ->
                            println("**** java dir: [$file]")
                        }
                    }
                }
                p.extensions.findByType(AppExtension::class.java)?.let {
                    it.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.let { set ->
                        set.java.srcDirs.forEach { file ->
                            println("**** java dir: [$file]")
                        }
                    }
                }
            }
        }
    }
}
