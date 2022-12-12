package net.meilcli.bibliothekar.extractor.plugin.bundled.apply

import net.meilcli.bibliothekar.extractor.plugin.bundled.TestingProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

class AndroidApplyFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun testJavaPlugin() {
        val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('com.android.application')
                    id('net.meilcli.bibliothekar.extractor.plugin.bundled')
                }
                android {
                    compileSdkVersion 32
                    defaultConfig {
                        minSdkVersion 21
                        targetSdkVersion 32
                    }
                    namespace 'net.meilcli.bibliothekar.test'
                }
                task showPlugins() {
                    project.plugins.each {
                       println it.class.canonicalName
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("showPlugins"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        assertTrue(result.output.contains("net.meilcli.bibliothekar.extractor.plugin.android.BibliothekarPlugin"), result.output)
    }

    @Test
    fun testKotlinPlugin1() {
        val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('com.android.application')
                    id('org.jetbrains.kotlin.android') version '1.7.20'
                    id('net.meilcli.bibliothekar.extractor.plugin.bundled')
                }
                android {
                    compileSdkVersion 32
                    defaultConfig {
                        minSdkVersion 21
                        targetSdkVersion 32
                    }
                    namespace 'net.meilcli.bibliothekar.test'
                }
                task showPlugins() {
                    project.plugins.each {
                       println it.class.canonicalName
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("showPlugins"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        assertTrue(result.output.contains("net.meilcli.bibliothekar.extractor.plugin.android.BibliothekarPlugin"), result.output)
    }

    @Test
    fun testKotlinPlugin2() {
        val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('org.jetbrains.kotlin.android') version '1.7.20'
                    id('com.android.application')
                    id('net.meilcli.bibliothekar.extractor.plugin.bundled')
                }
                android {
                    compileSdkVersion 32
                    defaultConfig {
                        minSdkVersion 21
                        targetSdkVersion 32
                    }
                    namespace 'net.meilcli.bibliothekar.test'
                }
                task showPlugins() {
                    project.plugins.each {
                       println it.class.canonicalName
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("showPlugins"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        assertTrue(result.output.contains("net.meilcli.bibliothekar.extractor.plugin.android.BibliothekarPlugin"), result.output)
    }
}
