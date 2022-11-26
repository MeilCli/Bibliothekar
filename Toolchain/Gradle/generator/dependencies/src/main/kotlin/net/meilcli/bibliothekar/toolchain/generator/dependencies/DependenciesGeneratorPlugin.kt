package net.meilcli.bibliothekar.toolchain.generator.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File

class DependenciesGeneratorPlugin : Plugin<Project> {

    companion object {

        private const val packageName = "net.meilcli.bibliothekar.config"
        private const val fileName = "Dependencies.kt"
        private const val libraryConfigurationName = "library"
    }

    data class Definition(
        val group: String,
        val name: String,
        val version: String
    ) {

        companion object {

            private val definitionSeparator = Regex("[.-]")
        }

        fun groupToObjectName(): String {
            if (group.isEmpty()) {
                return "UnknownGroup"
            }
            return group.split(definitionSeparator).joinToString(separator = "") { it.capitalized() }
        }

        fun nameToMemberName(): String {
            return name.split(definitionSeparator).joinToString(separator = "") { it.capitalized() }
        }

        fun toValue(): String {
            val sb = StringBuilder()
            if (group.isNotEmpty()) {
                sb.append(group)
                sb.append(':')
            }
            sb.append(name)
            if (version.isNotEmpty()) {
                sb.append(':')
                sb.append(version)
            }
            return sb.toString()
        }
    }

    override fun apply(project: Project) {
        val outputDirectory = project.file("${project.buildDir}/generated/source/dependencies/main/net/meilcli/bibliothekar/config")
        if (outputDirectory.exists().not()) {
            outputDirectory.mkdirs()
        }

        project.configureOutputDirectoryAsSourceSets(outputDirectory)

        project.configurations.create(libraryConfigurationName)

        project.afterEvaluate {
            val definitions = project.getDefinitions()

            val file = File(outputDirectory, fileName)
            if (file.exists().not()) {
                file.createNewFile()
            }

            file.writeText(makeFileContent(definitions, outputDirectory))
        }
    }

    private fun Project.configureOutputDirectoryAsSourceSets(outputDirectory: File) {
        extensions
            .findByType(KotlinProjectExtension::class.java)
            ?.apply {
                sourceSets.findByName("main")?.kotlin?.srcDir(outputDirectory)
            }
    }

    private fun Project.getDefinitions(): List<Definition> {
        return configurations
            .findByName(libraryConfigurationName)
            ?.dependencies
            ?.map { Definition(group = it.group ?: "", name = it.name, version = it.version ?: "") }
            .orEmpty()
    }

    private fun makeFileContent(definitions: List<Definition>, outputDirectory: File): String {
        val groups = definitions.groupBy { it.groupToObjectName() }

        var result = ""

        result += "package $packageName\n"
        result += "\n"
        result += "object Dependencies {\n"

        for (group in groups) {
            result += "    object ${group.key} {\n"
            for (definition in group.value) {
                result += "        const val ${definition.nameToMemberName()} = \"${definition.toValue()}\"\n"
            }
            result += "    }\n"
        }

        result += "\n"
        result += "    const val sourceSetsPath = \"${outputDirectory.absolutePath.replace("\\", "\\\\")}\"\n"

        result += "}\n"

        return result
    }
}
