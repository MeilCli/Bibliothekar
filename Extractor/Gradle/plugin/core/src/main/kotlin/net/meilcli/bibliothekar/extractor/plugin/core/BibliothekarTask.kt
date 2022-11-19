package net.meilcli.bibliothekar.extractor.plugin.core

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

open class BibliothekarTask : DefaultTask() {

    private val logger = LoggerFactory.getLogger(BibliothekarTask::class.java)

    @TaskAction
    fun action() {

    }
}