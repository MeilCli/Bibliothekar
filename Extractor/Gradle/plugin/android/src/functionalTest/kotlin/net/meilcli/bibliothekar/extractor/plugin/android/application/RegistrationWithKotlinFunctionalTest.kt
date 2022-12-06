package net.meilcli.bibliothekar.extractor.plugin.android.application

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class RegistrationWithKotlinFunctionalTest {

    class TestBibliothekarExtractTaskRegistration {

        @get:Rule
        val temporaryFolder = TemporaryFolder()

        @Test
        fun test() {
            val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
            testingProject.setup()
            testingProject.writeProject1BuildGradle(
                """
                    plugins {
                        id('com.android.application')
                        id('org.jetbrains.kotlin.android') version '1.7.20'
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
            runner.withArguments(testingProject.getProject1TaskName("tasks"))
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertTrue(result.output.contains("includingTestBibliothekarExtract - extract includingTest dependencies"), result.output)
            assertFalse(result.output.contains("excludingTestBibliothekarExtract - extract excludingTest dependencies"), result.output)
        }
    }

    class TestBibliothekarReportTaskRegistration {

        @get:Rule
        val temporaryFolder = TemporaryFolder()

        @Test
        fun test() {
            val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
            testingProject.setup()
            testingProject.writeProject1BuildGradle(
                """
                    plugins {
                        id('com.android.application')
                        id('org.jetbrains.kotlin.android') version '1.7.20'
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
            runner.withArguments(testingProject.getProject1TaskName("tasks"))
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
        }
    }

    class TestBibliothekarConfirmTaskRegistration {

        @get:Rule
        val temporaryFolder = TemporaryFolder()

        @Test
        fun test() {
            val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
            testingProject.setup()
            testingProject.writeProject1BuildGradle(
                """
                    plugins {
                        id('com.android.application')
                        id('org.jetbrains.kotlin.android') version '1.7.20'
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
            runner.withArguments(testingProject.getProject1TaskName("tasks"))
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarConfirm"), result.output)
            assertTrue(result.output.contains("releaseBibliothekarConfirm"), result.output)
        }
    }
}
