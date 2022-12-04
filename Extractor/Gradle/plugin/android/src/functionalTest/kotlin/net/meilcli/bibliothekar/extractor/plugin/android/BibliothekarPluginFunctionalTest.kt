package net.meilcli.bibliothekar.extractor.plugin.android

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class BibliothekarPluginFunctionalTest {

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
                        id('net.meilcli.bibliothekar.extractor.plugin.android')
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

            assertTrue(result.output.contains("includingTestBibliothekarExtract - extract includingTest dependencies"))
            assertFalse(result.output.contains("excludingTestBibliothekarExtract - extract excludingTest dependencies"))
        }
    }
}
