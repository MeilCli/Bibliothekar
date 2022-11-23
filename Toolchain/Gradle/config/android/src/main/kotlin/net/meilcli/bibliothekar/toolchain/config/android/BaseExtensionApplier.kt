package net.meilcli.bibliothekar.toolchain.config.android

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

object BaseExtensionApplier {

    fun apply(extension: BaseExtension) {
        extension.compileSdkVersion(32)
        extension.defaultConfig {
            it.minSdk = 21
            it.targetSdk = 32
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
        extension.compileOptions{
            it.setSourceCompatibility(JavaVersion.VERSION_1_8)
            it.setTargetCompatibility(JavaVersion.VERSION_1_8)
        }
    }
}