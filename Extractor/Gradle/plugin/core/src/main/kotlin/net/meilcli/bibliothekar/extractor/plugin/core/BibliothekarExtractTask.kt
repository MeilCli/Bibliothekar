package net.meilcli.bibliothekar.extractor.plugin.core

import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.DependencyLoader
import net.meilcli.bibliothekar.extractor.plugin.core.dependencies.IDependencyLoader
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonSerializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.xml.PomXmlParser
import net.meilcli.bibliothekar.extractor.plugin.core.pom.*
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

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
        ParentResolvingPomReader(
            CompositeCachingPomReader(
                gradlePomReader = GradlePomReader(project, PomXmlParser),
                inMemoryPomCache = inMemoryPomCache,
                filePomCache = FilePomCache(checkNotNull(cacheDirectory), PomJsonSerializer, PomJsonDeserializer)
            )
        )
    }

    private val outputPomWriter: IPomWriter by lazy {
        FilePomCache(checkNotNull(outputDirectory), PomJsonSerializer, PomJsonDeserializer)
    }

    private var configuration: Configuration? = null
    private var cacheDirectory: File? = null

    @OutputDirectory
    var outputDirectory: File? = null
        private set

    fun setup(rootProject: Project, project: Project, configuration: Configuration) {
        this.group = "bibliothekar_extract"
        this.description = "extract ${configuration.name} dependencies"
        this.configuration = configuration
        this.cacheDirectory = rootProject.buildDir
            .toPath()
            .resolve(Paths.get("cache/bibliothekar/maven"))
            .toFile()
        this.outputDirectory = project.buildDir
            .toPath()
            .resolve(Paths.get("output/bibliothekar/extract/${configuration.name}"))
            .toFile()
    }

    @TaskAction
    fun action() {
        val configuration = configuration ?: throw BibliothekarException("configuration must be initialized")
        val cacheDirectory = cacheDirectory ?: throw BibliothekarException("cacheDirectory must be initialized")
        val outputDirectory = outputDirectory ?: throw BibliothekarException("outputDirectory must be initialized")

        if (cacheDirectory.exists().not()) {
            cacheDirectory.mkdirs()
        }

        if (outputDirectory.exists().not()) {
            outputDirectory.mkdirs()
        } else {
            outputDirectory.deleteRecursively()
            outputDirectory.mkdirs()
        }

        val dependencies = dependencyLoader.load(configuration)
        val poms = dependencies.mapNotNull { pomReader.read(it) }

        poms.forEach {
            outputPomWriter.write(it)
        }

        logger.info("write ${poms.size} outputs to ${outputDirectory.absolutePath}")
    }
}
