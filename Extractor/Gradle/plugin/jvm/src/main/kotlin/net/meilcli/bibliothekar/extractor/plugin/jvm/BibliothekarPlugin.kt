package net.meilcli.bibliothekar.extractor.plugin.jvm

import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarExtractTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPlugin
import org.slf4j.LoggerFactory

class BibliothekarPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(BibliothekarPlugin::class.java)

    override fun apply(project: Project) {
        project.setUpExtractTask()

        if (project.plugins.hasPlugin(JavaPlugin::class.java)) {
            setupDump(project)
        } else {
            project.plugins.whenPluginAdded {
                if (it is JavaPlugin) {
                    setupDump(project)
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

    private fun setupDump(project: Project) {
        project.configurations
            .findByName(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME)
            ?.also { runtimeElementsConfiguration ->
                val dependencyConfigurations = runtimeElementsConfiguration.extendsFrom
                    .map { project.getDependencyDefinedConfiguration(it) }
                project.tasks
                    .register("bibliothekar", BibliothekarTask::class.java) { task ->
                        task.group = "dump"
                        task.dependsOn(dependencyConfigurations.map { BibliothekarExtractTask.taskName(it) })
                    }
            }
        project.afterEvaluate {
            project.configurations
                .filter { it.name == JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME }
                .forEach { runtimeElementsConfiguration ->
                    val dependencyProjects = runtimeElementsConfiguration.extendsFrom
                        .flatMap { it.dependencies }
                        .filterIsInstance<ProjectDependency>()
                        .map { it.dependencyProject }
                    dependencyProjects.forEach { dependencyProject ->
                        project.tasks
                            .findByName("bibliothekar")
                            ?.apply {
                                dependsOn("${dependencyProject.path}:bibliothekar")
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
