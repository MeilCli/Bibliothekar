package net.meilcli.bibliothekar.extractor.plugin.bundled.variant

import net.meilcli.bibliothekar.extractor.plugin.bundled.TestingProject
import net.meilcli.bibliothekar.extractor.plugin.bundled.unifyLineBreak
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

@Suppress("detekt.LongMethod")
class AndroidVariantFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun testSimpleAndroidToJvm() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                    }
                }
                dependencies {
                    implementation project('${testingProject.getProject2Name()}')
                }
            """.trimIndent()
        )
        testingProject.writeProject2BuildGradle(
            """
                plugins {
                    id('java')
                    id('net.meilcli.bibliothekar.extractor.plugin.bundled')
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
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        val expectChild = """
            ${testingProject.getProject2Name()}
            bibliothekarReport
            implementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
    }
}
