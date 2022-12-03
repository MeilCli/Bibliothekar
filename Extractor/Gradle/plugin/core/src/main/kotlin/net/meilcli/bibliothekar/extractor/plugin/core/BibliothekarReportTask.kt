package net.meilcli.bibliothekar.extractor.plugin.core

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

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

    fun setup() {
        this.group = "bibliothekar"
    }

    @TaskAction
    fun action() {
    }
}
