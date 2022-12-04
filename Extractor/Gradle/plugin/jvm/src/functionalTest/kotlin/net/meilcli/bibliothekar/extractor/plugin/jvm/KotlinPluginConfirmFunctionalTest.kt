package net.meilcli.bibliothekar.extractor.plugin.jvm

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class KotlinPluginConfirmFunctionalTest {

    class TestSingleProject {

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
            runner.withArguments("bibliothekarConfirm")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("bibliothekarReport"), result.output)
            assertTrue(result.output.contains("implementationDependenciesMetadataBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("runtimeOnlyDependenciesMetadataBibliothekarExtract"), result.output)
        }
    }

    class TestDualProject {

        @get:Rule
        val tempFolder = TemporaryFolder()

        private fun getProjectDir() = tempFolder.root
        private fun getSettingsFile() = getProjectDir().resolve("settings.gradle")
        private fun getProject1Dir() = getProjectDir().resolve("parent")
        private fun getProject1BuildFile() = getProjectDir().resolve("parent/build.gradle")
        private fun getProject2Dir() = getProjectDir().resolve("child")
        private fun getProject2BuildFile() = getProjectDir().resolve("child/build.gradle")

        @Test
        fun test() {
            getSettingsFile().writeText(
                """
                    include(':parent')
                    include(':child')
                """.trimIndent()
            )
            getProject1Dir().mkdirs()
            getProject1BuildFile().writeText(
                """
                    plugins {
                        id('org.jetbrains.kotlin.jvm') version '1.7.20'
                        id('net.meilcli.bibliothekar.extractor.plugin.jvm')
                    }
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                        implementation project(':child')
                    }
                """.trimIndent()
            )
            getProject2Dir().mkdirs()
            getProject2BuildFile().writeText(
                """
                    plugins {
                        id('org.jetbrains.kotlin.jvm') version '1.7.20'
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
            runner.withArguments(":parent:bibliothekarConfirm")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            val expectParent = """
                :parent
                bibliothekarReport
                implementationDependenciesMetadataBibliothekarExtract
                runtimeOnlyDependenciesMetadataBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            val expectChild = """
                :parent
                bibliothekarReport
                implementationDependenciesMetadataBibliothekarExtract
                runtimeOnlyDependenciesMetadataBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
            assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
        }
    }
}
