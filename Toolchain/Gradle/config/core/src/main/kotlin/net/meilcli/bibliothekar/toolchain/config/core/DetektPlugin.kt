package net.meilcli.bibliothekar.toolchain.config.core

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import net.meilcli.bibliothekar.config.Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.tasks.TaskProvider
import java.io.File

class DetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions
            .findByType(DetektExtension::class.java)
            ?.apply {
                val rootDir = project.findRootDirectory()
                buildUponDefaultConfig = true
                config = project.files(File(rootDir, "detekt.yml"))
                basePath = rootDir.absolutePath
            }
        val dependencies = mutableListOf<Dependency?>()
        dependencies += project.dependencies.add("detektPlugins", Dependencies.IoGitlabArturboschDetekt.DetektFormatting)
        dependencies += project.dependencies.add("detektPlugins", Dependencies.ToolchainDetektRule.ToolchainDetektRule)
        dependencies.filterIsInstance<ModuleDependency>()
            .forEach { moduleDependency ->
                moduleDependency.exclude(mapOf("group" to "org.slf4j"))
            }
        project.afterEvaluate {
            project.tasks
                .named("check")
                .configure {
                    it.setDependsOn(it.dependsOn.filterNot { it is TaskProvider<*> && it.name == "detekt" })
                }
        }
    }

    // need rootDir but Project.rootDir is root of composite module
    private fun Project.findRootDirectory(): File {
        var current = rootDir
        while (current.parentFile != null) {
            if (current.name == "Extractor" || current.name == "Toolchain") {
                current = current.parentFile
                break
            } else {
                current = current.parentFile
            }
        }
        return current
    }
}
