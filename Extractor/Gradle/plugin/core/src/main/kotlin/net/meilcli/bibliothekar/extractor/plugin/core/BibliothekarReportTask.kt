package net.meilcli.bibliothekar.extractor.plugin.core

import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonSerializer
import net.meilcli.bibliothekar.extractor.plugin.core.pom.PomReporter
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

open class BibliothekarReportTask : DefaultTask() {

    companion object {

        @Suppress("detekt.FunctionOnlyReturningConstant")
        fun taskName(): String {
            return "bibliothekarReport"
        }

        fun taskName(variantName: String): String {
            return "${variantName}BibliothekarReport"
        }
    }

    private val logger = LoggerFactory.getLogger(BibliothekarReportTask::class.java)

    private val reporter = PomReporter(PomJsonSerializer, PomJsonDeserializer)

    @OutputDirectory
    var outputDirectory: File? = null
        private set

    fun setup(project: Project) {
        this.group = TaskConstants.bibliothekarGroup
        this.outputDirectory = project.buildDir
            .toPath()
            .resolve(Paths.get("output/bibliothekar/report/${name}"))
            .toFile()
    }

    @TaskAction
    fun action() {
        val outputDirectory = outputDirectory ?: throw BibliothekarException("outputDirectory must be initialized")

        if (outputDirectory.exists().not()) {
            outputDirectory.mkdirs()
        } else {
            outputDirectory.deleteRecursively()
            outputDirectory.mkdirs()
        }

        val dependencyTasks = taskDependencies.getDependencies(this)
        val bibliothekarTasks = dependencyTasks.filter {
            it.group == TaskConstants.bibliothekarGroup || it.group == TaskConstants.bibliothekarExtractGroup
        }

        for (bibliothekarTask in bibliothekarTasks) {
            for (output in bibliothekarTask.outputs.files) {
                reporter.readCache(output)
            }
        }

        reporter.report(outputDirectory)

        logger.warn("report to ${outputDirectory.absolutePath}")
    }
}
