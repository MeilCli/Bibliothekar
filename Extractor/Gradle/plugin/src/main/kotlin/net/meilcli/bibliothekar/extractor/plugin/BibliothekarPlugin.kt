package net.meilcli.bibliothekar.extractor.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class BibliothekarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("greeting") { task ->
            task.doLast {
                println("Hello from plugin 'net.meilcli.bibliothekar.extractor.plugin'")
            }
        }
        project.configurations.forEach { configuration ->
            if (configuration.isCanBeResolved) {
                project.tasks.register("${configuration.name}Dump", DumpTask::class.java) { task ->
                    task.group = "dump"
                    task.setup(configuration)
                }
            }
        }
    }
}
