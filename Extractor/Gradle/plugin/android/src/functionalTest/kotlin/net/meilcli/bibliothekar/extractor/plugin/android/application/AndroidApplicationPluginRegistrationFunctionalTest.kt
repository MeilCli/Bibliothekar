package net.meilcli.bibliothekar.extractor.plugin.android.application

import net.meilcli.bibliothekar.extractor.plugin.android.writeAndroidSettingText
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class AndroidApplicationPluginRegistrationFunctionalTest {

    class TestBibliothekarExtractTaskRegistration {

        @get:Rule
        val tempFolder = TemporaryFolder()

        private fun getProjectDir() = tempFolder.root
        private fun getBuildFile() = getProjectDir().resolve("build.gradle")
        private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

        @Test
        fun test() {
            getSettingsFile().writeAndroidSettingText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('com.android.application') version '7.3.1'
                        id('net.meilcli.bibliothekar.extractor.plugin.android')
                    }
                    android {
                        compileSdkVersion 32
                        defaultConfig {
                            minSdkVersion 21
                            targetSdkVersion 32
                        }
                        namespace 'net.meilcli.bibliothekar.test'
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
            getSettingsFile().writeAndroidSettingText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('com.android.application') version '7.3.1'
                        id('net.meilcli.bibliothekar.extractor.plugin.android')
                    }
                    android {
                        compileSdkVersion 32
                        defaultConfig {
                            minSdkVersion 21
                            targetSdkVersion 32
                        }
                        namespace 'net.meilcli.bibliothekar.test'
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("tasks")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
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
            getSettingsFile().writeAndroidSettingText("")
            getBuildFile().writeText(
                """
                    plugins {
                        id('com.android.application') version '7.3.1'
                        id('net.meilcli.bibliothekar.extractor.plugin.android')
                    }
                    android {
                        compileSdkVersion 32
                        defaultConfig {
                            minSdkVersion 21
                            targetSdkVersion 32
                        }
                        namespace 'net.meilcli.bibliothekar.test'
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("tasks")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarConfirm"), result.output)
            assertTrue(result.output.contains("releaseBibliothekarConfirm"), result.output)
        }
    }
}
