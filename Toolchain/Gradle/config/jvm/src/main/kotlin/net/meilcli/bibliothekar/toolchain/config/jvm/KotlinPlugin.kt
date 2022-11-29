package net.meilcli.bibliothekar.toolchain.config.jvm

import net.meilcli.bibliothekar.config.Dependencies
import net.meilcli.bibliothekar.toolchain.config.core.BasePlugin
import org.gradle.api.artifacts.dsl.DependencyHandler

class KotlinPlugin : BasePlugin() {

    override fun DependencyHandler.applyDependencies() {
        superApplyDependencies()

        testImplementation(Dependencies.Junit.Junit)
        testImplementation(Dependencies.IoMockk.Mockk)
    }
}
