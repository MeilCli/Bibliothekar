package net.meilcli.bibliothekar.extractor.plugin.android

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class AndroidDynamicFeaturePluginConfirmFunctionalTest {

    class TestSingleProject {

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
                        id('com.android.dynamic-feature') version '7.3.1'
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
            runner.withArguments("debugBibliothekarConfirm")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
            assertTrue(result.output.contains("implementationDependenciesListBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("debugImplementationDependenciesListBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("runtimeOnlyDependenciesListBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("debugRuntimeOnlyDependenciesListBibliothekarExtract"), result.output)
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

        @Suppress("detekt.LongMethod")
        @Test
        fun test() {
            getSettingsFile().writeAndroidSettingText(
                """
                    include(':parent')
                    include(':child')
                """.trimIndent()
            )
            getProject1Dir().mkdirs()
            getProject1BuildFile().writeText(
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
                        dynamicFeatures = [':child']
                    }
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                    }
                """.trimIndent()
            )
            getProject2Dir().mkdirs()
            getProject2BuildFile().writeText(
                """
                    plugins {
                        id('com.android.dynamic-feature') version '7.3.1'
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
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                        implementation project(':parent')
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withPluginClasspath()
            runner.withArguments(":parent:debugBibliothekarConfirm")
            runner.withProjectDir(getProjectDir())
            val result = runner.build()

            val expectParent = """
                :parent
                debugBibliothekarReport
                debugRuntimeOnlyDependenciesListBibliothekarExtract
                debugImplementationDependenciesListBibliothekarExtract
                runtimeOnlyDependenciesListBibliothekarExtract
                implementationDependenciesListBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            val expectChild = """
                :parent
                debugBibliothekarReport
                debugRuntimeOnlyDependenciesListBibliothekarExtract
                debugImplementationDependenciesListBibliothekarExtract
                runtimeOnlyDependenciesListBibliothekarExtract
                implementationDependenciesListBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
            assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
        }
    }
}
