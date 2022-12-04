package net.meilcli.bibliothekar.extractor.plugin.jvm

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class KotlinPluginRegistrationFunctionalTest {

    class TestBibliothekarExtractTaskRegistration {

        @get:Rule
        val tempFolder = TemporaryFolder()

        private fun getProjectDir() = tempFolder.root
        private fun getBuildFile() = getProjectDir().resolve("build.gradle")
        private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

        @Test
        fun test() {
            getSettingsFile().writeText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('org.jetbrains.kotlin.jvm') version '1.7.20'
                        id('net.meilcli.bibliothekar.extractor.plugin.jvm')
                    }
                    configurations.create("includingTest").setCanBeResolved(true)
                    configurations.create("excludingTest").setCanBeResolved(false)
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("tasks")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("includingTestBibliothekarExtract - extract includingTest dependencies"), result.output)
            assertFalse(result.output.contains("excludingTestBibliothekarExtract - extract excludingTest dependencies"), result.output)
        }
    }

    class TestBibliothekarReportTaskRegistration {

        @get:Rule
        val tempFolder = TemporaryFolder()

        private fun getProjectDir() = tempFolder.root
        private fun getBuildFile() = getProjectDir().resolve("build.gradle")
        private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

        @Test
        fun test() {
            getSettingsFile().writeText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('org.jetbrains.kotlin.jvm') version '1.7.20'
                        id('net.meilcli.bibliothekar.extractor.plugin.jvm')
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("tasks")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("bibliothekarReport"), result.output)
        }
    }

    class TestBibliothekarConfirmTaskRegistration {

        @get:Rule
        val tempFolder = TemporaryFolder()

        private fun getProjectDir() = tempFolder.root
        private fun getBuildFile() = getProjectDir().resolve("build.gradle")
        private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

        @Test
        fun test() {
            getSettingsFile().writeText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('org.jetbrains.kotlin.jvm') version '1.7.20'
                        id('net.meilcli.bibliothekar.extractor.plugin.jvm')
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("tasks")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("bibliothekarConfirm"), result.output)
        }
    }
}
