package net.meilcli.bibliothekar.extractor.plugin.core.dependencies

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import org.gradle.api.artifacts.Configuration

interface IDependencyLoader {

    fun load(configuration: Configuration): Set<Dependency>
}
