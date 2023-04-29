package net.meilcli.bibliothekar.extractor.plugin.core

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

open class BibliothekarConfirmTask : DefaultTask() {

    companion object {

        @Suppress("detekt.FunctionOnlyReturningConstant")
        fun taskName(): String {
            return "bibliothekarConfirm"
        }

        fun taskName(variantName: String): String {
            return "${variantName}BibliothekarConfirm"
        }
    }

    private val logger = LoggerFactory.getLogger(BibliothekarConfirmTask::class.java)

    private var variantName: String? = null

    fun setup(variantName: String?) {
        this.group = TaskConstants.bibliothekarGroup
        this.description = "Dump resolving dependencies"
        this.variantName = variantName
    }

    @TaskAction
    fun action() {
        val variantName = variantName
        val reportTask = if (variantName != null) {
            project.tasks.findByName(BibliothekarReportTask.taskName(variantName))
        } else {
            project.tasks.findByName(BibliothekarReportTask.taskName())
        }

        checkNotNull(reportTask) {
            "did not register reportTask"
        }

        val dependencyTasks = reportTask.findAllDependencyTasks()
        val projectAndTasks = dependencyTasks.groupBy { it.project.path }

        logger.warn("checking projects:")
        for (projectTasks in projectAndTasks) {
            logger.warn(projectTasks.key)
            projectTasks.value
                .filter { it.group == TaskConstants.bibliothekarGroup }
                .forEach {
                    logger.warn(it.name)
                }
            projectTasks.value
                .filter { it.group == TaskConstants.bibliothekarExtractGroup }
                .forEach {
                    logger.warn(it.name)
                }
            logger.warn("")
        }
    }

    private fun Task.findAllDependencyTasks(): Set<Task> {
        val dependencyTasks = getDependencyTasks()
        if (dependencyTasks.isEmpty()) {
            return setOf(this)
        }
        return dependencyTasks.flatMap { it.findAllDependencyTasks() }.toSet() + this
    }

    private fun Task.getDependencyTasks(): Set<Task> {
        return taskDependencies.getDependencies(this)
    }
}
