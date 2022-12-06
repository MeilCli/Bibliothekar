package net.meilcli.bibliothekar.extractor.plugin.android.dynamic

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class ExtractWithKotlinFunctionalTest {

    class TestExtract {

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
            runner.withArguments(testingProject.getProject1TaskName("implementationDependenciesMetadataBibliothekarExtract"), "--info")
            runner.withDebug(true)
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertNotNull("write\\s[1-9]\\soutputs\\sto".toRegex().find(result.output))
        }
    }
}
