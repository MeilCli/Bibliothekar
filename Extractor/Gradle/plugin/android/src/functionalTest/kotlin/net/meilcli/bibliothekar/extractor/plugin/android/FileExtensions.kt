package net.meilcli.bibliothekar.extractor.plugin.android

import java.io.File

fun File.writeAndroidSettingText(text: String) {
    writeText(
        """
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                    google()
                }
                resolutionStrategy {
                    eachPlugin {
                        if (requested.id.id == "com.android.library") {
                            useModule("com.android.tools.build:gradle:${"$"}{requested.version}")
                        }
                        if (requested.id.id == "com.android.application") {
                            useModule("com.android.tools.build:gradle:${"$"}{requested.version}")
                        }
                        if (requested.id.id == "com.android.dynamic-feature") {
                            useModule("com.android.tools.build:gradle:${"$"}{requested.version}")
                        }
                    }
                }
            }            
        """.trimIndent() + "\n" + text
    )
}
