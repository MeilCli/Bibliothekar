package net.meilcli.bibliothekar.extractor.plugin.core

import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.DependencyLoader
import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.IDependencyLoader
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact
import org.slf4j.LoggerFactory

// temp file
open class BibliothekarExtractTask : DefaultTask() {

    companion object {

        fun taskName(configuration: Configuration): String {
            return "${configuration.name}BibliothekarExtract"
        }
    }

    private val logger = LoggerFactory.getLogger(BibliothekarExtractTask::class.java)

    private val dependencyLoader: IDependencyLoader = DependencyLoader()

    private var configuration: Configuration? = null

    fun setup(configuration: Configuration) {
        this.group = "bibliothekar_extract"
        this.description = "extract ${configuration.name} dependencies"
        this.configuration = configuration
    }

    @TaskAction
    fun action() {
        val configuration = configuration ?: throw BibliothekarException("configuration must be initialized")

        logger.warn("hello DumpTask")

        dependencyLoader.load(configuration)
            .forEach {
                logger.warn(it.toString())
            }

        configuration.resolvedConfiguration
            .lenientConfiguration
            .firstLevelModuleDependencies
            .forEach { resolvedDependency ->
                resolvedDependency.allModuleArtifacts
                    .forEach { resolvedArtifact ->
                        val id = resolvedArtifact.moduleVersion.id
                        logger.warn("${id.group}:${id.name}:${id.version} ${resolvedArtifact.file}")

                        val moduleComponentIdentifier = DefaultModuleComponentIdentifier(
                            DefaultModuleIdentifier.newId(id.group, id.name),
                            id.version
                        )

                        val components = project.dependencies
                            .createArtifactResolutionQuery()
                            .forComponents(moduleComponentIdentifier)
                            .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
                            .execute()
                        if (components.resolvedComponents.isEmpty()) {
                            logger.warn("$moduleComponentIdentifier has no PomFile")
                            return
                        }

                        val artifacts =
                            components.resolvedComponents.first().getArtifacts(MavenPomArtifact::class.java)
                        if (artifacts.isEmpty()) {
                            logger.warn("$moduleComponentIdentifier not handle ${artifacts.first().javaClass}")
                            return
                        }

                        val artifact = artifacts.first()
                        if (artifact !is ResolvedArtifactResult) {
                            logger.warn("$moduleComponentIdentifier not handle ${artifacts.first().javaClass}")
                            return
                        }
                    }
            }
    }
}
