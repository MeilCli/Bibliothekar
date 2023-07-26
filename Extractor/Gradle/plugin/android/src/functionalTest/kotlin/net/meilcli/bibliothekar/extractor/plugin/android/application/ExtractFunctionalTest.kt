package net.meilcli.bibliothekar.extractor.plugin.android.application

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class ExtractFunctionalTest {

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
                        id('com.android.application')
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
                        implementation("com.soywiz.korlibs.klock:klock-android:4.0.3")
                    }
                """.trimIndent()
            )

            val runner = GradleRunner.create()
            runner.forwardOutput()
            runner.withArguments(testingProject.getProject1TaskName("implementationDependenciesListBibliothekarExtract"), "--info")
            runner.withDebug(true)
            runner.withProjectDir(testingProject.getProjectDirectory())
            val result = runner.build()

            assertNotNull("write\\s[1-9]\\soutputs\\sto".toRegex().find(result.output))
            assert(result.output.isNotEmpty()){
                result.output
            }
        }
    }
}
