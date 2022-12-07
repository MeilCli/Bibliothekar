package net.meilcli.bibliothekar.extractor.plugin.android.variant

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import net.meilcli.bibliothekar.extractor.plugin.android.unifyLineBreak
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

@Suppress("detekt.LongMethod")
class LibraryToLibraryVariantFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun testSimpleBuildType() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('com.android.library')
                    id('net.meilcli.bibliothekar.extractor.plugin.android')
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
                        staging {}
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
                    id('com.android.library')
                    id('net.meilcli.bibliothekar.extractor.plugin.android')
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
                        staging {}
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("stagingBibliothekarConfirm"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        val expectParent = """
            ${testingProject.getProject1Name()}
            stagingBibliothekarReport
            stagingRuntimeOnlyDependenciesListBibliothekarExtract
            stagingImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        val expectChild = """
            ${testingProject.getProject2Name()}
            stagingBibliothekarReport
            stagingRuntimeOnlyDependenciesListBibliothekarExtract
            stagingImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
    }

    @Test
    fun testSimpleProductFlavor() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
        testingProject.setup()
        testingProject.writeProject1BuildGradle(
            """
                plugins {
                    id('com.android.library')
                    id('net.meilcli.bibliothekar.extractor.plugin.android')
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
                    flavorDimensions "version"
                    productFlavors {
                        demo {
                            dimension "version"
                        }
                        full {
                            dimension "version"
                        }
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
                    id('com.android.library')
                    id('net.meilcli.bibliothekar.extractor.plugin.android')
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
                        staging {}
                    }
                    flavorDimensions "version"
                    productFlavors {
                        demo {
                            dimension "version"
                        }
                        full {
                            dimension "version"
                        }
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("demoDebugBibliothekarConfirm"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        val expectParent = """
            ${testingProject.getProject1Name()}
            demoDebugBibliothekarReport
            demoDebugRuntimeOnlyDependenciesListBibliothekarExtract
            demoDebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            demoRuntimeOnlyDependenciesListBibliothekarExtract
            demoImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        val expectChild = """
            ${testingProject.getProject2Name()}
            demoDebugBibliothekarReport
            demoDebugRuntimeOnlyDependenciesListBibliothekarExtract
            demoDebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            demoRuntimeOnlyDependenciesListBibliothekarExtract
            demoImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
    }
}
