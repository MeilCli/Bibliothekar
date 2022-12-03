package net.meilcli.bibliothekar.toolchain.config.android

import com.android.build.gradle.AppExtension
import net.meilcli.bibliothekar.config.Dependencies
import net.meilcli.bibliothekar.toolchain.config.core.BasePlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationPlugin : BasePlugin() {

    override fun apply(project: Project) {
        super.apply(project)

        project.extensions
            .findByType(AppExtension::class.java)
            ?.let { BaseExtensionApplier.apply(it) }
        project.tasks
            .withType(KotlinCompile::class.java)
            .forEach {
                it.kotlinOptions.jvmTarget = "1.8"
            }
    }

    override fun RepositoryHandler.applyRepositories() {
        superApplyRepositories()

        google()
    }

    override fun DependencyHandler.applyDependencies() {
        superApplyDependencies()

        implementation(Dependencies.AndroidxAppcompat.Appcompat)

        testImplementation(Dependencies.Junit.Junit)

        androidTestImplementation(Dependencies.AndroidxTestExt.Junit)
        androidTestImplementation(Dependencies.AndroidxTestEspresso.EspressoCore)
    }
}
