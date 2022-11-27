package net.meilcli.bibliothekar.extractor.plugin.core.dependencies

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier

class DependencyLoader : IDependencyLoader {

    override fun load(configuration: Configuration): Set<Dependency> {
        return configuration
            .resolvedConfiguration
            .lenientConfiguration
            .firstLevelModuleDependencies
            .filter { it.moduleVersion != "unspecified" }
            .flatMap { resolvedDependency ->
                resolvedDependency.allModuleArtifacts.map { it.moduleVersion.id.toDependency() } + resolvedDependency.module.id.toDependency()
            }
            .toSet()
    }

    private fun ModuleVersionIdentifier.toDependency(): Dependency {
        return Dependency(group = group, name = name, version = version)
    }
}
