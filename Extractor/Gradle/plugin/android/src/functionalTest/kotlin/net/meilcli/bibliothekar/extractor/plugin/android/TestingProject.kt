package net.meilcli.bibliothekar.extractor.plugin.android

import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import java.io.File

sealed class TestingProject(
    protected val root: File
) {

    class SingleChildModule(
        root: File
    ) : TestingProject(root) {

        private val settingGradle = root.resolve("settings.gradle")
        private val buildGradle = root.resolve("build.gradle")
        private val project1 = root.resolve("project1")
        private val project1BuildGradle = root.resolve("project1/build.gradle")

        override fun setup() {
            settingGradle.writeText(
                """
                    include(':project1')
                """.trimIndent()
            )
            buildGradle.writeText(
                """
                    buildscript {
                        repositories {
                            google()
                            mavenCentral()
                        }
                        dependencies {
                            classpath 'com.android.tools.build:gradle:7.3.1'
                            ${pluginPaths().joinToString(separator = "\n") { "classpath files('$it')" }}
                        }
                    }
                """.trimIndent()
            )
            project1.mkdir()
        }

        fun writeProject1BuildGradle(text: String) {
            project1BuildGradle.writeText(text)
        }

        fun getProject1TaskName(taskName: String): String {
            return ":project1:$taskName"
        }
    }

    class DualChildModule(
        root: File
    ) : TestingProject(root) {

        private val settingGradle = root.resolve("settings.gradle")
        private val buildGradle = root.resolve("build.gradle")
        private val project1 = root.resolve("project1")
        private val project1BuildGradle = root.resolve("project1/build.gradle")
        private val project2 = root.resolve("project2")
        private val project2BuildGradle = root.resolve("project2/build.gradle")

        override fun setup() {
            settingGradle.writeText(
                """
                    include(':project1')
                    include(':project2')
                """.trimIndent()
            )
            buildGradle.writeText(
                """
                    buildscript {
                        repositories {
                            google()
                            mavenCentral()
                        }
                        dependencies {
                            classpath 'com.android.tools.build:gradle:7.3.1'
                            ${pluginPaths().joinToString(separator = "\n") { "classpath files('$it')" }}
                        }
                    }
                """.trimIndent()
            )
            project1.mkdir()
            project2.mkdir()
        }

        fun writeProject1BuildGradle(text: String) {
            project1BuildGradle.writeText(text)
        }

        fun writeProject2BuildGradle(text: String) {
            project2BuildGradle.writeText(text)
        }

        @Suppress("detekt.FunctionOnlyReturningConstant")
        fun getProject1Name(): String {
            return ":project1"
        }

        @Suppress("detekt.FunctionOnlyReturningConstant")
        fun getProject2Name(): String {
            return ":project2"
        }

        fun getProject1TaskName(taskName: String): String {
            return ":project1:$taskName"
        }
    }

    abstract fun setup()

    // ref: https://github.com/gradle/gradle/issues/22466
    // if using GradleRunner.withPluginClasspath, project will be used isolated ClassLoader
    protected fun pluginPaths(): List<String> {
        return PluginUnderTestMetadataReading.readImplementationClasspath()
            .map { it.absolutePath }
            .filter { it.contains("Bibliothekar") || it.contains("kotlinx") }
            .map { it.replace("\\", "\\\\") }
    }

    fun getProjectDirectory(): File {
        return root
    }
}
