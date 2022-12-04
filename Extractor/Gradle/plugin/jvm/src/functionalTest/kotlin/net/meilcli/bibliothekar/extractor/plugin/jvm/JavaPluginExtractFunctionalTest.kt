package net.meilcli.bibliothekar.extractor.plugin.jvm

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class JavaPluginExtractFunctionalTest {

    class TestExtract {

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
                        id('java')
                        id('net.meilcli.bibliothekar.extractor.plugin.jvm')
                    }
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments("implementationDependenciesListBibliothekarExtract", "--info")
            runner.withDebug(true)
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertNotNull("write\\s[1-9]\\soutputs\\sto".toRegex().find(result.output))
        }
    }
}
