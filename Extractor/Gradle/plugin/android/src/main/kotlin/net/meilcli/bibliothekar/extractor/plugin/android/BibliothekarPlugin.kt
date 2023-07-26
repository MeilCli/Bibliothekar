package net.meilcli.bibliothekar.extractor.plugin.android

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.DynamicFeaturePlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarConfirmTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarExtractTask
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarReportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.slf4j.LoggerFactory

@Suppress("unused")
class BibliothekarPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(BibliothekarPlugin::class.java)
    private fun dependencyListConfigurationName(configuration: Configuration): String {
        return "${configuration.name}DependenciesList"
    }

    override fun apply(project: Project) {
        if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            project.setUpConfigurations()
            project.setUpTasks()
        } else {
            project.plugins.whenPluginAdded {
                if (it is LibraryPlugin) {
                    project.setUpConfigurations()
                    project.setUpTasks()
                }
            }
        }
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            project.setUpConfigurations()
            project.setUpTasks()
        } else {
            project.plugins.whenPluginAdded {
                if (it is AppPlugin) {
                    project.setUpConfigurations()
                    project.setUpTasks()
                }
            }
        }
        if (project.plugins.hasPlugin(DynamicFeaturePlugin::class.java)) {
            project.setUpConfigurations()
            project.setUpTasks()
        } else {
            project.plugins.whenPluginAdded {
                if (it is DynamicFeaturePlugin) {
                    project.setUpConfigurations()
                    project.setUpTasks()
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.setUpConfigurations() {
        project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?.onVariants { variant ->
                variant.runtimeConfiguration
                    .extendsFrom
                    //.filter { it.isCanBeResolved.not() }
                    .forEach {
                        if (configurations.findByName(dependencyListConfigurationName(it)) != null) {
                            return@forEach
                        }
                        configurations.create(dependencyListConfigurationName(it)).apply {
                            it.dependencies.forEach { dependency ->
                                if (dependency !is ProjectDependency) {
                                    dependencies.add(dependency)
                                }
                            }
                            attributes {
                                it.attribute(
                                    Usage.USAGE_ATTRIBUTE,
                                    objects.named(Usage::class.java, Usage.JAVA_RUNTIME)
                                )
                                it.attribute(
                                    Category.CATEGORY_ATTRIBUTE,
                                    objects.named(Category::class.java, Category.LIBRARY)
                                )
                                it.attribute(
                                    KotlinPlatformType.attribute,
                                    KotlinPlatformType.androidJvm
                                )
                            }
                            isCanBeResolved = true
                        }
                    }
            }
    }

    private fun Project.setUpTasks() {
        setUpExtractTask()
        setUpReportTask()
        setUpConfirmTask()
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
                    logger.warn("registerrrr")
                    task.setup(this)
                    logger.warn("setupped")

                    val dependencyConfigurations = variant.runtimeConfiguration
                        .extendsFrom
                        .mapNotNull { getDependencyDefinedConfiguration(it) }
                    logger.warn("dependencyConfigurations")

                    task.dependsOn(dependencyConfigurations.map { BibliothekarExtractTask.taskName(it) })

                    // android dynamic feature module
                    val reversedDependencyProjects = configurations.findByName("${variant.name}ReverseMetadataValues")
                        ?.dependencies
                        ?.filterIsInstance<ProjectDependency>()
                        ?.map { it.dependencyProject }
                        .orEmpty()
                    val runtimeDependencyProjects = variant.runtimeConfiguration
                        .extendsFrom
                        .flatMap { it.dependencies }
                        .filterIsInstance<ProjectDependency>()
                        .map { it.dependencyProject }
                    val dependencyProjects = runtimeDependencyProjects + reversedDependencyProjects
                    val dependencyProjectsWithVariant = DependencyProjectFinder.findProjectsWithVariant(this, variant)

                    dependencyProjects.forEach { dependencyProject ->
                        if (dependencyProject.plugins.hasPlugin(LibraryPlugin::class.java) ||
                            dependencyProject.plugins.hasPlugin(AppPlugin::class.java) ||
                            dependencyProject.plugins.hasPlugin(DynamicFeaturePlugin::class.java)
                        ) {
                            if (this.plugins.hasPlugin(DynamicFeaturePlugin::class.java) &&
                                dependencyProject.plugins.hasPlugin(LibraryPlugin::class.java).not()
                            ) {
                                // reversed dependency
                                return@forEach
                            }
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

    private fun Project.setUpConfirmTask() {
        project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?.onVariants { variant ->
                tasks.register(BibliothekarConfirmTask.taskName(variant.name), BibliothekarConfirmTask::class.java) { task ->
                    task.setup(variant.name)
                }
            }
    }

    private fun Project.getDependencyDefinedConfiguration(configuration: Configuration): Configuration? {
        if(configurations.findByName(dependencyListConfigurationName(configuration))!=null){
            return configurations.findByName(dependencyListConfigurationName(configuration))
        }else {
            logger.warn("resolvable ${configuration.name} configuration22222 cannot find")
            return null
        }
        if (configuration.isCanBeResolved) {
            return configuration
        }
        val result = configurations.findByName("${configuration.name}DependenciesMetadata") // defined by Kotlin Plugin
            ?: configurations.findByName(dependencyListConfigurationName(configuration))
        if (result == null) {
            logger.warn("resolvable ${configuration.name} configuration cannot find")
        }
        return result
    }
}
