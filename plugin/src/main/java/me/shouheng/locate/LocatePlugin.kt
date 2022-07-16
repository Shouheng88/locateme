package me.shouheng.locate

import org.gradle.api.Plugin
import org.gradle.api.Project

/** The plugin. */
class LocatePlugin : Plugin<Project> {
    override fun apply(p0: Project) {
        println("Locate plugin comes into effect.")
    }
}
