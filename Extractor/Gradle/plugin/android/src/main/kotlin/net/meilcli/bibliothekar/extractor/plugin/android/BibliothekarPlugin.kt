package net.meilcli.bibliothekar.extractor.plugin.android

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarExtractTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.slf4j.LoggerFactory

class BibliothekarPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(BibliothekarPlugin::class.java)

    override fun apply(project: Project) {
        project.setUpExtractTask()

        if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            setupDumpWithLibraryPlugin(project)
        } else {
            project.plugins.whenPluginAdded {
                if (it is LibraryPlugin) {
                    setupDumpWithLibraryPlugin(project)
                }
            }
        }
    }

    private fun Project.setUpExtractTask() {
        afterEvaluate {
            configurations.forEach { configuration ->
                if (configuration.isCanBeResolved) {
                    project.tasks.register(BibliothekarExtractTask.taskName(configuration), BibliothekarExtractTask::class.java) { task ->
                        task.setup(configuration)
                    }
                }
            }
        }
    }

    private fun setupDumpWithLibraryPlugin(project: Project) {
        project.afterEvaluate {
            project.getVariants()
                .forEach { variant ->
                    val dependencyConfigurations = variant.runtimeConfiguration
                        .extendsFrom
                        .map { project.getDependencyDefinedConfiguration(it) }
                    val dependencyProjects = variant.runtimeConfiguration
                        .extendsFrom
                        .flatMap { it.dependencies }
                        .filterIsInstance<ProjectDependency>()
                        .map { it.dependencyProject }
                    project.tasks
                        .register("${variant.name}Bibliothekar", BibliothekarTask::class.java) { task ->
                            task.group = "dump"
                            task.dependsOn(dependencyConfigurations.map { BibliothekarExtractTask.taskName(it) })

                            dependencyProjects.forEach { dependencyProject ->
                                if (dependencyProject.plugins.hasPlugin(LibraryPlugin::class.java) ||
                                    dependencyProject.plugins.hasPlugin(AppPlugin::class.java)
                                ) {
                                    val dependencyProjectVariant = dependencyProject.getVariants()
                                        .findMatchVariant(variant)
                                    task.dependsOn("${dependencyProject.path}:${dependencyProjectVariant.name}Bibliothekar")
                                } else {
                                    task.dependsOn("${dependencyProject.path}:bibliothekar")
                                }
                            }
                        }
                }
        }
    }

    private fun Project.getDependencyDefinedConfiguration(configuration: Configuration): Configuration {
        if (configuration.isCanBeResolved) {
            return configuration
        }
        return configurations.findByName("${configuration.name}DependenciesMetadata") ?: throw IllegalStateException("a")
    }

    private fun Project.getVariants(): List<Variant> {
        val libraryVariants = project.plugins
            .findPlugin(LibraryPlugin::class.java)
            ?.variantManager
            ?.mainComponents
            ?.map { it.variant }
        val applicationVariants = project.plugins
            .findPlugin(AppPlugin::class.java)
            ?.variantManager
            ?.mainComponents
            ?.map { it.variant }
        return libraryVariants ?: applicationVariants ?: throw Exception()
    }

    private fun List<Variant>.findMatchVariant(target: Variant): Variant {
        // exactly match
        val exactlyMatch = find { it.name == target.name }
        if (exactlyMatch != null) {
            return exactlyMatch
        }

        // lazy match for flavor
        for (variant in this) {
            if (variant.buildType != target.buildType) {
                // buildType needs exactly match
                continue
            }

            val match = variant.productFlavors
                .all { findFlavor ->
                    target.productFlavors
                        .any { targetFlavor ->
                            targetFlavor.first == findFlavor.first && targetFlavor.second == findFlavor.second
                        }
                }
            if (match) {
                return variant
            }
        }

        throw Exception("variants ${this.size}")
    }
}
