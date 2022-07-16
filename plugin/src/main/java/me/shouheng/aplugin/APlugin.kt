package me.shouheng.aplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class APlugin : Plugin<Project> {
    override fun apply(p0: Project) {
        println("APlugin comes into effect.")
    }
}