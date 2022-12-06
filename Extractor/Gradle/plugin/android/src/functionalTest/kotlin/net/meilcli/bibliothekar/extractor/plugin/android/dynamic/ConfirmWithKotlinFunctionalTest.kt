package net.meilcli.bibliothekar.extractor.plugin.android.dynamic

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import net.meilcli.bibliothekar.extractor.plugin.android.unifyLineBreak
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class ConfirmWithKotlinFunctionalTest {

    class TestSingleProject {

        @get:Rule
        val temporaryFolder = TemporaryFolder()

        @Test
        fun test() {
            val testingProject = TestingProject.SingleChildModule(temporaryFolder.root)
            testingProject.setup()
            testingProject.writeProject1BuildGradle(
                """
                    plugins {
                        id('com.android.dynamic-feature')
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
            runner.withArguments(testingProject.getProject1TaskName("debugBibliothekarConfirm"))
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertTrue(result.output.contains("debugBibliothekarReport"), result.output)
            assertTrue(result.output.contains("implementationDependenciesMetadataBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("debugImplementationDependenciesMetadataBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("runtimeOnlyDependenciesMetadataBibliothekarExtract"), result.output)
            assertTrue(result.output.contains("debugRuntimeOnlyDependenciesMetadataBibliothekarExtract"), result.output)
        }
    }

    class TestDualProject {

        @get:Rule
        val temporaryFolder = TemporaryFolder()

        @Suppress("detekt.LongMethod")
        @Test
        fun test() {
            val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                        dynamicFeatures = ['${testingProject.getProject2Name()}']
                    }
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                    }
                """.trimIndent()
            )
            testingProject.writeProject2BuildGradle(
                """
                    plugins {
                        id('com.android.dynamic-feature')
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
                    repositories {
                        mavenCentral()
                    }
                    dependencies {
                        implementation 'junit:junit:4.13.2'
                        implementation project('${testingProject.getProject1Name()}')
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withArguments(testingProject.getProject1TaskName("debugBibliothekarConfirm"))
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            val expectParent = """
                ${testingProject.getProject1Name()}
                debugBibliothekarReport
                debugRuntimeOnlyDependenciesMetadataBibliothekarExtract
                debugImplementationDependenciesMetadataBibliothekarExtract
                runtimeOnlyDependenciesMetadataBibliothekarExtract
                implementationDependenciesMetadataBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            val expectChild = """
                ${testingProject.getProject2Name()}
                debugBibliothekarReport
                debugRuntimeOnlyDependenciesMetadataBibliothekarExtract
                debugImplementationDependenciesMetadataBibliothekarExtract
                runtimeOnlyDependenciesMetadataBibliothekarExtract
                implementationDependenciesMetadataBibliothekarExtract
            """.trimIndent().unifyLineBreak()
            assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
            assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
        }
    }
}
