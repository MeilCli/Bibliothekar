package net.meilcli.bibliothekar.extractor.plugin.jvm

import kotlin.test.assertTrue
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class BibliothekarPluginFunctionalTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun getProjectDir() = tempFolder.root
    private fun getBuildFile() = getProjectDir().resolve("build.gradle")
    private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")

    @Test fun `can run task`() {
        getSettingsFile().writeText("")
        getBuildFile().writeText("""
plugins {
    id('net.meilcli.bibliothekar.extractor.plugin.jvm')
}
""")

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("greeting")
        runner.withProjectDir(getProjectDir())
        val result = runner.build();

        assertTrue(result.output.contains("Hello from plugin 'net.meilcli.bibliothekar.extractor.plugin.jvm'"))
    }
}
