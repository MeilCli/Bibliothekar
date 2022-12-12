package net.meilcli.bibliothekar.extractor.plugin.bundled.apply

import net.meilcli.bibliothekar.extractor.plugin.bundled.TestingProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

class JavaApplyFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun testJavaPlugin() {
        val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('java')
                    id('net.meilcli.bibliothekar.extractor.plugin.bundled')
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

        assertTrue(result.output.contains("net.meilcli.bibliothekar.extractor.plugin.jvm.BibliothekarPlugin"), result.output)
    }
}
