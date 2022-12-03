package net.meilcli.bibliothekar.extractor.plugin.android

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarExtractTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarReportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency

@Suppress("unused")
class BibliothekarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.setUpExtractTask()

        if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            project.setUpReportTask()
        } else {
            project.plugins.whenPluginAdded {
                if (it is LibraryPlugin) {
                    project.setUpReportTask()
                }
            }
        }
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            project.setUpReportTask()
        } else {
            project.plugins.whenPluginAdded {
                if (it is AppPlugin) {
                    project.setUpReportTask()
                }
            }
        }
    }

    private fun Project.setUpExtractTask() {
        afterEvaluate {
            configurations.forEach { configuration ->
                if (configuration.isCanBeResolved) {
                    project.tasks.register(BibliothekarExtractTask.taskName(configuration), BibliothekarExtractTask::class.java) { task ->
                        task.setup(rootProject, this, configuration)
                    }
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.setUpReportTask() {
        project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?.onVariants { variant ->
                tasks.register(BibliothekarReportTask.taskName(variant.name), BibliothekarReportTask::class.java) { task ->
                    task.setup()

                    val dependencyConfigurations = variant.runtimeConfiguration
                        .extendsFrom
                        .map { getDependencyDefinedConfiguration(it) }

                    task.dependsOn(dependencyConfigurations.map { BibliothekarExtractTask.taskName(it) })

                    val dependencyProjects = variant.runtimeConfiguration
                        .extendsFrom
                        .flatMap { it.dependencies }
                        .filterIsInstance<ProjectDependency>()
                        .map { it.dependencyProject }
                    val dependencyProjectsWithVariant = DependencyProjectFinder.findProjectsWithVariant(this, variant)

                    dependencyProjects.forEach { dependencyProject ->
                        if (dependencyProject.plugins.hasPlugin(LibraryPlugin::class.java) ||
                            dependencyProject.plugins.hasPlugin(AppPlugin::class.java)
                        ) {
                            val variantName = dependencyProjectsWithVariant.find { it.project.path == dependencyProject.path }
                                ?.variantName
                                ?: throw BibliothekarException("cannot found dependency project's variant")
                            task.dependsOn(
                                "${dependencyProject.path}:${BibliothekarReportTask.taskName(variantName)}"
                            )
                        } else {
                            task.dependsOn("${dependencyProject.path}:${BibliothekarReportTask.taskName()}")
                        }
                    }
                }
            }
    }

    private fun Project.getDependencyDefinedConfiguration(configuration: Configuration): Configuration {
        if (configuration.isCanBeResolved) {
            return configuration
        }
        return configurations.findByName("${configuration.name}DependenciesMetadata")
            ?: throw BibliothekarException("cannot find resolvable configuration of ${configuration.name}")
    }
}
