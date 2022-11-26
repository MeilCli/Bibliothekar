package net.meilcli.bibliothekar.toolchain.config.android

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

object BaseExtensionApplier {

    private const val compileSdkVersion = 32
    private const val minSdkVersion = 21
    private const val targetSdkVersion = 32

    fun apply(extension: BaseExtension) {
        extension.compileSdkVersion(compileSdkVersion)
        extension.defaultConfig {
            it.minSdk = minSdkVersion
            it.targetSdk = targetSdkVersion
            it.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            it.consumerProguardFile("consumer-rules.pro")
        }
        extension.buildTypes.getByName("release") {
            it.minifyEnabled(false)
            it.proguardFiles(
                extension.getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        extension.compileOptions {
            it.setSourceCompatibility(JavaVersion.VERSION_1_8)
            it.setTargetCompatibility(JavaVersion.VERSION_1_8)
        }
    }
}
