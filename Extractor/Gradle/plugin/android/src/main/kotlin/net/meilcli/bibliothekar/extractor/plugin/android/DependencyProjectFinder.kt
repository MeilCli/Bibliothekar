package net.meilcli.bibliothekar.extractor.plugin.android

import com.android.build.api.variant.Variant
import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.configurationcache.extensions.capitalized

object DependencyProjectFinder {

    class ProjectWithVariant(
        val project: Project,
        val variantName: String
    )

    private val packageRenderscriptRegex = Regex("package(.+?)Renderscript")

    fun findProjectsWithVariant(sourceProject: Project, sourceVariant: Variant): List<ProjectWithVariant> {
        val taskName = "compile${sourceVariant.name.capitalized()}Renderscript"
        val rootTask = sourceProject.tasks.findByName(taskName) ?: throw BibliothekarException("cannot find $taskName")
        val childTasks = rootTask.getDependencyTasks()
        val grandChildTasks = childTasks.flatMap { it.getDependencyTasks() }
        val targetTasks = childTasks + grandChildTasks

        val result = mutableListOf<ProjectWithVariant>()
        for (targetTask in targetTasks) {
            val matchResult = packageRenderscriptRegex.find(targetTask.name) ?: continue
            if (2 <= matchResult.groupValues.size) {
                val variantName = "${matchResult.groupValues[1][0].lowercase()}${matchResult.groupValues[1].substring(1)}"
                result += ProjectWithVariant(targetTask.project, variantName)
            }
        }
        return result
    }

    private fun Task.getDependencyTasks(): Set<Task> {
        return taskDependencies.getDependencies(this)
    }
}
