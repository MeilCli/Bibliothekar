package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.xml.IPomXmlParser
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact
import org.slf4j.LoggerFactory
import java.io.File

class GradlePomReader(
    private val project: Project,
    private val pomXmlParser: IPomXmlParser
) : IPomReader {

    private val logger = LoggerFactory.getLogger(GradlePomReader::class.java)

    override fun read(dependency: Dependency): Pom? {
        val pomFile = dependency.getPomFile(project) ?: return null
        return pomFile.inputStream().use {
            pomXmlParser.parse(it)
        }
    }

    private fun Dependency.getPomFile(project: Project): File? {
        val moduleComponentIdentifier = DefaultModuleComponentIdentifier(
            DefaultModuleIdentifier.newId(group, name),
            version
        )
        val components = project.dependencies
            .createArtifactResolutionQuery()
            .forComponents(moduleComponentIdentifier)
            .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
            .execute()
        if (components.resolvedComponents.isEmpty()) {
            logger.warn("$moduleComponentIdentifier has no PomFile")
            return null
        }

        val artifacts = components.resolvedComponents.first().getArtifacts(MavenPomArtifact::class.java)
        if (artifacts.isEmpty()) {
            logger.warn("$moduleComponentIdentifier not handle ${artifacts.first().javaClass}")
            return null
        }

        val artifact = artifacts.first()
        if (artifact !is ResolvedArtifactResult) {
            logger.warn("$moduleComponentIdentifier not handle ${artifacts.first().javaClass}")
            return null
        }
        return artifact.file
    }
}
