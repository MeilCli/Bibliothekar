package net.meilcli.bibliothekar.extractor.plugin.core

import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.DependencyLoader
import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.IDependencyLoader
import net.meilcli.bibliothekar.extractor.plugin.core.entities.xml.PomXmlParser
import net.meilcli.bibliothekar.extractor.plugin.core.pom.*
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

// temp file
open class BibliothekarExtractTask : DefaultTask() {

    companion object {

        private val dependencyLoader: IDependencyLoader = DependencyLoader()
        private val inMemoryPomCache: IPomCache = InMemoryPomCache()

        fun taskName(configuration: Configuration): String {
            return "${configuration.name}BibliothekarExtract"
        }
    }

    private val logger = LoggerFactory.getLogger(BibliothekarExtractTask::class.java)

    private val pomReader: IPomReader by lazy {
        CompositeCachingPomReader(
            gradlePomReader = GradlePomReader(project, PomXmlParser),
            inMemoryPomCache = inMemoryPomCache
        )
    }

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

        val dependencies = dependencyLoader.load(configuration)
        val poms = dependencies.mapNotNull { pomReader.read(it) }

        poms.forEach {
            logger.warn("${it.group}:${it.artifact}:${it.version} is ${it.licenses.joinToString { license -> license.name ?: "" }}")
        }
    }
}
