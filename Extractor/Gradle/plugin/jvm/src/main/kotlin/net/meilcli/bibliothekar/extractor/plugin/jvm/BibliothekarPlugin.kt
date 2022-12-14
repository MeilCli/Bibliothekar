package net.meilcli.bibliothekar.extractor.plugin.jvm

import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarConfirmTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarExtractTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarReportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPlugin

class BibliothekarPlugin : Plugin<Project> {

    private fun dependencyListConfigurationName(configuration: Configuration): String {
        return "${configuration.name}DependenciesList"
    }

    override fun apply(project: Project) {
        if (project.plugins.hasPlugin(JavaPlugin::class.java)) {
            project.setUpConfigurations()
            project.setUpExtractTask()
            project.setUpReportTask()
            project.setUpConfirmTask()
        } else {
            project.plugins.whenPluginAdded {
                if (it is JavaPlugin) {
                    project.setUpConfigurations()
                    project.setUpExtractTask()
                    project.setUpReportTask()
                    project.setUpConfirmTask()
                }
            }
        }
    }

    private fun Project.setUpConfigurations() {
        afterEvaluate {
            configurations
                .findByName(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME)
                ?.also { runtimeElementsConfiguration ->
                    runtimeElementsConfiguration.extendsFrom
                        .filter { it.isCanBeResolved.not() }
                        .forEach {
                            configurations.create(dependencyListConfigurationName(it)).apply {
                                it.dependencies.forEach { dependency ->
                                    dependencies.add(dependency)
                                }
                                isCanBeResolved = true
                            }
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

    private fun Project.setUpReportTask() {
        afterEvaluate {
            configurations
                .findByName(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME)
                ?.also { runtimeElementsConfiguration ->
                    tasks.register(BibliothekarReportTask.taskName(), BibliothekarReportTask::class.java) { task ->
                        task.setup(this)

                        val dependencyConfigurations = runtimeElementsConfiguration.extendsFrom
                            .map { getDependencyDefinedConfiguration(it) }

                        task.dependsOn(dependencyConfigurations.map { BibliothekarExtractTask.taskName(it) })

                        val dependencyProjects = runtimeElementsConfiguration.extendsFrom
                            .flatMap { it.dependencies }
                            .filterIsInstance<ProjectDependency>()
                            .map { it.dependencyProject }
                        dependencyProjects.forEach { dependencyProject ->
                            task.dependsOn("${dependencyProject.path}:${BibliothekarReportTask.taskName()}")
                        }
                    }
                }
        }
    }

    private fun Project.setUpConfirmTask() {
        afterEvaluate {
            tasks.register(BibliothekarConfirmTask.taskName(), BibliothekarConfirmTask::class.java) { task ->
                task.setup(null)
            }
        }
    }

    private fun Project.getDependencyDefinedConfiguration(configuration: Configuration): Configuration {
        if (configuration.isCanBeResolved) {
            return configuration
        }
        return configurations.findByName("${configuration.name}DependenciesMetadata") // defined by Kotlin Plugin
            ?: configurations.findByName(dependencyListConfigurationName(configuration))
            ?: throw BibliothekarException("resolvable ${configuration.name} configuration cannot find")
    }
}
