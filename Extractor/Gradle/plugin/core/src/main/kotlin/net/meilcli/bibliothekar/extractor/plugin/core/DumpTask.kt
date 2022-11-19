package net.meilcli.bibliothekar.extractor.plugin.core

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
open class DumpTask : DefaultTask() {

    private val logger = LoggerFactory.getLogger(DumpTask::class.java)

    private var configuration: Configuration? = null

    fun setup(configuration: Configuration) {
        this.configuration = configuration
    }

    @TaskAction
    fun action() {
        val configuration = configuration ?: throw IllegalStateException("configuration must be initialized")

        logger.warn("hello DumpTask")

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

                        logger.warn("file: ${artifact.file}")
                    }
            }
    }
}