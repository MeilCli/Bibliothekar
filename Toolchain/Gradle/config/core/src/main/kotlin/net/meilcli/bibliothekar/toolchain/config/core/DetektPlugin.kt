package net.meilcli.bibliothekar.toolchain.config.core

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import net.meilcli.bibliothekar.config.Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        project.dependencies.add("detektPlugins", Dependencies.IoGitlabArturboschDetekt.DetektFormatting)
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
