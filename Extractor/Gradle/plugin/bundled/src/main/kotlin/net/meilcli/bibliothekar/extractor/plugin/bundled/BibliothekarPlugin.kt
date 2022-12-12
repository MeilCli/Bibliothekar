package net.meilcli.bibliothekar.extractor.plugin.bundled

import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.DynamicFeaturePlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

@Suppress("unused")
class BibliothekarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.handlePlugin<AppPlugin> { project.tryInstallAndroidPlugin() }
        project.handlePlugin<LibraryPlugin> { project.tryInstallAndroidPlugin() }
        project.handlePlugin<DynamicFeaturePlugin> { project.tryInstallAndroidPlugin() }
        project.handlePlugin("org.jetbrains.kotlin.android") { project.tryInstallAndroidPlugin() }
        project.handlePlugin<JavaPlugin> { project.tryInstallJvmPlugin() }
    }

    private inline fun <reified T : Plugin<*>> Project.handlePlugin(crossinline handler: () -> Unit) {
        if (plugins.hasPlugin(T::class.java)) {
            handler()
        } else {
            plugins.whenPluginAdded {
                if (it is T) {
                    handler()
                }
            }
        }
    }

    private fun Project.handlePlugin(id: String, handler: () -> Unit) {
        if (plugins.hasPlugin(id)) {
            handler()
        } else {
            plugins.whenPluginAdded {
                if (plugins.hasPlugin(id)) {
                    handler()
                }
            }
        }
    }

    private fun Project.isInstalledPlugin(): Boolean {
        return plugins.hasPlugin("net.meilcli.bibliothekar.extractor.plugin.jvm") ||
            plugins.hasPlugin("net.meilcli.bibliothekar.extractor.plugin.android")
    }

    private fun Project.tryInstallJvmPlugin() {
        if (isInstalledPlugin()) {
            return
        }
        plugins.apply("net.meilcli.bibliothekar.extractor.plugin.jvm")
    }

    private fun Project.tryInstallAndroidPlugin() {
        if (isInstalledPlugin()) {
            return
        }
        plugins.apply("net.meilcli.bibliothekar.extractor.plugin.android")
    }
}
