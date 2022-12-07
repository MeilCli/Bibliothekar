package net.meilcli.bibliothekar.extractor.plugin.android.variant

import net.meilcli.bibliothekar.extractor.plugin.android.TestingProject
import net.meilcli.bibliothekar.extractor.plugin.android.unifyLineBreak
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

@Suppress("detekt.LongMethod", "detekt.LargeClass")
class TransitiveVariantFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun testMatchingFallbackBuildType() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                        staging {
                            matchingFallbacks = ["debug"]
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
            debugBibliothekarReport
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
    }

    @Test
    fun testMatchingFallbackProductFlavor() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                    }
                    flavorDimensions "version"
                    productFlavors {
                        demo {
                            dimension "version"
                            matchingFallbacks = ["trial"]
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
                    }
                    flavorDimensions "version"
                    productFlavors {
                        trial {
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
            trialDebugBibliothekarReport
            trialDebugRuntimeOnlyDependenciesListBibliothekarExtract
            trialDebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            trialRuntimeOnlyDependenciesListBibliothekarExtract
            trialImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
    }

    @Test
    fun testMissingDimensionStrategyProductFlavor() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                        missingDimensionStrategy 'version', 'demo', 'full'
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

    @Test
    fun testSeriesDependencyProductFlavor() {
        val testingProject = TestingProject.TripleChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                    }
                    flavorDimensions "version", "minApi"
                    productFlavors {
                        demo {
                            dimension "version"
                        }
                        full {
                            dimension "version"
                        }
                        min23 {
                            dimension "minApi"
                        }
                        min30 {
                            dimension "minApi"
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
                    implementation project('${testingProject.getProject3Name()}')
                }
            """.trimIndent()
        )
        testingProject.writeProject3BuildGradle(
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
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("demoMin23DebugBibliothekarConfirm"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        val expectParent = """
            ${testingProject.getProject1Name()}
            demoMin23DebugBibliothekarReport
            demoMin23DebugRuntimeOnlyDependenciesListBibliothekarExtract
            demoMin23DebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            demoMin23RuntimeOnlyDependenciesListBibliothekarExtract
            demoMin23ImplementationDependenciesListBibliothekarExtract
            demoRuntimeOnlyDependenciesListBibliothekarExtract
            demoImplementationDependenciesListBibliothekarExtract
            min23RuntimeOnlyDependenciesListBibliothekarExtract
            min23ImplementationDependenciesListBibliothekarExtract
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
        val expectGrandChild = """
            ${testingProject.getProject3Name()}
            debugBibliothekarReport
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectGrandChild), result.output)
    }

    @Test
    fun testParallelDependencyProductFlavor() {
        val testingProject = TestingProject.TripleChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                    }
                    flavorDimensions "version", "minApi"
                    productFlavors {
                        demo {
                            dimension "version"
                        }
                        full {
                            dimension "version"
                        }
                        min23 {
                            dimension "minApi"
                        }
                        min30 {
                            dimension "minApi"
                        }
                    }
                }
                dependencies {
                    implementation project('${testingProject.getProject2Name()}')
                    implementation project('${testingProject.getProject3Name()}')
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
        testingProject.writeProject3BuildGradle(
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
                    flavorDimensions "minApi"
                    productFlavors {
                        min23 {
                            dimension "minApi"
                        }
                        min30 {
                            dimension "minApi"
                        }
                    }
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withArguments(testingProject.getProject1TaskName("demoMin23DebugBibliothekarConfirm"))
        runner.withProjectDir(testingProject.getProjectDirectory())
        val result = runner.build()

        val expectParent = """
            ${testingProject.getProject1Name()}
            demoMin23DebugBibliothekarReport
            demoMin23DebugRuntimeOnlyDependenciesListBibliothekarExtract
            demoMin23DebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            demoMin23RuntimeOnlyDependenciesListBibliothekarExtract
            demoMin23ImplementationDependenciesListBibliothekarExtract
            demoRuntimeOnlyDependenciesListBibliothekarExtract
            demoImplementationDependenciesListBibliothekarExtract
            min23RuntimeOnlyDependenciesListBibliothekarExtract
            min23ImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        val expectChild1 = """
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
        val expectChild2 = """
            ${testingProject.getProject3Name()}
            min23DebugBibliothekarReport
            min23DebugRuntimeOnlyDependenciesListBibliothekarExtract
            min23DebugImplementationDependenciesListBibliothekarExtract
            debugRuntimeOnlyDependenciesListBibliothekarExtract
            debugImplementationDependenciesListBibliothekarExtract
            min23RuntimeOnlyDependenciesListBibliothekarExtract
            min23ImplementationDependenciesListBibliothekarExtract
            runtimeOnlyDependenciesListBibliothekarExtract
            implementationDependenciesListBibliothekarExtract
        """.trimIndent().unifyLineBreak()
        assertTrue(result.output.unifyLineBreak().contains(expectParent), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild1), result.output)
        assertTrue(result.output.unifyLineBreak().contains(expectChild2), result.output)
    }

    @Test
    fun testPartialDependency() {
        val testingProject = TestingProject.DualChildModule(temporaryFolder.root)
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
                    buildTypes {
                        release {}
                        debug {}
                    }
                }
                dependencies {
                    debugImplementation project('${testingProject.getProject2Name()}')
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
                    }
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
